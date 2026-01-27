package ru.practicum.core.eventservice.event.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.core.eventservice.client.dto.CategoryDto;

import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    Long id;
    @NotNull(message = "Аннотация события не может быть пустой")
    String annotation;
    CategoryDto category;
    int confirmedRequests;
    @NotNull(message = "Дата события обязательна")
    LocalDateTime eventDate;
    UserShortDto initiator;
    boolean paid;
    @NotNull(message = "Название события не может быть пустым")
    String title;
    Long views;
}