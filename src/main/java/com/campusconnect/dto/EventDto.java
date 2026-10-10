package com.campusconnect.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventDto {

    private Long id;

    @NotBlank(message = "Event title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Event date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotBlank(message = "Venue is required")
    private String venue;

    @NotBlank(message = "Organizer is required")
    private String organizer;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private String imageUrl;

    private Integer availableSeats;
    private Integer registeredCount;
    private Boolean isPast;
    private Boolean isFull;
    private Boolean userRegistered;

    public EventDto() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Integer getRegisteredCount() {
        return registeredCount;
    }

    public void setRegisteredCount(Integer registeredCount) {
        this.registeredCount = registeredCount;
    }

    public Boolean getIsPast() {
        return isPast;
    }

    public void setIsPast(Boolean past) {
        isPast = past;
    }

    public Boolean getIsFull() {
        return isFull;
    }

    public void setIsFull(Boolean full) {
        isFull = full;
    }

    public Boolean getUserRegistered() {
        return userRegistered;
    }

    public void setUserRegistered(Boolean userRegistered) {
        this.userRegistered = userRegistered;
    }
}
