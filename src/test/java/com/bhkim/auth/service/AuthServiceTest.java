package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.AuthDto;
import com.bhkim.auth.dto.UserRequestDTO;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.record.SignInRequest;
import com.bhkim.auth.repository.AuthRepository;
import com.bhkim.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static com.bhkim.auth.common.TypeEnum.M;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
@SpringBootTest
class AuthServiceTest {
    @InjectMocks
    AuthService authService;

    @Mock
    AuthRepository authRepository;

    @Autowired
    UserRepository userRepository;

    User getUser() {
        return User.builder()
                .id("bhkim62")
                .password("1234qwer")
                .name("김병호")
                .age(30)
                .sex(M)
                .build();
    }

    UserRequestDTO.UserInfo getUserInfo(User m) {
        User user = getUser();
        return UserRequestDTO.UserInfo.builder()
                .id(user.getId())
                .name(user.getName())
                .age(user.getAge())
                .sex(user.getSex())
                .build();
    }

    @Test
    void 로그인에_정상적으로_성공() {
        //given
        User user = getUser();
        SignInRequest request = new SignInRequest("bhkim62", "1234qwer");
        UserRequestDTO.UserInfo userInfo = UserRequestDTO.UserInfo.builder().id(request.id()).password(request.password()).build();

        //mocking
        given(userRepository.save(any())).willReturn(user);

        //when
        ApiResponseResult<AuthDto.Token> tokenApiResponseResult = authService.signIn(userInfo);
        //then
        assertThat(tokenApiResponseResult.isSuccess()).isTrue();

    }

    @Test
    void 로그인시_회원정보가_틀렸을_경우() {
        //given
        User user = getUser();
        SignInRequest request = new SignInRequest("bhkim62", "1234qwer");
        UserRequestDTO.UserInfo userInfo = UserRequestDTO.UserInfo.builder().id(request.id()).password(request.password()).build();

        //mocking
        given(userRepository.save(any())).willReturn(user);

        //when
        ApiResponseResult<AuthDto.Token> tokenApiResponseResult = authService.signIn(userInfo);
        //then
        assertThat(tokenApiResponseResult.isSuccess()).isFalse();
    }

    @Test
    void 로그인시_이미_로그인된_상태일_경우_redis_조회() {
        //given
        User user = getUser();
        SignInRequest request = new SignInRequest("bhkim63", "1234qwer");
        UserRequestDTO.UserInfo userInfo = UserRequestDTO.UserInfo.builder().id(request.id()).password(request.password()).build();

        //mocking
        given(userRepository.save(any())).willReturn(user);

        //when
        ApiResponseResult<AuthDto.Token> tokenApiResponseResult = authService.signIn(userInfo);
        //then
        assertThat(tokenApiResponseResult.isSuccess()).isFalse();
    }

    @Test
    void 로그아웃_성공() {

    }

    @Test
    void 회원가입_성공() {

    }

    @Test
    void 회원가입시_잘못된_request값() {

    }

    @Test
    void 토큰재발급_성공() {
    }

    @Test
    void 토큰재발급시_refreshToken_값이_유효하지_않는경우() {
    }

    @Test
    void accessToken_값이_유효하지_않는경우() {
    }

    //given

    //mocking

    //when

    //then
}