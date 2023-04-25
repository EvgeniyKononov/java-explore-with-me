package ru.practicum.ewm.raiting.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class RatingPK implements Serializable {
    private Long event;
    private Long rater;
}
