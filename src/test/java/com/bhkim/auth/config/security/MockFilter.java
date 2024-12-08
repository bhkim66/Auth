package com.bhkim.auth.config.security;

import com.bhkim.auth.common.RoleEnum;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.security.CustomUserDetail;
import com.bhkim.auth.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class MockFilter extends OncePerRequestFilter {
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CustomUserDetail userDetail = new CustomUserDetail(
                User.builder()
                        .id("bhkim62")
                        .role(RoleEnum.USER)
                        .build()
        );
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());

        // SecurityContextHolder에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
