package ru.practicum.ewm.raiting.mapper;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.raiting.dto.RatingDto;
import ru.practicum.ewm.raiting.model.Rate;
import ru.practicum.ewm.raiting.model.Rating;
import ru.practicum.ewm.user.model.User;

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
}
