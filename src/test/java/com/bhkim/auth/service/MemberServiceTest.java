package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiException;
import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.common.ExceptionEnum;
import com.bhkim.auth.dto.MemberDto;
import com.bhkim.auth.entity.jpa.Member;
import com.bhkim.auth.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;

import static com.bhkim.auth.common.ExceptionEnum.DATABASE_INSERT_ERROR;
import static com.bhkim.auth.common.TypeEnum.M;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberServiceImpl memberService;

    @Mock
    MemberRepository memberRepository;

    Member getMember() {
        return Member.builder()
                .id("bhkim62")
                .name("김병호")
                .age(31)
                .sex(M)
                .phoneNumber(null)
                .build();
    }

    MemberDto.MemberInfo getMemberInfo(Member member) {
        return MemberDto.MemberInfo.builder()
                .id(member.getId())
                .name(member.getName())
                .age(member.getAge())
                .sex(member.getSex())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }

    @Test
    void 특정_멤버_조회() {

    }

    @Test
    @Rollback(false)
    void 회원가입() {
        //given
        Member member = getMember();
        MemberDto.MemberInfo memberInfo = getMemberInfo(member);
        Long fakeSeq = 1L;

        // stubbing: memberRepository.save()가 호출되면 member 객체를 반환하도록 설정
        given(memberRepository.save(any())).willReturn(member);
        ReflectionTestUtils.setField(member, "seq", fakeSeq);

        // when
        ApiResponseResult<HttpStatus> result = memberService.signUp(memberInfo);

        // then
        // 반환된 결과가 성공 인지를 확인
        assertThat(true).isEqualTo(result.isSuccess());
    }

    @Test
    void 회원가입_실패_저장실패() {
        // given
        Member member = getMember();
        MemberDto.MemberInfo memberInfo = getMemberInfo(member);

        // stubbing: memberRepository.save()가 호출되면 ApiException을 던지도록 설정
        given(memberRepository.save(any())).willThrow(new ApiException(DATABASE_INSERT_ERROR));

        // when
        // then
        ApiException ex = assertThrows(ApiException.class, () -> memberService.signUp(memberInfo));
        log.info("ex enum={}", ex.getExceptionEnum());
        assertThat(ex.getExceptionEnum()).isEqualTo(DATABASE_INSERT_ERROR);
    }

    @Test
    void 멤버_정보_수정() {
        //given
        Member member = getMember();
        MemberDto.MemberInfo memberInfo = getMemberInfo(member);

        //when


        //then


    }


}