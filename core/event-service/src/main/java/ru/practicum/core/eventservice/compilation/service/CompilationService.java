package ru.practicum.core.eventservice.compilation.service;

import ru.practicum.core.eventservice.compilation.model.dto.CompilationDto;
import ru.practicum.core.eventservice.compilation.model.dto.NewCompilationDto;
import ru.practicum.core.eventservice.compilation.model.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAllComps(Boolean pinned, int from, int size);

    CompilationDto getCompById(long compId);

    CompilationDto create(NewCompilationDto newCompilationDto);

    void deleteById(long compId);

    CompilationDto update(long compId, UpdateCompilationRequest updateRequest);


}
