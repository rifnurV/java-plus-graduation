package ru.practicum.event.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.dto.*;
import ru.practicum.user.model.mapper.UserMapper;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {CategoryMapper.class, UserMapper.class})
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
    EventFullDto toDto(Event event);

    NewEventDto toNewEventDto(UpdateEventAdminRequest updateEventAdminRequest);


    NewEventDto toNewEventDto(UpdateEventUserRequest updateEventUserRequest);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "annotation", target = "annotation"),
            @Mapping(source = "eventDate", target = "eventDate"),
            @Mapping(source = "paid", target = "paid"),
            @Mapping(source = "category", target = "category"),
            @Mapping(source = "initiator", target = "initiator"),
            @Mapping(target = "confirmedRequests", ignore = true),
            @Mapping(target = "views", ignore = true),
    })
    EventShortDto toShortDto(Event event);
}
