package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.AuthDto;
import com.bhkim.auth.dto.UserDto;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.repository.AuthRepository;
import com.bhkim.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static com.bhkim.auth.common.TypeEnum.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    AuthService authService;

    @Mock
    AuthRepository authRepository;

    @Autowired
    UserRepository userRepository;

    User getMember() {
        return User.builder()
                .id("bhkim62")
                .name("김병호")
                .age(30)
                .sex(M)
                .build();
    }

    UserDto.UserInfo getMemberInfo(User m) {
        return UserDto.UserInfo.builder()
                .member(getMember())
                .build();
    }

    @Test
    void signIn() {
        //given
        Long memberSeq = 1L;
        User user = getMember();
        ReflectionTestUtils.setField(user, "seq", memberSeq); //Seq가 자동생성이므로 필드에 직접 생성 가능

        UserDto.UserInfo userInfo = getMemberInfo(user);
        //mocking
        given(userRepository.save(any())).willReturn(user);

        //when
        ApiResponseResult<AuthDto.Token> tokenApiResponseResult = authService.signIn(userInfo);
        //then
//        tokenApiResponseResult
    }

    @Test
    void signOut() {
    }

    @Test
    void signUp() {
    }

    @Test
    void reissueToken() {
    }

    @Test
    void validationToken() {
    }

    //given

    //mocking

    //when

    //then
}