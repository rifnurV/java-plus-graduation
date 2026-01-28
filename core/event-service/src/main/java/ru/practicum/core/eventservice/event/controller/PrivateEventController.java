package ru.practicum.core.eventservice.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.core.eventservice.event.model.dto.EventFullDto;
import ru.practicum.core.eventservice.event.model.dto.NewEventDto;
import ru.practicum.core.eventservice.event.model.dto.UpdateEventUserRequest;
import ru.practicum.core.eventservice.event.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService eventService;

    private static final String EVENT_ID_PATH = "/{eventId}";
    private final String eventIdRequests = "/{eventId}/requests";


    @PostMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> postEvent(@PathVariable(name = "userId") Long userId,
                                                  @Valid @RequestBody NewEventDto newEventDto) {
        log.info("POST-запрос на создание события {} пользователем {}", newEventDto.getTitle(), userId);
        EventFullDto eventDto = eventService.create(newEventDto, userId);
        log.info("Создается новое событие title={}, date={}", eventDto.getTitle(), eventDto.getEventDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(eventDto);
    }

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<EventFullDto>> getEventsByUser(@PathVariable(name = "userId") @Positive Long userId,
                                                              @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                              @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {

        log.info("Получен GET-запрос на получение событий пользователя {} с параметрами: from={}, size={}",
                userId, from, size);
        List<EventFullDto> eventShortDtos = eventService.getAllByUser(userId, from, size);
        log.info("Получен список событий пользователя с id={}", userId);
        return ResponseEntity.ok(eventShortDtos);
    }


    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByUserAndId(@PathVariable(name = "userId") @Positive Long userId,
                                            @PathVariable(name = "eventId") @Positive Long eventId) {
        EventFullDto eventFullDto = eventService.getByUserAndId(userId, eventId);
        log.info("Получено событие с Id={}  пользователя с id={}", eventId, userId);
        return eventFullDto;
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable(name = "userId") @Positive Long userId,
                                    @PathVariable(name = "eventId") @Positive Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateRequest) {
        EventFullDto eventFullDto = eventService.updateEvent(userId, eventId, updateRequest);
        log.info("Обновлено событие с Id={} , добавленное пользователем с id={}", eventId, userId);
        return eventFullDto;
    }

}
