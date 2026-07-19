package com.pradeep.slms.service;

import com.pradeep.slms.dto.user.UserAssignmentRequestDTO;
import com.pradeep.slms.dto.user.UserResponseDTO;
import com.pradeep.slms.dto.user.UserUpdateProfileRequestDTO;
import com.pradeep.slms.dto.user.UserUpdateRequestDTO;
import com.pradeep.slms.entity.User;

import java.util.List;

public interface UserService {

    void updateUserRole(String clerkId, String role);
    String getUserRole(String clerkId);
    void updateOwnProfile(String clerkUserId, UserUpdateProfileRequestDTO request);
    void assignShopAndStatus(UserAssignmentRequestDTO request);

    List<UserResponseDTO> getAllUsers(Long shopId, User.UserRole role);
    UserResponseDTO getUserById(Long id);
    UserResponseDTO updateUser(Long id, UserUpdateRequestDTO request);
}
