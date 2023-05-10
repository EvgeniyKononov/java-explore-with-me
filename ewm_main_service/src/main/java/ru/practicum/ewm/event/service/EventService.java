package ru.practicum.ewm.event.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.raiting.model.Rate;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatusUpdateDto;
import ru.practicum.ewm.request.dto.RequestsByStatusDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EventService {
    Optional<Event> findById(Long eventId);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsByUserId(Long userId, PageRequest page);

    Map<Long, Integer> getStats(List<Event> events);

    EventFullDto getEventsById(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDto updateEventUserDto);

    List<RequestDto> getRequestsByEventId(Long userId, Long eventId);

    RequestsByStatusDto updateEventRequestsStatus(Long eventId, Long userId, RequestStatusUpdateDto statusUpdateDto);

    List<EventFullDto> getEventsByAdminFilters(List<Long> users, List<String> states, List<Long> categories,
                                               String rangeStart, String rangeEnd, PageRequest page);

    List<EventShortDto> getEventsByPublicFilters(String text, List<Long> categories, Boolean paid, String rangeStart,
                                                 String rangeEnd, Boolean onlyAvailable, String sort, PageRequest of,
                                                 HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);

    EventShortDto addRateToEvent(Long userId, Long eventId, Rate rate);

    EventShortDto deleteRateFromEvent(Long userId, Long eventId);

    List<Event> findByIds(List<Long> eventsId);
}
