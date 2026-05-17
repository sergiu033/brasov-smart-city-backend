package com.smartcity.event.repository;

import com.smartcity.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
        SELECT e
        FROM Event e
        WHERE e.startTime >= :weekStart
        AND e.endTime < :nextWeekStart
        AND (:title IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%')))
        ORDER BY e.startTime ASC
    """)
    Page<Event> findEventsByWeek(
            @Param("weekStart") LocalDateTime weekStart,
            @Param("nextWeekStart") LocalDateTime nextWeekStart,
            @Param("title") String title,
            Pageable pageable
    );

}
