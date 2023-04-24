package ru.practicum.ewm.event.controller.pub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.raiting.model.Rate;
import ru.practicum.ewm.raiting.model.SortType;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/events")
public class EventPublicController {
    private final EventService eventService;

    @Autowired
    public EventPublicController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping(value = "/{eventId}")
    public EventFullDto getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Getting events by id={}", eventId);
        return eventService.getEventById(eventId, request);
    }

    @GetMapping
    public List<EventShortDto> getEventsByFilters(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false) String rangeStart,
                                                  @RequestParam(required = false) String rangeEnd,
                                                  @RequestParam(required = false, defaultValue = "false")
                                                  Boolean onlyAvailable,
                                                  @RequestParam(required = false) String sort,
                                                  @RequestParam(required = false, defaultValue = "0") int from,
                                                  @RequestParam(required = false, defaultValue = "10") int size,
                                                  HttpServletRequest request) {
        log.info("Getting events by following filters text={}, categories={}, paid={}, rangeStart={}, " +
                        "rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}", text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);
        return eventService.getEventsByPublicFilters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                PageRequest.of(from, size), request);
    }

    @GetMapping(value = "/ratings")
    public List<EventShortDto> getEventsByRating(@RequestParam(required = false, defaultValue = "LIKE") Rate rate,
                                                 @RequestParam(required = false, defaultValue = "DESC") SortType sort,
                                                 @RequestParam(required = false, defaultValue = "0") int from,
                                                 @RequestParam(required = false, defaultValue = "10") int size,
                                                 HttpServletRequest request) {
        log.info("Getting events by rating rate={}, sort={}, from={}, size={}", rate, sort, from, size);
        return eventService.getEventsByRating(rate, sort, PageRequest.of(from, size), request);
    }
}
