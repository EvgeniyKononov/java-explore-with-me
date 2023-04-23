package ru.practicum.ewm.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class ResponseStatsDto {
    private String app;
    private String uri;
    private Integer hits;
}
