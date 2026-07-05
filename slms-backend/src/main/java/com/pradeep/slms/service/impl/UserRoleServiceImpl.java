package com.pradeep.slms.service.impl;

import com.pradeep.slms.entity.User;
import com.pradeep.slms.exception.AppException;
import com.pradeep.slms.repository.UserRepository;
import com.pradeep.slms.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {


    private final UserRepository userRepository;

    @Override
    public void updateUserRole(String clerkId, String role) {
        // null check
        if (role == null || role.isEmpty()) {
            throw AppException.badRequest("role cannot be null or empty");
        }
        // find user by clerkId
        User user = userRepository.findByClerkUserId(clerkId)
                .orElseThrow(() -> AppException.notFound(
                        "User not found: " + clerkId
                ));
        try {
            user.setRole(User.UserRole.valueOf(role));
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw AppException.badRequest("Invalid role: " + role);
        }

    }

    @Override
    public String getUserRole(String clerkId) {
        User user = userRepository.findByClerkUserId(clerkId)
                .orElseThrow(() -> AppException.notFound(
                        "User not found: " + clerkId
                ));

        return user.getRole().toString();
    }
}
