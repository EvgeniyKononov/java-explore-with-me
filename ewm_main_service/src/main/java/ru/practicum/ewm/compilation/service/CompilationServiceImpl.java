package ru.practicum.ewm.compilation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dao.CompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.raiting.dto.RatingDto;
import ru.practicum.ewm.raiting.service.RatingService;

import java.util.*;

import static ru.practicum.ewm.constant.Constant.NOT_FOUND_COMPILATION_MSG;
import static ru.practicum.ewm.constant.Constant.NOT_FOUND_ID_REASON;

@Service
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventService eventService;
    private final RatingService ratingService;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository, EventService eventService, RatingService ratingService) {
        this.compilationRepository = compilationRepository;
        this.eventService = eventService;
        this.ratingService = ratingService;
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = getEvents(newCompilationDto);
        Compilation compilation = compilationRepository.save(CompilationMapper.toNewEntity(newCompilationDto, events));
        log.info("Created compilation {}", compilation);
        Map<Long, Integer> views = eventService.getStats(events);
        Map<Long, RatingDto> ratings = ratingService.getRatingsByEvents(events);
        return CompilationMapper.toDto(compilation, views, ratings);
    }

    @Override
    public void deleteById(Long compId) {
        if (compilationRepository.findById(compId).isPresent()) {
            compilationRepository.deleteById(compId);
        } else throw new NotFoundException(NOT_FOUND_COMPILATION_MSG, NOT_FOUND_ID_REASON);
    }

    @Override
    public CompilationDto updateCompilation(NewCompilationDto newCompilationDto, Long compId) {
        if (compilationRepository.findById(compId).isPresent()) {
            List<Event> events = getEvents(newCompilationDto);
            Compilation compilation1 = compilationRepository.findById(compId)
                    .orElseThrow(() -> new NotFoundException("", ""));
            Compilation compilation = CompilationMapper.toEntity(newCompilationDto, events, compilation1);
            compilation = compilationRepository.save(compilation);
            log.info("Updated compilation {}", compilation);
            Map<Long, Integer> views = eventService.getStats(events);
            Map<Long, RatingDto> ratings = ratingService.getRatingsByEvents(events);
            return CompilationMapper.toDto(compilation, views, ratings);
        } else throw new NotFoundException(NOT_FOUND_COMPILATION_MSG, NOT_FOUND_ID_REASON);
    }

    @Override
    public List<CompilationDto> getCompilations(boolean pinned, PageRequest page) {
        List<Compilation> compilations = compilationRepository.findAllByPinnedIs(pinned, page);
        Set<Event> events = new HashSet<>();
        for (Compilation compilation : compilations) {
            events.addAll(compilation.getEvents());
        }
        Map<Long, Integer> views = new HashMap<>(eventService.getStats(new ArrayList<>(events)));
        Map<Long, RatingDto> ratings = new HashMap<>(ratingService.getRatingsByEvents(new ArrayList<>(events)));
        return CompilationMapper.toDtos(compilations, views, ratings);
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_COMPILATION_MSG, NOT_FOUND_ID_REASON));
        Map<Long, Integer> views = eventService.getStats(compilation.getEvents());
        Map<Long, RatingDto> ratings = ratingService.getRatingsByEvents(compilation.getEvents());
        return CompilationMapper.toDto(compilation, views, ratings);
    }

    private List<Event> getEvents(NewCompilationDto newCompilationDto) {
        List<Long> eventsId = new ArrayList<>(newCompilationDto.getEvents());
        return eventService.findByIds(eventsId);
    }
}
