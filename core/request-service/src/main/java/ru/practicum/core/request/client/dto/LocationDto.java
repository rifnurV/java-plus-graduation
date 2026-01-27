package ru.practicum.core.request.client.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationDto {
    @NotNull(message = "Широта обязательна и должна быть числом")
    private Float lat;

    @NotNull(message = "Долгота обязательна и должна быть числом")
    private Float lon;
}
