package com.campusconnect.controller;

import com.campusconnect.dto.ApiResponse;
import com.campusconnect.dto.EventDto;
import com.campusconnect.entity.User;
import com.campusconnect.service.AuthService;
import com.campusconnect.service.EventService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final AuthService authService;

    public EventController(EventService eventService, AuthService authService) {
        this.eventService = eventService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventDto>>> getEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        User currentUser = authService.getCurrentUserEntity().orElse(null);
        List<EventDto> events = eventService.getEvents(search, category, date, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Events fetched successfully", events));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDto>> getEventById(@PathVariable Long id) {
        User currentUser = authService.getCurrentUserEntity().orElse(null);
        EventDto event = eventService.getEventById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Event details fetched successfully", event));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EventDto>> createEvent(@Valid @RequestBody EventDto dto) {
        EventDto created = eventService.createEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Event created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDto>> updateEvent(@PathVariable Long id, @Valid @RequestBody EventDto dto) {
        EventDto updated = eventService.updateEvent(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Event updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.success("Event deleted successfully"));
    }
}
