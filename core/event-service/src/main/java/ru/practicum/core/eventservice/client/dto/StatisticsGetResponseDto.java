package ru.practicum.core.eventservice.client.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsGetResponseDto {
    String app;
    String uri;
    Long hits;
}