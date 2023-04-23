package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EventFullDto extends EventShortDto {
    private String createdOn;
    private String description;
    @NotNull
    private LocationDto location;
    private Integer participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private State state;

    @Builder(builderMethodName = "childBuilder")
    public EventFullDto(@NotNull String annotation, @NotNull CategoryDto category, Integer confirmedRequests,
                        @NotNull String eventDate, Long id, @NotNull UserShortDto initiator, @NotNull Boolean paid,
                        @NotNull String title, Integer views, String createdOn, String description,
                        LocationDto location, Integer participantLimit, String publishedOn, Boolean requestModeration,
                        State state) {
        super(annotation, category, confirmedRequests, eventDate, id, initiator, paid, title, views);
        this.createdOn = createdOn;
        this.description = description;
        this.location = location;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
    }

    public Integer getParticipantLimit() {
        return Objects.isNull(participantLimit) ? 0 : participantLimit;
    }

    public Boolean getRequestModeration() {
        return Objects.isNull(requestModeration) || requestModeration;
    }

}
