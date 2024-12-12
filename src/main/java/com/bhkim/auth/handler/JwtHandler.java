package com.bhkim.auth.handler;

import com.bhkim.auth.dto.RedisDTO;
import com.bhkim.auth.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static com.bhkim.auth.common.ConstDef.REFRESH_TOKEN_EXPIRE_TIME;
import static com.bhkim.auth.exception.ExceptionEnum.INVALID_TOKEN_VALUE;

@Component
public class JwtHandler {
    private static final String BEARER_TYPE = "Bearer";
    private final Key key;

    public JwtHandler() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public String createJwt(Map<String, Object> privateClaims, Long expireTime) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .setIssuedAt(now) // 토큰 발급 시간
                .setIssuer("auth.bhkim.com") // 토큰 발급자
                .setClaims(privateClaims)
                .setExpiration(expireDate) // 만료 시간
                .signWith(key) // 사용 암호 알고리즘
                .compact();
    }

    public Optional<Claims> parseClaims(String token) {
        return Optional.of(Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody());
    }
}
