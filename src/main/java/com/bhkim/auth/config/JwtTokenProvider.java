package com.bhkim.auth.config;

import com.bhkim.auth.entity.TokenInfo;
import com.bhkim.auth.entity.jpa.Member;
import com.bhkim.auth.exception.ApiException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.bhkim.auth.exception.ExceptionEnum.INVALID_TOKEN_VALUE_ERROR;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String MEMBER_SEQ = "seq";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME =  30 * 60 * 1000L;             // 30분
    private static final long ACCESS_TOKEN_EXPIRE_TIME_LOCAL = 12 * 60 * 60 * 1000L;    // 12시간
    private static final long REDIS_EXPIRE_TIME = 60 * 60 * 1000L;                 // 1시간

    private final Key key;
//    PropertiesValue properties

//    @Autowired
//    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public JwtTokenProvider(@Value("${security.jwt.key}") String key) {
        System.out.println("key = " + key);
//        byte[] keyBytes = Decoders.BASE64.decode(key);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public TokenInfo generateToken(Member memberInfo) {
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
                .setSubject(memberInfo.getId()) // 발급 받는 주체 구분 값
                .setIssuer("auth.bhkim.com") // 토큰 발급자
                .claim(MEMBER_SEQ , memberInfo.getSeq()) // payload 값
                .setExpiration(accessTokenExpiresIn) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 사용 암호 알고리즘
                .compact();
        // Refresh Token 생성
        Date refreshTokenExpiresIn = new Date((new Date()).getTime() + REDIS_EXPIRE_TIME);

        String refreshToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(memberInfo.getId())
                .setIssuer("auth.bhkim.com")
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo.builder()
                .memId(memberInfo.getId())
                .memSeq(memberInfo.getSeq())
                .memberType(typeList)
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
        UserDetails principal = new User(claims.getSubject(), "", authorities);
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

    public String getUserIdFromJWTToAop(String token) {
        if(token == null) {
            return "";
        }
        String userId = null;
        Claims claims = parseClaims(token);
        if(claims != null) {
            userId = (String)claims.get("sub");
        }
        return userId;
    }


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

//    public void insertMemberRedis(TokenInfo tokenInfo) {
//        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
//        Date rtkExpiredDate = new Date((new Date()).getTime() + tokenInfo.getRtkExpirationTime());
//        String userKey = ConstDef.connectChannel + ConstDef.REDIS_KEY_PREFIX + tokenInfo.getUserId();
//
//        hashOperations.put(userKey, "memSeq", String.valueOf(tokenInfo.getMemSeq()));
//        hashOperations.put(userKey, "userId", tokenInfo.getUserId());
//        hashOperations.put(userKey, "accessToken", tokenInfo.getAccessToken());
//        hashOperations.put(userKey, "refreshToken", tokenInfo.getRefreshToken());
//        hashOperations.put(userKey, "rtkExpirationTime", String.valueOf((tokenInfo.getRtkExpirationTime() / 1000)) );
//        hashOperations.put(userKey, "rtkExpirationDate", rtkExpiredDate.toString());
//        redisTemplate.expire(userKey , tokenInfo.getRtkExpirationTime(), MILLISECONDS);
//    }
//
//    public void deleteMemberRedis(String token) {
//        String userId = getUserIdFromJWT(token);
//        String userKey = ConstDef.connectChannel + ConstDef.REDIS_KEY_PREFIX + userId;
//
//        if(existMemberInRedis(userId, token)) {
//            SecurityContextHolder.clearContext();
//            throw new ApiException(REDIS_USER_NOT_EXIST);
//        }
//        redisTemplate.delete(userKey);
//        redisTemplate.opsForValue().set(token, "SIGH-OUT");
//        redisTemplate.expire(token, ACCESS_TOKEN_SIGN_OUT_TIME, MILLISECONDS);
//    }

//    public void deleteHomeMemberRedis(String token, String userKey) {
//        String decToken = decTokenStr(token);
//        String userId = getUserIdFromJWT(decToken);
//
//        if(existMemberInRedis(userId, decToken)) {
//            SecurityContextHolder.clearContext();
//            throw new ApiException(REDIS_USER_NOT_EXIST);
//        }
//        redisTemplate.delete(userKey);
//        redisTemplate.opsForValue().set(decToken, "SIGH-OUT");
//        redisTemplate.expire(decToken, ACCESS_TOKEN_SIGN_OUT_TIME, MILLISECONDS);
//    }

//    public boolean existMemberInRedis(String userId, String atk) {
//        String userKey = ConstDef.connectChannel + ConstDef.REDIS_KEY_PREFIX + userId;
//        String loginUser = (String) redisTemplate.opsForHash().get(userKey, "userId" );
//        String loginAtk = (String) redisTemplate.opsForHash().get(userKey, "accessToken");
//        //유저는 존재하나 atk값이 다를 경우(중복로그인)
//        if (userId.equals(loginUser) && !atk.equals(loginAtk)) {
//            return true;
//        }
//        //유저 없음
//        return false;
//    }

    //토큰 암호화
//    public TokenInfo encTokenInfo(TokenInfo tokenInfo) {
//        if(!StringUtil.isEmpty(tokenInfo.getAccessToken()) && !StringUtil.isEmpty(tokenInfo.getRefreshToken())) {
//            tokenInfo.setAccessToken(AESUtil.urlEncrypt(tokenInfo.getAccessToken()));
//            tokenInfo.setRefreshToken(AESUtil.urlEncrypt(tokenInfo.getRefreshToken()));
//        } else {
//            throw new ApiException(TOKEN_HAS_NOT_VALUE_ERROR);
//        }
//        return tokenInfo;
//    }

    //토큰 복호화
//    public String decTokenStr(String token) {
//        try {
//            if (!StringUtil.isEmpty(token)) token = AESUtil.urlDecrypt(token);
//        } catch (Exception e) {
//            throw new ApiException(INVALID_TOKEN_VALUE_ERROR);
//        }
//        return token;
//    }

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