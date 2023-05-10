package ru.practicum.ewm.raiting.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.raiting.dao.RatingRepository;
import ru.practicum.ewm.raiting.dto.InitiatorRatingDto;
import ru.practicum.ewm.raiting.dto.RatingDto;
import ru.practicum.ewm.raiting.mapper.RatingMapper;
import ru.practicum.ewm.raiting.model.*;
import ru.practicum.ewm.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

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
        log.info("Getting rating for event={}", event);
        return RatingMapper.toDto(event.getId(), likes, dislikes);
    }

    @Override
    public Map<Long, RatingDto> getRatingsByEvents(List<Event> events) {
        List<Rating> ratings = ratingRepository.findAllByEventIn(events);
        LikeDislike likeDislike = getLikeDislike(ratings);
        log.info("Getting rating for events={}", events);
        return RatingMapper.toDtoMap(events, likeDislike.getLikes(), likeDislike.getDislike());
    }

    @Override
    public RatingDto deleteRate(User rater, Event event) {
        ratingRepository.deleteById(new RatingPK(event.getId(), rater.getId()));
        log.info("Deleted rating for event={}", event);
        return getRatingByEvent(event);
    }

    @Override
    public List<RatingDto> getRatingsForEvents(Rate rate, PageRequest page) {
        List<RatingDto> ratingDtos = getRatingsForEvents();
        log.info("Getting summary rating of events {}", ratingDtos);
        if (Objects.equals(rate, Rate.LIKE)) {
            return ratingDtos.stream()
                    .sorted(Comparator.comparingLong(RatingDto::getLikes).reversed())
                    .skip(page.getPageNumber())
                    .limit(page.getPageSize())
                    .collect(Collectors.toList());
        } else {
            return ratingDtos.stream()
                    .sorted(Comparator.comparingLong(RatingDto::getDislikes).reversed())
                    .skip(page.getPageNumber())
                    .limit(page.getPageSize())
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<InitiatorRatingDto> getRatingsForInitiators(Rate rate, PageRequest page) {
        List<Rating> ratings = ratingRepository.findAll();
        LikeDislike likeDislike = getLikeDislike(ratings);
        List<InitiatorRatingDto> initiatorRatingDtos = RatingMapper.toInitiatorDtos(likeDislike);
        log.info("Getting summary rating of initiators {}", initiatorRatingDtos);
        if (Objects.equals(rate, Rate.LIKE)) {
            return initiatorRatingDtos.stream()
                    .sorted(Comparator.comparingLong(InitiatorRatingDto::getLikes).reversed())
                    .skip(page.getPageNumber())
                    .limit(page.getPageSize())
                    .collect(Collectors.toList());
        } else {
            return initiatorRatingDtos.stream()
                    .sorted(Comparator.comparingLong(InitiatorRatingDto::getDislikes).reversed())
                    .skip(page.getPageNumber())
                    .limit(page.getPageSize())
                    .collect(Collectors.toList());
        }
    }

    private Integer getLikes(Event event) {
        List<Rating> ratings = ratingRepository.findAllByEventAndRate(event, Rate.LIKE);
        return ratings.size();
    }

    private Integer getDislikes(Event event) {
        List<Rating> ratings = ratingRepository.findAllByEventAndRate(event, Rate.DISLIKE);
        return ratings.size();
    }

    private List<RatingDto> getRatingsForEvents() {
        List<Rating> ratings = ratingRepository.findAll();
        LikeDislike likeDislike = getLikeDislike(ratings);
        return RatingMapper.toDtoList(likeDislike);
    }

    private LikeDislike getLikeDislike(List<Rating> ratings) {
        Map<Event, Integer> likes = new HashMap<>();
        Map<Event, Integer> dislikes = new HashMap<>();
        for (Rating rating : ratings) {
            if (Objects.equals(rating.getRate(), Rate.LIKE)) {
                if (likes.containsKey(rating.getEvent())) {
                    likes.put(rating.getEvent(), likes.get(rating.getEvent()) + 1);
                } else {
                    likes.put(rating.getEvent(), 1);
                }
            } else {
                if (dislikes.containsKey(rating.getEvent())) {
                    dislikes.put(rating.getEvent(), likes.get(rating.getEvent()) + 1);
                } else {
                    dislikes.put(rating.getEvent(), 1);
                }
            }
        }
        return new LikeDislike(likes, dislikes);
    }
}
