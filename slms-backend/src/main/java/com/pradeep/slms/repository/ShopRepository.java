package com.pradeep.slms.repository;

import com.pradeep.slms.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    Optional<Shop> findByIdAndDeletedAtIsNull(Long id);

    List<Shop> findAllByDeletedAtIsNull();

    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);
}