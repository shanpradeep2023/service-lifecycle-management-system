package com.pradeep.slms.dto.user;


import com.pradeep.slms.entity.User.UserStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAssignmentRequestDTO {

    @NotBlank(message = "Clerk user id is required")
    private String clerkUserId;

    private Long shopId;      // null = no change
    private UserStatus status; // null = no change
}
