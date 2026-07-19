package com.pradeep.slms.dto.task;

import com.pradeep.slms.entity.ServiceRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusUpdateRequestDTO {

    @NotNull(message = "Status is required")
    private ServiceRequest.RequestStatus status;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    private String notes;
}
