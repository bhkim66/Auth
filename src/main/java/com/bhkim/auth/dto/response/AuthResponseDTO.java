package com.bhkim.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

public class AuthResponseDTO {
    @Getter
    @Builder
    @ToString
    public static class Token {
        private String accessToken;
        private String refreshToken;
        @Builder.Default
        private LocalDateTime publishTime = LocalDateTime.now();
    }
}
