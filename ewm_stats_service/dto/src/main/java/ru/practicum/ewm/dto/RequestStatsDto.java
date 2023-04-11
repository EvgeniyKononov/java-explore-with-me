package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RequestStatsDto {

    private Long id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;

}
