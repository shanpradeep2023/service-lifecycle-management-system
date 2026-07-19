package com.pradeep.slms.dto.dashboard;

import com.pradeep.slms.dto.task.TaskResponseDTO;
import com.pradeep.slms.entity.ServiceRequest;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record AdminDashboardResponseDTO(
        Long shopId,
        String shopName,
        long totalTasks,
        Map<ServiceRequest.RequestStatus, Long> tasksByStatus,
        Map<ServiceRequest.Priority, Long> tasksByPriority,
        long totalUsers,
        Map<String, Long> usersByRole,
        List<TaskResponseDTO> recentTasks
) {
}
