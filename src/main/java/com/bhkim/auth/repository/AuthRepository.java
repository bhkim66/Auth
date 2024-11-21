package com.bhkim.auth.repository;

import com.bhkim.auth.entity.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<User, Long> {

}
