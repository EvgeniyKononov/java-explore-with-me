package ru.practicum.ewm.client;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.dto.ResponseStatsDto;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<List<ResponseStatsDto>> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    protected <T> ResponseEntity<List<ResponseStatsDto>> post(String path, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body);
    }

    private <T> ResponseEntity<List<ResponseStatsDto>> makeAndSendRequest(HttpMethod method, String path,
                                                                          @Nullable Map<String, Object> parameters,
                                                                          @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<List<ResponseStatsDto>> ewmStatsServerResponse = null;
        ParameterizedTypeReference<List<ResponseStatsDto>> typeRef = new ParameterizedTypeReference<>() {
        };
        if (parameters != null) {
            ewmStatsServerResponse = rest.exchange(path, method, requestEntity, typeRef, parameters);
        } else {
            rest.exchange(path, method, requestEntity, Object.class);
        }
        return prepareGatewayResponse(ewmStatsServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<List<ResponseStatsDto>> prepareGatewayResponse(
            ResponseEntity<List<ResponseStatsDto>> response) {
        if (Objects.isNull(response)) {
            return null;
        }
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }
}
