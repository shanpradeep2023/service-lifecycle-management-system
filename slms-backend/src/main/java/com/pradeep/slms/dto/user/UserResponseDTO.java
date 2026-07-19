package com.pradeep.slms.dto.user;

import com.pradeep.slms.entity.User.UserRole;
import com.pradeep.slms.entity.User.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String clerkUserId;
    private String name;
    private String phone;
    private String email;
    private UserRole role;
    private Long shopId;
    private UserStatus status;
    private OffsetDateTime createdAt;
}
