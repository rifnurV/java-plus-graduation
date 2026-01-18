package ru.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatisticsPostResponseDto;
import ru.practicum.mappers.StatMapper;
import ru.practicum.model.Statistics;
import ru.practicum.repository.StatisticsRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService{

    private final StatisticsRepository statisticsRepository;

    @Override
    @Transactional
    public void createHit(StatisticsPostResponseDto createHitDTO) {
        Statistics statistics = StatMapper.fromDto(createHitDTO);
        statisticsRepository.save(statistics);
    }
}
