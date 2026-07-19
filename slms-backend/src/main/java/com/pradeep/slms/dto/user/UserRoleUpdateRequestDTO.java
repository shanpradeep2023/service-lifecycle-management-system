package com.pradeep.slms.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleUpdateRequestDTO {

    @NotBlank
    @NotNull
    String clerkId;
    String role;
}
