package ru.practicum.ewm.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.RequestStatsDto;
import ru.practicum.ewm.dto.ResponseStatsDto;
import ru.practicum.ewm.event.dao.EventRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.service.LocationService;
import ru.practicum.ewm.raiting.dto.RatingDto;
import ru.practicum.ewm.raiting.model.Rate;
import ru.practicum.ewm.raiting.service.RatingService;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatusUpdateDto;
import ru.practicum.ewm.request.dto.RequestsByStatusDto;
import ru.practicum.ewm.request.service.RequestService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;
import static ru.practicum.ewm.constant.Constant.*;

@Service
@Slf4j
public class EventServiceImpl implements EventService {
    @PersistenceContext
    private EntityManager entityManager;
    private final EventRepository eventRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final LocationService locationService;
    private final RequestService requestService;
    private final StatsClient statsClient;
    private final RatingService ratingService;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, CategoryService categoryService,
                            UserService userService, LocationService locationService, RequestService requestService,
                            StatsClient statsClient, RatingService ratingService) {
        this.eventRepository = eventRepository;
        this.categoryService = categoryService;
        this.userService = userService;
        this.locationService = locationService;
        this.requestService = requestService;
        this.statsClient = statsClient;
        this.ratingService = ratingService;
    }

    @Override
    public Optional<Event> findById(Long eventId) {
        return eventRepository.findById(eventId);
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        Category category = categoryService.getCategoryEntity(newEventDto.getCategory());
        User initiator = userService.findById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_MSG, NOT_FOUND_ID_REASON));
        Location location = locationService.saveLocation(newEventDto.getLocation());
        Event event = EventMapper.toNewEntity(newEventDto, category, initiator, location);
        checkTime(event);
        event = eventRepository.save(event);
        Integer views = 0;
        RatingDto rating = RatingDto.builder().eventId(event.getId()).likes(0).dislikes(0).build();
        log.info("Created event {}", event);
        return EventMapper.toFullDto(event, views, rating);
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, PageRequest page) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, page);
        Map<Long, Integer> views = getStats(events);
        Map<Long, RatingDto> ratings = ratingService.getRatingsByEvents(events);
        log.info("Getting events {}", events);
        return EventMapper.toShortDtos(events, views, ratings);
    }

    @Override
    public EventFullDto getEventsById(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EVENT_MSG, NOT_FOUND_ID_REASON));
        Integer views = getStats(event.getId());
        RatingDto rating = ratingService.getRatingByEvent(event);
        log.info("Getting event {}", event);
        return EventMapper.toFullDto(event, views, rating);
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDto updateEventUserDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EVENT_MSG, NOT_FOUND_ID_REASON));
        checkState(userId, event);
        Category category = Objects.nonNull(updateEventUserDto.getCategory())
                ? categoryService.getCategoryEntity(updateEventUserDto.getCategory()) : null;
        Location location = Objects.nonNull(updateEventUserDto.getLocation())
                ? locationService.saveLocation(updateEventUserDto.getLocation()) : null;
        event = EventMapper.toUpdatedEntity(event, updateEventUserDto, category, location);
        checkTime(event);
        event = eventRepository.save(event);
        Integer views = getStats(event.getId());
        RatingDto rating = ratingService.getRatingByEvent(event);
        log.info("Updated event {}", event);
        return EventMapper.toFullDto(event, views, rating);
    }

    @Override
    public List<RequestDto> getRequestsByEventId(Long userId, Long eventId) {
        return requestService.getRequestsByEventId(eventId);
    }

    @Override
    public RequestsByStatusDto updateEventRequestsStatus(Long eventId, Long userId,
                                                         RequestStatusUpdateDto statusUpdateDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EVENT_MSG, NOT_FOUND_ID_REASON));
        return requestService.updateRequestsStatusByEvent(statusUpdateDto, event);
    }

    @Override
    public List<EventFullDto> getEventsByAdminFilters(List<Long> users, List<String> statesStr, List<Long> categories,
                                                      String rangeStart, String rangeEnd, PageRequest page) {
        LocalDateTime start = Objects.isNull(rangeStart) ? null : LocalDateTime.parse(rangeStart, FORMAT);
        LocalDateTime end = Objects.isNull(rangeEnd) ? null : LocalDateTime.parse(rangeEnd, FORMAT);
        List<Event> events = getEventsByFilters(null, null, users, statesStr, categories, start, end, page);
        Map<Long, Integer> views = getStats(events);
        Map<Long, RatingDto> ratings = ratingService.getRatingsByEvents(events);
        log.info("Getting events {}", events);
        return EventMapper.toFullDtos(events, views, ratings);
    }

    @Override
    public List<EventShortDto> getEventsByPublicFilters(String text, List<Long> categories, Boolean paid,
                                                        String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                        String sort, PageRequest page, HttpServletRequest request) {
        LocalDateTime start = Objects.isNull(rangeStart)
                ? LocalDateTime.now() : LocalDateTime.parse(rangeStart, FORMAT);
        LocalDateTime end = Objects.isNull(rangeEnd) ? null : LocalDateTime.parse(rangeEnd, FORMAT);
        List<Event> events = getEventsByFilters(text, paid, null, List.of(State.PUBLISHED.toString()), categories,
                start, end, page);
        Map<Long, Integer> views = getStats(events);
        saveStats(request);
        Map<Long, RatingDto> ratings = ratingService.getRatingsByEvents(events);
        log.info("Getting events {}", events);
        List<EventShortDto> shortDtos = EventMapper.toShortDtos(events, views, ratings);
        return sortDto(sort, shortDtos);
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EVENT_MSG, NOT_FOUND_ID_REASON));
        Integer views = getStats(event.getId());
        saveStats(request);
        RatingDto rating = ratingService.getRatingByEvent(event);
        log.info("Getting event {}", event);
        return EventMapper.toFullDto(event, views, rating);
    }

    @Override
    public EventShortDto addRateToEvent(Long userId, Long eventId, Rate rate) {
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EVENT_MSG, NOT_FOUND_ID_REASON));
        User rater = userService.findById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_MSG, NOT_FOUND_ID_REASON));
        if (Objects.equals(event.getInitiator(), rater)) {
            throw new ConflictException(INCORRECT_RATE_ADDING_MSG, INCORRECT_RATER_REASON);
        }
        RatingDto rating = ratingService.addRate(rater, event, rate);
        Integer views = getStats(event.getId());
        return EventMapper.toShortDto(event, views, rating);
    }

    @Override
    public EventShortDto deleteRateFromEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EVENT_MSG, NOT_FOUND_ID_REASON));
        User rater = userService.findById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_MSG, NOT_FOUND_ID_REASON));
        RatingDto rating = ratingService.deleteRate(rater, event);
        Integer views = getStats(event.getId());
        return EventMapper.toShortDto(event, views, rating);
    }


    @Override
    public List<Event> findByIds(List<Long> eventsId) {
        return eventRepository.findAllById(eventsId);
    }

    @Override
    public Map<Long, Integer> getStats(List<Event> events) {
        Map<Long, Integer> views = new HashMap<>();
        for (Event event : events) {
            Long id = event.getId();
            Integer view = getStats(id);
            views.put(id, view);
        }
        return views;
    }

    private Integer getStats(Long id) {
        ResponseEntity<List<ResponseStatsDto>> response = statsClient.getStats(START, END, URI + id, UNIQUE);
        List<ResponseStatsDto> responseStatsDtos = response.getBody();
        assert responseStatsDtos != null;
        return responseStatsDtos.size() > 0 ? responseStatsDtos.get(0).getHits() : 0;
    }

    private void saveStats(HttpServletRequest request) {
        RequestStatsDto requestStatsDto = RequestStatsDto.builder()
                .app(APPLICATION_NAME)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(FORMAT))
                .build();
        statsClient.createStats(requestStatsDto);
    }

    private void checkState(Long userId, Event event) {
        if (Objects.nonNull(userId)) {
            if (Objects.equals(event.getState(), State.PUBLISHED)) {
                throw new ConflictException(INCORRECT_STATE_MSG, INCORRECT_STATE_REASON);
            }
        }
    }

    private void checkTime(Event event) {
        if (event.getEventDate().minusHours(HOURS_BEFORE_EVENT).isBefore(event.getCreatedOn())) {
            throw new ConflictException(INCORRECT_TIME_MSG, INCORRECT_TIME_REASON);
        }
        if (Objects.nonNull(event.getPublishedOn())) {
            if (event.getEventDate().minusHours(HOUR_BEFORE_EVENT).isBefore(event.getPublishedOn())) {
                throw new ConflictException(INCORRECT_TIME_MSG, INCORRECT_TIME_REASON);
            }
        }
    }

    private List<EventShortDto> sortDto(String sort, List<EventShortDto> shortDtos) {
        if (Objects.isNull(sort)) {
            return shortDtos;
        } else if (Objects.equals(sort, "EVENT_DATE")) {
            return shortDtos.stream()
                    .sorted(Comparator.comparing(EventShortDto::getEventDate))
                    .collect(Collectors.toList());
        } else if (Objects.equals(sort, "VIEWS")) {
            return shortDtos.stream()
                    .sorted(comparingInt(EventShortDto::getViews))
                    .collect(Collectors.toList());
        } else if (Objects.equals(sort, "LIKES")) {
            return shortDtos.stream()
                    .sorted(Comparator.comparingLong(EventShortDto::getLikes).reversed())
                    .collect(Collectors.toList());
        } else if (Objects.equals(sort, "DISLIKES")) {
            return shortDtos.stream()
                    .sorted(comparingInt(EventShortDto::getDislikes).reversed())
                    .collect(Collectors.toList());
        } else {
            throw new ConflictException(INCORRECT_SORT_TYPE_MSG, INCORRECT_SORT_TYPE_REASON);
        }
    }

    private List<Event> getEventsByFilters(String text, Boolean paid, List<Long> users, List<String> statesStr,
                                           List<Long> categories, LocalDateTime start, LocalDateTime end,
                                           PageRequest page) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);
        Root<Event> event = query.from(Event.class);
        Predicate criteria = builder.conjunction();
        if (Objects.nonNull(users)) {
            Predicate inUsers = event.get("initiator").in(users);
            criteria = builder.and(criteria, inUsers);
        }
        if (Objects.nonNull(statesStr)) {
            List<State> states = new ArrayList<>();
            for (String state : statesStr) {
                states.add(State.valueOf(state));
            }
            Predicate inStates = event.get("state").in(states);
            criteria = builder.and(criteria, inStates);
        }
        if (Objects.nonNull(categories)) {
            Predicate inCategory = event.get("category").in(categories);
            criteria = builder.and(criteria, inCategory);
        }
        if (Objects.nonNull(end)) {
            Predicate beforeEnd = builder.lessThanOrEqualTo(event.get("eventDate"), end);
            criteria = builder.and(criteria, beforeEnd);
        }
        if (Objects.nonNull(start)) {
            Predicate afterStart = builder.greaterThanOrEqualTo(event.get("eventDate"), start);
            criteria = builder.and(criteria, afterStart);
        }
        if (Objects.nonNull(text)) {
            Predicate annotation = builder.like(builder.lower(event.get("annotation")),
                    "%" + text.toLowerCase() + "%");
            Predicate description = builder.like(builder.lower(event.get("description")),
                    "%" + text.toLowerCase() + "%");
            Predicate hasText = builder.or(annotation, description);
            criteria = builder.and(criteria, hasText);
        }
        if (Objects.nonNull(paid)) {
            Predicate isPaid = event.get("paid").in(paid);
            criteria = builder.and(criteria, isPaid);
        }
        query.select(event).where(criteria);
        return entityManager.createQuery(query).setFirstResult(page.getPageNumber()).setMaxResults(page.getPageSize())
                .getResultList();
    }
}
