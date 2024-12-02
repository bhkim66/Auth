package com.bhkim.auth.util;

import com.bhkim.auth.entity.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.bhkim.auth.common.ConstDef.REDIS_KEY_PREFIX;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public void insertMemberRedis(TokenInfo tokenInfo) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        Date rtkExpiredDate = new Date((new Date()).getTime() + tokenInfo.getRtkExpirationTime());

        String userKey = REDIS_KEY_PREFIX + tokenInfo.getUserId();

        hashOperations.put(userKey, "seq", String.valueOf(tokenInfo.getUserSeq()));
        hashOperations.put(userKey, "id", tokenInfo.getUserId());
        hashOperations.put(userKey, "authority ", tokenInfo.getAuthorityList());
        hashOperations.put(userKey, "accessToken", tokenInfo.getAccessToken());
        hashOperations.put(userKey, "refreshToken", tokenInfo.getRefreshToken());
        hashOperations.put(userKey, "rtkExpirationTime", String.valueOf((tokenInfo.getRtkExpirationTime() / 1000)) );
        hashOperations.put(userKey, "rtkExpirationDate", rtkExpiredDate.toString());
        redisTemplate.expire(userKey , tokenInfo.getRtkExpirationTime(), MILLISECONDS);
    }

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
}
