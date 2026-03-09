package ru.practicum.stats.service.analyzer.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.service.analyzer.model.Similarities;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SimilaritiesRepository extends JpaRepository<Similarities, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO similarities (event1, event2, similarity, ts)
            VALUES (:event1, :event2, :score, CURRENT_TIMESTAMP)
            ON CONFLICT (event1, event2)
            DO UPDATE SET similarity = EXCLUDED.similarity, ts = CURRENT_TIMESTAMP
            """, nativeQuery = true)
    void updateScore(@Param("event1") long event1, @Param("event2") long event2, @Param("score") double score);

    // Записи схожести, связанные с указанными мероприятиями.
    @Query("""
            SELECT es
            FROM Similarities es
            WHERE es.event1 = :event1 OR es.event2 = :event2
            """)
    List<Similarities> findAllByEvent1OrEvent2(Long event1, Long event2);

    @Query("""
            SELECT DISTINCT es
            FROM Similarities es
            WHERE es.event1 IN :sourceEventIds OR es.event2 IN :targetEventIds
            """)
    List<Similarities> findAllBySourceEventIdInOrTargetEventIdIn(Set<Long> sourceEventIds, Set<Long> targetEventIds);


    Optional<Similarities> findByEvent1AndEvent2(Long event1, Long event2);
}
