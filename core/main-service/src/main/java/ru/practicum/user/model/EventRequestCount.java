package ru.practicum.user.model;

public record EventRequestCount(
        Long eventId,
        Long requestsCount
) {
}
