package com.bhkim.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.boot.autoconfigure.data.redis.RedisProperties.*;

@SpringBootTest
class AuthApplicationTests {
}
