package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.request.AuthRequestDTO;
import com.bhkim.auth.dto.response.AuthResponseDTO;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.exception.ApiException;
import com.bhkim.auth.record.SignInRequest;
import com.bhkim.auth.record.SignUpRequest;
import com.bhkim.auth.repository.UserRepository;
import com.bhkim.auth.service.impl.AuthServiceImpl;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static com.bhkim.auth.common.RoleEnum.USER;
import static com.bhkim.auth.common.TypeEnum.M;
import static com.bhkim.auth.exception.ExceptionEnum.DUPLICATION_VALUE_IN_DATABASE_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
@Transactional
class AuthServiceTest {
    @Autowired
    AuthServiceImpl authService;

    @Autowired
    EntityManager em;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        User newUser = User.builder()
                .id("bhkim62")
                .password(passwordEncoder.encode("test1234"))
                .name("박병호")
                .role(USER)
                .age(35)
                .sex(M)
                .build();
        em.persist(newUser);
        em.flush();
    }

    @AfterEach
    void afterEach() {
        em.clear();
    }

    @Test
    void 로그인에_정상적으로_성공() {
        //given
        SignInRequest request = new SignInRequest("bhkim62", "test1234");
        AuthRequestDTO.SignIn loginUser = AuthRequestDTO.SignIn.builder()
                .userId(request.id())
                .password(request.password())
                .build();

        //when
        AuthResponseDTO.Token result = authService.signIn(loginUser);

        //then
        assertThat(result).extracting("accessToken").isNotNull();
        assertThat(result).extracting("refreshToken").isNotNull();
    }

    @Test
    void 로그인시_회원정보가_틀렸을_경우() {
        //given
        SignInRequest request = new SignInRequest("bhkim63", "1234qwer");
        AuthRequestDTO.SignIn loginUser = AuthRequestDTO.SignIn.builder()
                .userId(request.id())
                .password(request.password())
                .build();

        //when
        // 오류 사항 적을것
        assertThatThrownBy(() -> authService.signIn(loginUser)).hasMessage("아이디 또는 비밀번호를 잘못 입력했습니다.\n" + "입력하신 내용을 다시 확인해주세요.");
    }

    @Test
    void 회원가입() {
        //given
        SignUpRequest signup = new SignUpRequest("bhkim63", "test1234", "김병호", 30, M, "01029292020");
        AuthRequestDTO.Signup signupDTO = AuthRequestDTO.Signup.builder()
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
        User user = userRepository.findById(signup.id()).orElseThrow();

        // then
        // 반환된 결과가 성공 인지를 확인
        assertThat(user).extracting("id").isEqualTo(signup.id());
    }

    @Test
    void 회원가입_중복id_값_전달_실패() {
        // given
        SignUpRequest signup = new SignUpRequest("bhkim62", "test1234", "김병호", 30, M, "01029292020");
        AuthRequestDTO.Signup signupDTO = AuthRequestDTO.Signup.builder()
                .userId(signup.id())
                .password(signup.password())
                .name(signup.name())
                .age(signup.age())
                .sex(signup.sex())
                .phoneNumber(signup.phoneNumber())
                .build();
        // stubbing: memberRepository.save()가 호출되면 ApiException을 던지도록 설정
//        given(userRepository.save(any())).willThrow(new ApiException(DATABASE_INSERT_ERROR));

        // when
        // then
        assertThatThrownBy(() -> {
                    authService.signUp(signupDTO);
                    em.flush();
                }
        ).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void 회원가입_중복_ID_아닐경우() {
        // given
        String newId = "bhkim63";
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
        // stubbing: memberRepository.save()가 호출되면 ApiException을 던지도록 설정
//        given(userRepository.save(any())).willThrow(new ApiException(DATABASE_INSERT_ERROR));

        // when
        // then
        assertThatThrownBy(() -> authService.checkDuplicateId(newId)).isInstanceOf(ApiException.class).hasMessage(DUPLICATION_VALUE_IN_DATABASE_ERROR.getErrorMessage());
    }
}