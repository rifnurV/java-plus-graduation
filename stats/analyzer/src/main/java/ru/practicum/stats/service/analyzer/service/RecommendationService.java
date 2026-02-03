package ru.practicum.stats.service.analyzer.service;

import ru.practicum.ewm.stats.proto.*;

import java.util.List;

public interface RecommendationService {
    List<RecommendedEventProto> findSimilarEvents(SimilarEventsRequestProto similarEventsRequest);
    List<RecommendedEventProto> predictForUser(UserRecommendationsRequestProto userPredictionsRequest);
    List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto interactionsCountRequest);
}
