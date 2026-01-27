package ru.practicum.core.eventservice.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;
import ru.practicum.core.eventservice.event.client.LocationDto;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {
    @Nullable
    @Size(min = 20, max = 2000)
    String annotation;
    Long category;
    @Nullable
    @Size(min = 20, max = 7000)
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    StateActionAdmin stateAction;
    @Nullable
    @Size(min = 3, max = 120)
    String title;
}