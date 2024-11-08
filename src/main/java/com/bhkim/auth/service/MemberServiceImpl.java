package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiException;
import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.MemberDto;
import com.bhkim.auth.entity.jpa.Member;
import com.bhkim.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bhkim.auth.common.ExceptionEnum.DATABASE_INSERT_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponseResult<MemberDto.MemberInfo> getMemberInfo() {
        return null;
    }

    @Override
    @Transactional
    public ApiResponseResult<HttpStatus> signUp(MemberDto.MemberInfo memberInfo) {
        Member member = memberInfo.dtoConvertMember(passwordEncoder);
        Member savedMember= memberRepository.save(member);

        if(savedMember.getSeq() < 0) {
            throw new ApiException(DATABASE_INSERT_ERROR);
        }

        return ApiResponseResult.success(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ApiResponseResult<HttpStatus> setMember(MemberDto.MemberInfo memberInfo) {

        return null;
    }

    @Override
    @Transactional
    public ApiResponseResult<HttpStatus> changePassword(MemberDto.MemberInfo memberInfo) {
        return null;
    }
}
