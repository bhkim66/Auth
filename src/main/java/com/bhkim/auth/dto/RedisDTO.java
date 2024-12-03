package com.bhkim.auth.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

public class RedisDTO {
    @Builder
    public static class Token {
        private String userId;
        private String refreshToken;
        private String expiredDateTime;

        public Map<String, Object> convertMap() {
            return Map.of(
                    "userId", this.userId,
                    "refreshToken", this.refreshToken,
                    "expiredDateTime", this.expiredDateTime
            );
        }
    }
}
