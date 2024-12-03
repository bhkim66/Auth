package com.bhkim.auth.repository;

import com.bhkim.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByRefreshToken(String RefreshToken);

    Optional<RefreshToken> findByUId(String uid);
}
