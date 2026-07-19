package com.pradeep.slms.service.impl;

import com.pradeep.slms.dto.user.UserAssignmentRequestDTO;
import com.pradeep.slms.dto.user.UserUpdateProfileRequestDTO;
import com.pradeep.slms.entity.Shop;
import com.pradeep.slms.entity.User;
import com.pradeep.slms.exception.AppException;
import com.pradeep.slms.repository.ShopRepository;
import com.pradeep.slms.repository.UserRepository;
import com.pradeep.slms.security.SecurityContextService;
import com.pradeep.slms.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final SecurityContextService securityContextService;

    @Override
    public void updateUserRole(String clerkId, String role) {
        // null check
        if (role == null || role.isEmpty()) {
            throw AppException.badRequest("role cannot be null or empty");
        }

        // parse user role from enum
        User.UserRole userRole = Arrays.stream(User.UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> AppException.badRequest(
                        "Invalid role: " + role
                ));

        // find user by clerkId
        User user = userRepository.findByClerkUserId(clerkId)
                .orElseThrow(() -> AppException.notFound(
                        "User not found: " + clerkId
                ));

            user.setRole(userRole);
            userRepository.save(user);
    }

    @Override
    public String getUserRole(String clerkId) {
        User user = userRepository.findByClerkUserId(clerkId)
                .orElseThrow(() -> AppException.notFound(
                        "User not found: " + clerkId
                ));

        return user.getRole().toString();
    }

    @Transactional
    public void updateOwnProfile(String clerkUserId, UserUpdateProfileRequestDTO request) {
        User user = userRepository.findByClerkUserId(clerkUserId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        user.setName(request.getName());
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        // no save() call needed if inside @Transactional + managed entity (dirty checking)
    }

    @Transactional
    public void assignShopAndStatus(UserAssignmentRequestDTO request) {
        User user = userRepository.findByClerkUserId(request.getClerkUserId())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        var actor = securityContextService.currentUser(); // must expose role() + shopId()

        if (request.getShopId() != null) {
            // ADMIN is tenant-scoped, cannot move users across shops. COMMANDER can.
            if (actor.role() == User.UserRole.ADMIN && !actor.shopId().equals(request.getShopId())) {
                throw new AppException("Cannot assign user to a different shop", HttpStatus.FORBIDDEN);
            }

            Shop shop = shopRepository.findByIdAndDeletedAtIsNull(request.getShopId())
                    .orElseThrow(() -> new AppException("Shop not found", HttpStatus.NOT_FOUND));
            user.setShop(shop);
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
    }
}
