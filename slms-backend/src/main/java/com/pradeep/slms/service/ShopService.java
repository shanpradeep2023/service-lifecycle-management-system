package com.pradeep.slms.service;

import com.pradeep.slms.dto.shop.ShopRequestDTO;
import com.pradeep.slms.dto.shop.ShopResponseDTO;

import java.util.List;

public interface ShopService {
    ShopResponseDTO createShop(ShopRequestDTO request);
    ShopResponseDTO getShop(Long id);
    List<ShopResponseDTO> getAllShops();
    ShopResponseDTO updateShop(Long id, ShopRequestDTO request);
    void deleteShop(Long id);
}
