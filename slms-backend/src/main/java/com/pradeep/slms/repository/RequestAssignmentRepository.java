package com.pradeep.slms.repository;

import com.pradeep.slms.entity.RequestAssignment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestAssignmentRepository extends JpaRepository<RequestAssignment, Long> {

    @EntityGraph(attributePaths = {"worker"})
    Optional<RequestAssignment> findByRequestIdAndIsCurrentTrue(Long requestId);

    @EntityGraph(attributePaths = {"worker"})
    List<RequestAssignment> findAllByRequestIdAndIsCurrentTrue(Long requestId);

    @EntityGraph(attributePaths = {"worker", "request", "request.shop", "request.createdBy", "request.updatedBy", "request.customerUser"})
    List<RequestAssignment> findAllByWorkerIdAndIsCurrentTrue(Long workerId);

    @EntityGraph(attributePaths = {"worker", "request", "request.shop", "request.createdBy", "request.updatedBy", "request.customerUser"})
    List<RequestAssignment> findAllByWorkerId(Long workerId);

    long countByWorkerIdAndIsCurrentTrue(Long workerId);

    long countByWorkerIdAndRequest_Status(Long workerId, com.pradeep.slms.entity.ServiceRequest.RequestStatus status);

    boolean existsByRequestIdAndWorkerIdAndIsCurrentTrue(Long requestId, Long workerId);
}
