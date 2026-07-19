package com.pradeep.slms.dto.user;

import com.pradeep.slms.entity.User.UserRole;
import com.pradeep.slms.entity.User.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequestDTO {

    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;

    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone must be in E.164 format")
    private String phone;

    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    private UserRole role;
    private Long shopId;
    private UserStatus status;
}
