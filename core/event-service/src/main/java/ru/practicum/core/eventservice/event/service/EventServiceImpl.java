package ru.practicum.core.eventservice.event.service;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.core.eventservice.client.CategoryClient;
import ru.practicum.core.eventservice.client.RequestClient;
import ru.practicum.core.eventservice.client.StatsClient;
import ru.practicum.core.eventservice.client.UserClient;
import ru.practicum.core.eventservice.client.dto.RequestEventDto;
import ru.practicum.core.eventservice.client.dto.StatisticsGetResponseDto;
import ru.practicum.core.eventservice.client.dto.StatisticsPostResponseDto;
import ru.practicum.core.eventservice.client.dto.CategoryDto;
import ru.practicum.core.eventservice.event.model.Event;
import ru.practicum.core.eventservice.event.model.EventParam;
import ru.practicum.core.eventservice.event.model.EventState;
import ru.practicum.core.eventservice.event.model.dto.*;
import ru.practicum.core.eventservice.event.client.LocationDto;
import ru.practicum.core.eventservice.event.model.mapper.EventMapper;
import ru.practicum.core.eventservice.event.repository.EventRepository;
import ru.practicum.core.eventservice.exception.BadParameterException;
import ru.practicum.core.eventservice.exception.ConflictException;
import ru.practicum.core.eventservice.exception.NotFoundException;
//import ru.practicum.dto.StatisticsGetResponseDto;
//import ru.practicum.dto.StatisticsPostResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.HOURS;
import static ru.practicum.core.eventservice.event.repository.EventRepository.EventSpec.withAdminParams;
import static ru.practicum.core.eventservice.event.repository.EventRepository.UserEventSpec.withUserParams;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventRepository eventRepository;
    private final CategoryClient categoryClient;
    private final RequestClient requestClient;
    private final StatsClient statsClient;
    private final EventMapper eventMapper;

    @Transactional
    @Override
    public EventFullDto create(NewEventDto newEventDto, Long userId) {
        if (HOURS.between(LocalDateTime.now(), newEventDto.getEventDate()) < 2) {
            throw new ValidationException("Начало события должно быть минимум на два часа позднее текущего момента");
        }

        CategoryDto category = categoryClient.getCategoryById(newEventDto.getCategory());

        Event event = eventMapper.toModel(newEventDto);
        event.setInitiator(userId);
        event.setCategory(category.getId());
        event.setCreatedOn(LocalDateTime.now());
        event.setPaid(Objects.requireNonNullElse(newEventDto.getPaid(), false));
        event.setParticipantLimit(Objects.requireNonNullElse(newEventDto.getParticipantLimit(), 0));
        event.setRequestModeration(Objects.requireNonNullElse(newEventDto.getRequestModeration(), true));
        Event savedEvent = eventRepository.save(event);

        EventFullDto savedEventFullDto = eventMapper.toDto(savedEvent);
        log.info("Событие создано: {}", savedEventFullDto);
        return eventMapper.toDto(savedEvent);
    }

    public List<EventFullDto> getAllByUser(Long userId, int from, int size) {

        PageRequest page = PageRequest.of(from / size, size, Sort.by("id").ascending());

        List<Event> events = eventRepository.getAllByUser(userId, page);

        List<EventFullDto> eventFullDtos = events.stream()
                .map(eventMapper::toDto)
                .toList();
        loadViews(eventFullDtos,
                eventFullDtos.stream().map(EventFullDto::getCreatedOn).min(LocalDateTime::compareTo).orElse(LocalDateTime.now()),
                eventFullDtos.stream().map(EventFullDto::getEventDate).min(LocalDateTime::compareTo).orElse(null)
                );
        loadConfirmedRequests(eventFullDtos);
        return eventFullDtos;
    }

    @Override
    public EventFullDto getByUserAndId(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с ID=%d не найдено",eventId)));

        if (!event.getInitiator().equals(userId)) {
            throw new ConflictException(String.format("Событие с ID=%d не соответствует инициатору с ID=%d", event.getId(), userId));
        }
        return eventMapper.toDto(event);
    }

    public EventFullDto getByUserAndId(int userId, int eventId) {
        Event event = eventRepository.getByIdAndUserId(eventId, userId);
        if (event == null) {
            throw new NotFoundException(String.format("События с id=%d и initiatorId=%d не найдено", eventId, userId));
        }

        EventFullDto eventFullDto = eventMapper.toDto(event);

        loadViews(List.of(eventFullDto),event.getPublishedOn(), event.getEventDate());
        loadConfirmedRequests(List.of(eventFullDto));
        return eventFullDto;
    }


    public EventFullDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("События с id=%d не найдено", eventId)));

        return eventMapper.toDto(event);
    }

    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        EventFullDto eventDto = this.getEvent(eventId);
        if (eventDto.getState() != EventState.PUBLISHED) {
            throw new NotFoundException(String.format("Событие с id=%d не опубликовано", eventId));
        }
        StatisticsPostResponseDto endpointHitDto = new StatisticsPostResponseDto();
        endpointHitDto.setApp("ewm-main-event-service");
        endpointHitDto.setIp(request.getRemoteAddr());
        endpointHitDto.setTimestamp(LocalDateTime.now());
        endpointHitDto.setUri(request.getRequestURI());

        statsClient.createHit(endpointHitDto);

        return eventDto;
    }


    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        Event event = eventRepository.findById(eventId).
                orElseThrow(() ->new ConflictException("Событие с ID " + eventId+ " не найдено"));
        if (!event.getInitiator().equals(userId)) {
            throw new ConflictException("Событие с ID " + event.getId()+ " не соответствует инициатору "+ userId);
        }

        if (event == null) {
            throw new NotFoundException(String.format("События с id=%d и initiatorId=%d не найдено", eventId, userId));
        }

        if (event.getState() == EventState.PUBLISHED) {
            throw new BadParameterException("Нельзя обновлять событие в состоянии 'Опубликовано'");
        }

        String annotation = updateRequest.getAnnotation();
        if (!(annotation == null || annotation.isBlank())) {
            event.setAnnotation(annotation);
        }
        Long categoryId = updateRequest.getCategory();
        if (categoryId != null && categoryId > 0) {
            CategoryDto categoryDto = categoryClient.getCategoryById(categoryId);
            if (categoryDto != null) {
                event.setCategory(categoryDto.getId());
            }
        }
        LocalDateTime newDate = updateRequest.getEventDate();
        if (!(newDate == null)) {
            if (HOURS.between(LocalDateTime.now(), newDate) < 2) {
                throw new ValidationException("Начало события должно быть минимум на два часа позднее текущего момента");
            }
            event.setEventDate(newDate);
        }
        LocationDto location = updateRequest.getLocation();
        if (location != null) {
            event.setLocationLat(location.getLat());
            event.setLocationLon(location.getLon());
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            if (updateRequest.getParticipantLimit() < 0) {
                throw new ValidationException("Participant limit cannot be negative");
            }
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
            }
        }
        String title = updateRequest.getTitle();
        if (!(title == null || title.isBlank())) {
            event.setTitle(title);
        }

        eventRepository.save(event);
        EventFullDto eventFullDto = eventMapper.toDto(event);

        loadViews(List.of(eventFullDto), event.getPublishedOn(),event.getEventDate());
        loadConfirmedRequests(List.of(eventFullDto));
        return eventFullDto;
    }

@Transactional
public EventFullDto updateAdminEvent(long eventId, UpdateEventAdminRequest adminRequest) {
    Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException(String.format("События с id=%d не найдено", eventId)));

    if (adminRequest.getEventDate() != null) {
        if (adminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата начала события должна быть минимум на 2 часа позже текущего момента");
        }
    }

    if (adminRequest.getStateAction() != null) {
        validateAdminStateAction(adminRequest.getStateAction(), event);

        switch (adminRequest.getStateAction()) {
            case PUBLISH_EVENT:

                LocalDateTime eventDate = adminRequest.getEventDate() != null ?
                        adminRequest.getEventDate() : event.getEventDate();
                if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                    throw new ConflictException("Дата начала события должна быть минимум за час до публикации");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                break;

            case REJECT_EVENT:
                event.setState(EventState.CANCELED);
                break;
        }
    }

    updateEventFields(event, adminRequest);

    Event savedEvent = eventRepository.save(event);
    EventFullDto dto = eventMapper.toDto(savedEvent);

    loadViews(List.of(dto), savedEvent.getPublishedOn(), savedEvent.getEventDate());
    loadConfirmedRequests(List.of(dto));

    return dto;
}


    @Override
    public Set<Event> getEventsByIds(Set<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Event> eventList = eventRepository.findEventsWIthUsersByIdSet(eventIds);
        return new HashSet<>(eventList);
    }

    @Override
    public List<EventFullDto> findAllByUserParams(EventParam eventParam) {
        // Проверка корректности диапазона дат
        if (eventParam.getRangeStart() != null &&
                eventParam.getRangeEnd() != null &&
                !eventParam.getRangeEnd().isAfter(eventParam.getRangeStart())) {
            throw new ValidationException("rangeEnd должен быть позже, чем rangeStart");
        }

        // Создание объекта PageRequest для пагинации и сортировки
        PageRequest pageRequest = PageRequest.of(
                eventParam.getFrom() / eventParam.getSize(),
                eventParam.getSize(),
                Sort.by("eventDate").ascending()
        );

        // Получение событий из репозитория по спецификации
        Page<Event> eventsPage = eventRepository.findAll(withUserParams(eventParam), pageRequest);

        // Преобразование моделей событий в DTO
        // ArrayList для сортировки
        List<EventFullDto> eventDtos = new ArrayList<>(eventsPage.stream()
                .map(eventMapper::toDto)
                .toList());

        // Загрузка статистики просмотров и подтверждённых заявок
        loadViews(eventDtos, eventParam.getRangeStart(), eventParam.getRangeEnd());
        loadConfirmedRequests(eventDtos);

        // Проверяем наличие и значение параметра сортировки
        if (eventParam.getSort() != null
                && eventParam.getSort().equals("VIEWS")) {
            eventDtos.sort(Comparator.comparing(EventFullDto::getViews).reversed());
        }

        return eventDtos;
    }

    @Override
    public void sendHit(HttpServletRequest request) {
        StatisticsPostResponseDto dto = StatisticsPostResponseDto
                .builder()
                .app("main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.createHit(dto);
        log.debug("Отправлен hit: {}", dto);
    }

    @Override
    public EventFullDto findPublishedEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).
                orElseThrow(() -> new NotFoundException("Событие с ID=" + eventId + " не найдено"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие с ID=" + eventId + " не найдено или не опубликовано");
        }

        EventFullDto eventFullDto = eventMapper.toDto(event);

        loadViews(List.of(eventFullDto), event.getPublishedOn(), event.getEventDate());
        loadConfirmedRequests(List.of(eventFullDto));

        return eventFullDto;
    }

    @Override
    public boolean existsByCategory(Long categoryId) {
        return eventRepository.existsByCategory(categoryId);
    }

    @Override
    public EventFullDto findEventDtoById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с ID=%d не найдено",eventId)));

        return eventMapper.toDto(event);
    }

    @Transactional
    public List<EventFullDto> getEventsAdmin(EventParam eventParam) {

        PageRequest pageRequest = PageRequest.of(
                eventParam.getFrom()/eventParam.getSize(),
                eventParam.getSize(),
                Sort.by("eventDate").ascending()
        );

        Page<Event> eventsPage = eventRepository.findAll(withAdminParams(eventParam), pageRequest);

        List<EventFullDto> eventFullDtos = eventsPage.stream()
                .map(event -> eventMapper.toDto(event))
                .collect(Collectors.toList());

        loadViews(eventFullDtos, eventParam.getRangeStart(), eventParam.getRangeEnd());
        loadConfirmedRequests(eventFullDtos);
        return eventFullDtos;
    }

    private void loadViews(List<EventFullDto> events, LocalDateTime start, LocalDateTime end) {
        if (events.isEmpty()) {
            return;
        }

        // Создаём маппинг между ID события и URI для запроса статистики
        Map<Long, String> eventUriMap = events.stream()
                .collect(Collectors.toMap(
                        EventFullDto::getId,
                        event -> "/events/" + event.getId(), // Формат URI согласно требованиям статистики
                        (existing, replacement) -> existing)); // Обработка дубликатов (должно не случаться)

        try {
            // Получаем статистику просмотров из внешнего сервиса
            ResponseEntity<List<StatisticsGetResponseDto>> statsResponse = statsClient.getStats(
                    start,
                    end,
                    List.copyOf(eventUriMap.values()), // Гарантируем неизменяемость списка
                    true // Учитываем уникальные IP-адреса (статистика по уникальным просмотрам)
            );

            // Если данные получены, создаём маппинг URI → количество просмотров
            if (statsResponse.hasBody()) {
                List<StatisticsGetResponseDto> stats = statsResponse.getBody();
                if (stats != null && !stats.isEmpty()) {
                    Map<String, Long> uriToHits = stats.stream()
                            .collect(Collectors.toMap(StatisticsGetResponseDto::getUri, StatisticsGetResponseDto::getHits));

                    // Обновляем DTO событий значениями статистики
                    for (EventFullDto event : events) {
                        String uri = eventUriMap.get(event.getId());
                        event.setViews(uriToHits.getOrDefault(uri, 0L));
                    }
                    return;
                }
            }
            // Если данных нет или тело пустое, устанавливаем просмотры в 0 для всех событий
            events.forEach(event -> event.setViews(0L));
        } catch (FeignException e) {
            log.error("Ошибка при получении статистики просмотров: {}", e.getMessage(), e);
            // В случае ошибки оставляем текущие значения views без изменений
        }
    }

    private void loadConfirmedRequests(List<EventFullDto> events) {
        if (events.isEmpty()) {
            return;
        }

        // Получаем ID всех событий из DTO
        List<Long> eventIds = events.stream()
                .map(EventFullDto::getId)
                .toList();

        // Запрашиваем количество подтверждённых заявок по каждому событию
        List<RequestEventDto> confirmedRequests = requestClient.getEventRequestsCount(eventIds).getBody();

        // Создаём маппинг: ID события → количество подтверждённых заявок
        Map<Long, Long> eventIdToRequestsCount = confirmedRequests.stream()
                .collect(Collectors.toMap(
                        RequestEventDto::getEventId,
                        RequestEventDto::getRequestCount
                ));

        // Обновляем DTO событий значениями количества подтверждённых заявок
        for (EventFullDto event : events) {
            Long count = eventIdToRequestsCount.getOrDefault(event.getId(), 0L);
            event.setConfirmedRequests(Math.toIntExact(count));
        }
    }

    private void updateEventFields(Event event, UpdateEventAdminRequest request) {
        if (request.getAnnotation() != null) event.setAnnotation(request.getAnnotation());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getPaid() != null) event.setPaid(request.getPaid());
        if (request.getParticipantLimit() != null) event.setParticipantLimit(request.getParticipantLimit());
        if (request.getRequestModeration() != null) event.setRequestModeration(request.getRequestModeration());
        if (request.getTitle() != null && !request.getTitle().isBlank()) event.setTitle(request.getTitle());
        if (request.getCategory() != null) {
            event.setCategory(request.getCategory());
        }
        if (request.getLocation() != null) {
            event.setLocationLat(request.getLocation().getLat());
            event.setLocationLon(request.getLocation().getLon());
        }
    }

    private void validateAdminStateAction(StateActionAdmin action, Event event) {
        if (action == StateActionAdmin.PUBLISH_EVENT) {
            if (event.getState() != EventState.PENDING) {
                throw new ConflictException("Событие можно опубликовать только если оно в состоянии PENDING. " +
                        "Текущее состояние: " + event.getState());
            }
        }

        if (action == StateActionAdmin.REJECT_EVENT) {
            if (event.getState() == EventState.PUBLISHED) {
                throw new ConflictException("Нельзя отклонить уже опубликованное событие.");
            }
        }
    }



}