package com.pradeep.slms.dto.dashboard;

import lombok.Builder;

import java.util.List;
import java.util.Map;

import com.pradeep.slms.dto.task.TaskResponseDTO;
import com.pradeep.slms.entity.ServiceRequest;
import com.pradeep.slms.entity.User;

@Builder
public record DashboardResponseDTO(
        long totalTasks,
        Map<ServiceRequest.RequestStatus, Long> tasksByStatus,
        Map<ServiceRequest.Priority, Long> tasksByPriority,
        long totalShops,
        long totalUsers,
        Map<User.UserRole, Long> usersByRole,
        List<ShopSummaryDTO> shopSummaries,
        List<TaskResponseDTO> recentTasks
) {
}
