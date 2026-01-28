package ru.practicum.core.eventservice.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.core.eventservice.event.model.Event;
import ru.practicum.core.eventservice.event.model.EventParam;
import ru.practicum.core.eventservice.event.model.dto.EventFullDto;
import ru.practicum.core.eventservice.event.model.dto.NewEventDto;
import ru.practicum.core.eventservice.event.model.dto.UpdateEventAdminRequest;
import ru.practicum.core.eventservice.event.model.dto.UpdateEventUserRequest;

import java.util.List;
import java.util.Set;

public interface EventService {

    List<EventFullDto> getEventsAdmin(EventParam p);

    EventFullDto create(NewEventDto newEventDto, Long Long);

    List<EventFullDto> getAllByUser(Long userId, int from, int size);

    EventFullDto getByUserAndId(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    EventFullDto updateAdminEvent(long eventId, UpdateEventAdminRequest adminRequest);

    Set<Event> getEventsByIds(Set<Long> eventIds);

    List<EventFullDto> findAllByUserParams(EventParam eventParam);

    void sendHit(HttpServletRequest request);

    EventFullDto findPublishedEvent(Long eventId);

    boolean existsByCategory(Long categoryId);

    EventFullDto findEventDtoById(Long eventId);
}
