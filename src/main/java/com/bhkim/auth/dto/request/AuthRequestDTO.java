package com.bhkim.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

public class AuthRequestDTO {
    @Getter
    @Builder
    public static class Token {
        @NotBlank
        private String accessToken;
        @NotBlank
        private String refreshToken;
    }
}
