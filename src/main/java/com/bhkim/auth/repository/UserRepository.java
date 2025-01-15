package com.bhkim.auth.repository;

import com.bhkim.auth.entity.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findBySeq(Long seq);

    Optional<User> findById(String id);

    boolean existsById(String id);

    @Query("select u from User u join fetch u.addresses a")
    Optional<User> getUserAddressList(@Param("userSeq") Long userSeq);
}
