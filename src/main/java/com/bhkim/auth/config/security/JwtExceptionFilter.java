package com.bhkim.auth.config.security;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@Slf4j
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void setErrorResponse(HttpServletResponse res, ApiException e) throws IOException {
        res.setContentType("application/json; charset=UTF-8");

        ApiResponseResult<String> result = ApiResponseResult.failure(e.getException());
        String resultSrt = objectMapper.writeValueAsString(result);
        res.getWriter().write(resultSrt);
        res.flushBuffer();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        try {
            filterChain.doFilter(request, response); // go to 'JwtAuthenticationFilter'
        } catch (ApiException e) {
            log.error("ApiException : {}", e.getMessage());
            setErrorResponse(response, e);
        }
        log.info("exception filter end");
    }
}