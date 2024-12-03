//package com.bhkim.auth.entity.redis;
//
//import jakarta.persistence.Id;
//import lombok.*;
//import org.springframework.data.redis.core.RedisHash;
//import org.springframework.data.redis.core.index.Indexed;
//
//import static com.bhkim.auth.common.ConstDef.REDIS_EXPIRE_TIME;
//
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@RedisHash(value = "REFRESH_TOKEN", timeToLive = 1000)
//public class RefreshToken {
//    @Id
//    private String uid;
//
//    @Indexed
//    private String refreshToken;
//
//    private String role;
//}
