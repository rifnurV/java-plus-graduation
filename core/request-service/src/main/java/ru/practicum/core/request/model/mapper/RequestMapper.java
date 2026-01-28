package ru.practicum.core.request.model.mapper;

import ru.practicum.core.request.model.Request;
import ru.practicum.core.request.model.dto.RequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RequestMapper {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static RequestDto toRequestDto(Request request) {
        RequestDto dto = new RequestDto();
        dto.setCreated(request.getCreated().format(TIME_FORMAT));
        dto.setEvent(request.getEventId());
        dto.setId(request.getId());
        dto.setRequester(request.getRequesterId());
        dto.setStatus(request.getStatus());
        return dto;
    }

    public static Request toRequest(RequestDto dto, Long eventId, Long userId) {
        Request pr = new Request();
        pr.setId(dto.getId());
        pr.setCreated(LocalDateTime.parse(dto.getCreated(), TIME_FORMAT));
        pr.setEventId(eventId);
        pr.setRequesterId(userId);
        pr.setStatus(dto.getStatus());
        return pr;
    }
}
