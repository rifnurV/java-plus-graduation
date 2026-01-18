package ru.practicum.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.model.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.request.model.dto.RequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateUserController {
    private final EventService eventService;
    private final RequestService requestService;
    private final RequestService service;
    private static final String EVENT_ID_PATH = "/{eventId}";
    private final String eventIdRequests = "/{eventId}/requests";


    @PostMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> postEvent(@PathVariable(name = "userId") int userId,
                                  @Valid @RequestBody NewEventDto newEventDto) {
        log.info("POST-запрос на создание события {} пользователем {}", newEventDto.getTitle(), userId);
        EventFullDto eventDto = eventService.create(newEventDto, userId);
        log.info("Создается новое событие title={}, date={}", eventDto.getTitle(), eventDto.getEventDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(eventDto);
    }

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<EventFullDto>> getEventsByUser(@PathVariable(name = "userId") @Positive int userId,
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
    public EventFullDto getEventByUserAndId(@PathVariable(name = "userId") @Positive int userId,
                                            @PathVariable(name = "eventId") @Positive int eventId) {
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


    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getParticipationInfo(@PathVariable(name = "userId") @Positive Long userId,
                                                 @PathVariable(name = "eventId") @Positive Long eventId) {
        List<RequestDto> partRequestDtoList = eventService.getParticipationInfo(userId, eventId);
        log.info("Получена информация о запросах на учатсие в событии с Id={} пользователя с id={}", eventId, userId);
        return partRequestDtoList;
    }


    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateEventStatus(@PathVariable(name = "userId") @Positive long userId,
                                                            @PathVariable(name = "eventId") @Positive long eventId,
                                                            @RequestBody EventRequestStatusUpdateRequest statusUpdateRequest) {
//        EventRequestStatusUpdateResult updateStatusResult = eventService.updateStatus(userId, eventId, statusUpdateRequest);
        EventRequestStatusUpdateResult result = requestService.updateRequestStatus(statusUpdateRequest, userId, eventId);
        log.info("Обновлен статус события с Id={} пользователя с id={}. Статус = {}", eventId, userId, statusUpdateRequest.getStatus().toString());
        return result;
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<RequestDto>> getAll(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(service.getAll(userId));
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RequestDto> create(@PathVariable("userId") Long userId, @RequestParam("eventId") @NotNull Long eventId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(userId, eventId));
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<RequestDto> cancelRequest(@PathVariable("userId") Long userId, @PathVariable("requestId") Long requestId) {
        return ResponseEntity.ok(service.cancelRequest(userId, requestId));
    }
}
