package ru.practicum.stats.service.analyzer.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.service.analyzer.model.Similarities;

import java.util.List;

public interface SimilaritiesRepository extends JpaRepository<Similarities, Long> {

    @Query("SELECT s FROM Similarities s WHERE s.event1 = :id OR s.event2 = :id")
    List<Similarities> findSimilarEventsForEvent(long id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO similarities (event1, event2, similarity, ts) " +
            "VALUES (:event1, :event2, :score, CURRENT_TIMESTAMP) " +
            "ON CONFLICT (event1, event2) " +
            "DO UPDATE SET similarity = EXCLUDED.similarity, ts = CURRENT_TIMESTAMP", nativeQuery = true)
    void updateScore(@Param("event1") long event1, @Param("event2") long event2, @Param("score") double score);

    @Query(value = "SELECT s.* FROM similarities s " +
            "JOIN interactions ui ON (ui.event_id = s.event1 OR ui.event_id = s.event2) " +
            "WHERE (s.event1 = :targetId OR s.event2 = :targetId) " +
            "AND ui.user_id = :userId AND ui.event_id != :targetId " +
            "ORDER BY s.similarity DESC LIMIT :k", nativeQuery = true)
    List<Similarities> findTopSimilarToTargetAmongUserInteractions(long targetId, long userId, int k);
}
