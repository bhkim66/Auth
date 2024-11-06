package com.bhkim.auth.repository;

import com.bhkim.auth.entity.jpa.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Member, Long> {

}
