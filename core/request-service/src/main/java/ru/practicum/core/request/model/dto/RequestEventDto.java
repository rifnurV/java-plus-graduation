package ru.practicum.core.request.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestEventDto {
    Long eventId;
    Long requestCount;
}
