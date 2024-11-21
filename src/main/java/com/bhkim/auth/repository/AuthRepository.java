package com.bhkim.auth.repository;

import com.bhkim.auth.entity.jpa.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface AuthRepository extends JpaRepository<Member, Long> {

}
