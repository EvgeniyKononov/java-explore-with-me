package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.RequestStatsDto;
import ru.practicum.ewm.dto.ResponseStatsDto;

import java.util.List;

public interface StatsService {
    RequestStatsDto createStats(RequestStatsDto requestStatsDto);

    List<ResponseStatsDto> getStats(String start, String end, List<String> uris, Boolean unique);
}
