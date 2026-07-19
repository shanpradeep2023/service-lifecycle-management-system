package com.pradeep.slms.dto.task;

import com.pradeep.slms.entity.ServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequestDTO {

    private Long shopId;

    @NotBlank(message = "Customer name is required")
    @Size(max = 150, message = "Customer name must not exceed 150 characters")
    private String customerName;

    @NotBlank(message = "Customer phone is required")
    @Size(max = 20, message = "Customer phone must not exceed 20 characters")
    private String customerPhone;

    @Size(max = 2000, message = "Address must not exceed 2000 characters")
    private String address;

    private Double latitude;
    private Double longitude;
    private ServiceRequest.IssueType issueType;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    private ServiceRequest.Priority priority;
}
