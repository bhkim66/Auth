package com.bhkim.auth.config.security;

import com.bhkim.auth.common.UserRole;
import com.bhkim.auth.exception.ApiException;
import com.bhkim.auth.handler.JwtHandler;
import com.bhkim.auth.handler.RedisHandler;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

import static com.bhkim.auth.common.ConstDef.*;
import static com.bhkim.auth.exception.ExceptionEnum.INVALID_TOKEN_VALUE_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final Key key = Keys.secretKeyFor(KEY_USE_ALGORITHM);
    private final JwtHandler jwtHandler;
    private final RedisHandler redisHandler;

    public String generateToken(PrivateClaims privateClaims) {
        return jwtHandler.createJwt(Map.of(USER_ID, privateClaims.getMemberId(), ROLE_TYPES, privateClaims.getRoleTypes()), ACCESS_TOKEN_EXPIRE_TIME, key);
    }

    //토큰 재발급에서 쓰임 - Refresh Token이 유효한지 확인
    public Optional<PrivateClaims> parseRefreshToken(String refreshToken, String id) {
        String redisRefreshToken = redisHandler.getHashData(id, refreshToken);
        return jwtHandler.checkRefreshToken(refreshToken, redisRefreshToken, key.getAlgorithm()).map(claims -> convert(claims));
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String token) {
        // 토큰 복호화
        Claims claims = parseClaims(token);
        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
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

//    public void existSignOutMemberInRedis(String token) {
//        if (!StringUtil.isEmpty((String) redisTemplate.opsForValue().get(token))) {
//            throw new ApiException(INVALID_TOKEN_VALUE_ERROR);
//        }
//    }

    // Request Header 에서 토큰 정보 추출
//    public String resolveToken(HttpServletRequest request, String tokenType) {
//        String bearerToken = decTokenStr(request.getHeader(tokenType));
//        if (StringUtils.hasText(bearerToken)) {
//            return bearerToken;
//        }
//        return null;
//    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Long getExpiration(String token) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    public boolean matchRefreshToken(String refreshToken, String storedRefreshToken) {
        return storedRefreshToken.equals(refreshToken);
    }

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
        return new PrivateClaims(claims.get(USER_ID, String.class), claims.get(ROLE_TYPES, UserRole.class));
    }

    @Getter
    @AllArgsConstructor
    public static class PrivateClaims {
        private String memberId;
        private UserRole roleTypes;
    }

//    public TokenInfo getTokenInfo(String token) {
//        if(token == null || !validateToken(token)) {
//            return null;
//        }
//        Claims claims = parseClaims(token);
//        return TokenInfo.builder()
//                .userId(claims.getSubject())
//                .memSeq(((Number) claims.get(MEMBER_SEQ)).longValue() )
//                .memEmail(String.valueOf(claims.get(MEMBER_EMAIL)))
//                .memberType((List<String>) claims.get(MEMBER_TYPE))
//                .profileImage(String.valueOf(claims.get(MEMBER_PROFILE_IMAGE)))
//                .build();
//    }
}