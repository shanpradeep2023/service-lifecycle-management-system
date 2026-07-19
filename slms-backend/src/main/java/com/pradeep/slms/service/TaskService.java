package com.pradeep.slms.service;

import com.pradeep.slms.dto.task.TaskAssignmentRequestDTO;
import com.pradeep.slms.dto.task.TaskRequestDTO;
import com.pradeep.slms.dto.task.TaskResponseDTO;
import com.pradeep.slms.dto.task.TaskUpdateRequestDTO;

import java.util.List;

public interface TaskService {
    TaskResponseDTO createTask(TaskRequestDTO request);
    TaskResponseDTO getTask(Long id);
    List<TaskResponseDTO> getAllTasks(Long shopId);
    TaskResponseDTO updateTask(Long id, TaskUpdateRequestDTO request);
    void deleteTask(Long id);
    TaskResponseDTO assignTaskToTechnician(Long id, TaskAssignmentRequestDTO request);
}
