package com.pradeep.slms.repository;

import com.pradeep.slms.entity.ServiceRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    @EntityGraph(attributePaths = {"shop", "createdBy", "updatedBy", "customerUser"})
    Optional<ServiceRequest> findByIdAndDeletedAtIsNull(Long id);

    @EntityGraph(attributePaths = {"shop", "createdBy", "updatedBy", "customerUser"})
    Optional<ServiceRequest> findByIdAndShopIdAndDeletedAtIsNull(Long id, Long shopId);

    @EntityGraph(attributePaths = {"shop", "createdBy", "updatedBy", "customerUser"})
    List<ServiceRequest> findAllByDeletedAtIsNull();

    @EntityGraph(attributePaths = {"shop", "createdBy", "updatedBy", "customerUser"})
    List<ServiceRequest> findAllByShopIdAndDeletedAtIsNull(Long shopId);

    long countByStatusAndDeletedAtIsNull(ServiceRequest.RequestStatus status);
    long countByShopIdAndStatusAndDeletedAtIsNull(Long shopId, ServiceRequest.RequestStatus status);
    
    long countByPriorityAndDeletedAtIsNull(ServiceRequest.Priority priority);
    long countByShopIdAndPriorityAndDeletedAtIsNull(Long shopId, ServiceRequest.Priority priority);

    long countByDeletedAtIsNull();
    long countByShopIdAndDeletedAtIsNull(Long shopId);

    @EntityGraph(attributePaths = {"shop", "createdBy", "updatedBy", "customerUser"})
    List<ServiceRequest> findAllByCreatedByIdAndDeletedAtIsNull(Long userId);

    long countByCreatedByIdAndDeletedAtIsNull(Long userId);
    long countByCreatedByIdAndStatusAndDeletedAtIsNull(Long userId, ServiceRequest.RequestStatus status);
}
