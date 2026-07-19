package com.pradeep.slms.controller;

import com.pradeep.slms.dto.ApiResponse;
import com.pradeep.slms.dto.task.TaskAssignmentRequestDTO;
import com.pradeep.slms.dto.task.TaskRequestDTO;
import com.pradeep.slms.dto.task.TaskResponseDTO;
import com.pradeep.slms.dto.task.TaskUpdateRequestDTO;
import com.pradeep.slms.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMANDER')")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> createTask(@RequestBody @Valid TaskRequestDTO request) {
        log.info("Creating task");
        TaskResponseDTO response = taskService.createTask(request);
        return ResponseEntity.ok(ApiResponse.ok("Task created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> getTask(@PathVariable Long id) {
        log.info("Fetching task {}", id);
        TaskResponseDTO response = taskService.getTask(id);
        return ResponseEntity.ok(ApiResponse.ok("Task fetched successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMMANDER', 'ADMIN', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<List<TaskResponseDTO>>> getAllTasks(
            @RequestParam(required = false) Long shopId
    ) {
        log.info("Fetching tasks (shopId filter={})", shopId);
        List<TaskResponseDTO> response = taskService.getAllTasks(shopId);
        return ResponseEntity.ok(ApiResponse.ok("Tasks fetched successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMANDER')")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> updateTask(
            @PathVariable Long id,
            @RequestBody @Valid TaskUpdateRequestDTO request
    ) {
        log.info("Updating task {}", id);
        TaskResponseDTO response = taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Task updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMANDER')")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        log.info("Deleting task {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.ok("Task deleted successfully"));
    }

    @PatchMapping("/{id}/assign-technician")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMANDER')")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> assignTaskToTechnician(
            @PathVariable Long id,
            @RequestBody @Valid TaskAssignmentRequestDTO request
    ) {
        log.info("Assigning task {} to technician {}", id, request.getTechnicianId());
        TaskResponseDTO response = taskService.assignTaskToTechnician(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Task assigned successfully", response));
    }
}
