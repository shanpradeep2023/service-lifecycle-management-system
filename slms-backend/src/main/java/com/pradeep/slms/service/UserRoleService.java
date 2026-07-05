package com.pradeep.slms.service;

import com.pradeep.slms.entity.User;
import com.pradeep.slms.enums.UserRole;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserRoleService {
    void updateUserRole(String clerkId, String role);
    String getUserRole(String clerkId);
}
