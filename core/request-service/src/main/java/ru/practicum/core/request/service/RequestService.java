package ru.practicum.core.request.service;

import ru.practicum.core.request.client.dto.EventRequestStatusUpdateRequest;
import ru.practicum.core.request.client.dto.EventRequestStatusUpdateResult;
import ru.practicum.core.request.model.dto.RequestDto;
import ru.practicum.core.request.model.dto.RequestEventDto;

import java.util.List;

public interface RequestService {

    List<RequestDto> getAll(Long userId);

    RequestDto create(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> getAllRequestsEventId(Long eventId);

    void updateAll(List<RequestDto> requestDtoList, Long eventId);

    void update(RequestDto requestDto, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(
            EventRequestStatusUpdateRequest changeRequestStatusDto,
            Long userId,
            Long eventId);

    List<RequestDto> getUserRequestsForEvent(Long userId, Long eventId);

    List<RequestEventDto> getEventRequestsCount(List<Long> eventIds);

    Boolean hasRequest(Long userId, Long eventId);
}
