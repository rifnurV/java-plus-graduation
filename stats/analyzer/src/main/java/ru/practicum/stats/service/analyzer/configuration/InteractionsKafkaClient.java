package ru.practicum.stats.service.analyzer.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.service.analyzer.service.InteractionsService;

@Slf4j
@Component
@RequiredArgsConstructor
public class InteractionsKafkaClient {

    private final InteractionsService interactionsService;

    @KafkaListener(
            topics = "${analyzer.kafka.consumer.topics.user-actions}",
            containerFactory = "userActionListenerContainerFactory"
    )
    public void listenActions(UserActionAvro actionAvro) {
        log.info("Получено действие пользователя: {}", actionAvro);
        interactionsService.handleUserAction(actionAvro);
    }

}
