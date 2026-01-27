package ru.practicum.core.eventservice.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.core.eventservice.compilation.model.Compilation;
import ru.practicum.core.eventservice.compilation.model.CompilationMapper;
import ru.practicum.core.eventservice.compilation.model.dto.CompilationDto;
import ru.practicum.core.eventservice.compilation.model.dto.NewCompilationDto;
import ru.practicum.core.eventservice.compilation.model.dto.UpdateCompilationRequest;
import ru.practicum.core.eventservice.compilation.repository.CompilationRepository;
import ru.practicum.core.eventservice.event.model.Event;
import ru.practicum.core.eventservice.event.service.EventService;
import ru.practicum.core.eventservice.exception.BadParameterException;
import ru.practicum.core.eventservice.exception.DataConflictException;
import ru.practicum.core.eventservice.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImp implements CompilationService {
    private final EventService eventService;
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        try {
            validateNewCompilationDto(newCompilationDto);
            Set<Event> eventSet = getEventsForCompilation(newCompilationDto.getEvents());
            Compilation compilation = Compilation.builder()
                    .title(newCompilationDto.getTitle())
                    .pinned(newCompilationDto.getPinned() != null ? newCompilationDto.getPinned() : false)
                    .events(eventSet)
                    .build();

            return compilationMapper.toDto(compilationRepository.save(compilation));
        } catch (DataAccessException e) {
            log.error("Access error", e);
            throw new DataConflictException("Access error");
        } catch (Exception e) {
            log.error("Database error", e);
            throw new DataConflictException("Database error");
        }
    }

    @Override
    public void deleteById(long compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Сompilation with id=%d not found", compId)));
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto update(long compId, UpdateCompilationRequest updateRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%d not found", compId)));
        Set<Long> eventIds = updateRequest.getEvents();
        Set<Event> eventsSet;
        if (eventIds == null || eventIds.isEmpty()) {
            eventsSet = new HashSet<>();
        } else {
            eventsSet = eventService.getEventsByIds(eventIds);
        }
        Boolean pinned = updateRequest.getPinned();
        if (pinned != null) {
            compilation.setPinned(pinned);
        }
        String title = updateRequest.getTitle();
        if (title != null) {
            compilation.setTitle(title);
        }
        compilation.setEvents(eventsSet);
        compilation = compilationRepository.save(compilation);
        return compilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getAllComps(Boolean pinned, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return (pinned != null)
                ? compilationRepository.findByPinned(pinned, page).stream()
                .map(compilationMapper::toDto)
                .collect(Collectors.toList())
                : compilationRepository.findAll(page).stream()
                .map(compilationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompById(long compId) {

        if (compId <= 0) {
            throw new BadParameterException("Id value is less than 1");
        }

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%d not found", compId)));
        return compilationMapper.toDto(
                compilationRepository.findById(compId)
                        .orElseThrow(() -> new NotFoundException("Compilation with id=%d not found".formatted(compId)))
        );
    }

    private void validateNewCompilationDto(NewCompilationDto newCompilationDto) {
        if (newCompilationDto == null) {
            throw new IllegalArgumentException("newCompilationDto is null");
        }
        if (newCompilationDto.getTitle() == null || newCompilationDto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is null");
        }
    }

    private Set<Event> getEventsForCompilation(Set<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Event> eventsWithInitiators = eventService.getEventsByIds(eventIds);
        return new HashSet<>(eventsWithInitiators);
    }
}