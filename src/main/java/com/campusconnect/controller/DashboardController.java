package com.campusconnect.controller;

import com.campusconnect.config.DataInitializer;
import com.campusconnect.dto.ApiResponse;
import com.campusconnect.dto.DashboardStatsDto;
import com.campusconnect.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DashboardController {

    private final DashboardService dashboardService;
    private final DataInitializer dataInitializer;

    public DashboardController(DashboardService dashboardService, DataInitializer dataInitializer) {
        this.dashboardService = dashboardService;
        this.dataInitializer = dataInitializer;
    }

    @GetMapping("/api/dashboard/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getStats() {
        DashboardStatsDto stats = dashboardService.getStats();
        return ResponseEntity.ok(ApiResponse.success("Statistics fetched successfully", stats));
    }

    @PostMapping("/api/data/reset")
    public ResponseEntity<ApiResponse<Void>> resetData() {
        dataInitializer.resetData();
        return ResponseEntity.ok(ApiResponse.success("Database has been reset to default seed data successfully"));
    }
}
