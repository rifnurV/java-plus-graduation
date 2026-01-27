package ru.practicum.core.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.core.request.model.dto.RequestEventDto;
import ru.practicum.core.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/client/request")
@RequiredArgsConstructor
public class RequestClientController {
    private final RequestService requestService;

    @GetMapping("/count")
    public ResponseEntity<List<RequestEventDto>> getEventRequestsCount(@RequestParam("eventIds") List<Long> eventIds){
        log.info("GET /client/request/count?eventIds={}", eventIds);
        return ResponseEntity.ok(requestService.getEventRequestsCount(eventIds));
    }

    @GetMapping("/has-request")
    public ResponseEntity<Boolean> hasRequest(@RequestParam("userId") Long userId,
                                              @RequestParam("eventId") Long eventId){
        log.info("GET /internal/request/has-request?userId={}&eventId={} - " +
                "Получен внутренний запрос на проверку наличия заявки на участие в мероприятии", userId, eventId);
        return ResponseEntity.ok(requestService.hasRequest(userId, eventId));
    }

}
