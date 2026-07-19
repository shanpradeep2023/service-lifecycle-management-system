package com.pradeep.slms.controller;

import com.pradeep.slms.dto.ApiResponse;
import com.pradeep.slms.dto.dashboard.AdminDashboardResponseDTO;
import com.pradeep.slms.dto.dashboard.UserTaskSummaryDTO;
import com.pradeep.slms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'COMMANDER')")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<AdminDashboardResponseDTO>> getDashboard() {
        log.info("Fetching admin dashboard");
        AdminDashboardResponseDTO dashboard = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(ApiResponse.ok("Admin dashboard fetched successfully", dashboard));
    }

    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<ApiResponse<UserTaskSummaryDTO>> getUserTaskSummary(@PathVariable Long userId) {
        log.info("Fetching task summary for user {} in admin dashboard", userId);
        UserTaskSummaryDTO summary = dashboardService.getUserTaskSummary(userId);
        return ResponseEntity.ok(ApiResponse.ok("User task summary fetched successfully", summary));
    }
}
