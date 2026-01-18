package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.model.EventParam;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@Validated
@RequiredArgsConstructor
@Slf4j
public class EventControllerPublic {
    private final EventService eventService;
    private final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String EVENT_ID_PATH = "/{eventId}";

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "categories", required = false) List<Long> categories,
            @RequestParam(name = "paid", required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeStart,
            @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeEnd,
            @RequestParam(name = "onlyAvailable", required = false) Boolean onlyAvailable,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") int size,
            HttpServletRequest request) {

        log.info("Получен GET-запрос на получение событий с параметрами: text={}, categories={}, paid={}, onlyAvailable={}, rangeStart={}, rangeEnd={}, sort={}, from={}, size={}",
                text, categories, paid, onlyAvailable, rangeStart, rangeEnd, sort, from, size);

        EventParam p = EventParam.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();

        eventService.sendHit(request);

        List<EventFullDto> events = eventService.findAllByUserParams(p);
        log.info("events={}", events);
        log.info("Возвращено {} событий", events.size());
        return ResponseEntity.ok().body(events);
    }

    @GetMapping(EVENT_ID_PATH)
    public ResponseEntity<EventFullDto> getEvent(@PathVariable(name = "eventId") long eventId,
                                 HttpServletRequest request) {
        log.info("Выполнен запрос получения события с id={}", eventId);
        eventService.sendHit(request);

        EventFullDto eventFullDto = eventService.findPublishedEvent(eventId);

        return ResponseEntity.ok().body(eventFullDto);
    }
}
