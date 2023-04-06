package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.RequestStatsDto;
import ru.practicum.ewm.dto.ResponseStatsDto;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    public RequestStatsDto create(@RequestBody RequestStatsDto requestStatsDto) {
        log.info("Creating stats {}", requestStatsDto);
        return statsService.createStats(requestStatsDto);
    }

    @GetMapping("/stats")
    public List<ResponseStatsDto> get(@RequestParam(name = "start") String start,
                                      @RequestParam(name = "end") String end,
                                      @RequestParam(name = "uris", required = false) List<String> uris,
                                      @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("Getting stats from {} to {}, uris = {}, unique = {}", start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }
}
