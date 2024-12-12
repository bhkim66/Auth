package com.bhkim.auth.config.security;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

import static com.bhkim.auth.exception.ExceptionEnum.UNAUTHORIZED_EXCEPTION;
import static jakarta.servlet.http.HttpServletResponse.*;

/**
 * 인증되지 않는 요청 오류 처리
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        setErrorResponse(response, authException);
    }

    private void setErrorResponse(HttpServletResponse res, RuntimeException e) throws IOException {
        ApiResponseResult<String> result = ApiResponseResult.failure(UNAUTHORIZED_EXCEPTION);
        ObjectMapper objectMapper = new ObjectMapper();
        String resultSrt = objectMapper.writeValueAsString(result);

        res.setContentType("application/json; charset=UTF-8");
        res.getWriter().write(resultSrt);
        res.setStatus(SC_UNAUTHORIZED);
        res.flushBuffer();
    }
}
