package ru.practicum.ewm.raiting.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.raiting.model.Rate;
import ru.practicum.ewm.raiting.model.Rating;
import ru.practicum.ewm.raiting.model.RatingPK;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, RatingPK> {
    List<Rating> findAllByEventAndRate(Event event, Rate like);
    //List<Rating> findAllByRate - для 3-его этапа. Прошу не смотреть
}
