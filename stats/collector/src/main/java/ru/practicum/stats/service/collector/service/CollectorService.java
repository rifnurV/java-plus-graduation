package ru.practicum.stats.service.collector.service;

import ru.practicum.ewm.stats.proto.UserActionProto;

public interface CollectorService {

    void newUserAction(UserActionProto userActionProto);
}
