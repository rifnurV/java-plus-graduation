package ru.practicum.core.eventservice.event.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.core.eventservice.client.dto.CategoryDto;
import ru.practicum.core.eventservice.event.model.Event;
import ru.practicum.core.eventservice.event.model.dto.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {

    @Mapping(target = "state", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "locationLon", source = "location.lon")
    @Mapping(target = "locationLat", source = "location.lat")
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "category", ignore = true)
    Event toModel(NewEventDto newEventDto);

    @Mapping(target = "views", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(source = "locationLat", target = "location.lat")
    @Mapping(source = "locationLon", target = "location.lon")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "initiator", target = "initiator")
    EventFullDto toDto(Event event);

    @Mapping(source = "category", target = "category")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    EventShortDto toShortDto(Event event);

    default CategoryDto mapCategory(Long categoryId) {
        if (categoryId == null) return null;
        CategoryDto dto = new CategoryDto();
        dto.setId(categoryId);
        return dto;
    }

    default UserShortDto mapInitiator(Long initiatorId) {
        if (initiatorId == null) return null;
        UserShortDto dto = new UserShortDto();
        dto.setId(initiatorId);
        return dto;
    }
}

