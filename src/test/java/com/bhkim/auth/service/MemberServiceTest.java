package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
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

import static com.bhkim.auth.common.TypeEnum.M;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

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
        // 반환된 결과가 HttpStatus.OK인지를 확인
        assertThat(true).isEqualTo(result.isSuccess());
    }

    @Test
    void 회원가입_실패_저장실패() {
        // given
        Member member = getMember();
        MemberDto.MemberInfo memberInfo = getMemberInfo(member);

        // stubbing: memberRepository.save()가 호출되면 저장된 member 객체를 반환하도록 설정
        given(memberRepository.save(any())).willReturn(member);

        // when
        ReflectionTestUtils.setField(member, "seq", -1L); //Seq가 자동생성이므로 필드에 직접 생성 가능

        ApiResponseResult<HttpStatus> result = memberService.signUp(memberInfo);

        // then
        // 저장 실패 시 failure 응답을 반환하는지 확인
        assertThat(HttpStatus.INTERNAL_SERVER_ERROR).isEqualTo(result.getE());  // 예시로 실패 상태를 INTERNAL_SERVER_ERROR로 설정
    }


}