package com.pradeep.slms.controller;

import com.pradeep.slms.dto.ClerkWebhookPayloadDTO;
import com.pradeep.slms.service.UserService;
import com.svix.Webhook;
import com.svix.exceptions.WebhookVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Value("${clerk.webhook.secret}")
    private String clerkWebhookSecret;

    @PostMapping("/clerk")
    public ResponseEntity<String> handleClerkWebhook(HttpServletRequest request) {
        try {
            // Read headers required by Svix
            String svixId = request.getHeader("svix-id");
            String svixTimestamp = request.getHeader("svix-timestamp");
            String svixSignature = request.getHeader("svix-signature");

            if (svixId == null || svixTimestamp == null || svixSignature == null) {
                log.warn("Missing svix headers");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing svix headers");
            }

            // Read the raw body
            String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            // Construct headers map for Svix
            java.util.Map<String, java.util.List<String>> headersMap = new java.util.HashMap<>();
            headersMap.put("svix-id", java.util.Collections.singletonList(svixId));
            headersMap.put("svix-timestamp", java.util.Collections.singletonList(svixTimestamp));
            headersMap.put("svix-signature", java.util.Collections.singletonList(svixSignature));

            java.net.http.HttpHeaders httpHeaders = java.net.http.HttpHeaders.of(headersMap, (k, v) -> true);

            // Verify signature
            Webhook webhook = new Webhook(clerkWebhookSecret);
            webhook.verify(payload, httpHeaders);

            // If verification succeeds, parse and process the payload
            ClerkWebhookPayloadDTO clerkPayload = objectMapper.readValue(payload, ClerkWebhookPayloadDTO.class);
            userService.processClerkWebhook(clerkPayload);

            return ResponseEntity.ok("Webhook processed successfully");

        } catch (WebhookVerificationException e) {
            log.error("Webhook signature verification failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        } catch (IOException e) {
            log.error("Error reading webhook payload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading payload");
        } catch (Exception e) {
            log.error("Unexpected error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}
