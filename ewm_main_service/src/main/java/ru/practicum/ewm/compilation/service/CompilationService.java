package ru.practicum.ewm.compilation.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteById(Long compId);

    CompilationDto updateCompilation(NewCompilationDto newCompilationDto, Long compId);

    List<CompilationDto> getCompilations(boolean pinned, PageRequest page);

    CompilationDto getCompilation(Long compId);
}
