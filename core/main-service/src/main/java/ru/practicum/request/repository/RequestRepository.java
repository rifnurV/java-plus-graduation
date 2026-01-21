package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.user.model.EventRequestCount;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request r WHERE r.requester.id = :requesterId")
    List<Request> findByRequester(@Param("requesterId") Long requesterId);

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
            "WHERE pr.event.id = ?1")
    List<Request> findAllByEventId(Long eventId);

    @Query(nativeQuery = true, value = "SELECT * FROM requests WHERE event = :eventId AND requester = :requesterId")
    Request findByIdAndRequester(@Param("eventId") Long eventId, @Param("requesterId") Long requesterId);

    @Query("""
            SELECT NEW ru.practicum.user.model.EventRequestCount(r.event.id, COUNT(r.id))
            FROM Request r
            WHERE r.status = :status
              AND r.event.id IN :eventIds
            GROUP BY r.event.id
            ORDER BY COUNT(r.id) ASC
            """)
    List<EventRequestCount> countByStatusForEvents(
            @Param("eventIds") List<Long> eventIds,
            @Param("status") RequestStatus status);

    @Query("""
            SELECT COUNT(r)
            FROM Request r
            WHERE r.event.id = :id
              AND r.status = :status
            """)
    long countByEventIdAndStatus(
            @Param("id") long eventId,
            @Param("status") RequestStatus status);
}
