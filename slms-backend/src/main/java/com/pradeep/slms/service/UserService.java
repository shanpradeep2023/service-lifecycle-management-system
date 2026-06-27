package com.pradeep.slms.service;

import com.pradeep.slms.dto.ClerkWebhookPayloadDTO;
import com.pradeep.slms.entity.User;
import com.pradeep.slms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void processClerkWebhook(ClerkWebhookPayloadDTO payload) {
        if (!"user.created".equals(payload.getType())) {
            log.info("Ignoring webhook type: {}", payload.getType());
            return;
        }

        ClerkWebhookPayloadDTO.ClerkUserData data = payload.getData();
        if (data == null || data.getId() == null) {
            log.warn("Invalid webhook payload received");
            return;
        }

        Optional<User> existingUser = userRepository.findByClerkUserId(data.getId());
        if (existingUser.isPresent()) {
            log.info("User already exists with clerkId: {}", data.getId());
            return;
        }

        String name = "";
        if (data.getFirstName() != null) name += data.getFirstName();
        if (data.getLastName() != null) name += " " + data.getLastName();
        name = name.trim();
        if (name.isEmpty()) name = "Unknown User";

        String email = null;
        if (data.getEmailAddresses() != null && !data.getEmailAddresses().isEmpty()) {
            email = data.getEmailAddresses().get(0).getEmailAddress();
        }

        User user = User.builder()
                .clerkUserId(data.getId())
                .name(name)
                .email(email)
                .role(User.UserRole.CUSTOMER) // Default role
                .status(User.UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
        log.info("Successfully saved new user from Clerk webhook: {}", data.getId());
    }
}
