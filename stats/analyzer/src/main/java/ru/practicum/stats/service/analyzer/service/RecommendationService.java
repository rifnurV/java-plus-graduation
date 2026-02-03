package ru.practicum.stats.service.analyzer.service;

import ru.practicum.ewm.stats.proto.*;

import java.util.List;

public interface RecommendationService {
    List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto similarEventsRequest);

    List<RecommendedEventProto> getPredictForUser(UserRecommendationsRequestProto userPredictionsRequest);

    List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto interactionsCountRequest);
}
