package ru.practicum.core.eventservice.client.dto;

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
