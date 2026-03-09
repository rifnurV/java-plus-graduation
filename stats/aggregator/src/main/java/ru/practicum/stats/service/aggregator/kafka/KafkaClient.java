package ru.practicum.stats.service.aggregator.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.service.aggregator.service.AggregatorService;

@Component
@RequiredArgsConstructor
public class KafkaClient {

    private final AggregatorService aggregator;

    @KafkaListener(topics = "${collector.kafka.consumer.topics.user-actions}")
    public void listen(UserActionAvro userAction) {
        aggregator.aggregateUserAction(userAction);
    }
}
