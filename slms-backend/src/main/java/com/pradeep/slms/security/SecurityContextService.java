package com.pradeep.slms.security;

import com.pradeep.slms.exception.AppException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextService {

    public AuthenticatedUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof SlmsAuthenticationToken slmsAuthenticationToken) {
            return slmsAuthenticationToken.getPrincipal();
        }

        throw AppException.forbidden("Authenticated user context is unavailable");
    }

    public Long currentShopId() {
        Long shopId = currentUser().shopId();
        if (shopId == null) {
            throw AppException.forbidden("Authenticated user is not assigned to a shop");
        }
        return shopId;
    }
}
