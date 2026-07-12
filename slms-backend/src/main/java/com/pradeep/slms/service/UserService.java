package com.pradeep.slms.service;

import com.pradeep.slms.dto.UserAssignmentRequestDTO;
import com.pradeep.slms.dto.UserUpdateProfileRequestDTO;

public interface UserService {

    void updateUserRole(String clerkId, String role);
    String getUserRole(String clerkId);
    void updateOwnProfile(String clerkUserId, UserUpdateProfileRequestDTO request);
    void assignShopAndStatus(UserAssignmentRequestDTO request);
}
