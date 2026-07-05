package com.pradeep.slms.controller;

import com.pradeep.slms.dto.ApiResponse;
import com.pradeep.slms.dto.RoleUpdateRequestDTO;
import com.pradeep.slms.service.UserRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping("/get-user-role")
    public ResponseEntity<ApiResponse<String>> getUserRole(@RequestBody RoleUpdateRequestDTO request) {
        log.info("Get User Role");
        String role = userRoleService.getUserRole(request.getClerkId());
        log.info("User Role Response: {}", role);
        return ResponseEntity.ok(ApiResponse.ok("Fetched User Role Successfully", role));

    }

    @PostMapping("/update-user-role")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(@RequestBody @Valid RoleUpdateRequestDTO request) {
        log.info("Updating role for {} to {}", request.getClerkId(), request.getRole());
        userRoleService.updateUserRole(request.getClerkId(), request.getRole());
        log.info("User Role Updated");
        return ResponseEntity.ok(ApiResponse.ok("User role updated successfully"));
    }
}
