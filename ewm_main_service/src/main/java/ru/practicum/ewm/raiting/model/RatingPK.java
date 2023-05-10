package ru.practicum.ewm.raiting.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class RatingPK implements Serializable {
    private Long event;
    private Long rater;
}
