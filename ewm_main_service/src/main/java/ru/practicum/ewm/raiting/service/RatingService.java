package ru.practicum.ewm.raiting.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.raiting.dto.RatingDto;
import ru.practicum.ewm.raiting.model.Rate;
import ru.practicum.ewm.raiting.model.SortType;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.Map;

public interface RatingService {
    RatingDto addRate(User rater, Event event, Rate rate);

    RatingDto getRatingByEvent(Event event);

    Map<Long, RatingDto> getRatingsByEvents(List<Event> events);

    RatingDto deleteRate(User rater, Event event);

    List<RatingDto> getRatingsSortedByRate(Rate rate, SortType sort, PageRequest page);
}
