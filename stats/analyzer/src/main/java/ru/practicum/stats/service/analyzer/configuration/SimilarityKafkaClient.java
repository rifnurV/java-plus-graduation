package ru.practicum.stats.service.analyzer.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.service.analyzer.service.SimilaritiesService;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimilarityKafkaClient {
    private final SimilaritiesService interactionService;

    @KafkaListener(
            topics = "${analyzer.kafka.consumer.topics.events-similarity}",
            containerFactory = "similarityListenerContainerFactory"
    )
    public void listenActions(EventSimilarityAvro eventSimilarityAvro) {
        log.info("Получена схожесть: {}", eventSimilarityAvro);
        interactionService.handleEventSimilarity(eventSimilarityAvro);
    }

}
