package ru.practicum.ewm.raiting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.event.model.Event;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class LikeDislike {
    private Map<Event, Integer> likes;
    private Map<Event, Integer> dislike;
}
