package ru.practicum.stats.service.analyzer.service;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public interface SimilaritiesService {

    void handleEventSimilarity(EventSimilarityAvro eventSimilarityAvro);

}
