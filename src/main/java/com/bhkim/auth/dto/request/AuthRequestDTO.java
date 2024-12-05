package com.bhkim.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

public class AuthRequestDTO {
    @Getter
    @Builder
    public static class RefreshToken {
        @NotBlank
        private String refreshToken;
    }
}
