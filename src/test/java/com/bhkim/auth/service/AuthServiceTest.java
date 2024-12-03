package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.common.UserRole;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.UserResponseDTO;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.record.SignInRequest;
import com.bhkim.auth.repository.UserRepository;
import com.bhkim.auth.service.impl.AuthServiceImpl;
import com.bhkim.auth.util.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.bhkim.auth.common.TypeEnum.M;
import static com.bhkim.auth.common.UserRole.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
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
        ApiResponseResult<UserResponseDTO.Token> result = authService.signIn(loginUser);

        //then
        assertThat(result.isSuccess()).isTrue();
        System.out.println("result : " + result.getData());

    }

    @Test
    void 로그인시_회원정보가_틀렸을_경우() {
        //given
        User newUser = User.builder()
                .id("bhkim62")
                .password("test1234")
                .name("박병호")
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
        ApiResponseResult<UserResponseDTO.Token> tokenApiResponseResult = authService.signIn(loginUser);
        //then
        assertThat(tokenApiResponseResult.isSuccess()).isFalse();
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
    void 로그아웃_성공() {

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