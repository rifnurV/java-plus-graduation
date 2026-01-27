package ru.practicum.core.eventservice.compilation.model;

import ru.practicum.core.eventservice.compilation.model.dto.CompilationDto;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import ru.practicum.core.eventservice.event.model.mapper.EventMapper;

/**
 * Маппер для преобразования между сущностью {@link Compilation} и её DTO-представлением.
 * Использует MapStruct для автоматической генерации логики преобразования.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {EventMapper.class})
@Component
public interface CompilationMapper {

    /**
     * Преобразует сущность подборки событий в её DTO-представление.
     *
     * @param compilation Сущность подборки событий
     * @return DTO-объект подборки событий
     */
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "pinned", target = "pinned"),
            @Mapping(source = "events", target = "events")
    })
    CompilationDto toDto(Compilation compilation);
}
