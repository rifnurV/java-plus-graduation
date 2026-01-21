package ru.practicum.event.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.request.model.RequestStatus;

import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateRequest {
    @NotNull(message = "Список заявок не может быть пустым")
    @Size(min = 1, message = "Минимум одна заявка должна быть указана")
    List<Long> requestIds;
    @NotNull(message = "Статус не может быть пустым")
    RequestStatus status;
}