package ru.practicum.userservice.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.userservice.client.dto.RequestEventDto;

import java.util.List;

@FeignClient(name = "request-service", path = "/request")
public interface RequestClient {

    @GetMapping("/count")
    ResponseEntity<List<RequestEventDto>> getEventRequestsCount(@RequestParam List<Long> eventIds) throws FeignException;

    @GetMapping("/has-request")
    ResponseEntity<Boolean> hasRequest(@RequestParam Long userId, @RequestParam Long eventId) throws FeignException;
}
