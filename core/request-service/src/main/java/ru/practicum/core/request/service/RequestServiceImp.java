package ru.practicum.core.request.service;

import feign.FeignException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.core.request.client.EventClient;
import ru.practicum.core.request.client.UserClient;
import ru.practicum.core.request.client.dto.EventDto;
import ru.practicum.core.request.client.dto.EventRequestStatusUpdateRequest;
import ru.practicum.core.request.client.dto.EventRequestStatusUpdateResult;
import ru.practicum.core.request.client.dto.EventState;
import ru.practicum.core.request.exception.BadParameterException;
import ru.practicum.core.request.exception.ConflictException;
import ru.practicum.core.request.exception.NotFoundException;
import ru.practicum.core.request.model.Request;
import ru.practicum.core.request.model.RequestStatus;
import ru.practicum.core.request.model.dto.*;
import ru.practicum.core.request.model.mapper.RequestMapper;
import ru.practicum.core.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor

public class RequestServiceImp implements RequestService {

    private final EntityManager entityManager;
    private final EventClient eventClient;
    private final UserClient userClient;
    private final RequestRepository requestRepository;

    @Override
    public List<RequestDto> getAll(Long userId) {
        validateUser(userId);

        return requestRepository.findByRequesterId(userId).stream().map(RequestMapper::toRequestDto).toList();
    }

    @Override
    @Transactional // Добавьте транзакционность
    public RequestDto create(Long userId, Long eventId) {
        validateUser(userId);
        UserDto requestor = userClient.getUserId(userId).getBody();

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Запрос уже существует");
        }

        EventDto event = eventClient.getEventById(eventId).getBody();
        if (event == null) {
            throw new NotFoundException("Событие не найдено");
        }

        if (event.getInitiator().equals(userId)) {
            throw new ConflictException("Инициатор не может участвовать в своем событии");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Событие не опубликовано");
        }

        if (event.getParticipantLimit() > 0) {
            long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            if (confirmedCount >= event.getParticipantLimit()) {
                throw new ConflictException("Лимит участников исчерпан");
            }
        }

        Request request = new Request();
        request.setRequesterId(userId);
        request.setEventId(eventId);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        request.setCreated(LocalDateTime.now());
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        UserDto user = userClient.getUserId(userId).getBody();
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(String.format("Request with id = %d not found", requestId)));
        requestRepository.updateToCanceled(requestId);
        requestRepository.flush();
        entityManager.clear();
        return RequestMapper.toRequestDto(requestRepository.findById(requestId).get());
    }

    @Override
    public List<RequestDto> getAllRequestsEventId(Long eventId) {
        if (eventId < 0) {
            throw new BadParameterException("Id события должен быть больше 0");
        }

        List<Request> partRequests = requestRepository.findAllByEventId(eventId);
        if (partRequests == null || partRequests.isEmpty()) {
            return new ArrayList<>();
        }

        return partRequests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateAll(List<RequestDto> requestDtoList, Long eventId) {

    }

    @Override
    public void update(RequestDto requestDto, Long eventId) {

    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(EventRequestStatusUpdateRequest updateDto, Long userId, Long eventId) {
        EventDto event = eventClient.getEventById(eventId).getBody();
        if (event == null) {
            throw new NotFoundException("Событие не найдено");
        }

        if (!event.getInitiator().equals(userId)) {
            throw new ConflictException("Пользователь не является инициатором события");
        }

        List<Request> requests = requestRepository.findAllById(updateDto.getRequestIds());

        for (Request r : requests) {
            if (!r.getEventId().equals(eventId)) {
                throw new ConflictException("Заявка не относится к данному событию");
            }
            if (r.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Статус можно изменить только у заявок в состоянии PENDING");
            }
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        if (updateDto.getStatus() == RequestStatus.CONFIRMED) {
            long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            long limit = event.getParticipantLimit();

            if (limit > 0 && confirmedCount >= limit) {
                throw new ConflictException("Лимит заявок для данного события исчерпан");
            }

            for (Request request : requests) {
                if (limit == 0 || confirmedCount < limit) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(request);
                    confirmedCount++;
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(request);
                }
            }
        } else {
            for (Request request : requests) {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(request);
            }
        }

        requestRepository.saveAll(requests);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests.stream().map(RequestMapper::toRequestDto).toList())
                .rejectedRequests(rejectedRequests.stream().map(RequestMapper::toRequestDto).toList())
                .build();
    }

    @Override
    public List<RequestDto> getUserRequestsForEvent(Long userId, Long eventId) {
        return requestRepository.findAllByEventId(eventId)
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestEventDto> getEventRequestsCount(List<Long> eventIds) {
        return requestRepository.countByStatusForEvents(eventIds, RequestStatus.CONFIRMED).stream()
                .toList();
    }

    @Override
    public Boolean hasRequest(Long userId, Long eventId) {
        return requestRepository.existsByRequesterIdAndEventId(userId, eventId);
    }

    private void validateUser(Long userId) {
        try {
            ResponseEntity<UserDto> response = userClient.getUserId(userId);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NotFoundException(String.format("User with id %s not found", userId));
            }
        } catch (FeignException.NotFound e) {
            throw new NotFoundException(String.format("User with id %s not found", userId));
        } catch (FeignException e) {
            log.error("Ошибка при обращении к user-service: {}", e.getMessage());
            throw new RuntimeException("Сервис пользователей временно недоступен");
        }
    }

}
