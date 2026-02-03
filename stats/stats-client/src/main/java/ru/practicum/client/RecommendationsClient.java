package ru.practicum.client;

import com.google.common.collect.Lists;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.proto.*;

import java.util.List;

@Component
public class RecommendationsClient {

    @GrpcClient("analyzer")
    private RecommentationsControllerGrpc.RecommentationsControllerBlockingStub recommendationsController;

    /**
     * Рекомендации для пользователя
     * @param userPredictionsRequest
     * @return
     */
    public List<RecommendedEventProto> getRecommendedEventsForUser(UserRecommendationsRequestProto userPredictionsRequest) {
        return Lists.newArrayList(recommendationsController.getRecommendationsForUser(userPredictionsRequest));
    }

    /**
     * Список похожих событий
     * @param similarEventsRequest
     * @return
     */
    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto similarEventsRequest) {
        return Lists.newArrayList(recommendationsController.getSimilarEvents(similarEventsRequest));
    }

    /**
     * Список количества с данным событием
     * @param interactionsCountRequest
     * @return
     */
    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto interactionsCountRequest) {
        return Lists.newArrayList(recommendationsController.getInteractionsCount(interactionsCountRequest));
    }
}
