package ru.practicum.client;

import com.google.common.collect.Lists;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.recommendations.messages.InteractionsCountRequest;
import ru.practicum.recommendations.messages.RecommendedEvent;
import ru.practicum.recommendations.messages.SimilarEventsRequest;
import ru.practicum.recommendations.messages.UserPredictionsRequest;
import ru.practicum.recommendations.services.RecommendationsControllerGrpc;

import java.util.List;

@Component
public class RecommendationsClient {

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub recommendationsController;

    /**
     * Рекомендации для пользователя
     * @param userPredictionsRequest
     * @return
     */
    public List<RecommendedEvent> getRecommendedEventsForUser(UserPredictionsRequest userPredictionsRequest) {
        return Lists.newArrayList(recommendationsController.getRecommendationsForUser(userPredictionsRequest));
    }

    /**
     * Список похожих событий
     * @param similarEventsRequest
     * @return
     */
    public List<RecommendedEvent> getSimilarEvents(SimilarEventsRequest similarEventsRequest) {
        return Lists.newArrayList(recommendationsController.getSimilarEvents(similarEventsRequest));
    }

    /**
     * Список количества с данным событием
     * @param interactionsCountRequest
     * @return
     */
    public List<RecommendedEvent> getInteractionsCount(InteractionsCountRequest interactionsCountRequest) {
        return Lists.newArrayList(recommendationsController.getInteractionsCount(interactionsCountRequest));
    }
}
