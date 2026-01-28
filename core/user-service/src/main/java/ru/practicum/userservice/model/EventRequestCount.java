package ru.practicum.userservice.model;

public record EventRequestCount(
        Long eventId,
        Long requestsCount
) {
}
