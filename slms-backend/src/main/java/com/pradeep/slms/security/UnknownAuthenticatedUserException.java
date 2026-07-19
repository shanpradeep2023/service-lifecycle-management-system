package com.pradeep.slms.security;

import org.springframework.security.core.AuthenticationException;

public class UnknownAuthenticatedUserException extends AuthenticationException {

    public UnknownAuthenticatedUserException(String clerkUserId) {
        super("Authenticated Clerk user is not registered: " + clerkUserId);
    }
}
