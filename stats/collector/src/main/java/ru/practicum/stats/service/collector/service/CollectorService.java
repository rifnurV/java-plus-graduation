package ru.practicum.stats.service.collector.service;

import ru.practicum.recommendations.messages.UserActionProto;

public interface CollectorService {

    void newUserAction(UserActionProto userActionProto);
}
