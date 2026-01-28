package ru.practicum.core.eventservice.event.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@EqualsAndHashCode
@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "events", schema = "public")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "annotation",nullable = false, length = 2000)
    String annotation;

    @Column(name = "category", nullable = false)
    Long category;

    @Column(name = "created_on", nullable = false)
    LocalDateTime createdOn;

    @Column(name = "description", nullable = false, length = 7000)
    String description;

    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;

    @Column(name = "initiator", nullable = false)
    Long initiator;

    @Column(name = "location_lat", nullable = false)
    Float locationLat;

    @Column(name = "location_lon", nullable = false)
    Float locationLon;

    @Column(name = "paid", nullable = false)
    Boolean paid = false;

    @Column(name = "participant_limit", nullable = false)
    Integer participantLimit = 0;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(name = "request_moderation", nullable = false)
    Boolean requestModeration = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    EventState state = EventState.PENDING;

    @Column(name = "title",nullable = false, length = 120)
    String title;

    public Boolean isRequestModeration() {
        return this.requestModeration;
    }
}
