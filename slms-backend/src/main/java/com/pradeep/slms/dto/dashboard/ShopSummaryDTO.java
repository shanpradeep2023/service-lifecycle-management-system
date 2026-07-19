package com.pradeep.slms.dto.dashboard;

import com.pradeep.slms.entity.ServiceRequest;
import lombok.Builder;

import java.util.Map;

@Builder
public record ShopSummaryDTO(
        Long shopId,
        String shopName,
        long totalTasks,
        Map<ServiceRequest.RequestStatus, Long> tasksByStatus,
        long totalUsers,
        long totalTechnicians,
        long totalAdmins
) {
}
