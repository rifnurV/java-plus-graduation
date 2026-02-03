package ru.practicum.stats.service.analyzer.controller;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.client.RecommentationsControllerGrpc;
import ru.practicum.ewm.stats.proto.*;
import ru.practicum.stats.service.analyzer.service.RecommendationService;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RecommendationsController extends RecommentationsControllerGrpc.RecommentationsControllerImplBase {

    private final RecommendationService recommendationService;

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        List<RecommendedEventProto> results = recommendationService.findSimilarEvents(request);

        results.forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public void getRecommendationsForUser(UserRecommendationsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        List<RecommendedEventProto> results = recommendationService.predictForUser(request);

        results.forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    public void getInteractionsCount(InteractionsCountRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        recommendationService.getInteractionsCount(request)
                .forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }
}
