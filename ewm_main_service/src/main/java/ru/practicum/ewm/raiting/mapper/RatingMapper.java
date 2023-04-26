package ru.practicum.ewm.raiting.mapper;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.raiting.dto.InitiatorRatingDto;
import ru.practicum.ewm.raiting.dto.RatingDto;
import ru.practicum.ewm.raiting.model.LikeDislike;
import ru.practicum.ewm.raiting.model.Rate;
import ru.practicum.ewm.raiting.model.Rating;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.util.*;

public class RatingMapper {
    public static Rating toNewEntity(User rater, Event event, Rate rate) {
        return Rating.builder()
                .rater(rater)
                .event(event)
                .rate(rate)
                .build();
    }

    public static RatingDto toDto(Long id, Integer likes, Integer dislikes) {
        return RatingDto.builder()
                .eventId(id)
                .likes(likes)
                .dislikes(dislikes)
                .build();
    }

    public static Map<Long, RatingDto> toDtoMap(List<Event> events, Map<Event, Integer> likes,
                                                Map<Event, Integer> dislikes) {
        Map<Long, RatingDto> ratingDtoMap = new HashMap<>();
        for (Event event : events) {
            Long id = event.getId();
            ratingDtoMap.put(event.getId(), toDto(id, likes.get(event), dislikes.get(event)));
        }
        return ratingDtoMap;
    }

    public static List<RatingDto> toDtoList(LikeDislike likeDislike) {
        List<RatingDto> ratingDtoList = new ArrayList<>();
        Set<Event> events = getEvents(likeDislike);
        for (Event event : events) {
            ratingDtoList.add(toDto(event.getId(), likeDislike.getLikes().get(event),
                    likeDislike.getDislike().get(event)));
        }
        return ratingDtoList;
    }

    public static List<InitiatorRatingDto> toInitiatorDtos(LikeDislike likeDislike) {
        Set<Event> events = getEvents(likeDislike);
        Map<User, InitiatorRatingDto> dtoMap = new HashMap<>();
        for (Event event : events) {
            if (dtoMap.containsKey(event.getInitiator())) {
                InitiatorRatingDto dto = dtoMap.get(event.getInitiator());
                Set<Long> eventsId = dto.getEventsId();
                eventsId.add(event.getId());
                Integer likes = dto.getLikes() + likeDislike.getLikes().get(event);
                Integer dislikes = dto.getDislikes() + likeDislike.getDislike().get(event);
                dtoMap.put(event.getInitiator(), toInitiatorDto(event.getInitiator(), eventsId, likes, dislikes));
            } else {
                dtoMap.put(event.getInitiator(), toInitiatorDto(likeDislike, event));
            }
        }
        return new ArrayList<>(dtoMap.values());
    }

    private static InitiatorRatingDto toInitiatorDto(LikeDislike likeDislike, Event event) {
        return InitiatorRatingDto.builder()
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .eventsId(new HashSet<>(Set.of(event.getId())))
                .likes(likeDislike.getLikes().get(event))
                .dislikes(likeDislike.getDislike().get(event))
                .build();
    }

    private static InitiatorRatingDto toInitiatorDto(User initiator, Set<Long> eventsId, Integer likes,
                                                     Integer dislikes) {
        return InitiatorRatingDto.builder()
                .initiator(UserMapper.toShortDto(initiator))
                .eventsId(eventsId)
                .likes(likes)
                .dislikes(dislikes)
                .build();
    }

    private static Set<Event> getEvents(LikeDislike likeDislike) {
        Set<Event> events = new HashSet<>();
        events.addAll(likeDislike.getLikes().keySet());
        events.addAll(likeDislike.getDislike().keySet());
        return events;
    }
}
