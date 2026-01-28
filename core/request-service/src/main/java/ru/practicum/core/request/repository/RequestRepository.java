package ru.practicum.core.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.core.request.model.Request;
import ru.practicum.core.request.model.RequestStatus;
import ru.practicum.core.request.model.dto.RequestEventDto;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequesterId(@Param("requesterId") Long requesterId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE requests SET status = 'REJECTED' WHERE id = :requestId ")
    void updateToRejected(@Param("requestId") Long requestId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE requests SET status = 'CANCELED' WHERE id = :requestId ")
    void updateToCanceled(@Param("requestId") Long requestId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE requests SET status = 'CONFIRMED' WHERE id = :requestId ")
    void updateToConfirmed(@Param("requestId") Long requestId);

    @Query("SELECT pr " +
            "FROM Request as pr " +
            "WHERE pr.eventId = ?1")
    List<Request> findAllByEventId(Long eventId);

    @Query("""
            SELECT NEW ru.practicum.core.request.model.dto.RequestEventDto(r.eventId, COUNT(r.id))
            FROM Request r
            WHERE r.status = :status
              AND r.eventId IN :eventIds
            GROUP BY r.eventId
            ORDER BY COUNT(r.id) ASC
            """)
    List<RequestEventDto> countByStatusForEvents(
            @Param("eventIds") List<Long> eventIds,
            @Param("status") RequestStatus status);

    @Query("""
            SELECT COUNT(r)
            FROM Request r
            WHERE r.eventId = :id
              AND r.status = :status
            """)
    long countByEventIdAndStatus(
            @Param("id") long eventId,
            @Param("status") RequestStatus status);

    Boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);
}
