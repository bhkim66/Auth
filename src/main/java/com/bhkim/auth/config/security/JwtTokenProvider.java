package com.bhkim.auth.config.security;

import com.bhkim.auth.common.RoleEnum;
import com.bhkim.auth.exception.ApiException;
import com.bhkim.auth.handler.JwtHandler;
import com.bhkim.auth.handler.RedisHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.bhkim.auth.common.ConstDef.*;
import static com.bhkim.auth.exception.ExceptionEnum.INVALID_TOKEN_VALUE_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtHandler jwtHandler;
    private final RedisHandler redisHandler;

    @Getter
    @AllArgsConstructor
    public static class PrivateClaims {
        private String userId;
        private RoleEnum roleTypes;
    }

    public String generateToken(PrivateClaims privateClaims, Long expireTime) {
        return jwtHandler.createJwt(Map.of(USER_ID, privateClaims.getUserId(), ROLE_TYPES, privateClaims.getRoleTypes()), expireTime);
    }

    //토큰 재발급에서 쓰임 - Refresh Token이 유효한지 확인
    public PrivateClaims parseRefreshToken(String refreshToken) {
        String redisRefreshToken = redisHandler.getHashData(getUserId(refreshToken), REDIS_KEY_REFRESH_TOKEN);
        return jwtHandler.checkRefreshToken(refreshToken, redisRefreshToken).map(this::convert).orElseThrow();
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token){
        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//            existSignOutMemberInRedis(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        throw new ApiException(INVALID_TOKEN_VALUE_ERROR);
    }

    public String getUserId(String token) {
        if (token.isBlank()) {
            throw new IllegalArgumentException();
        }
        Claims claims = jwtHandler.getClaims(token).orElseThrow();
        return (String) claims.get(USER_ID);
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
//    public Authentication getAuthentication(String token) {
//        // 토큰 복호화
//        Claims claims = parseClaims(token);
//        // 클레임에서 권한 정보 가져오기
//        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
//
//        // UserDetails 객체를 만들어서 Authentication 리턴
//        UserDetails principal = new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);
//        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
//    }


    // Request Header 에서 토큰 정보 추출
//    public String resolveToken(HttpServletRequest request, String tokenType) {
//        String bearerToken = decTokenStr(request.getHeader(tokenType));
//        if (StringUtils.hasText(bearerToken)) {
//            return bearerToken;
//        }
//        return null;
//    }

//    public Long getExpiration(String token) {
//        // accessToken 남은 유효시간
//        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
//        // 현재 시간
//        Long now = new Date().getTime();
//        return (expiration.getTime() - now);
//    }

//    // 어세스 토큰 헤더 설정
//    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
//        response.setHeader("authorization", "bearer "+ accessToken);
//    }
//
//    // 리프레시 토큰 헤더 설정
//    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
//        response.setHeader("refreshToken", "bearer "+ refreshToken);
//    }

    private PrivateClaims convert(Claims claims) {
        return new PrivateClaims(claims.get(USER_ID, String.class), RoleEnum.valueOf(claims.get(ROLE_TYPES, String.class)));
    }
}