package com.pradeep.slms.repository;

import com.pradeep.slms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByClerkUserId(String clerkUserId);

    User.UserRole getRoleByClerkUserId(String clerkUserId);
}
