package com.pradeep.slms.service.impl;



import com.pradeep.slms.dto.shop.ShopRequestDTO;
import com.pradeep.slms.dto.shop.ShopResponseDTO;
import com.pradeep.slms.entity.Shop;
import com.pradeep.slms.exception.AppException;
import com.pradeep.slms.repository.ShopRepository;
import com.pradeep.slms.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;

    @Transactional
    public ShopResponseDTO createShop(ShopRequestDTO request) {
        if (shopRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(request.getName())) {
            throw new AppException("Shop with this name already exists", HttpStatus.CONFLICT);
        }

        Shop shop = Shop.builder()
                .name(request.getName())
                .address(request.getAddress())
                .build();

        return ShopResponseDTO.from(shopRepository.save(shop));
    }

    @Transactional(readOnly = true)
    public ShopResponseDTO getShop(Long id) {
        Shop shop = shopRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException("Shop not found", HttpStatus.NOT_FOUND));
        return ShopResponseDTO.from(shop);
    }

    @Transactional(readOnly = true)
    public List<ShopResponseDTO> getAllShops() {
        return shopRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(ShopResponseDTO::from)
                .toList();
    }

    @Transactional
    public ShopResponseDTO updateShop(Long id, ShopRequestDTO request) {
        Shop shop = shopRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException("Shop not found", HttpStatus.NOT_FOUND));

        if (!shop.getName().equalsIgnoreCase(request.getName())
                && shopRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(request.getName())) {
            throw new AppException("Shop with this name already exists", HttpStatus.CONFLICT);
        }

        shop.setName(request.getName());
        shop.setAddress(request.getAddress());

        return ShopResponseDTO.from(shop);
    }

    @Transactional
    public void deleteShop(Long id) {
        Shop shop = shopRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException("Shop not found", HttpStatus.NOT_FOUND));

        shop.setDeletedAt(OffsetDateTime.now());
    }
}
