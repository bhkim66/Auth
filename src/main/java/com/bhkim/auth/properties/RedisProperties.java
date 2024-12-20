package com.bhkim.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.redis")
public class RedisProperties {
    private int port;
    private String host;
    private String password;
}
