package com.bhkim.auth.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import static com.bhkim.auth.common.ConstDef.REDIS_EXPIRE_TIME;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "REFRESH_TOKEN", timeToLive = REDIS_EXPIRE_TIME)
public class RefreshToken {
    @Id
    private String uid;
    @Indexed
    private String refreshToken;

    private String role;
}
