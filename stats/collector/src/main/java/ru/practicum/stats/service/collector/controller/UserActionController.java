package ru.practicum.stats.service.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import ru.practicum.client.proto.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.practicum.stats.service.collector.service.CollectorService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserActionController extends UserActionControllerGrpc.UserActionControllerImplBase {

    private final CollectorService collectorService;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        if (request == null || !request.hasTimestamp()) {
            log.warn("Получен некорректный запрос: {}", request);
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Запрос и временная метка не должны быть null")
                    .asRuntimeException());
            return;
        }

        try {
            log.debug("Начата обработка действия пользователя {} для события {}",
                    request.getUserId(), request.getEventId());

            collectorService.newUserAction(request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (Exception e) {
            handleError(e, responseObserver);
        }
    }

    private void handleError(Exception e, StreamObserver<Empty> responseObserver) {
        Status status;
        if (e instanceof IllegalArgumentException) {
            log.error("Ошибка валидации: {}", e.getMessage());
            status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
        } else {
            log.error("Системная ошибка при сборе данных: ", e);
            status = Status.INTERNAL.withDescription("Ошибка на стороне сервиса Collector");
        }
        responseObserver.onError(status.withCause(e).asRuntimeException());
    }
}
