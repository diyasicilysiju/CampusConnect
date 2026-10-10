package com.campusconnect.repository;

import com.campusconnect.entity.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByOrderByEventDateAscStartTimeAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT e FROM Event e WHERE " +
           "(:title IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR LOWER(e.category) = LOWER(:category)) AND " +
           "(:date IS NULL OR e.eventDate = :date) " +
           "ORDER BY e.eventDate ASC, e.startTime ASC")
    List<Event> searchEvents(@Param("title") String title,
                             @Param("category") String category,
                             @Param("date") LocalDate date);
}
