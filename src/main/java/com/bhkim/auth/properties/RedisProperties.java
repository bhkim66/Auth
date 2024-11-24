package com.bhkim.auth.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("spring.redis")
public class RedisProperties {
    private final int port;
    private final String host;
}
