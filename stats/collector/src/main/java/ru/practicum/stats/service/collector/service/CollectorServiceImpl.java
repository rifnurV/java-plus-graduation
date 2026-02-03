package ru.practicum.stats.service.collector.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.recommendations.messages.ActionTypeProto;
import ru.practicum.recommendations.messages.UserActionProto;
import ru.practicum.stats.service.collector.configuration.KafkaTopics;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectorServiceImpl implements CollectorService {
    private final KafkaTemplate<String, UserActionAvro> kafkaTemplate;
    private final KafkaTopics kafkaTopics;

    @Override
    public void newUserAction(UserActionProto proto) {
        log.debug("Преобразование действия пользователя {} в формат Avro", proto.getUserId());

        try {
            // 1. Маппинг Protobuf -> Avro
            UserActionAvro avro = UserActionAvro.newBuilder()
                    .setUserId(proto.getUserId())
                    .setEventId(proto.getEventId())
                    .setActionType(mapActionType(proto.getActionType()))
                    .setTimestamp(mapTimestamp(proto.getTimestamp()))
                    .build();

            // 2. Асинхронная отправка в Kafka
            // Используем userId как ключ (key), чтобы события одного пользователя
            // всегда попадали в один и тот же раздел (partition) и сохраняли порядок.
            String topic = kafkaTopics.getUserActions();
            kafkaTemplate.send(topic, avro)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.debug("Сообщение успешно отправлено в Kafka: {}", result.getRecordMetadata());
                        } else {
                            log.error("Ошибка при отправке сообщения в Kafka для пользователя {}", avro.getUserId(), ex);
                        }
                    });

        } catch (Exception e) {
            log.error("Не удалось обработать действие пользователя: {}", e.getMessage());
            throw new IllegalArgumentException("Ошибка при формировании данных Avro", e);
        }
    }

    private ActionTypeAvro mapActionType(ActionTypeProto protoType) {
        return switch (protoType) {
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            default -> {
                log.error("Неизвестный тип действия: {}", protoType);
                throw new IllegalArgumentException("Неподдерживаемый тип действия: " + protoType);
            }
        };
    }

    private Instant mapTimestamp(com.google.protobuf.Timestamp protoTimestamp) {
        return Instant.ofEpochSecond(
                protoTimestamp.getSeconds(),
                protoTimestamp.getNanos()
        );
    }
}
