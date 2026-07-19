package com.pradeep.slms.security;

import com.pradeep.slms.entity.User;

public record AuthenticatedUser(
        Long id,
        String clerkUserId,
        User.UserRole role,
        Long shopId
) {
}
