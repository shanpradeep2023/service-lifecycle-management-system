package com.pradeep.slms.service.impl;

import com.pradeep.slms.dto.user.UserAssignmentRequestDTO;
import com.pradeep.slms.dto.user.UserResponseDTO;
import com.pradeep.slms.dto.user.UserUpdateProfileRequestDTO;
import com.pradeep.slms.dto.user.UserUpdateRequestDTO;
import com.pradeep.slms.entity.Shop;
import com.pradeep.slms.entity.User;
import com.pradeep.slms.exception.AppException;
import com.pradeep.slms.repository.ShopRepository;
import com.pradeep.slms.repository.UserRepository;
import com.pradeep.slms.security.SecurityContextService;
import com.pradeep.slms.service.UserService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<UserResponseDTO> getAllUsers(Long shopId, User.UserRole role) {
        var actor = securityContextService.currentUser();
        
        Specification<User> spec = (root, query, cb) -> {
            Predicate predicate = cb.isNull(root.get("deletedAt"));
            
            if (actor.role() == User.UserRole.ADMIN) {
                predicate = cb.and(predicate, cb.equal(root.join("shop").get("id"), actor.shopId()));
            } else if (shopId != null) {
                predicate = cb.and(predicate, cb.equal(root.join("shop").get("id"), shopId));
            }

            if (role != null) {
                predicate = cb.and(predicate, cb.equal(root.get("role"), role));
            }
            return predicate;
        };

        return userRepository.findAll(spec).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        var actor = securityContextService.currentUser();
        if (actor.role() == User.UserRole.ADMIN && (user.getShop() == null || !user.getShop().getId().equals(actor.shopId()))) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }

        return mapToResponseDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        var actor = securityContextService.currentUser();
        if (actor.role() == User.UserRole.ADMIN && (user.getShop() == null || !user.getShop().getId().equals(actor.shopId()))) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }

        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getStatus() != null) user.setStatus(request.getStatus());

        if (request.getShopId() != null) {
            if (actor.role() == User.UserRole.ADMIN && !actor.shopId().equals(request.getShopId())) {
                throw new AppException("Cannot change user shop", HttpStatus.FORBIDDEN);
            }
            Shop shop = shopRepository.findByIdAndDeletedAtIsNull(request.getShopId())
                    .orElseThrow(() -> new AppException("Shop not found", HttpStatus.NOT_FOUND));
            user.setShop(shop);
        }

        return mapToResponseDTO(user);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .clerkUserId(user.getClerkUserId())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .role(user.getRole())
                .shopId(user.getShop() != null ? user.getShop().getId() : null)
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
