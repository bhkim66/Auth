package com.bhkim.auth.service;

import com.bhkim.auth.dto.request.AuthRequestDTO;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.AuthResponseDTO;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.record.SignInRequest;
import com.bhkim.auth.repository.UserRepository;
import com.bhkim.auth.service.impl.AuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.bhkim.auth.common.TypeEnum.M;
import static com.bhkim.auth.common.UserRole.USER;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class AuthServiceTest {
    @Autowired
    AuthServiceImpl authService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    User getUser() {
        return User.builder()
                .id("bhkim62")
                .password("1234qwer")
                .name("김병호")
                .role(USER)
                .age(30)
                .sex(M)
                .build();
    }

    UserRequestDTO.Signup getUserInfo(User m) {
        User user = getUser();
        return UserRequestDTO.Signup.builder()
                .id(user.getId())
                .name(user.getName())
                .age(user.getAge())
                .sex(user.getSex())
                .build();
    }

    @Test
    void 로그인에_정상적으로_성공() {
        //given
        User newUser = User.builder()
                .id("bhkim62")
                .password(passwordEncoder.encode("test1234"))
                .name("박병호")
                .role(USER)
                .age(35)
                .sex(M)
                .build();

        userRepository.save(newUser);

        SignInRequest request = new SignInRequest("bhkim62", "test1234");
        UserRequestDTO.SignIn loginUser = UserRequestDTO.SignIn.builder()
                .id(request.id())
                .password(request.password())
                .build();

        //when
        AuthResponseDTO.Token result = authService.signIn(loginUser);

        //then
        assertThat(result).extracting("accessToken").isNotNull();
        assertThat(result).extracting("refreshToken").isNotNull();
        System.out.println("result : " + result);

    }

    @Test
    void 로그인시_회원정보가_틀렸을_경우() {
        //given
        User newUser = User.builder()
                .id("bhkim62")
                .password("test1234")
                .name("박병호")
                .role(USER)
                .age(35)
                .sex(M)
                .build();
        userRepository.save(newUser);

        SignInRequest request = new SignInRequest("bhkim62", "1234qwer");
        UserRequestDTO.SignIn loginUser = UserRequestDTO.SignIn.builder()
                .id(request.id())
                .password(request.password())
                .build();

        //when
        // 오류 사항 적을것
//        AuthResponseDTO.Token result = authService.signIn(loginUser);
    }

//    @Test
//    void 로그인시_이미_로그인된_상태일_경우_redis_조회() {
//        //given
//        SignInRequest request = new SignInRequest("bhkim62", "1234qwer");
//        User newUser = User.builder()
//                .id("bhkim62")
//                .password("test1234")
//                .name("박병호")
//                .age(35)
//                .sex(M)
//                .build();
//        userRepository.save(newUser);
//
//        UserRequestDTO.SignIn loginUser = UserRequestDTO.SignIn.builder()
//                .id(request.id())
//                .password(request.password())
//                .build();
//
//        String userKey = "U" + request.id();
//        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
//        System.out.println("hash = " + hash);
//        hash.put(userKey, "id", request.id());
//        long expireTime = 60 * 60 * 1000L;
//
//        //when
//        ApiResponseResult<UserResponseDTO.Token> tokenApiResponseResult = authService.signIn(loginUser);
//        System.out.println("tokenApiResponseResult = " + tokenApiResponseResult);
//        redisTemplate.expire(userKey, expireTime, MILLISECONDS);
//        String userKeyGetRedis = redisTemplate.opsForValue().get(userKey);
//        System.out.println("userKeyGetRedis = " + userKeyGetRedis);
//
//        //then
//        assertThat(userKeyGetRedis).isEqualTo(userKey);
//    }

    @Test
    void 토큰재발급_성공() {
        User newUser = User.builder()
                .id("bhkim62")
                .password(passwordEncoder.encode("test1234"))
                .name("박병호")
                .role(USER)
                .age(35)
                .sex(M)
                .build();
        userRepository.save(newUser);

        SignInRequest request = new SignInRequest("bhkim62", "test1234");
        UserRequestDTO.SignIn loginUser = UserRequestDTO.SignIn.builder()
                .id(request.id())
                .password(request.password())
                .build();

        AuthResponseDTO.Token rawToken = authService.signIn(loginUser);
        System.out.println("rawToken = " + rawToken);


        AuthRequestDTO.Token token = AuthRequestDTO.Token.builder()
                .refreshToken(rawToken.getRefreshToken())
                .build();


        AuthResponseDTO.Token reissueToken = authService.reissueToken(token);
        System.out.println("reissueToken = " + reissueToken);
        assertThat(rawToken.getAccessToken()).isNotEqualTo(reissueToken.getAccessToken());
        assertThat(rawToken.getRefreshToken()).isNotEqualTo(reissueToken.getRefreshToken());

    }

    @Test
    void 토큰재발급시_refreshToken가_만료된_경우() {
        User newUser = User.builder()
                .id("bhkim62")
                .password(passwordEncoder.encode("test1234"))
                .name("박병호")
                .role(USER)
                .age(35)
                .sex(M)
                .build();
        userRepository.save(newUser);

        SignInRequest request = new SignInRequest("bhkim62", "test1234");
        UserRequestDTO.SignIn loginUser = UserRequestDTO.SignIn.builder()
                .id(request.id())
                .password(request.password())
                .build();

        AuthResponseDTO.Token rawToken = authService.signIn(loginUser);
        System.out.println("rawToken = " + rawToken);


        AuthRequestDTO.Token token = AuthRequestDTO.Token.builder()
                .refreshToken(rawToken.getRefreshToken())
                .build();


        AuthResponseDTO.Token reissueToken = authService.reissueToken(token);
        assertThat(rawToken.getAccessToken()).isNotEqualTo(reissueToken.getAccessToken());
        assertThat(rawToken.getRefreshToken()).isNotEqualTo(reissueToken.getRefreshToken());

        System.out.println("reissueToken = " + reissueToken);
    }

    @Test
    void 로그아웃() {
        User newUser = User.builder()
                .id("bhkim62")
                .password(passwordEncoder.encode("test1234"))
                .name("박병호")
                .role(USER)
                .age(35)
                .sex(M)
                .build();
        userRepository.save(newUser);

        SignInRequest request = new SignInRequest("bhkim62", "test1234");
        UserRequestDTO.SignIn loginUser = UserRequestDTO.SignIn.builder()
                .id(request.id())
                .password(request.password())
                .build();

        AuthResponseDTO.Token rawToken = authService.signIn(loginUser);
        System.out.println("rawToken = " + rawToken);

        authService.signOut();
    }
}