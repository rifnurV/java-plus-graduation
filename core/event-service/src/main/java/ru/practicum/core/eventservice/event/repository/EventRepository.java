package ru.practicum.core.eventservice.event.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.util.StringUtils;
import ru.practicum.core.eventservice.event.model.Event;
import ru.practicum.core.eventservice.event.model.EventParam;
import ru.practicum.core.eventservice.event.model.EventState;
import ru.practicum.core.eventservice.event.client.RequestDto;
import ru.practicum.core.eventservice.event.client.RequestStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @Query("""
            SELECT e
            FROM Event AS e
            WHERE e.category = ?1
            """)
    List<Event> findByCategoryId(long catId);


    @Query("""
            SELECT e
            FROM Event as e
            WHERE e.initiator = ?1
            """)
    List<Event> getAllByUser(long userId, PageRequest page);

    @Query("""
            SELECT e
            FROM Event as e
            WHERE e.id = ?1
            AND e.initiator = ?2
            """)
    Event getByIdAndUserId(long eventId, long userId);

    List<Event> findByIdIn(Set<Long> eventIds);

    @Query("""
            SELECT e
            FROM Event as e
            WHERE e.id in ?1 
            """)
    List<Event> findEventsWIthUsersByIdSet(Set<Long> eventIds);

    class EventSpec {
        public static Specification<Event> withAdminParams(EventParam params) {
            return (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();

                // Фильтр по инициаторам
                if (params.getUsers() != null) {
                    predicates.add(root.get("initiator").in(params.getUsers()));
                }

                // Фильтр по статусам
                if (params.getStates() != null) {
                    predicates.add(root.get("state").in(params.getStates()));
                }

                // Фильтр по категориям
                if (params.getCategories() != null) {
                    predicates.add(root.get("category").in(params.getCategories()));
                }

                // Диапазон дат
                addDatePredicates(cb, root, predicates, params.getRangeStart(), params.getRangeEnd());

                return cb.and(predicates.toArray(new Predicate[0]));
            };
        }
    }

    class UserEventSpec {
        public static Specification<Event> withUserParams(EventParam params) {
            return (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();

                // Основной фильтр: только опубликованные события
                predicates.add(cb.equal(root.get("state"), EventState.PUBLISHED));

                // Поиск по тексту
                if (StringUtils.hasText(params.getText())) {
                    String pattern = "%" + params.getText().toLowerCase() + "%";
                    predicates.add(
                            cb.or(
                                    cb.like(cb.lower(root.get("annotation")), pattern),
                                    cb.like(cb.lower(root.get("description")), pattern)
                            )
                    );
                }

                // Фильтр по категориям
                if (params.getCategories() != null) {
                    predicates.add(root.get("category").in(params.getCategories()));
                }

                // Платные/бесплатные
                if (params.getPaid() != null) {
                    predicates.add(cb.equal(root.get("paid"), params.getPaid()));
                }

                // Доступность по лимиту участников
                if (params.getOnlyAvailable() != null && params.getOnlyAvailable()) {
                    Subquery<Long> subquery = query != null ? query.subquery(Long.class) : null;
                    Root<RequestDto> reqRoot = Objects.requireNonNull(subquery).from(RequestDto.class);
                    subquery.select(cb.count(reqRoot.get("id")))
                            .where(
                                    cb.equal(reqRoot.get("event").get("id"), root.get("id")),
                                    cb.equal(reqRoot.get("status"), RequestStatus.CONFIRMED)
                            )
                            .groupBy(reqRoot.get("event").get("id"));

                    predicates.add(
                            cb.or(
                                    cb.isNull(subquery),
                                    cb.lessThan(subquery, root.get("participantLimit"))
                            )
                    );
                }

                // Диапазон дат
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime start = params.getRangeStart() != null ? params.getRangeStart() : now;

                addDatePredicates(cb, root, predicates, start, params.getRangeEnd());

                return cb.and(predicates.toArray(new Predicate[0]));
            };
        }
    }

    private static void addDatePredicates(CriteriaBuilder cb, Root<Event> root,
                                          List<Predicate> predicates,
                                          LocalDateTime start, LocalDateTime end) {
        if (start != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), start));
        }
        if (end != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), end));
        }
    }

    boolean existsByCategory(Long categoryId);
}
