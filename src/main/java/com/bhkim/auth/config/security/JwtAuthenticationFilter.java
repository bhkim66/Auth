package com.bhkim.auth.config.security;

import com.bhkim.auth.exception.ApiException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Optional;

import static com.bhkim.auth.common.ConstDef.GET_HEADER_ACCESS_TOKEN;
import static com.bhkim.auth.exception.ExceptionEnum.MEMBER_REQUIRED;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Request Header 에서 JWT 토큰 추출
        Optional<String> extractToken = extractToken(request);
        // header 출력
        printHeaderInfo(request);

        try {
            if(extractToken.isPresent()) {
                String token = extractToken.get();
                jwtTokenProvider.validateToken(token);
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException e) {
            log.error("e message : {} ", e.getMessage());
            throw new ApiException(MEMBER_REQUIRED);
        }
        filterChain.doFilter(request, response);
    }

    // 헤더 전체정보 보기
    private static void printHeaderInfo(HttpServletRequest request) {
        Enumeration<String> em = request.getHeaderNames();

        while(em.hasMoreElements()){
            String name = em.nextElement() ;
            String val = request.getHeader(name) ;

            log.info("header : {}", name);
            log.info("val : {}", val);
        }
    }

    private Optional<String> extractToken(ServletRequest request) {
        return Optional.ofNullable(((HttpServletRequest) request).getHeader(GET_HEADER_ACCESS_TOKEN));
    }
}
