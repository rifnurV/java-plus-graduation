package ru.practicum.stats.service.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.proto.*;
import ru.practicum.stats.service.analyzer.model.Interactions;
import ru.practicum.stats.service.analyzer.model.Similarities;
import ru.practicum.stats.service.analyzer.repository.InteractionsRepository;
import ru.practicum.stats.service.analyzer.repository.SimilaritiesRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final SimilaritiesRepository similaritiesRepository;
    private final InteractionsRepository interactionsRepository;

    @Override
    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        log.info("Поиск похожих мероприятий для события {}", request.getEventId());

        long eventId = request.getEventId();
        long userId = request.getUserId();
        long maxResults = request.getMaxResults();
        if (maxResults <= 0) {
            maxResults = 10;
        }

        // Получаем список всех похожих событий
        List<Similarities> allSimilarities = similaritiesRepository
                .findAllByEvent1OrEvent2(eventId, eventId);

        // Получаем список событий, которые пользователь уже просматривал
        Set<Long> viewedEvents = interactionsRepository.findByUserId(userId)
                .stream()
                .map(Interactions::getEventId)
                .collect(Collectors.toSet());

        // Фильтруем только непросмотренные события
        List<RecommendedEventProto> result = allSimilarities.stream()
                .filter(similarity -> {
                    Long otherEvent = getOtherEvent(similarity, eventId);
                    return otherEvent != null && !viewedEvents.contains(otherEvent);
                })
                .map(similarity -> {
                    Long otherEvent = getOtherEvent(similarity, eventId);
                    return RecommendedEventProto.newBuilder()
                            .setEventId(otherEvent)
                            .setScore(similarity.getSimilarity())
                            .build();
                })
                .sorted(Comparator.comparingDouble(RecommendedEventProto::getScore).reversed())
                .limit(maxResults)
                .toList();

        log.info("Найдено {} похожих мероприятий", result.size());
        return result;
    }

    @Override
    public List<RecommendedEventProto> getPredictForUser(UserRecommendationsRequestProto request) {
        log.info("Генерация рекомендаций для пользователя {}", request.getUserId());

        long userId = request.getUserId();
        long maxResults = request.getMaxResults(); // Всегда возвращает long (в proto3 0, если не задано)
        if (maxResults <= 0) {
            maxResults = 10;
        }

        // Получаем последние N взаимодействий пользователя
        List<Interactions> recentActions = interactionsRepository.findByUserId(userId)
                .stream()
                .sorted(Comparator.comparing(Interactions::getTs).reversed())
                .limit(maxResults)
                .toList();

        if (recentActions.isEmpty()) {
            return Collections.emptyList();
        }

        // Формируем список идентификаторов событий, с которыми взаимодействовал пользователь
        Set<Long> userInteractedEvents = recentActions.stream()
                .map(Interactions::getEventId)
                .collect(Collectors.toSet());

        // Находим все события, похожие на те, с которыми взаимодействовал пользователь
        List<Similarities> similarEvents = similaritiesRepository
                .findAllBySourceEventIdInOrTargetEventIdIn(userInteractedEvents, userInteractedEvents);

        // Выбираем уникальные непросмотренные мероприятия
        Set<Long> recommendedEvents = similarEvents.stream()
                .flatMap(es -> Stream.of(es.getEvent1(), es.getEvent2()))
                .filter(eventId -> !userInteractedEvents.contains(eventId))
                .distinct()
                .limit(maxResults)
                .collect(Collectors.toSet());

        // Рассчитываем взвешенные оценки
        Map<Long, Double> weightedScores = new HashMap<>();
        for (Similarities similarity : similarEvents) {
            for (Long baseEventId : userInteractedEvents) {
                Long candidateEventId = getOtherEvent(similarity, baseEventId);

                if (candidateEventId == null || userInteractedEvents.contains(candidateEventId)) {
                    continue;
                }

                double userScore = getUserScore(baseEventId);
                double similarityScore = similarity.getSimilarity();
                weightedScores.put(candidateEventId,
                        weightedScores.getOrDefault(candidateEventId, 0.0) + (userScore * similarityScore));
            }
        }

        // Сортируем и формируем ответ
        return weightedScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(maxResults)
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build())
                .toList();
    }

    @Override
    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        log.info("Подсчёт взаимодействий для событий: {}", request.getEventIdsList());

        Map<Long, Double> interactionCounts = interactionsRepository.findByEventIdIn(request.getEventIdsList())
                .stream()
                .collect(Collectors.groupingBy(
                        Interactions::getEventId,
                        Collectors.summingDouble(Interactions::getRating)
                ));

        return interactionCounts.entrySet().stream()
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build())
                .sorted(Comparator.comparingDouble(RecommendedEventProto::getScore).reversed())
                .collect(Collectors.toList());
    }


    // Возвращает идентификатор события, отличного от заданного.
    private Long getOtherEvent(Similarities similarity, Long baseEventId) {
        if (similarity.getEvent1().equals(baseEventId)) {
            return similarity.getEvent2();
        } else if (similarity.getEvent2().equals(baseEventId)) {
            return similarity.getEvent1();
        }
        return null;
    }

    //Возвращает среднюю оценку (вес) взаимодействий с заданным мероприятием.
    private double getUserScore(Long eventId) {
        return interactionsRepository.findByEventId(eventId)
                .stream()
                .mapToDouble(Interactions::getRating)
                .average()
                .orElse(1.0);
    }
}
