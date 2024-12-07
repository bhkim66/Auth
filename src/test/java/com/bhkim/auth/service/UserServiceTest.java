package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.config.security.JwtTokenProvider;
import com.bhkim.auth.dto.request.AuthRequestDTO;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.AuthResponseDTO;
import com.bhkim.auth.dto.response.UserResponseDTO;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.exception.ApiException;
import com.bhkim.auth.record.SignInRequest;
import com.bhkim.auth.record.UpdateUser;
import com.bhkim.auth.repository.UserRepository;
import com.bhkim.auth.service.impl.AuthServiceImpl;
import com.bhkim.auth.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static com.bhkim.auth.common.TypeEnum.M;
import static com.bhkim.auth.common.RoleEnum.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class UserServiceTest {
    @Autowired
    AuthServiceImpl authService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id("bhkim62")
                .password(passwordEncoder.encode("test1234"))
                .name("박병호")
                .role(USER)
                .age(35)
                .sex(M)
                .build();
        userRepository.save(user);

        UserRequestDTO.SignIn signIn = UserRequestDTO.SignIn.builder()
                .userId("bhkim62")
                .password("test1234")
                .build();
        authService.signIn(signIn);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }


    @Test
    void 특정_멤버_조회() {
        String userId = "bhkim62";
        // when
        UserResponseDTO.UserInfo memberInfo = userService.getMemberInfo(userId);

        // then
        // 반환된 결과가 성공 인지를 확인
        assertThat(memberInfo).extracting("id").isEqualTo(userId);
    }



    @Test
    void 멤버_정보_수정_성공() {
        String userId = "bhkim62";
        UpdateUser updateUser = new UpdateUser("김병호", 30, M, "01029292020");
        UserRequestDTO.UpdateUserInfo updateUserInfo = UserRequestDTO.UpdateUserInfo.builder()
                .name(updateUser.name())
                .age(updateUser.age())
                .sex(updateUser.sex())
                .phoneNumber(updateUser.phoneNumber())
                .build();

        //when
        ApiResponseResult<Boolean> result = userService.updateUser(updateUserInfo);

        em.flush();
        //then
        assertThat(result.getData()).isTrue();
    }

    @Test
    void 멤버_비밀번호_수정_성공() throws Exception {
        //given
        UserRequestDTO.UpdatePassword requestUserInfo = UserRequestDTO.UpdatePassword.builder()
                .password("qwer1234") // 비밀번호 암호화
                .build();
        String requestJson = "{\"userId\":\"bhkim62\", \"password\": \"test1234\"}";

        //토큰 값 추출
        String contentAsString = mvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //accessToken 값만 빼내오기
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(contentAsString);
        String accessToken = rootNode.path("data").path("accessToken").asText();
        System.out.println("contentAsString = " + accessToken);

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        //Authentication 객체 넣기
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when
        ApiResponseResult<Boolean> result = userService.changePassword(requestUserInfo);

        em.flush();
        //then
        assertThat(result.getData()).isTrue();
    }

    @Test
    void 멤버_비밀번호_이전과_동일_값_오류() {
        UserRequestDTO.UpdatePassword requestUserInfo = UserRequestDTO.UpdatePassword.builder()
                .password(passwordEncoder.encode("test1234")) // 비밀번호 암호화
                .build();
        //when
        assertThatThrownBy(() -> userService.changePassword(requestUserInfo)).isInstanceOf(ApiException.class).hasMessage("이전 비밀번호와 다른 비밀번호를 입력해주세요");
    }

    @Test
    void 로그아웃() {
        SignInRequest request = new SignInRequest("bhkim62", "test1234");
        UserRequestDTO.SignIn loginUser = UserRequestDTO.SignIn.builder()
                .userId(request.id())
                .password(request.password())
                .build();

        AuthResponseDTO.Token rawToken = authService.signIn(loginUser);
        System.out.println("rawToken = " + rawToken);

        userService.signOut();
    }

    @Test
    void 토큰재발급_성공() throws Exception {
        SignInRequest request = new SignInRequest("bhkim62", "test1234");
        UserRequestDTO.SignIn loginUser = UserRequestDTO.SignIn.builder()
                .userId(request.id())
                .password(request.password())
                .build();

        AuthResponseDTO.Token rawToken = authService.signIn(loginUser);
        System.out.println("rawToken = " + rawToken);


        AuthRequestDTO.Token token = AuthRequestDTO.Token.builder()
                .refreshToken(rawToken.getRefreshToken())
                .build();

        Thread.sleep(1000);

        AuthResponseDTO.Token reissueToken = userService.reissueToken(token);
        System.out.println("reissueToken = " + reissueToken);
        assertThat(rawToken.getAccessToken()).isNotEqualTo(reissueToken.getAccessToken());
        assertThat(rawToken.getRefreshToken()).isNotEqualTo(reissueToken.getRefreshToken());
    }

    @Test
    void 토큰재발급시_refreshToken가_만료된_경우() throws Exception {
        SignInRequest request = new SignInRequest("bhkim62", "test1234");
        UserRequestDTO.SignIn loginUser = UserRequestDTO.SignIn.builder()
                .userId(request.id())
                .password(request.password())
                .build();

        AuthResponseDTO.Token rawToken = authService.signIn(loginUser);
        System.out.println("rawToken = " + rawToken);


        AuthRequestDTO.Token token = AuthRequestDTO.Token.builder()
                .refreshToken(rawToken.getRefreshToken())
                .build();

        Thread.sleep(1000);


        AuthResponseDTO.Token reissueToken = userService.reissueToken(token);
        assertThat(rawToken.getAccessToken()).isNotEqualTo(reissueToken.getAccessToken());
        assertThat(rawToken.getRefreshToken()).isNotEqualTo(reissueToken.getRefreshToken());

        System.out.println("reissueToken = " + reissueToken);
    }
}