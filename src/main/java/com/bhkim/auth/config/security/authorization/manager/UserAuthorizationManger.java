package com.bhkim.auth.config.security.authorization.manager;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

import static com.bhkim.auth.common.RoleEnum.USER;

@Component
public class UserAuthorizationManger implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        Authentication auth = authentication.get();

        if(auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        return new AuthorizationDecision(auth.getAuthorities()
                .stream()
                .anyMatch(grant -> grant.getAuthority().equals(USER.getValue())));
    }
}
