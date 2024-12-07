package com.bhkim.auth.facade;

import com.bhkim.auth.exception.ApiException;
import com.bhkim.auth.security.CustomUserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.bhkim.auth.exception.ExceptionEnum.INVALID_TOKEN_VALUE_ERROR;

@Component
public class AuthFacade {
    public CustomUserDetail getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApiException(INVALID_TOKEN_VALUE_ERROR);
        }
        return (CustomUserDetail) authentication.getPrincipal();
    }

    public String getCurrentUserId() {
        return getCurrentUserDetails().getId();
    }
}
