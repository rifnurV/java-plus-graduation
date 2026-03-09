package ru.practicum.stats.service.aggregator.service;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface AggregatorService {

    void aggregateUserAction(UserActionAvro userActionAvro);
}
