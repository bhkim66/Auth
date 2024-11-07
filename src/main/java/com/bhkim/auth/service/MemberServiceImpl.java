package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.MemberDto;
import com.bhkim.auth.entity.jpa.Member;
import com.bhkim.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.bhkim.auth.common.ExceptionEnum.DATABASE_INSERT_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    @Autowired
    private final MemberRepository memberRepository;

    @Override
    public ApiResponseResult<MemberDto.MemberInfo> getMemberInfo() {
        return null;
    }

    @Override
    @Transactional
    public ApiResponseResult<HttpStatus> signUp(MemberDto.MemberInfo memberInfo) {
        Member member = memberInfo.dtoConvertMember();
        Member savedMember = memberRepository.save(member);
        System.out.println("savedMember = " + savedMember.getId());
        System.out.println("savedMember = " + savedMember.getName());

//        if(savedMember.getSeq() < 0) {
//            ApiResponseResult.failure(DATABASE_INSERT_ERROR);
//        }

        return ApiResponseResult.success(HttpStatus.OK);
    }
}
