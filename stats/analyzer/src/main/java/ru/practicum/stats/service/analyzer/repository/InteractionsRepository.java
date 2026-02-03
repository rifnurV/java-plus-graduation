package ru.practicum.stats.service.analyzer.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.service.analyzer.model.Interactions;

import java.util.List;
import java.util.Optional;

public interface InteractionsRepository extends JpaRepository<Interactions, Long> {

    @Query("SELECT i.rating FROM Interactions i WHERE i.userId = :userId AND i.eventId = :eventId")
    Double getRating(@Param("userId") long userId, @Param("eventId") long eventId);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO interactions (user_id, event_id, rating, ts)
            VALUES (:userId, :eventId, :rating, CURRENT_TIMESTAMP)
            ON CONFLICT (user_id, event_id)
            DO UPDATE SET rating = EXCLUDED.rating, ts = CURRENT_TIMESTAMP
            """, nativeQuery = true)
    void updateRating(@Param("userId") long userId, @Param("eventId") long eventId, @Param("rating") double rating);

    List<Interactions> findByUserId(Long userId);

    List<Interactions> findByEventIdIn(List<Long> eventIds);

    List<Interactions> findByEventId(Long eventId);

    Optional<Interactions> findByUserIdAndEventId(Long userId, Long eventId);
}
