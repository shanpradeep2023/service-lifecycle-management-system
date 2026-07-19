package com.pradeep.slms.service.impl;

import com.pradeep.slms.dto.dashboard.AdminDashboardResponseDTO;
import com.pradeep.slms.dto.dashboard.DashboardResponseDTO;
import com.pradeep.slms.dto.dashboard.ShopSummaryDTO;
import com.pradeep.slms.dto.dashboard.UserTaskSummaryDTO;
import com.pradeep.slms.dto.task.TaskResponseDTO;
import com.pradeep.slms.dto.user.UserResponseDTO;
import com.pradeep.slms.entity.RequestAssignment;
import com.pradeep.slms.entity.ServiceRequest;
import com.pradeep.slms.entity.Shop;
import com.pradeep.slms.entity.User;
import com.pradeep.slms.exception.AppException;
import com.pradeep.slms.repository.RequestAssignmentRepository;
import com.pradeep.slms.repository.ServiceRequestRepository;
import com.pradeep.slms.repository.ShopRepository;
import com.pradeep.slms.repository.UserRepository;
import com.pradeep.slms.security.AuthenticatedUser;
import com.pradeep.slms.security.SecurityContextService;
import com.pradeep.slms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final RequestAssignmentRepository requestAssignmentRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final SecurityContextService securityContextService;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponseDTO getCommanderDashboard() {
        AuthenticatedUser actor = securityContextService.currentUser();
        if (actor.role() != User.UserRole.COMMANDER) {
            throw AppException.forbidden("Only commanders can access the global dashboard");
        }

        Map<ServiceRequest.RequestStatus, Long> tasksByStatus = new EnumMap<>(ServiceRequest.RequestStatus.class);
        for (ServiceRequest.RequestStatus status : ServiceRequest.RequestStatus.values()) {
            tasksByStatus.put(status, serviceRequestRepository.countByStatusAndDeletedAtIsNull(status));
        }

        Map<ServiceRequest.Priority, Long> tasksByPriority = new EnumMap<>(ServiceRequest.Priority.class);
        for (ServiceRequest.Priority priority : ServiceRequest.Priority.values()) {
            tasksByPriority.put(priority, serviceRequestRepository.countByPriorityAndDeletedAtIsNull(priority));
        }

        Map<User.UserRole, Long> usersByRole = new EnumMap<>(User.UserRole.class);
        for (User.UserRole role : User.UserRole.values()) {
            usersByRole.put(role, userRepository.countByRoleAndDeletedAtIsNull(role));
        }

        List<Shop> shops = shopRepository.findAllByDeletedAtIsNull();
        List<ShopSummaryDTO> shopSummaries = shops.stream().map(this::buildShopSummary).toList();

        List<ServiceRequest> recentTasksEntities = serviceRequestRepository.findAllByDeletedAtIsNull()
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
                .toList();
        
        List<TaskResponseDTO> recentTasks = recentTasksEntities.stream()
                .map(this::toResponse)
                .toList();

        return DashboardResponseDTO.builder()
                .totalTasks(serviceRequestRepository.countByDeletedAtIsNull())
                .tasksByStatus(tasksByStatus)
                .tasksByPriority(tasksByPriority)
                .totalShops(shops.size())
                .totalUsers(userRepository.countByDeletedAtIsNull())
                .usersByRole(usersByRole)
                .shopSummaries(shopSummaries)
                .recentTasks(recentTasks)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponseDTO getAdminDashboard() {
        AuthenticatedUser actor = securityContextService.currentUser();
        if (actor.role() != User.UserRole.ADMIN && actor.role() != User.UserRole.COMMANDER) {
            throw AppException.forbidden("Only admins and commanders can access the admin dashboard");
        }

        Long shopId = securityContextService.currentShopId();
        Shop shop = shopRepository.findByIdAndDeletedAtIsNull(shopId)
                .orElseThrow(() -> AppException.notFound("Shop not found"));

        Map<ServiceRequest.RequestStatus, Long> tasksByStatus = new EnumMap<>(ServiceRequest.RequestStatus.class);
        for (ServiceRequest.RequestStatus status : ServiceRequest.RequestStatus.values()) {
            tasksByStatus.put(status, serviceRequestRepository.countByShopIdAndStatusAndDeletedAtIsNull(shopId, status));
        }

        Map<ServiceRequest.Priority, Long> tasksByPriority = new EnumMap<>(ServiceRequest.Priority.class);
        for (ServiceRequest.Priority priority : ServiceRequest.Priority.values()) {
            tasksByPriority.put(priority, serviceRequestRepository.countByShopIdAndPriorityAndDeletedAtIsNull(shopId, priority));
        }

        Map<String, Long> usersByRole = new EnumMap<>(User.UserRole.class).entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), e -> userRepository.countByShopIdAndRoleAndDeletedAtIsNull(shopId, e.getKey())));

        List<ServiceRequest> recentTasksEntities = serviceRequestRepository.findAllByShopIdAndDeletedAtIsNull(shopId)
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
                .toList();
        
        List<TaskResponseDTO> recentTasks = recentTasksEntities.stream()
                .map(this::toResponse)
                .toList();

        return AdminDashboardResponseDTO.builder()
                .shopId(shop.getId())
                .shopName(shop.getName())
                .totalTasks(serviceRequestRepository.countByShopIdAndDeletedAtIsNull(shopId))
                .tasksByStatus(tasksByStatus)
                .tasksByPriority(tasksByPriority)
                .totalUsers(userRepository.countByShopIdAndDeletedAtIsNull(shopId))
                .usersByRole(usersByRole)
                .recentTasks(recentTasks)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserTaskSummaryDTO getUserTaskSummary(Long userId) {
        AuthenticatedUser actor = securityContextService.currentUser();
        User targetUser = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> AppException.notFound("User not found"));

        if (actor.role() == User.UserRole.ADMIN) {
            if (targetUser.getShop() == null || !targetUser.getShop().getId().equals(actor.shopId())) {
                throw AppException.forbidden("Cannot view users outside your shop");
            }
        } else if (actor.role() != User.UserRole.COMMANDER) {
            throw AppException.forbidden("Access denied");
        }

        UserResponseDTO userResponse = UserResponseDTO.builder()
                .id(targetUser.getId())
                .clerkUserId(targetUser.getClerkUserId())
                .name(targetUser.getName())
                .phone(targetUser.getPhone())
                .email(targetUser.getEmail())
                .role(targetUser.getRole())
                .shopId(targetUser.getShop() != null ? targetUser.getShop().getId() : null)
                .status(targetUser.getStatus())
                .createdAt(targetUser.getCreatedAt())
                .build();

        long totalTasks = 0;
        Map<ServiceRequest.RequestStatus, Long> tasksByStatus = new EnumMap<>(ServiceRequest.RequestStatus.class);
        List<TaskResponseDTO> tasks;

        if (targetUser.getRole() == User.UserRole.TECHNICIAN) {
            List<RequestAssignment> assignments = requestAssignmentRepository.findAllByWorkerId(targetUser.getId());
            totalTasks = assignments.size();
            
            for (ServiceRequest.RequestStatus status : ServiceRequest.RequestStatus.values()) {
                tasksByStatus.put(status, requestAssignmentRepository.countByWorkerIdAndRequest_Status(targetUser.getId(), status));
            }
            
            tasks = assignments.stream()
                    .map(a -> TaskResponseDTO.from(a.getRequest(), a))
                    .toList();
        } else {
            List<ServiceRequest> createdTasks = serviceRequestRepository.findAllByCreatedByIdAndDeletedAtIsNull(targetUser.getId());
            totalTasks = createdTasks.size();

            for (ServiceRequest.RequestStatus status : ServiceRequest.RequestStatus.values()) {
                tasksByStatus.put(status, serviceRequestRepository.countByCreatedByIdAndStatusAndDeletedAtIsNull(targetUser.getId(), status));
            }

            tasks = createdTasks.stream()
                    .map(this::toResponse)
                    .toList();
        }

        return UserTaskSummaryDTO.builder()
                .user(userResponse)
                .totalTasks(totalTasks)
                .tasksByStatus(tasksByStatus)
                .tasks(tasks)
                .build();
    }

    private ShopSummaryDTO buildShopSummary(Shop shop) {
        Long shopId = shop.getId();
        Map<ServiceRequest.RequestStatus, Long> tasksByStatus = new EnumMap<>(ServiceRequest.RequestStatus.class);
        for (ServiceRequest.RequestStatus status : ServiceRequest.RequestStatus.values()) {
            tasksByStatus.put(status, serviceRequestRepository.countByShopIdAndStatusAndDeletedAtIsNull(shopId, status));
        }

        return ShopSummaryDTO.builder()
                .shopId(shop.getId())
                .shopName(shop.getName())
                .totalTasks(serviceRequestRepository.countByShopIdAndDeletedAtIsNull(shopId))
                .tasksByStatus(tasksByStatus)
                .totalUsers(userRepository.countByShopIdAndDeletedAtIsNull(shopId))
                .totalTechnicians(userRepository.countByShopIdAndRoleAndDeletedAtIsNull(shopId, User.UserRole.TECHNICIAN))
                .totalAdmins(userRepository.countByShopIdAndRoleAndDeletedAtIsNull(shopId, User.UserRole.ADMIN))
                .build();
    }

    private TaskResponseDTO toResponse(ServiceRequest task) {
        RequestAssignment assignment = requestAssignmentRepository.findByRequestIdAndIsCurrentTrue(task.getId())
                .orElse(null);
        return TaskResponseDTO.from(task, assignment);
    }
}
