package ru.practicum.core.request.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.core.request.client.EventClient;
import ru.practicum.core.request.client.dto.EventRequestStatusUpdateRequest;
import ru.practicum.core.request.client.dto.EventRequestStatusUpdateResult;
import ru.practicum.core.request.model.dto.RequestDto;
import ru.practicum.core.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final RequestService requestService;
    private static final String EVENT_ID_PATH = "/{eventId}";
    private final String eventIdRequests = "/{eventId}/requests";
    private final EventClient eventClient;

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getParticipationInfo(@PathVariable(name = "userId") @Positive Long userId,
                                                 @PathVariable(name = "eventId") @Positive Long eventId) {
        List<RequestDto> partRequestDtoList = requestService.getUserRequestsForEvent(userId,eventId);
        log.info("Получена информация о запросах на учатсие в событии с Id={} пользователя с id={}", eventId, userId);
        return partRequestDtoList;
    }


    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateEventStatus(@PathVariable(name = "userId") @Positive long userId,
                                                            @PathVariable(name = "eventId") @Positive long eventId,
                                                            @RequestBody EventRequestStatusUpdateRequest statusUpdateRequest) {
        EventRequestStatusUpdateResult result = requestService.updateRequestStatus(statusUpdateRequest, userId, eventId);
        log.info("Обновлен статус события с Id={} пользователя с id={}. Статус = {}", eventId, userId, statusUpdateRequest.getStatus().toString());
        return result;
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<RequestDto>> getAll(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(requestService.getAll(userId));
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<RequestDto> create(@PathVariable("userId") Long userId, @RequestParam("eventId") @NotNull Long eventId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.create(userId, eventId));
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<RequestDto> cancelRequest(@PathVariable("userId") Long userId, @PathVariable("requestId") Long requestId) {
        return ResponseEntity.ok(requestService.cancelRequest(userId, requestId));
    }
}
