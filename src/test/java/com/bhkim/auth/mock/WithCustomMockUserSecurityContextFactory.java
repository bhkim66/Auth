package com.bhkim.auth.mock;

import com.bhkim.auth.common.RoleEnum;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.security.CustomUserDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

import static com.bhkim.auth.common.RoleEnum.*;

public class WithCustomMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {
    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        String id = annotation.id();
        String role = annotation.role();
        CustomUserDetail userDetail = new CustomUserDetail(
                User.builder()
                        .id(id)
                        .role(USER)
                        .build());

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(userDetail, "password", List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return context;
    }
}
