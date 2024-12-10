package com.bhkim.auth.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

import static com.bhkim.auth.common.ConstDef.*;

public class RedisDTO {
    @Builder
    public static class Token {
        private String userId;
        private String refreshToken;
        private Long expiredDateTime;

        public Map<String, Object> convertMap() {
            return Map.of(
                    REDIS_KEY_USER_ID, this.userId,
                    REDIS_KEY_REFRESH_TOKEN, this.refreshToken,
                    REDIS_KEY_EXPIRED_DATE_TIME, String.valueOf(LocalDateTime.now().plusSeconds(this.expiredDateTime))
            );
        }
    }
}
