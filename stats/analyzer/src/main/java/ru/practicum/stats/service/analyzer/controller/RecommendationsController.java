package ru.practicum.stats.service.analyzer.controller;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.recommendations.messages.InteractionsCountRequest;
import ru.practicum.recommendations.messages.RecommendedEvent;
import ru.practicum.recommendations.messages.SimilarEventsRequest;
import ru.practicum.recommendations.messages.UserPredictionsRequest;
import ru.practicum.recommendations.services.RecommendationsControllerGrpc;
import ru.practicum.stats.service.analyzer.service.RecommendationService;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationService recommendationService;

    @Override
    public void getSimilarEvents(SimilarEventsRequest request,
                                 StreamObserver<RecommendedEvent> responseObserver) {
        List<RecommendedEvent> results = recommendationService.findSimilarEvents(request);

        results.forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public void getRecommendationsForUser(UserPredictionsRequest request,
                                          StreamObserver<RecommendedEvent> responseObserver) {
        List<RecommendedEvent> results = recommendationService.predictForUser(request);

        results.forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    public void getInteractionsCount(InteractionsCountRequest request, StreamObserver<RecommendedEvent> responseObserver) {
        recommendationService.getInteractionsCount(request)
                .forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }
}
