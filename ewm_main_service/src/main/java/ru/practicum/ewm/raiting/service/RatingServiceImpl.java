package ru.practicum.ewm.raiting.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.raiting.dao.RatingRepository;
import ru.practicum.ewm.raiting.dto.RatingDto;
import ru.practicum.ewm.raiting.mapper.RatingMapper;
import ru.practicum.ewm.raiting.model.Rate;
import ru.practicum.ewm.raiting.model.Rating;
import ru.practicum.ewm.raiting.model.RatingPK;
import ru.practicum.ewm.raiting.model.SortType;
import ru.practicum.ewm.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public RatingDto addRate(User rater, Event event, Rate rate) {
        Rating rating = RatingMapper.toNewEntity(rater, event, rate);
        rating = ratingRepository.save(rating);
        log.info("Saved rating {}", rating);
        return getRatingByEvent(event);
    }

    @Override
    public RatingDto getRatingByEvent(Event event) {
        Integer likes = getLikes(event);
        Integer dislikes = getDislikes(event);
        return RatingMapper.toDto(event.getId(), likes, dislikes);
    }

    @Override
    public Map<Long, RatingDto> getRatingsByEvents(List<Event> events) {
        Map<Long, RatingDto> ratings = new HashMap<>();
        for (Event event : events) {
            ratings.put(event.getId(), getRatingByEvent(event));
        }
        return ratings;
    }

    @Override
    public RatingDto deleteRate(User rater, Event event) {
        ratingRepository.deleteById(new RatingPK(event.getId(), rater.getId()));
        return getRatingByEvent(event);
    }

    @Override
    public List<RatingDto> getRatingsSortedByRate(Rate rate, SortType sort, PageRequest page) {
        // List<Rating> ratings =- для 3-его этапа. Прошу не смотреть
        return null;
    }

    private Integer getLikes(Event event) {
        List<Rating> ratings = ratingRepository.findAllByEventAndRate(event, Rate.LIKE);
        return ratings.size();
    }

    private Integer getDislikes(Event event) {
        List<Rating> ratings = ratingRepository.findAllByEventAndRate(event, Rate.DISLIKE);
        return ratings.size();
    }

}
