package com.campusconnect.service;

import com.campusconnect.dto.DashboardStatsDto;
import com.campusconnect.entity.RegistrationStatus;
import com.campusconnect.entity.Role;
import com.campusconnect.repository.EventRepository;
import com.campusconnect.repository.RegistrationRepository;
import com.campusconnect.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

    public DashboardService(EventRepository eventRepository,
                            UserRepository userRepository,
                            RegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
    }

    public DashboardStatsDto getStats() {
        long totalEvents = eventRepository.count();
        long totalStudents = userRepository.countByRole(Role.STUDENT);
        long totalRegistrations = registrationRepository.countByStatus(RegistrationStatus.REGISTERED);

        return new DashboardStatsDto(totalEvents, totalStudents, totalRegistrations);
    }
}
