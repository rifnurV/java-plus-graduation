package ru.practicum.stats.service.analyzer.service;


import ru.practicum.recommendations.messages.InteractionsCountRequest;
import ru.practicum.recommendations.messages.RecommendedEvent;
import ru.practicum.recommendations.messages.SimilarEventsRequest;
import ru.practicum.recommendations.messages.UserPredictionsRequest;

import java.util.List;

public interface RecommendationService {
    List<RecommendedEvent> findSimilarEvents(SimilarEventsRequest similarEventsRequest);
    List<RecommendedEvent> predictForUser(UserPredictionsRequest userPredictionsRequest);
    List<RecommendedEvent> getInteractionsCount(InteractionsCountRequest interactionsCountRequest);
}
