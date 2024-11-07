package com.bhkim.auth.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Token {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime publishTime;

    public Token(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.publishTime = LocalDateTime.now();
    }
}
