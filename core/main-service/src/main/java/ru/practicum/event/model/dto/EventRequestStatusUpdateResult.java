package ru.practicum.event.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.request.model.dto.RequestDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateResult {
    @Builder.Default
    List<RequestDto> confirmedRequests = new ArrayList<>();
    @Builder.Default
    List<RequestDto> rejectedRequests = new ArrayList<>();
}