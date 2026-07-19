package com.pradeep.slms.controller;

import com.pradeep.slms.dto.ApiResponse;
import com.pradeep.slms.dto.user.UserAssignmentRequestDTO;
import com.pradeep.slms.dto.user.UserResponseDTO;
import com.pradeep.slms.dto.user.UserRoleUpdateRequestDTO;
import com.pradeep.slms.dto.user.UserUpdateProfileRequestDTO;
import com.pradeep.slms.dto.user.UserUpdateRequestDTO;
import com.pradeep.slms.entity.User;
import com.pradeep.slms.security.SecurityContextService;
import com.pradeep.slms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final SecurityContextService securityContextService;
    private final UserService userService;

    @GetMapping("/role")
    public ResponseEntity<ApiResponse<String>> getCurrentUserRole() {
        log.info("Get User Role");
        String role = userService.getUserRole(securityContextService.currentUser().clerkUserId());
        log.info("User Role Response: {}", role);
        return ResponseEntity.ok(ApiResponse.ok("Fetched User Role Successfully", role));
    }

    @PostMapping("/update-user-role")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMANDER')")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(@RequestBody @Valid UserRoleUpdateRequestDTO request) {
        log.info("Updating role for {} to {}", request.getClerkId(), request.getRole());
        userService.updateUserRole(request.getClerkId(), request.getRole());
        log.info("User Role Updated");
        return ResponseEntity.ok(ApiResponse.ok("User role updated successfully"));
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateOwnProfile(@RequestBody @Valid UserUpdateProfileRequestDTO request) {
        String clerkUserId = securityContextService.currentUser().clerkUserId();
        log.info("User {} updating own profile", clerkUserId);
        userService.updateOwnProfile(clerkUserId, request);
        log.info("User Profile Updated");
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully"));
    }

    @PatchMapping("/assign-shop")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMANDER')")
    public ResponseEntity<ApiResponse<Void>> assignShopAndStatus(@RequestBody @Valid UserAssignmentRequestDTO request) {
        log.info("Assigning shop/status for {}", request.getClerkUserId());
        userService.assignShopAndStatus(request);
        log.info("Assignment complete");
        return ResponseEntity.ok(ApiResponse.ok("User updated successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMANDER')")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers(
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) User.UserRole role) {
        log.info("Fetching users with shopId={} and role={}", shopId, role);
        List<UserResponseDTO> users = userService.getAllUsers(shopId, role);
        return ResponseEntity.ok(ApiResponse.ok("Users fetched successfully", users));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMANDER')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        log.info("Fetching user {}", id);
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.ok("User fetched successfully", user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMANDER')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequestDTO request) {
        log.info("Updating user {}", id);
        UserResponseDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.ok("User updated successfully", updatedUser));
    }
}
