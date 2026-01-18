package ru.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.StatisticsGetResponseDto;
import ru.practicum.dto.StatisticsPostResponseDto;
import ru.practicum.service.HitService;
import ru.practicum.service.StatService;
import ru.practicum.service.StatServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController implements StatsClient {

    private final StatService statService;
    private final HitService hitService;

    @Override
    public ResponseEntity<List<StatisticsGetResponseDto>> getStats(LocalDateTime start,
                                                                   LocalDateTime end,
                                                                   List<String> uris,
                                                                   boolean unique) throws FeignException {
        log.info("GET /stats?start={}&end={}&uris={}&unique={} - Получен запрос на получение статистики",
                start, end, uris, unique);
        return ResponseEntity.ok().body(statService.getStats(start, end, uris, unique));
    }

    @Override
    public ResponseEntity<Void> createHit(StatisticsPostResponseDto createHitDTO) throws FeignException {
        log.info("POST /hit - Получен запрос на создание hit: {}", createHitDTO);
        hitService.createHit(createHitDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
