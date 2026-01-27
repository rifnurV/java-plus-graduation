package ru.practicum.userservice.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;
    @NotNull
    Long category;
    @NotBlank
    @Size(min = 20, max = 7000)
    String description;
    @NotNull(message = "Дата события обязательна")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @NotNull(message = "Геолокация события обязательна")
    LocationDto location;
    Boolean paid = false;
    @Min(value = 0, message = "Лимит участников не может быть отрицательным")
    Integer participantLimit;
    Boolean requestModeration;
    @NotBlank(message = "Заголовок события не может быть пустым")
    @Size(min = 3, max = 120, message = "Заголовок события должен содержать от 3 до 120 символов")
    String title;
}
