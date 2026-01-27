package ru.practicum.core.request.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.core.request.model.RequestStatus;

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
