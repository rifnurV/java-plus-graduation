package ru.practicum.stats.service.analyzer.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.service.analyzer.model.Interactions;
import ru.practicum.stats.service.analyzer.repository.InteractionsRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@AllArgsConstructor
public class InteractionsServiceImpl implements InteractionsService {

    private final InteractionsRepository interactionsRepository;

    @Override
    public void handleUserAction(UserActionAvro userActionAvro) {
        if (userActionAvro == null) {
            log.warn("Получен null-запрос о пользовательском действии");
            return;
        }

        long userId = userActionAvro.getUserId();
        long eventId = userActionAvro.getEventId();
        double newScore = calcInteractionScore(userActionAvro.getActionType());

        log.debug("Обработка действия пользователя {}: {}", userId, userActionAvro.getActionType());

        // Определяем локальное время из timestamp Avro (UTC)
        LocalDateTime interactAt = LocalDateTime.ofInstant(
                userActionAvro.getTimestamp(), ZoneId.of("UTC"));

        interactionsRepository.findByUserIdAndEventId(userId, eventId)
                .ifPresentOrElse(
                        existingAction -> {
                            if (existingAction.getRating() < newScore) {
                                updateExistingAction(existingAction, newScore, interactAt);
                            } else {
                                log.debug("Действие пользователя {} с мероприятием {} не обновлено: текущий вес {} >= {}",
                                        userId, eventId, existingAction.getRating(), newScore);
                            }
                        },
                        () -> {
                            saveNewAction(userId, eventId, newScore, interactAt);
                            log.info("Создано новое действие пользователя {} с мероприятием {}", userId, eventId);
                        }
                );
    }

    private void updateExistingAction(Interactions existingAction, double newScore, LocalDateTime interactAt) {
        existingAction.setRating(newScore);
        existingAction.setTs(interactAt);
        interactionsRepository.save(existingAction);
        log.info("Обновлено действие пользователя {} с мероприятием {}: вес {}",
                existingAction.getUserId(), existingAction.getEventId(), newScore);
    }

    //Создаёт новое действие пользователя с мероприятием.
    private void saveNewAction(long userId, long eventId, double newScore, LocalDateTime interactAt) {
        Interactions newUserAction = Interactions.builder()
                .userId(userId)
                .eventId(eventId)
                .rating(newScore)
                .ts(interactAt)
                .build();
        interactionsRepository.save(newUserAction);
    }

    //Рассчитывает вес взаимодействия на основе типа действия.
    private double calcInteractionScore(ActionTypeAvro type) {
        return switch (type) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
            default -> {
                log.warn("Неизвестный тип действия: {}", type);
                yield 0.0; // По умолчанию
            }
        };
    }
}
