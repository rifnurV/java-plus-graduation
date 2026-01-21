package ru.practicum.compilation.model;

import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.event.model.mapper.EventMapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между сущностью {@link Compilation} и её DTO-представлением.
 * <p>
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
