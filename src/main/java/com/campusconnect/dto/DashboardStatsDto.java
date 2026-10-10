package com.campusconnect.dto;

public class DashboardStatsDto {

    private long totalEvents;
    private long totalStudents;
    private long totalRegistrations;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(long totalEvents, long totalStudents, long totalRegistrations) {
        this.totalEvents = totalEvents;
        this.totalStudents = totalStudents;
        this.totalRegistrations = totalRegistrations;
    }

    public long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalRegistrations() {
        return totalRegistrations;
    }

    public void setTotalRegistrations(long totalRegistrations) {
        this.totalRegistrations = totalRegistrations;
    }
}
