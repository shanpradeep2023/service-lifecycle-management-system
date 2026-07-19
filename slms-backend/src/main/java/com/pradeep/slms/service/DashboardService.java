package com.pradeep.slms.service;

import com.pradeep.slms.dto.dashboard.AdminDashboardResponseDTO;
import com.pradeep.slms.dto.dashboard.DashboardResponseDTO;
import com.pradeep.slms.dto.dashboard.UserTaskSummaryDTO;

public interface DashboardService {
    DashboardResponseDTO getCommanderDashboard();
    AdminDashboardResponseDTO getAdminDashboard();
    UserTaskSummaryDTO getUserTaskSummary(Long userId);
}
