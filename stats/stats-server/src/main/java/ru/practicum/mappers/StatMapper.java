package ru.practicum.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.StatisticsPostResponseDto;
import ru.practicum.model.Statistics;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatMapper {

    public static StatisticsPostResponseDto toDto(Statistics statistics) {
        return StatisticsPostResponseDto.builder()
                .ip(statistics.getIp())
                .app(statistics.getApp())
                .uri(statistics.getUri())
                .timestamp(statistics.getTimestamp())
                .build();
    }

    public static Statistics fromDto(StatisticsPostResponseDto statisticsPostResponseDto) {
        return Statistics.builder()
                .uri(statisticsPostResponseDto.getUri())
                .app(statisticsPostResponseDto.getApp())
                .ip(statisticsPostResponseDto.getIp())
                .timestamp(statisticsPostResponseDto.getTimestamp())
                .build();
    }

}
