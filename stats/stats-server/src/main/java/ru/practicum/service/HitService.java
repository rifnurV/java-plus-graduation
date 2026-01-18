package ru.practicum.service;

import ru.practicum.dto.StatisticsPostResponseDto;

public interface HitService {
    void createHit(StatisticsPostResponseDto createHitDTO);
}
