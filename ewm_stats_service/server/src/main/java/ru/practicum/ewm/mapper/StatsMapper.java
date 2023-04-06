package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.RequestStatsDto;
import ru.practicum.ewm.dto.ResponseStatsDto;
import ru.practicum.ewm.model.Stats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsMapper {
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Stats toEntity(RequestStatsDto requestStatsDto) {


        return Stats.builder()
                .app(requestStatsDto.getApp())
                .ip(requestStatsDto.getIp())
                .uri(requestStatsDto.getUri())
                .timestamp(LocalDateTime.parse(requestStatsDto.getTimestamp(), format))
                .build();
    }

    public static RequestStatsDto toRequestDto(Stats stats) {
        return RequestStatsDto.builder()
                .id(stats.getId())
                .app(stats.getApp())
                .ip(stats.getIp())
                .uri(stats.getUri())
                .timestamp(stats.getTimestamp().format(format))
                .build();
    }

    public static ResponseStatsDto toResponseDto(String app, String uri, Integer hits) {
        return ResponseStatsDto.builder()
                .app(app)
                .uri(uri)
                .hits(hits)
                .build();
    }
}
