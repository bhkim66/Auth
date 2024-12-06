package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.AuthResponseDTO;
import com.bhkim.auth.dto.response.UserResponseDTO;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.exception.ApiException;
import com.bhkim.auth.record.SignInRequest;
import com.bhkim.auth.record.SignUpRequest;
import com.bhkim.auth.repository.UserRepository;
import com.bhkim.auth.service.impl.AuthServiceImpl;
import com.bhkim.auth.service.impl.UserServiceImpl;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.bhkim.auth.common.TypeEnum.M;
import static com.bhkim.auth.common.UserRole.USER;
import static com.bhkim.auth.exception.ExceptionEnum.DUPLICATION_VALUE_IN_DATABASE_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class AuthServiceTest {
    @Autowired
    AuthServiceImpl authService;

    @Autowired
    EntityManager em;

    @Autowired
    UserServiceImpl userService;

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
                .userId(user.getId())
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
                .userId(request.id())
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
                .userId(request.id())
                .password(request.password())
                .build();

        //when
        // 오류 사항 적을것
//        AuthResponseDTO.Token result = authService.signIn(loginUser);
    }

    @Test
    void 회원가입() {
        //given
        Long userSeq = 1L;
        SignUpRequest signup = new SignUpRequest("bhkim62", "test1234", "김병호", 30, M, "01029292020");
        UserRequestDTO.Signup signupDTO = UserRequestDTO.Signup.builder()
                .userId(signup.id())
                .password(signup.password())
                .name(signup.name())
                .age(signup.age())
                .sex(signup.sex())
                .phoneNumber(signup.phoneNumber())
                .build();


        // stubbing: memberRepository.save()가 호출되면 member 객체를 반환하도록 설정
//        given(userRepository.save(any())).willReturn(signup);

        // when
        authService.signUp(signupDTO);
        em.flush();
        UserResponseDTO.UserInfo memberInfo = userService.getMemberInfo(userSeq);

        // then
        // 반환된 결과가 성공 인지를 확인
        assertThat(memberInfo).extracting("id").isEqualTo(signup.id());
    }

    @Test
    void 회원가입_중복id_값_전달_실패() {
        // given
        SignUpRequest signup = new SignUpRequest("bhkim62", "test1234", "김병호", 30, M, "01029292020");
        UserRequestDTO.Signup signupDTO = UserRequestDTO.Signup.builder()
                .userId(signup.id())
                .password(signup.password())
                .name(signup.name())
                .age(signup.age())
                .sex(signup.sex())
                .phoneNumber(signup.phoneNumber())
                .build();
        User existUser = User.builder()
                .id("bhkim62")
                .password("test1234")
                .name("박병호")
                .age(35)
                .sex(M)
                .build();
        userRepository.save(existUser);
        // stubbing: memberRepository.save()가 호출되면 ApiException을 던지도록 설정
//        given(userRepository.save(any())).willThrow(new ApiException(DATABASE_INSERT_ERROR));

        // when
        // then
        assertThatThrownBy(() -> authService.signUp(signupDTO)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void 회원가입_중복_ID_아닐경우() {
        // given
        String newId = "bhkim63";
        User existUser = User.builder()
                .id("bhkim62")
                .password("test1234")
                .name("박병호")
                .age(35)
                .sex(M)
                .build();
        userRepository.save(existUser);
        // stubbing: memberRepository.save()가 호출되면 ApiException을 던지도록 설정
//        given(userRepository.save(any())).willThrow(new ApiException(DATABASE_INSERT_ERROR));

        // when
        ApiResponseResult<Boolean> result = authService.checkDuplicateId(newId);
        // then
        assertThat(result.getData()).isTrue();
    }

    @Test
    void 회원가입_중복_ID() {
        // given
        String newId = "bhkim62";
        User existUser = User.builder()
                .id("bhkim62")
                .password("test1234")
                .name("박병호")
                .age(35)
                .sex(M)
                .build();
        userRepository.save(existUser);
        // stubbing: memberRepository.save()가 호출되면 ApiException을 던지도록 설정
//        given(userRepository.save(any())).willThrow(new ApiException(DATABASE_INSERT_ERROR));

        // when
        // then
        assertThatThrownBy(() -> authService.checkDuplicateId(newId)).isInstanceOf(ApiException.class).hasMessage(DUPLICATION_VALUE_IN_DATABASE_ERROR.getErrorMessage());
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
}