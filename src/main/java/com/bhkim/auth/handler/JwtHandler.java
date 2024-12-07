package com.bhkim.auth.handler;

import com.bhkim.auth.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

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
        Date accessTokenExpiresIn = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .setIssuedAt(now) // 토큰 발급 시간
                .setIssuer("auth.bhkim.com") // 토큰 발급자
                .setClaims(privateClaims)
                .setExpiration(accessTokenExpiresIn) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 사용 암호 알고리즘
                .compact();
    }

    public Optional<Claims> checkRefreshToken(String refreshToken, String redisRefreshToken) {
        if (!refreshToken.equals(redisRefreshToken)) {
            throw new ApiException(INVALID_TOKEN_VALUE);
        }
        return parseClaims(refreshToken);
    }

    public Optional<Claims> parseClaims(String token) {
        return Optional.of(Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody());
    }

    public void setJwtClaimsJws(String token) {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
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
