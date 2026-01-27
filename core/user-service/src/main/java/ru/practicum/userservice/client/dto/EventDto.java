package ru.practicum.userservice.client.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.userservice.model.dto.UserShortDto;

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
    private Long categoryId;
    private UserShortDto initiator;
    @NotNull(message = "Дата события обязательна")
    private LocalDateTime eventDate;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private LocationDto location;
    private Integer participantLimit;
    private Boolean paid;
    private Boolean requestModeration;
    private String state;
    private Long confirmedRequests;
    private Long views;
}
