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

import java.util.*;

import static ru.practicum.ewm.constant.Constant.NOT_FOUND_COMPILATION_MSG;
import static ru.practicum.ewm.constant.Constant.NOT_FOUND_ID_REASON;

@Service
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventService eventService;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository, EventService eventService) {
        this.compilationRepository = compilationRepository;
        this.eventService = eventService;
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = getEvents(newCompilationDto);
        Compilation compilation = compilationRepository.save(CompilationMapper.toNewEntity(newCompilationDto, events));
        log.info("Created compilation {}", compilation);
        Map<Long, Integer> views = eventService.getStats(events);
        return CompilationMapper.toDto(compilation, views);
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
            return CompilationMapper.toDto(compilation, views);
        } else throw new NotFoundException(NOT_FOUND_COMPILATION_MSG, NOT_FOUND_ID_REASON);
    }

    @Override
    public List<CompilationDto> getCompilations(boolean pinned, PageRequest page) {
        List<Compilation> compilations = compilationRepository.findAllByPinnedIs(pinned, page);
        Map<Long, Integer> views = new HashMap<>();
        for (Compilation compilation : compilations) {
            views.putAll(eventService.getStats(compilation.getEvents()));
        }
        return CompilationMapper.toDtos(compilations, views);
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_COMPILATION_MSG, NOT_FOUND_ID_REASON));
        Map<Long, Integer> views = eventService.getStats(compilation.getEvents());
        return CompilationMapper.toDto(compilation, views);
    }

    private List<Event> getEvents(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();
        for (Long id : newCompilationDto.getEvents()) {
            Optional<Event> event = eventService.findById(id);
            event.ifPresent(events::add);
        }
        return events;
    }
}
