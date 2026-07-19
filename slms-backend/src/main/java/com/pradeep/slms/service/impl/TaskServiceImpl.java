package com.pradeep.slms.service.impl;

import com.pradeep.slms.dto.task.TaskAssignmentRequestDTO;
import com.pradeep.slms.dto.task.TaskRequestDTO;
import com.pradeep.slms.dto.task.TaskResponseDTO;
import com.pradeep.slms.dto.task.TaskUpdateRequestDTO;
import com.pradeep.slms.entity.RequestAssignment;
import com.pradeep.slms.entity.RequestStatusHistory;
import com.pradeep.slms.entity.ServiceRequest;
import com.pradeep.slms.entity.Shop;
import com.pradeep.slms.entity.User;
import com.pradeep.slms.exception.AppException;
import com.pradeep.slms.repository.RequestAssignmentRepository;
import com.pradeep.slms.repository.RequestStatusHistoryRepository;
import com.pradeep.slms.repository.ServiceRequestRepository;
import com.pradeep.slms.repository.ShopRepository;
import com.pradeep.slms.repository.UserRepository;
import com.pradeep.slms.security.AuthenticatedUser;
import com.pradeep.slms.security.SecurityContextService;
import com.pradeep.slms.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final RequestAssignmentRepository requestAssignmentRepository;
    private final RequestStatusHistoryRepository requestStatusHistoryRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final SecurityContextService securityContextService;

    @Override
    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO request) {
        AuthenticatedUser actor = securityContextService.currentUser();
        Shop shop = resolveWritableShop(request.getShopId(), actor);
        User createdBy = getCurrentUser(actor);

        ServiceRequest task = ServiceRequest.builder()
                .shop(shop)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .issueType(request.getIssueType())
                .description(request.getDescription())
                .priority(request.getPriority() == null ? ServiceRequest.Priority.NORMAL : request.getPriority())
                .status(ServiceRequest.RequestStatus.CREATED)
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();

        ServiceRequest savedTask = serviceRequestRepository.save(task);
        saveStatusHistory(savedTask, null, savedTask.getStatus(), createdBy, "Task created");
        return toResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDTO getTask(Long id) {
        return toResponse(getVisibleTask(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getAllTasks() {
        AuthenticatedUser actor = securityContextService.currentUser();
        List<ServiceRequest> tasks = actor.role() == User.UserRole.COMMANDER
                ? serviceRequestRepository.findAllByDeletedAtIsNull()
                : serviceRequestRepository.findAllByShopIdAndDeletedAtIsNull(securityContextService.currentShopId());

        return tasks.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TaskResponseDTO updateTask(Long id, TaskUpdateRequestDTO request) {
        ServiceRequest task = getVisibleTask(id);
        User actor = getCurrentUser(securityContextService.currentUser());
        ServiceRequest.RequestStatus oldStatus = task.getStatus();

        task.setCustomerName(request.getCustomerName());
        task.setCustomerPhone(request.getCustomerPhone());
        task.setAddress(request.getAddress());
        task.setLatitude(request.getLatitude());
        task.setLongitude(request.getLongitude());
        task.setIssueType(request.getIssueType());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority() == null ? ServiceRequest.Priority.NORMAL : request.getPriority());
        task.setUpdatedBy(actor);

        if (request.getStatus() != null && request.getStatus() != oldStatus) {
            task.setStatus(request.getStatus());
            saveStatusHistory(task, oldStatus, request.getStatus(), actor, "Task status updated");
        }

        return toResponse(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        ServiceRequest task = getVisibleTask(id);
        User actor = getCurrentUser(securityContextService.currentUser());

        requestAssignmentRepository.findAllByRequestIdAndIsCurrentTrue(task.getId())
                .forEach(assignment -> {
                    assignment.setIsCurrent(false);
                    assignment.setUnassignedAt(OffsetDateTime.now());
                });

        task.setDeletedAt(OffsetDateTime.now());
        task.setUpdatedBy(actor);
    }

    @Override
    @Transactional
    public TaskResponseDTO assignTaskToTechnician(Long id, TaskAssignmentRequestDTO request) {
        ServiceRequest task = getVisibleTask(id);
        User actor = getCurrentUser(securityContextService.currentUser());
        User technician = userRepository.findByIdAndDeletedAtIsNull(request.getTechnicianId())
                .orElseThrow(() -> AppException.notFound("Technician not found"));

        if (technician.getRole() != User.UserRole.TECHNICIAN) {
            throw AppException.badRequest("Selected user is not a technician");
        }
        if (technician.getStatus() != User.UserStatus.ACTIVE) {
            throw AppException.badRequest("Technician is not active");
        }
        if (technician.getShop() == null || !technician.getShop().getId().equals(task.getShop().getId())) {
            throw AppException.forbidden("Technician must belong to the task shop");
        }

        requestAssignmentRepository.findAllByRequestIdAndIsCurrentTrue(task.getId())
                .forEach(assignment -> {
                    assignment.setIsCurrent(false);
                    assignment.setUnassignedAt(OffsetDateTime.now());
                });

        RequestAssignment assignment = RequestAssignment.builder()
                .shop(task.getShop())
                .request(task)
                .worker(technician)
                .assignedBy(actor)
                .isCurrent(true)
                .build();
        RequestAssignment savedAssignment = requestAssignmentRepository.save(assignment);

        if (task.getStatus() == ServiceRequest.RequestStatus.CREATED) {
            ServiceRequest.RequestStatus oldStatus = task.getStatus();
            task.setStatus(ServiceRequest.RequestStatus.ASSIGNED);
            saveStatusHistory(task, oldStatus, ServiceRequest.RequestStatus.ASSIGNED, actor, "Task assigned to technician");
        }
        task.setUpdatedBy(actor);

        return TaskResponseDTO.from(task, savedAssignment);
    }

    private ServiceRequest getVisibleTask(Long id) {
        AuthenticatedUser actor = securityContextService.currentUser();
        if (actor.role() == User.UserRole.COMMANDER) {
            return serviceRequestRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> AppException.notFound("Task not found"));
        }

        return serviceRequestRepository.findByIdAndShopIdAndDeletedAtIsNull(id, securityContextService.currentShopId())
                .orElseThrow(() -> AppException.notFound("Task not found"));
    }

    private Shop resolveWritableShop(Long requestedShopId, AuthenticatedUser actor) {
        Long shopId = actor.role() == User.UserRole.COMMANDER ? requestedShopId : securityContextService.currentShopId();
        if (shopId == null) {
            throw AppException.badRequest("Shop id is required");
        }
        if (actor.role() != User.UserRole.COMMANDER && requestedShopId != null && !requestedShopId.equals(shopId)) {
            throw AppException.forbidden("Cannot create task for a different shop");
        }

        return shopRepository.findByIdAndDeletedAtIsNull(shopId)
                .orElseThrow(() -> AppException.notFound("Shop not found"));
    }

    private User getCurrentUser(AuthenticatedUser actor) {
        return userRepository.findByIdAndDeletedAtIsNull(actor.id())
                .orElseThrow(() -> AppException.notFound("Current user not found"));
    }

    private TaskResponseDTO toResponse(ServiceRequest task) {
        RequestAssignment assignment = requestAssignmentRepository.findByRequestIdAndIsCurrentTrue(task.getId())
                .orElse(null);
        return TaskResponseDTO.from(task, assignment);
    }

    private void saveStatusHistory(
            ServiceRequest task,
            ServiceRequest.RequestStatus oldStatus,
            ServiceRequest.RequestStatus newStatus,
            User actor,
            String notes
    ) {
        RequestStatusHistory history = RequestStatusHistory.builder()
                .shop(task.getShop())
                .request(task)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(actor)
                .notes(notes)
                .build();
        requestStatusHistoryRepository.save(history);
    }
}
