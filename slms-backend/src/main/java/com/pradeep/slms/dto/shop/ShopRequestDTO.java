package com.pradeep.slms.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopRequestDTO {

    @NotBlank(message = "Shop name is required")
    @Size(max = 150, message = "Shop name must not exceed 150 characters")
    private String name;

    @Size(max = 2000, message = "Address must not exceed 2000 characters")
    private String address;
}