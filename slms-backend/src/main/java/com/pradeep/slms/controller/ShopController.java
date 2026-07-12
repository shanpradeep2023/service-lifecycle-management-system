package com.pradeep.slms.controller;

import com.pradeep.slms.dto.ApiResponse;
import com.pradeep.slms.dto.shop.ShopRequestDTO;
import com.pradeep.slms.dto.shop.ShopResponseDTO;
import com.pradeep.slms.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
@Slf4j
public class ShopController {

    private final ShopService shopService;


    @PostMapping("/create-shop")
    @PreAuthorize("hasAnyRole('COMMANDER')")
    public ResponseEntity<ApiResponse<ShopResponseDTO>> createShop(@RequestBody @Valid ShopRequestDTO request) {
        log.info("URL : /api/shops/create-shop");
        log.info("starting creation of shop | request data : {}", request);
        ShopResponseDTO shopResponseDTO = shopService.createShop(request);
        log.info("finish creation of shop | shopResponseDTO : {}", shopResponseDTO);
        return ResponseEntity.ok(ApiResponse.ok("Shop created successfully", shopResponseDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShopResponseDTO>> getShop(@PathVariable String id) {
        log.info("URL : /api/shops/{}",id);
        log.info("starting getting shop | request data : {}", id);
        ShopResponseDTO shopResponseDTO = shopService.getShop(Long.valueOf(id));
        log.info("finished getting shop | shopResponseDTO : {}", shopResponseDTO);
        return ResponseEntity.ok(ApiResponse.ok("Shop found successfully", shopResponseDTO));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMMANDER')")
    public ResponseEntity<ApiResponse<List<ShopResponseDTO>>> getAllShops() {
        log.info("URL : /api/shops");
        log.info("starting fetching all shops");
        List<ShopResponseDTO> shopResponseDTOS = shopService.getAllShops();
        log.info("finished fetching all shops | shopResponseDTOS : {}", shopResponseDTOS);
        return ResponseEntity.ok(ApiResponse.ok("All Shops fetched successfully", shopResponseDTOS));

    }

    @PutMapping("/update-shop/{id}")
    @PreAuthorize("hasAnyRole('COMMANDER')")
    public ResponseEntity<ApiResponse<ShopResponseDTO>> updateShop(@PathVariable String id, @RequestBody @Valid ShopRequestDTO request) {
        log.info("URL : /api/shops/update-shop");
        log.info("starting updating shop | request data : {}", request);
        ShopResponseDTO shopResponseDTO = shopService.updateShop(Long.valueOf(id), request);
        log.info("finish updating shop | shopResponseDTO : {}", shopResponseDTO);
        return ResponseEntity.ok(ApiResponse.ok("Shop updated successfully", shopResponseDTO));
    }

    @DeleteMapping("/delete-shop/{id}")
    @PreAuthorize("hasAnyRole('COMMANDER')")
    public ResponseEntity<ApiResponse<Void>> deleteShop(@PathVariable String id) {
        log.info("URL : /api/shops/delete-shop");
        log.info("starting deleting shop | shop ID : {}", id);
        shopService.deleteShop(Long.valueOf(id));
        log.info("finished deleting shop | shop ID : {}", id);
        return ResponseEntity.ok(ApiResponse.ok("Shop deleted successfully"));
    }
}
