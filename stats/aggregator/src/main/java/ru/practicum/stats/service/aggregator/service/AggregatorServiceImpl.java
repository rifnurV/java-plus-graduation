package ru.practicum.stats.service.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.service.aggregator.configuration.KafkaConfig;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class AggregatorServiceImpl implements AggregatorService {

    private final KafkaTemplate<String, EventSimilarityAvro> kafkaTemplate;
    private final KafkaConfig kafkaConfig;

    // Матрица: EventID -> (UserID -> MaxWeight)
    private final Map<Long, Map<Long, Double>> weightedUserActionsMatrix = new ConcurrentHashMap<>();

    // Суммарные веса: EventID -> Sum(Weights)
    private final Map<Long, Double> totalWeights = new ConcurrentHashMap<>();

    // Числитель сходства: Min(EventA, EventB) -> (Max(EventA, EventB) -> Sum(MinWeights))
    private final Map<Long, Map<Long, Double>> minWeightsSums = new ConcurrentHashMap<>();

    @Override
    public void aggregateUserAction(UserActionAvro actionAvro) {
        double newWeight = getActionWeight(actionAvro.getActionType());
        long userId = actionAvro.getUserId();
        long eventId = actionAvro.getEventId();

        // Получаем/создаем карту весов для этого события
        Map<Long, Double> userWeights = weightedUserActionsMatrix.computeIfAbsent(eventId, k -> new ConcurrentHashMap<>());

        // Проверяем, вырос ли вес действия пользователя
        Double oldWeight = userWeights.getOrDefault(userId, 0.0);
        if (newWeight <= oldWeight) {
            log.debug("Вес не увеличился (old: {}, new: {}). Игнорируем.", oldWeight, newWeight);
            return;
        }

        // Обновляем локальное состояние
        userWeights.put(userId, newWeight);
        double delta = newWeight - oldWeight;
        totalWeights.merge(eventId, delta, Double::sum);

        // Пересчитываем сходство только с теми мероприятиями,
        // с которыми также взаимодействовал этот пользователь
        List<EventSimilarityAvro> similarities = new ArrayList<>();

        for (Map.Entry<Long, Map<Long, Double>> entry : weightedUserActionsMatrix.entrySet()) {
            long otherEventId = entry.getKey();
            if (otherEventId == eventId) continue;

            // Если пользователь взаимодействовал и с 'eventId', и с 'otherEventId'
            Double weightInOtherEvent = entry.getValue().get(userId);
            if (weightInOtherEvent != null) {
                updateSimilarityPair(eventId, otherEventId, oldWeight, newWeight, weightInOtherEvent)
                        .ifPresent(similarities::add);
            }
        }

        sendSimilarities(similarities);
    }

    private Optional<EventSimilarityAvro> updateSimilarityPair(long eventA, long eventB,
                                                               double oldWA, double newWA, double weightB) {
        // Обновляем сумму минимальных весов (числитель)
        double oldMin = Math.min(oldWA, weightB);
        double newMin = Math.min(newWA, weightB);
        double deltaMin = newMin - oldMin;

        double currentMinSum = updateMinWeightsSum(eventA, eventB, deltaMin);

        // Считаем косинусное сходство
        double normA = Math.sqrt(totalWeights.getOrDefault(eventA, 0.0));
        double normB = Math.sqrt(totalWeights.getOrDefault(eventB, 0.0));

        if (normA == 0 || normB == 0) return Optional.empty();

        double score = currentMinSum / (normA * normB);
        return Optional.of(createSimilarityAvro(eventA, eventB, score));
    }

    private double updateMinWeightsSum(long a, long b, double delta) {
        long first = Math.min(a, b);
        long second = Math.max(a, b);
        return minWeightsSums.computeIfAbsent(first, k -> new ConcurrentHashMap<>())
                .merge(second, delta, Double::sum);
    }

    private double getActionWeight(ActionTypeAvro type) {
        return switch (type) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
        };
    }

    private EventSimilarityAvro createSimilarityAvro(long a, long b, double score) {
        return EventSimilarityAvro.newBuilder()
                .setEventA(Math.min(a, b))
                .setEventB(Math.max(a, b))
                .setScore(score)
                .setTimestamp(Instant.now())
                .build();
    }

    private void sendSimilarities(List<EventSimilarityAvro> list) {
        list.forEach(sim -> kafkaTemplate.send(kafkaConfig.getEventsTopic(), sim));
    }
}