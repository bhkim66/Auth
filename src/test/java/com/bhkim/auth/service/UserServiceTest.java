package com.bhkim.auth.service;

import com.bhkim.auth.exception.ApiException;
import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.UserDto;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;

import static com.bhkim.auth.exception.ExceptionEnum.DATABASE_INSERT_ERROR;
import static com.bhkim.auth.common.TypeEnum.M;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserServiceImpl memberService;

    @Mock
    UserRepository userRepository;

    User getMember() {
        return User.builder()
                .id("bhkim62")
                .name("김병호")
                .age(31)
                .sex(M)
                .phoneNumber(null)
                .build();
    }

    User updateMember() {
        return User.builder()
                .name("박병호")
                .age(25)
                .sex(M)
                .phoneNumber("010-3039-3012")
                .build();
    }

    UserDto.UserInfo getMemberInfo(User user) {
        return UserDto.UserInfo.builder()
                .member(getMember())
                .build();
    }

    @Test
    void 특정_멤버_조회() {

    }

    @Test
    @Rollback(false)
    void 회원가입() {
        //given
        User user = getMember();
        UserDto.UserInfo userInfo = getMemberInfo(user);
        Long fakeSeq = 1L;

        // stubbing: memberRepository.save()가 호출되면 member 객체를 반환하도록 설정
        given(userRepository.save(any())).willReturn(user);
        ReflectionTestUtils.setField(user, "seq", fakeSeq);

        // when
        ApiResponseResult<HttpStatus> result = memberService.signUp(userInfo);

        // then
        // 반환된 결과가 성공 인지를 확인
        assertThat(true).isEqualTo(result.isSuccess());
    }

    @Test
    void 회원가입_실패_저장실패() {
        // given
        User user = getMember();
        UserDto.UserInfo userInfo = getMemberInfo(user);

        // stubbing: memberRepository.save()가 호출되면 ApiException을 던지도록 설정
        given(userRepository.save(any())).willThrow(new ApiException(DATABASE_INSERT_ERROR));

        // when
        // then
        ApiException ex = assertThrows(ApiException.class, () -> memberService.signUp(userInfo));
        log.info("ex enum={}", ex.getException());
        assertThat(ex.getException()).isEqualTo(DATABASE_INSERT_ERROR);
    }

    @Test
    void 멤버_정보_수정() {
        //given
        User user = getMember();
        User updateUser = updateMember();
        UserDto.UserInfo userInfo = getMemberInfo(updateUser);

        //when
        memberService.setMember(userInfo);

        //then

    }


}