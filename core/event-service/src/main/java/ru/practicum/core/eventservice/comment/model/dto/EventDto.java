package ru.practicum.core.eventservice.comment.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.core.eventservice.client.dto.CategoryDto;
import ru.practicum.core.eventservice.event.model.EventState;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDto {
    Long eventId;
    @NotNull(message = "Название события не может быть пустым")
    private String title;
    @NotNull(message = "Аннотация события не может быть пустой")
    private String annotation;
    @NotNull(message = "Описание события не может быть пустым")
    private String description;
    private CategoryDto category;
    private Long initiator;
    @NotNull(message = "Дата события обязательна")
    private LocalDateTime eventDate;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private LocationDto location;
    private Integer participantLimit;
    private Boolean paid;
    private Boolean requestModeration;
    private EventState state;
    private Long confirmedRequests;
    private Long views;
}
