package com.campusconnect.service;

import com.campusconnect.dto.EventDto;
import com.campusconnect.entity.Event;
import com.campusconnect.entity.Registration;
import com.campusconnect.entity.RegistrationStatus;
import com.campusconnect.entity.User;
import com.campusconnect.exception.BadRequestException;
import com.campusconnect.exception.ResourceNotFoundException;
import com.campusconnect.repository.EventRepository;
import com.campusconnect.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public EventService(EventRepository eventRepository, RegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEvents(String search, String category, LocalDate date, User currentUser) {
        List<Event> events;
        if ((search == null || search.trim().isEmpty()) &&
            (category == null || category.trim().isEmpty() || "All".equalsIgnoreCase(category)) &&
            date == null) {
            events = eventRepository.findAllByOrderByEventDateAscStartTimeAsc();
        } else {
            String filterCategory = ("All".equalsIgnoreCase(category)) ? null : category;
            events = eventRepository.searchEvents(
                    search != null && !search.trim().isEmpty() ? search.trim() : null,
                    filterCategory,
                    date
            );
        }

        return events.stream()
                .map(e -> mapToDto(e, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventDto getEventById(Long id, User currentUser) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));
        return mapToDto(event, currentUser);
    }

    @Transactional
    public EventDto createEvent(EventDto dto) {
        if (dto.getEventDate() == null) {
            throw new BadRequestException("Event date is required");
        }
        if (dto.getEventDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Event date cannot be in the past");
        }
        if (dto.getCapacity() == null || dto.getCapacity() <= 0) {
            throw new BadRequestException("Capacity must be greater than 0");
        }

        Event event = new Event();
        updateEventFields(event, dto);

        Event savedEvent = eventRepository.save(event);
        return mapToDto(savedEvent, null);
    }

    @Transactional
    public EventDto updateEvent(Long id, EventDto dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));

        if (dto.getEventDate() == null) {
            throw new BadRequestException("Event date is required");
        }
        if (dto.getEventDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Event date cannot be in the past");
        }
        if (dto.getCapacity() == null || dto.getCapacity() <= 0) {
            throw new BadRequestException("Capacity must be greater than 0");
        }

        long activeRegistrations = registrationRepository.countByEventIdAndStatus(id, RegistrationStatus.REGISTERED);
        if (dto.getCapacity() < activeRegistrations) {
            throw new BadRequestException("Capacity cannot be less than currently registered students (" + activeRegistrations + ")");
        }

        updateEventFields(event, dto);
        Event updatedEvent = eventRepository.save(event);
        return mapToDto(updatedEvent, null);
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));
        registrationRepository.deleteByEventId(id);
        eventRepository.delete(event);
    }

    private void updateEventFields(Event event, EventDto dto) {
        event.setTitle(dto.getTitle().trim());
        event.setDescription(dto.getDescription().trim());
        event.setCategory(dto.getCategory().trim());
        event.setEventDate(dto.getEventDate());
        event.setStartTime(dto.getStartTime());
        event.setVenue(dto.getVenue().trim());
        event.setOrganizer(dto.getOrganizer().trim());
        event.setCapacity(dto.getCapacity());
        event.setImageUrl(dto.getImageUrl() != null ? dto.getImageUrl().trim() : null);
    }

    public EventDto mapToDto(Event event, User currentUser) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setCategory(event.getCategory());
        dto.setEventDate(event.getEventDate());
        dto.setStartTime(event.getStartTime());
        dto.setVenue(event.getVenue());
        dto.setOrganizer(event.getOrganizer());
        dto.setCapacity(event.getCapacity());
        dto.setImageUrl(event.getImageUrl());

        long activeRegistrations = registrationRepository.countByEventIdAndStatus(event.getId(), RegistrationStatus.REGISTERED);
        int available = Math.max(0, event.getCapacity() - (int) activeRegistrations);

        dto.setRegisteredCount((int) activeRegistrations);
        dto.setAvailableSeats(available);
        dto.setIsFull(available <= 0);
        dto.setIsPast(event.getEventDate().isBefore(LocalDate.now()));

        if (currentUser != null) {
            Optional<Registration> reg = registrationRepository.findByUserIdAndEventId(currentUser.getId(), event.getId());
            dto.setUserRegistered(reg.isPresent() && reg.get().getStatus() == RegistrationStatus.REGISTERED);
        } else {
            dto.setUserRegistered(false);
        }

        return dto;
    }
}
