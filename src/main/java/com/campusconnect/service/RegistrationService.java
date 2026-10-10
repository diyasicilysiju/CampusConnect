package com.campusconnect.service;

import com.campusconnect.dto.RegistrationDto;
import com.campusconnect.entity.*;
import com.campusconnect.exception.BadRequestException;
import com.campusconnect.exception.ResourceNotFoundException;
import com.campusconnect.repository.EventRepository;
import com.campusconnect.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;

    public RegistrationService(RegistrationRepository registrationRepository, EventRepository eventRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public RegistrationDto registerForEvent(Long eventId, User user) {
        if (user.getRole() != Role.STUDENT) {
            throw new BadRequestException("Only students can register for events");
        }

        // Pessimistic lock prevents race conditions on capacity
        Event event = eventRepository.findByIdWithLock(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        // Check if event is in the past
        if (event.getEventDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot register for a past event");
        }

        // Check if user already registered
        Optional<Registration> existingReg = registrationRepository.findByUserIdAndEventId(user.getId(), eventId);
        if (existingReg.isPresent() && existingReg.get().getStatus() == RegistrationStatus.REGISTERED) {
            throw new BadRequestException("You are already registered for this event");
        }

        // Check available capacity
        long activeCount = registrationRepository.countByEventIdAndStatus(eventId, RegistrationStatus.REGISTERED);
        if (activeCount >= event.getCapacity()) {
            throw new BadRequestException("This event has reached full capacity. No seats available.");
        }

        Registration registration;
        if (existingReg.isPresent()) {
            registration = existingReg.get();
            registration.setStatus(RegistrationStatus.REGISTERED);
            registration.setRegistrationDate(LocalDateTime.now());
        } else {
            registration = new Registration(user, event, RegistrationStatus.REGISTERED);
        }

        Registration saved = registrationRepository.save(registration);
        return mapToDto(saved);
    }

    @Transactional
    public RegistrationDto cancelRegistration(Long registrationId, User user) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + registrationId));

        if (!registration.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can only cancel your own registrations");
        }

        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new BadRequestException("Registration has already been cancelled");
        }

        registration.setStatus(RegistrationStatus.CANCELLED);
        Registration saved = registrationRepository.save(registration);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<RegistrationDto> getMyRegistrations(User user) {
        return registrationRepository.findByUserIdWithEvent(user.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistrationDto> getEventRegistrations(Long eventId) {
        return registrationRepository.findByEventIdWithUser(eventId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private RegistrationDto mapToDto(Registration reg) {
        RegistrationDto dto = new RegistrationDto();
        dto.setId(reg.getId());
        dto.setEventId(reg.getEvent().getId());
        dto.setEventTitle(reg.getEvent().getTitle());
        dto.setEventCategory(reg.getEvent().getCategory());
        dto.setEventDate(reg.getEvent().getEventDate());
        dto.setEventStartTime(reg.getEvent().getStartTime());
        dto.setEventVenue(reg.getEvent().getVenue());

        dto.setUserId(reg.getUser().getId());
        dto.setStudentName(reg.getUser().getFullName());
        dto.setStudentId(reg.getUser().getStudentId());
        dto.setStudentEmail(reg.getUser().getEmail());

        dto.setRegistrationDate(reg.getRegistrationDate());
        dto.setStatus(reg.getStatus());
        dto.setCanCancel(reg.getStatus() == RegistrationStatus.REGISTERED &&
                         !reg.getEvent().getEventDate().isBefore(LocalDate.now()));

        return dto;
    }
}
