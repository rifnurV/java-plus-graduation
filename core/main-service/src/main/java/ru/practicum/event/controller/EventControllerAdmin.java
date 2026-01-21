package ru.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.model.EventParam;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.model.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/admin/events")
public class EventControllerAdmin {
    private final EventService eventService;
    private final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String EVENT_ID_PATH = "/{eventId}";


    @PatchMapping(EVENT_ID_PATH)
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable(name = "eventId") Long eventId,
                                                   @Valid @RequestBody UpdateEventAdminRequest adminRequest) {
        log.info("Получен PATCH-запрос на обновление события с ID {} и данными: {}", eventId, adminRequest);
        EventFullDto eventFullDto = eventService.updateAdminEvent(eventId, adminRequest);
        log.info("Админ обновил событие с Id={}", eventId);
        return ResponseEntity.ok().body(eventFullDto);
    }

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(@RequestParam(name = "users", required = false) List<Integer> users,
                                           @RequestParam(name = "states", required = false) List<String> states,
                                           @RequestParam(name = "categories", required = false) List<Long> categories,
                                           @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeStart,
                                           @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeEnd,
                                           @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        EventParam p = EventParam.builder()
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();

        List<EventFullDto> events = eventService.getEventsAdmin(p);
        log.info("Выполнен поиск событий администратором");
        return ResponseEntity.ok().body(events);
    }

}
