package ru.practicum.ewm.compilation.dto;

import lombok.*;
import ru.practicum.ewm.validation.Marker;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NewCompilationDto {
    private Set<Long> events;
    private Boolean pinned;
    @NotNull(groups = Marker.OnCreate.class)
    private String title;

    public Boolean getPinned() {
        return !Objects.isNull(pinned) && pinned;
    }
}
