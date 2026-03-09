package ru.practicum.stats.service.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.service.analyzer.model.Similarities;
import ru.practicum.stats.service.analyzer.repository.SimilaritiesRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimilaritiesServiceImpl implements SimilaritiesService {

    private final SimilaritiesRepository similaritiesRepository;

    @Override
    public void handleEventSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        log.info("Обработка события схожести: {}", eventSimilarityAvro);

        Similarities eventSimilarity = Similarities.builder()
                .event1(eventSimilarityAvro.getEventA())
                .event2(eventSimilarityAvro.getEventB())
                .similarity(eventSimilarityAvro.getScore())
                .ts(LocalDateTime.from(eventSimilarityAvro.getTimestamp()))
                .build();

        Optional<Similarities> existing = similaritiesRepository
                .findByEvent1AndEvent2(
                        eventSimilarity.getEvent1(),
                        eventSimilarity.getEvent2()
                );

        if (existing.isPresent()) {
            log.debug("Обновление существующей записи схожести для мероприятий {} и {}",
                    eventSimilarity.getEvent1(), eventSimilarity.getEvent2());
            Similarities updated = existing.get();
            updated.setSimilarity(eventSimilarity.getSimilarity());
            updated.setTs(eventSimilarity.getTs());
            similaritiesRepository.save(updated);
        } else {
            log.debug("Создание новой записи схожести для мероприятий {} и {}",
                    eventSimilarity.getEvent1(), eventSimilarity.getEvent2());
            similaritiesRepository.save(eventSimilarity);
        }



    }
}
