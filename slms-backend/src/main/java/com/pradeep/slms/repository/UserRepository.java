package com.pradeep.slms.repository;

import com.pradeep.slms.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @EntityGraph(attributePaths = "shop")
    Optional<User> findByClerkUserId(String clerkUserId);

    @EntityGraph(attributePaths = "shop")
    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    User.UserRole getRoleByClerkUserId(String clerkUserId);

    long countByDeletedAtIsNull();
    long countByRoleAndDeletedAtIsNull(User.UserRole role);
    long countByShopIdAndDeletedAtIsNull(Long shopId);
    long countByShopIdAndRoleAndDeletedAtIsNull(Long shopId, User.UserRole role);
}
