package com.campusconnect.controller;

import com.campusconnect.dto.ApiResponse;
import com.campusconnect.dto.RegistrationDto;
import com.campusconnect.entity.User;
import com.campusconnect.exception.BadRequestException;
import com.campusconnect.service.AuthService;
import com.campusconnect.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final AuthService authService;

    public RegistrationController(RegistrationService registrationService, AuthService authService) {
        this.registrationService = registrationService;
        this.authService = authService;
    }

    @PostMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<RegistrationDto>> registerForEvent(@PathVariable Long eventId) {
        User user = authService.getCurrentUserEntity()
                .orElseThrow(() -> new BadRequestException("Please log in to register for an event"));

        RegistrationDto registration = registrationService.registerForEvent(eventId, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Successfully registered for event!", registration));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<RegistrationDto>>> getMyRegistrations() {
        User user = authService.getCurrentUserEntity()
                .orElseThrow(() -> new BadRequestException("Please log in to view your registrations"));

        List<RegistrationDto> list = registrationService.getMyRegistrations(user);
        return ResponseEntity.ok(ApiResponse.success("Registrations fetched successfully", list));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<RegistrationDto>> cancelRegistration(@PathVariable Long id) {
        User user = authService.getCurrentUserEntity()
                .orElseThrow(() -> new BadRequestException("Please log in to cancel registration"));

        RegistrationDto cancelled = registrationService.cancelRegistration(id, user);
        return ResponseEntity.ok(ApiResponse.success("Registration cancelled successfully", cancelled));
    }

    @GetMapping("/event/{eventId}/students")
    public ResponseEntity<ApiResponse<List<RegistrationDto>>> getEventRegistrations(@PathVariable Long eventId) {
        List<RegistrationDto> list = registrationService.getEventRegistrations(eventId);
        return ResponseEntity.ok(ApiResponse.success("Event registrations fetched successfully", list));
    }
}
