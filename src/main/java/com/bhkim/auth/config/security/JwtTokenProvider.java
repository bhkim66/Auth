package com.bhkim.auth.config.security;

import com.bhkim.auth.entity.TokenInfo;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.exception.ApiException;
import com.bhkim.auth.util.AESUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.bhkim.auth.exception.ExceptionEnum.*;
import static com.bhkim.auth.util.AESUtil.*;
import static io.micrometer.common.util.StringUtils.isBlank;
import static io.micrometer.common.util.StringUtils.isEmpty;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String MEMBER_SEQ = "seq";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME =  30 * 60 * 1000L;             // 30분
    private static final long ACCESS_TOKEN_EXPIRE_TIME_LOCAL = 12 * 60 * 60 * 1000L;    // 12시간
    private static final long REDIS_EXPIRE_TIME = 60 * 60 * 1000L;                 // 1시간

    private final Key key;

    @Autowired
    public JwtTokenProvider() {
//        byte[] keyBytes = Decoders.BASE64.decode(key);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public TokenInfo generateToken(User userInfo) {
        // Access Token 생성
        List<String> typeList = new ArrayList<>();

        Date accessTokenExpiresIn = null;
//        if("local".equals(profile) ) {
            accessTokenExpiresIn = new Date((new Date()).getTime() + ACCESS_TOKEN_EXPIRE_TIME_LOCAL);
//        } else {
//            accessTokenExpiresIn = new Date((new Date()).getTime() + ACCESS_TOKEN_EXPIRE_TIME);
//        }

        String accessToken = Jwts.builder()
                .setHeaderParam("typ", "JWT") // 토큰 Header 설정 type = JWT 기본값
                .setSubject(userInfo.getId()) // 발급 받는 주체 구분 값
                .setIssuer("auth.bhkim.com") // 토큰 발급자
                .claim(MEMBER_SEQ , userInfo.getSeq()) // payload 값
                .setExpiration(accessTokenExpiresIn) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 사용 암호 알고리즘
                .compact();
        // Refresh Token 생성
        Date refreshTokenExpiresIn = new Date((new Date()).getTime() + REDIS_EXPIRE_TIME);

        String refreshToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(userInfo.getId())
                .setIssuer("auth.bhkim.com")
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo.builder()
                .userId(userInfo.getId())
                .userSeq(userInfo.getSeq())
                .authorityList(typeList)
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .rtkExpirationTime(REDIS_EXPIRE_TIME)
                .build();
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

    public String getUserIdFromGlobalJWT(String token) {
        String userId = null;
        Claims claims = parseClaims(token);
        if(claims != null) {
            userId = (String)claims.get("sub");
        }
        return userId;
    }

    public String getUserIdFromJWT(String token) {
        if(token == null || !validateToken(token)) {
            return "";
        }
        String userId = null;
        Claims claims = parseClaims(token);
        if(claims != null) {
            userId = (String)claims.get("sub");
        }
        return userId;
    }

//    public String getUserIdFromJWTToAop(String token) {
//        if(token == null) {
//            return "";
//        }
//        String userId = null;
//        Claims claims = parseClaims(token);
//        if(claims != null) {
//            userId = (String)claims.get("sub");
//        }
//        return userId;
//    }


    public Long getUserSeqFromJWT(String token) {
        if(token == null || !validateToken(token)) {
            return 0L;
        }
        Long memSeq = 0L;
        Claims claims = parseClaims(token);
        if(claims != null) {
            memSeq =  ((Number) claims.get(MEMBER_SEQ)).longValue() ;
        }
        return memSeq;
    }

    public boolean matchRefreshToken(String refreshToken, String storedRefreshToken) {
        return storedRefreshToken.equals(refreshToken);
    }

    // 어세스 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("authorization", "bearer "+ accessToken);
    }

    // 리프레시 토큰 헤더 설정
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader("refreshToken", "bearer "+ refreshToken);
    }


    //토큰 암호화
    public TokenInfo encToken(TokenInfo tokenInfo) {
        if(isBlank(tokenInfo.getAccessToken()) || isBlank(tokenInfo.getRefreshToken())) {
            throw new ApiException(ILLEGAL_TOKEN_VALUE);
        } else {
            tokenInfo.encToken(urlEncrypt(tokenInfo.getAccessToken()), urlEncrypt(tokenInfo.getRefreshToken()));
        }
        return tokenInfo;
    }

    // 토큰 복호화
    public String decTokenStr(String token) {
        try {
            if (!isBlank(token)) token = urlDecrypt(token);
        } catch (Exception e) {
            throw new ApiException(INVALID_TOKEN_VALUE_ERROR);
        }
        return token;
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