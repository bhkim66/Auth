package com.bhkim.auth.config;

import com.bhkim.auth.config.security.JwtAuthenticationFilter;
import com.bhkim.auth.config.security.JwtExceptionFilter;
import com.bhkim.auth.config.security.JwtTokenProvider;
import com.bhkim.auth.config.security.WebSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class TestSecurityConfiguration {
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, JwtExceptionFilter exceptionFilter) throws Exception {
        final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(null, null);
        final JwtAuthenticationFilter jwtAuthFilterForTest = new JwtAuthenticationFilter(jwtTokenProvider);

        return new WebSecurityConfig(
                jwtAuthFilterForTest,
                exceptionFilter)
                .securityFilterChain(http);
    }
}
