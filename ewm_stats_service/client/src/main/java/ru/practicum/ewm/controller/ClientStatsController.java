package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.RequestStatsDto;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ClientStatsController {
    private final StatsClient statsClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> create(@RequestBody RequestStatsDto requestStatsDto) {
        log.info("Sending request to server for creation stats {}", requestStatsDto);
        return statsClient.createStats(requestStatsDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> get(@RequestParam(name = "start") String start,
                                      @RequestParam(name = "end") String end,
                                      @RequestParam(name = "uris", required = false) String uris,
                                      @RequestParam(name = "unique", defaultValue = "false") String unique) {
        log.info("Sending request to server for getting stats from {} to {}, uris = {}, unique = {}", start, end, uris, unique);
        return statsClient.getStats(start, end, uris, unique);
    }
}
