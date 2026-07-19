package com.pradeep.slms.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskAssignmentRequestDTO {

    @NotNull(message = "Technician id is required")
    private Long technicianId;
}
