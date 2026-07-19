package com.pradeep.slms.dto.task;

import com.pradeep.slms.entity.RequestAssignment;
import com.pradeep.slms.entity.ServiceRequest;
import com.pradeep.slms.entity.User;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record TaskResponseDTO(
        Long id,
        Long shopId,
        String shopName,
        String customerName,
        String customerPhone,
        String address,
        Double latitude,
        Double longitude,
        ServiceRequest.IssueType issueType,
        String description,
        ServiceRequest.Priority priority,
        ServiceRequest.RequestStatus status,
        UserSummaryDTO createdBy,
        UserSummaryDTO updatedBy,
        UserSummaryDTO assignedTechnician,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static TaskResponseDTO from(ServiceRequest task, RequestAssignment currentAssignment) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .shopId(task.getShop().getId())
                .shopName(task.getShop().getName())
                .customerName(task.getCustomerName())
                .customerPhone(task.getCustomerPhone())
                .address(task.getAddress())
                .latitude(task.getLatitude())
                .longitude(task.getLongitude())
                .issueType(task.getIssueType())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .createdBy(UserSummaryDTO.from(task.getCreatedBy()))
                .updatedBy(UserSummaryDTO.from(task.getUpdatedBy()))
                .assignedTechnician(currentAssignment == null ? null : UserSummaryDTO.from(currentAssignment.getWorker()))
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    @Builder
    public record UserSummaryDTO(
            Long id,
            String clerkUserId,
            String name,
            String phone,
            String email,
            User.UserRole role
    ) {
        public static UserSummaryDTO from(User user) {
            if (user == null) {
                return null;
            }

            return UserSummaryDTO.builder()
                    .id(user.getId())
                    .clerkUserId(user.getClerkUserId())
                    .name(user.getName())
                    .phone(user.getPhone())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build();
        }
    }
}
