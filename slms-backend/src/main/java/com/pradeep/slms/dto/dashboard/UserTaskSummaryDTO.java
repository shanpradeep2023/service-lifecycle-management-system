package com.pradeep.slms.dto.dashboard;

import com.pradeep.slms.dto.task.TaskResponseDTO;
import com.pradeep.slms.dto.user.UserResponseDTO;
import com.pradeep.slms.entity.ServiceRequest;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record UserTaskSummaryDTO(
        UserResponseDTO user,
        long totalTasks,
        Map<ServiceRequest.RequestStatus, Long> tasksByStatus,
        List<TaskResponseDTO> tasks
) {
}
