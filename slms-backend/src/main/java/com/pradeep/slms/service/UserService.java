package com.pradeep.slms.service;

import com.pradeep.slms.dto.user.UserAssignmentRequestDTO;
import com.pradeep.slms.dto.user.UserUpdateProfileRequestDTO;

public interface UserService {

    void updateUserRole(String clerkId, String role);
    String getUserRole(String clerkId);
    void updateOwnProfile(String clerkUserId, UserUpdateProfileRequestDTO request);
    void assignShopAndStatus(UserAssignmentRequestDTO request);
}
