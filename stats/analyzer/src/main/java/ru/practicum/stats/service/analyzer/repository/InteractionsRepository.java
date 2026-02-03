package ru.practicum.stats.service.analyzer.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.service.analyzer.model.Interactions;

import java.util.List;
import java.util.Set;

public interface InteractionsRepository extends JpaRepository<Interactions, Long> {

    @Query("SELECT i.eventId FROM Interactions i WHERE i.userId = :userId")
    Set<Long> findAllEventIdsByUserId(@Param("userId") long userId);

    @Query("SELECT i FROM Interactions i WHERE i.userId = :userId ORDER BY i.ts DESC")
    List<Interactions> findLastInteractions(@Param("userId") long userId, Pageable pageable);

    @Query("SELECT i.rating FROM Interactions i WHERE i.userId = :userId AND i.eventId = :eventId")
    Double getRating(@Param("userId") long userId, @Param("eventId") long eventId);

    @Query("SELECT SUM(i.rating) FROM Interactions i WHERE i.eventId = :eventId")
    Double sumRatingsByEventId(@Param("eventId") long eventId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO interactions (user_id, event_id, rating, ts) " +
            "VALUES (:userId, :eventId, :rating, CURRENT_TIMESTAMP) " +
            "ON CONFLICT (user_id, event_id) " +
            "DO UPDATE SET rating = EXCLUDED.rating, ts = CURRENT_TIMESTAMP " +
            "WHERE interactions.rating < EXCLUDED.rating", nativeQuery = true)
    void updateRating(@Param("userId") long userId, @Param("eventId") long eventId, @Param("rating") double rating);
}
