package ru.practicum.stats.service.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.recommendations.messages.InteractionsCountRequest;
import ru.practicum.recommendations.messages.RecommendedEvent;
import ru.practicum.recommendations.messages.SimilarEventsRequest;
import ru.practicum.recommendations.messages.UserPredictionsRequest;
import ru.practicum.stats.service.analyzer.model.Interactions;
import ru.practicum.stats.service.analyzer.model.Similarities;
import ru.practicum.stats.service.analyzer.repository.InteractionsRepository;
import ru.practicum.stats.service.analyzer.repository.SimilaritiesRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final SimilaritiesRepository similaritiesRepository;
    private final InteractionsRepository interactionsRepository;

    @Override
    public List<RecommendedEvent> findSimilarEvents(SimilarEventsRequest request) {
        long userId = request.getUserId();
        long eventId = request.getEventId();
        long maxResults = request.getMaxResults();

        // Получаем список ID мероприятий, с которыми пользователь уже взаимодействовал
        Set<Long> interactedEvents = interactionsRepository.findAllEventIdsByUserId(userId);

        // Получаем все коэффициенты подобия для заданного мероприятия
        // Исключаем те, с которыми пользователь уже взаимодействовал
        return similaritiesRepository.findSimilarEventsForEvent(request.getEventId())
                .stream()
                .filter(s -> !interactedEvents.contains(getOtherId(s, eventId)))
                .sorted(Comparator.comparing(Similarities::getSimilarity).reversed())
                .limit(maxResults)
                .map(s -> createRecommendedEvent(getOtherId(s, eventId), s.getSimilarity()))
                .toList();
    }

    @Override
    public List<RecommendedEvent> predictForUser(UserPredictionsRequest userPredictionsRequest) {
        long userId = userPredictionsRequest.getUserId();
        long maxResults = userPredictionsRequest.getMaxResults();
        // Получаем последние N взаимодействий пользователя
        List<Interactions> lastInteractions = interactionsRepository.findLastInteractions(userId, PageRequest.of(0, 10));
        if (lastInteractions.isEmpty()) return List.of();

        Set<Long> interactedIds = lastInteractions.stream()
                .map(Interactions::getEventId).collect(Collectors.toSet());

        // Ищем мероприятия, похожие на те, что он смотрел, но новые для него
        Map<Long, Double> candidates = new HashMap<>();
        for (Interactions inter : lastInteractions) {
            List<Similarities> similarities = similaritiesRepository.findSimilarEventsForEvent(inter.getEventId());
            for (Similarities s : similarities) {
                long otherId = getOtherId(s, inter.getEventId());
                if (!interactedIds.contains(otherId)) {
                    candidates.merge(otherId, s.getSimilarity(), Math::max);
                }
            }
        }

        // Вычисление оценки (Score)
        return candidates.keySet().stream()
                .map(targetEventId -> {
                    double predictedScore = calculatePredictedScore(targetEventId, userId);
                    return createRecommendedEvent(targetEventId, predictedScore);
                })
                .sorted(Comparator.comparing(RecommendedEvent::getScore).reversed())
                .limit(maxResults)
                .toList();
    }

    private double calculatePredictedScore(long targetEventId, long userId) {
        // Находим просмотренных пользователем мероприятий, максимально похожих на целевое
        List<Similarities> topKSim = similaritiesRepository.findTopSimilarToTargetAmongUserInteractions(targetEventId, userId, 5);

        double weightedSum = 0.0;
        double similaritySum = 0.0;

        for (Similarities sim : topKSim) {
            long alreadyViewedId = getOtherId(sim, targetEventId);
            double userRate = interactionsRepository.getRating(userId, alreadyViewedId);

            weightedSum += userRate * sim.getSimilarity();
            similaritySum += sim.getSimilarity();
        }

        return similaritySum == 0 ? 0.0 : weightedSum / similaritySum;
    }

    @Override
    public List<RecommendedEvent> getInteractionsCount(InteractionsCountRequest interactionsCountRequest) {
        return interactionsCountRequest.getEventIdsList().stream()
                .map(id -> {
                    Double totalWeight = interactionsRepository.sumRatingsByEventId(id);
                    return createRecommendedEvent(id, totalWeight != null ? totalWeight : 0.0);
                })
                .toList();
    }

    private long getOtherId(Similarities sim, long currentId) {
        return sim.getEvent1() == currentId ? sim.getEvent2() : sim.getEvent1();
    }

    private RecommendedEvent createRecommendedEvent(long id, double score) {
        return RecommendedEvent.newBuilder()
                .setEventId(id)
                .setScore((float) score)
                .build();
    }
}
