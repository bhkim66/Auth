package com.bhkim.auth.repository;

import com.bhkim.auth.entity.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySeq(Long seq);

    Optional<User> findById(String id);

    boolean existsById(String id);
}
