package ru.practicum.core.request.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.core.request.client.dto.EventDto;

@FeignClient(name = "event-service", path = "/client/event")
public interface EventClient {

    @GetMapping("/{eventId}")
    ResponseEntity<EventDto> getEventById(@PathVariable("eventId") Long eventId) throws FeignException;

}
