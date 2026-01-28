package ru.practicum.core.eventservice.event.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.core.eventservice.event.model.dto.EventFullDto;
import ru.practicum.core.eventservice.event.service.EventService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/client/event")
public class EventClientController {

    private final EventService eventService;

    @GetMapping("/{eventId}")
    ResponseEntity<EventFullDto> getEventById(@PathVariable("eventId") Long eventId) throws FeignException{
        log.info("Выполнен запрос получения события с id={}", eventId);

        EventFullDto eventFullDto = eventService.findEventDtoById(eventId);

        return ResponseEntity.ok().body(eventFullDto);
    };

    @GetMapping("/exists")
    public ResponseEntity<Boolean> hasEventsWithCategory(@RequestParam Long categoryId) {
        // Вызываем сервис, чтобы проверить наличие хотя бы одного события
        boolean exists = eventService.existsByCategory(categoryId);
        return ResponseEntity.ok(exists);
    }
}
