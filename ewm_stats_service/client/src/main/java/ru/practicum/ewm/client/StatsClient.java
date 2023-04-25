package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.RequestStatsDto;
import ru.practicum.ewm.dto.ResponseStatsDto;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${ewm-stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .build()
        );
    }

    public void createStats(RequestStatsDto requestStatsDto) {
        ResponseEntity<List<ResponseStatsDto>> response = post("/hit", requestStatsDto);
        if (Objects.isNull(response)) {
            ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Информация сохранена");
        }
    }

    public ResponseEntity<List<ResponseStatsDto>> getStats(String start, String end, String uris, String unique) {
        if (Objects.nonNull(uris)) {
            Map<String, Object> parameters = Map.of(
                    "start", start,
                    "end", end,
                    "uris", uris,
                    "unique", unique
            );
            return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        } else {
            Map<String, Object> parameters = Map.of(
                    "start", start,
                    "end", end,
                    "unique", unique
            );
            return get("/stats?start={start}&end={end}&unique={unique}", parameters);
        }
    }
}
