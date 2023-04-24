package ru.practicum.ewm.raiting.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class RatingDto {
    private Long eventId;
    private Integer likes;
    private Integer dislikes;
}
