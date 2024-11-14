package com.bhkim.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AuthDto {
    public static class Token {
        private String accessToken;
        private String refreshToken;
        private LocalDateTime createdTime;

        @Builder
        public Token(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.createdTime = LocalDateTime.now();
        }
    }
}
