package com.bhkim.auth.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
@RequiredArgsConstructor
public class RedisHandler {
    private final RedisTemplate<String, Object> redisTemplate;

//    public void insertUser(TokenInfo tokenInfo) {
//        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
//        Date rtkExpiredDate = new Date((new Date()).getTime() + tokenInfo.getRtkExpirationTime());
//
//        String userKey = REDIS_KEY_PREFIX + tokenInfo.getUserId();
//
//        hashOperations.put(userKey, "seq", String.valueOf(tokenInfo.getUserSeq()));
//        hashOperations.put(userKey, "id", tokenInfo.getUserId());
//        hashOperations.put(userKey, "authority ", tokenInfo.getAuthorityList());
//        hashOperations.put(userKey, "accessToken", tokenInfo.getAccessToken());
//        hashOperations.put(userKey, "refreshToken", tokenInfo.getRefreshToken());
//        hashOperations.put(userKey, "rtkExpirationTime", String.valueOf((tokenInfo.getRtkExpirationTime() / 1000)) );
//        hashOperations.put(userKey, "rtkExpirationDate", rtkExpiredDate.toString());
//        redisTemplate.expire(userKey , tokenInfo.getRtkExpirationTime(), MILLISECONDS);
//    }

    public void setData(String key, String value,Long expiredTime){
        redisTemplate.opsForValue().set(key, value, expiredTime, MILLISECONDS);
    }

    public String getData(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    public String getObjectData(String id, String key){
        return (String) redisTemplate.opsForHash().get(id, key);
    }

    public void deleteData(String key){
        redisTemplate.delete(key);
    }
}
