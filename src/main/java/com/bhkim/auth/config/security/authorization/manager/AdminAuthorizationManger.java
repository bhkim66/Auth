package com.bhkim.auth.config.security.authorization.manager;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

import static com.bhkim.auth.common.RoleEnum.ADMIN;

@Component
@NoArgsConstructor
public class AdminAuthorizationManger implements AuthorizationManager<HttpServletRequest> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, HttpServletRequest object) {
        Authentication auth = authentication.get();

        if(auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        return new AuthorizationDecision(auth.getAuthorities()
                .stream()
                .anyMatch(grant -> grant.getAuthority().equals(ADMIN.getValue())));
    }
}
