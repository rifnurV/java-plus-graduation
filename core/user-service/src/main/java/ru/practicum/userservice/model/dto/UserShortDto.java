package ru.practicum.userservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserShortDto {
        long id;
        @NotBlank(message = "Имя не может быть пустым")
        @Size(max = 250, message = "Максимальная длина имени — 250 символов")
        String name;
}
