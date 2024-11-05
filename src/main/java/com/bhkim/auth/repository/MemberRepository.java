package com.bhkim.auth.repository;

import com.bhkim.auth.entity.jpa.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> getMemberInfo();
}
