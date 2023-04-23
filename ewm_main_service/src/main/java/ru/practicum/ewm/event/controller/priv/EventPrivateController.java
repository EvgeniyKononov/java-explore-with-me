package ru.practicum.ewm.event.controller.priv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatusUpdateDto;
import ru.practicum.ewm.request.dto.RequestsByStatusDto;
import ru.practicum.ewm.validation.Marker;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;

    @Autowired
    public EventPrivateController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Creating event={} from user={}", newEventDto, userId);
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable Long userId,
                                         @RequestParam(required = false, defaultValue = "0") int from,
                                         @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Getting events for user={} from={} size={}", userId, from, size);
        return eventService.getEventsByUserId(userId, PageRequest.of(from, size));
    }

    @GetMapping(value = "/{eventId}")
    public EventFullDto getEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Getting event={} fo user={}", eventId, userId);
        return eventService.getEventsById(userId, eventId);
    }

    @PatchMapping(value = "/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventDto updateEventUserDto) {
        log.info("Updating event={} from user={} by following data = {}", eventId, userId, updateEventUserDto);
        return eventService.updateEvent(userId, eventId, updateEventUserDto);
    }

    @GetMapping(value = "/{eventId}/requests")
    public List<RequestDto> getRequestsByEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Getting requests for event={} fo user={}", eventId, userId);
        return eventService.getRequestsByEventId(userId, eventId);
    }

    @PatchMapping(value = "/{eventId}/requests")
    public RequestsByStatusDto updateStatusRequests(@PathVariable Long userId, @PathVariable Long eventId,
                                                    @Valid @RequestBody RequestStatusUpdateDto requestStatusUpdateDto) {
        log.info("Updating status for requests={} for event={} from user={}", requestStatusUpdateDto, eventId, userId);
        return eventService.updateEventRequestsStatus(eventId, userId, requestStatusUpdateDto);
    }

}
