package com.pradeep.slms.dto.shop;

import com.pradeep.slms.entity.Shop;
import lombok.Builder;
import java.time.OffsetDateTime;

@Builder
public record ShopResponseDTO(
        Long id,
        String name,
        String address,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static ShopResponseDTO from(Shop shop) {
        return ShopResponseDTO.builder()
                .id(shop.getId())
                .name(shop.getName())
                .address(shop.getAddress())
                .createdAt(shop.getCreatedAt())
                .updatedAt(shop.getUpdatedAt())
                .build();
    }
}