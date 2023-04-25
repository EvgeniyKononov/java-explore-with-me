package ru.practicum.ewm.user.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserShortDto {
    private Long id;
    @NotNull
    private String name;
}
