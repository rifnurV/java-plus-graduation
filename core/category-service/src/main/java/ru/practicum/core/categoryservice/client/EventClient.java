package ru.practicum.core.categoryservice.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.core.categoryservice.client.dto.EventDto;


import java.util.List;

@FeignClient(name = "event-service", path = "/client/event")
public interface EventClient {

    @GetMapping("/{eventId}")
    ResponseEntity<EventDto> getEventById(@PathVariable("eventId") Long eventId) throws FeignException;

    @GetMapping()
    ResponseEntity<List<EventDto>> getAllEventsByInitiatorId(
            @RequestParam("initiatorId") Long initiatorId
    ) throws FeignException;

    @GetMapping("/{categoryId}")
    ResponseEntity<EventDto> getEventByCategoryId(@PathVariable("categoryId") Long categoryId);

    @GetMapping("/exists")
    ResponseEntity<Boolean> hasEventsWithCategory(@RequestParam("categoryId") Long categoryId);

}
