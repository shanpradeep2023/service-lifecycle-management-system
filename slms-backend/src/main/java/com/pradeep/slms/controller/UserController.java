package com.pradeep.slms.controller;

import com.pradeep.slms.dto.ApiResponse;
import com.pradeep.slms.dto.user.UserAssignmentRequestDTO;
import com.pradeep.slms.dto.user.UserRoleUpdateRequestDTO;
import com.pradeep.slms.dto.user.UserUpdateProfileRequestDTO;
import com.pradeep.slms.security.SecurityContextService;
import com.pradeep.slms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
}
