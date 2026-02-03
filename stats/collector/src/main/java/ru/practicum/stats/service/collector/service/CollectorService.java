package ru.practicum.stats.service.collector.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import ru.practicum.recommendations.messages.UserActionProto;

public interface CollectorService {

    void newUserAction(UserActionProto userActionProto);
}
