package ru.practicum.stats.service.analyzer.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.service.analyzer.repository.InteractionsRepository;
import ru.practicum.stats.service.analyzer.repository.SimilaritiesRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConfig {
    private final SimilaritiesRepository imilaritiesRepository;
    private final InteractionsRepository interactionRepository;

    @KafkaListener(topics = "stats.events-similarity.v1")
    public void listenSimilarities(EventSimilarityAvro avro) {
        // Обновляем коэффициент сходства (всегда храним min/max порядок для пары)
        Long a = Math.min(avro.getEventA(), avro.getEventB());
        Long b = Math.max(avro.getEventA(), avro.getEventB());
        imilaritiesRepository.updateScore(a, b, avro.getScore());
    }

    @KafkaListener(topics = "stats.user-actions.v1")
    public void listenUserActions(UserActionAvro avro) {
        double weight = getWeightByActionType(avro.getActionType());
        // Обновляем таблицу взаимодействий
        interactionRepository.updateRating(avro.getUserId(), avro.getEventId(), weight);
    }

    private double getWeightByActionType(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> 0.4;      // Просто посмотрел
            case LIKE -> 0.8;      // Понравилось (высокий интерес)
            case REGISTER -> 1.0;  // Зарегистрировался (максимальный интерес)
            default -> 0.0;
        };
    }
}