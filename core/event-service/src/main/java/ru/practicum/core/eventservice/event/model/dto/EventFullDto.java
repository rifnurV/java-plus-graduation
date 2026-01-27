package ru.practicum.core.eventservice.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.core.eventservice.client.dto.CategoryDto;
import ru.practicum.core.eventservice.comment.model.dto.LocationDto;
import ru.practicum.core.eventservice.event.model.EventState;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;
    @NotNull(message = "Аннотация события не может быть пустой")
    String annotation;
    CategoryDto category;
    Integer confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;
    @NotNull(message = "Описание события не может быть пустым")
    String description;
    @NotNull(message = "Дата события обязательна")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    Long initiator;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventState state;
    @NotNull(message = "Название события не может быть пустым")
    String title;
    Long views;
}