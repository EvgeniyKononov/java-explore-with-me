package ru.practicum.ewm.event.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.validation.Marker;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NewEventDto {
    @NotNull(groups = Marker.OnCreate.class)
    @Length(min = 20, max = 2000)
    private String annotation;
    @NotNull(groups = Marker.OnCreate.class)
    private Long category;
    @NotNull(groups = Marker.OnCreate.class)
    @Length(min = 20, max = 7000)
    private String description;
    @NotNull(groups = Marker.OnCreate.class)
    private String eventDate;
    @NotNull(groups = Marker.OnCreate.class)
    private LocationDto location;
    protected Boolean paid;
    protected Integer participantLimit;
    protected Boolean requestModeration;
    @NotNull(groups = Marker.OnCreate.class)
    private String title;

    public Boolean getPaid() {
        return !Objects.isNull(paid) && paid;
    }

    public Integer getParticipantLimit() {
        return Objects.isNull(participantLimit) ? 0 : participantLimit;
    }

    public Boolean getRequestModeration() {
        return Objects.isNull(requestModeration) || requestModeration;
    }

}
