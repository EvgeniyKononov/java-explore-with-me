package ru.practicum.ewm.raiting.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.util.Set;

@Getter
@Setter
@Builder
@ToString
public class InitiatorRatingDto {
    private UserShortDto initiator;
    private Set<Long> eventsId;
    private Integer likes;
    private Integer dislikes;
}
