package com.bhkim.auth.config.security;

import com.bhkim.auth.common.RoleEnum;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.exception.ApiException;
import com.bhkim.auth.handler.JwtHandler;
import com.bhkim.auth.security.CustomUserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

import static com.bhkim.auth.common.ConstDef.*;
import static com.bhkim.auth.exception.ExceptionEnum.INVALID_TOKEN_VALUE;
import static com.bhkim.auth.exception.ExceptionEnum.INVALID_TOKEN_VALUE_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtHandler jwtHandler;

    @Getter
    @AllArgsConstructor
    public static class PrivateClaims {
        private String userId;
        private RoleEnum role;
    }


    public String generateToken(PrivateClaims privateClaims, Long expireTime) {
        return jwtHandler.createJwt(Map.of(USER_ID, privateClaims.getUserId(), ROLE, privateClaims.getRole()), expireTime);
    }

    //토큰 재발급에서 쓰임 - Refresh Token이 유효한지 확인
    public PrivateClaims parseRefreshToken(String refreshToken, String redisRefreshToken) {
        if (!refreshToken.equals(redisRefreshToken)) {
            throw new ApiException(INVALID_TOKEN_VALUE);
        }
        return jwtHandler.parseClaims(refreshToken).map(this::convert).orElseThrow();
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token){
        try {
            Optional<Claims> claims = jwtHandler.parseClaims(token);
//            existSignOutMemberInRedis(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT Token");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT Token");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT Token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty.");
        }
        throw new ApiException(INVALID_TOKEN_VALUE_ERROR);
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String token) {
        // 토큰 복호화
        Claims claims = jwtHandler.parseClaims(token).orElseThrow();
        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" +
                claims.get(ROLE)));

        User user = User.builder()
                .id((String) claims.get(USER_ID))
                .role(RoleEnum.valueOf((String) claims.get(ROLE)))
                .build();
        // UserDetails 객체를 만들어서 Authentication 리턴
        CustomUserDetail principal = new CustomUserDetail(user);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public String getTokenInHeader() {
        String refreshToken = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader(HEADER_KEY_AUTHORIZATION);
        return validateToken(refreshToken) ? refreshToken : "";
    }

    private PrivateClaims convert(Claims claims) {
        return new PrivateClaims(claims.get(USER_ID, String.class), RoleEnum.valueOf(claims.get(ROLE, String.class)));

//    public Set<RoleEnum> extractUserRole() {
//        return getUserDetail().getAuthorities()
//                .stream()
//                .map(GrantedAuthority::getAuthority)
//                .map(RoleEnum::valueOf)
//                .collect(Collectors.toSet());
//    }
//
//    private CustomUserDetail getUserDetail() {
//        return (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    }
    }
}
