package ru.practicum.stats.service.analyzer.service;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface InteractionsService {

    void handleUserAction(UserActionAvro userActionAvro);
}
