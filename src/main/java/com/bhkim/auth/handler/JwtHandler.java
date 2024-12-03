package com.bhkim.auth.handler;

import com.bhkim.auth.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static com.bhkim.auth.common.ConstDef.KEY_USE_ALGORITHM;
import static com.bhkim.auth.exception.ExceptionEnum.INVALID_TOKEN_VALUE;

@Component
public class JwtHandler {
    private static final String BEARER_TYPE = "Bearer";

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public String createJwt(Map<String, Object> privateClaims, long expireTime, Key key) {
        Date now = new Date();
        Date accessTokenExpiresIn = new Date(now.getTime() + expireTime);

        return BEARER_TYPE + " " + Jwts.builder()
                .setIssuedAt(now) // 토큰 발급 시간
                .setIssuer("auth.bhkim.com") // 토큰 발급자
                .setClaims(privateClaims)
                .setExpiration(accessTokenExpiresIn) // 만료 시간
                .signWith(key, KEY_USE_ALGORITHM) // 사용 암호 알고리즘
                .compact();
    }

    public Optional<Claims> checkRefreshToken(String refreshToken, String redisRefreshToken, String key) {
        if (!refreshToken.equals(redisRefreshToken)) {
            throw new ApiException(INVALID_TOKEN_VALUE);
        }
        return Optional.of(Jwts.parserBuilder()
                .setSigningKey(key.getBytes())
                .build().parseClaimsJws(refreshToken).getBody());
    }

//    public String getUserIdFromJWT(String token) {
//        if (token == null || !validateToken(token)) {
//            return "";
//        }
//        String userId = null;
//        Claims claims = parseClaims(token);
//        if (claims != null) {
//            userId = (String) claims.get("sub");
//        }
//        return userId;
//    }
}
