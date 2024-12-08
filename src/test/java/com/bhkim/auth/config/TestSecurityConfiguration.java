package com.bhkim.auth.config;

//@Configuration
//public class TestSecurityConfiguration {
//    @Bean
//    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, JwtExceptionFilter exceptionFilter) throws Exception {
//        final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(null, null);
//        final JwtAuthenticationFilter jwtAuthFilterForTest = new JwtAuthenticationFilter(jwtTokenProvider);
//
//        return new WebSecurityConfig(
//                jwtAuthFilterForTest,
//                exceptionFilter)
//                .securityFilterChain(http);
//    }
//}
