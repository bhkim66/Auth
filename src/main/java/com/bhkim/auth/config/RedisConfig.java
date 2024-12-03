package com.bhkim.auth.config;

import com.bhkim.auth.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
        //ssh 추가
//            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, Integer.parseInt(port));
//            LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigBuilder = LettuceClientConfiguration.builder();
//            clientConfigBuilder.useSsl();
//
//            ClientOptions clientOptions = ClientOptions.builder()
//                    .socketOptions(SocketOptions.builder().keepAlive(true).build())
//                    .build();
//
//            DefaultClientResources clientResources = DefaultClientResources.builder()
//                    .build();
//
//            LettuceClientConfiguration clientConfig = clientConfigBuilder
//                    .clientOptions(clientOptions)
//                    .clientResources(clientResources)
//                    .build();
//
//            return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // 일반적인 key:value의 경우 시리얼라이저
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(RedisSerializer.java());

        // Hash를 사용할 경우 시리얼라이저
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(RedisSerializer.string());
        return redisTemplate;
    }

    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }
}


