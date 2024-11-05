package com.bhkim.auth.repository;

import com.bhkim.auth.entity.jpa.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
}
