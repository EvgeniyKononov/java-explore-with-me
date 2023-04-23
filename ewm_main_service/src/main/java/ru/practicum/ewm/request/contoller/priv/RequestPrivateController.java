package ru.practicum.ewm.request.contoller.priv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users/{userId}/requests")
public class RequestPrivateController {
    private final RequestService requestService;

    @Autowired
    public RequestPrivateController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Creating request for event={} from user={}", eventId, userId);
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping
    public List<RequestDto> getRequestsByUserId(@PathVariable Long userId) {
        log.info("Searching request for user={}", userId);
        return requestService.getRequestsByUserId(userId);
    }

    @PatchMapping(value = "/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Canceling request={} for user={}", requestId, userId);
        return requestService.cancelRequest(userId, requestId);
    }
}
