package com.pradeep.slms.security;

import com.pradeep.slms.entity.User;
import com.pradeep.slms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DatabaseJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRepository userRepository;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String clerkUserId = resolveClerkUserId(jwt);

        User user = userRepository.findByClerkUserId(clerkUserId)
                .orElseThrow(() -> new UnknownAuthenticatedUserException(clerkUserId));

        Long shopId = Optional.ofNullable(user.getShop())
                .map(shop -> shop.getId())
                .orElse(null);

        AuthenticatedUser principal = new AuthenticatedUser(
                user.getId(),
                user.getClerkUserId(),
                user.getRole(),
                shopId
        );

        return new SlmsAuthenticationToken(
                principal,
                jwt,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    private String resolveClerkUserId(Jwt jwt) {
        String userIdClaim = jwt.getClaimAsString("user_id");
        if (userIdClaim != null && !userIdClaim.isBlank()) {
            return userIdClaim;
        }
        return jwt.getSubject();
    }
}
