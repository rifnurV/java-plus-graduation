package ru.practicum.core.eventservice.event.client;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestDto {
    Long id;
    String created;
    Long event;
    Long requester;
    RequestStatus status;
}
