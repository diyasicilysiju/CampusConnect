package com.campusconnect.repository;

import com.campusconnect.entity.Registration;
import com.campusconnect.entity.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    @Query("SELECT r FROM Registration r JOIN FETCH r.user JOIN FETCH r.event WHERE r.user.id = :userId ORDER BY r.registrationDate DESC")
    List<Registration> findByUserIdWithEvent(@Param("userId") Long userId);

    @Query("SELECT r FROM Registration r JOIN FETCH r.user JOIN FETCH r.event WHERE r.event.id = :eventId ORDER BY r.registrationDate ASC")
    List<Registration> findByEventIdWithUser(@Param("eventId") Long eventId);

    Optional<Registration> findByUserIdAndEventId(Long userId, Long eventId);

    long countByEventIdAndStatus(Long eventId, RegistrationStatus status);

    long countByStatus(RegistrationStatus status);

    void deleteByEventId(Long eventId);
}
