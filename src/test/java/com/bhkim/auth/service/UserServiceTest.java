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
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

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
    void setUp() throws Exception {
        User user = User.builder()
                .id("bhkim62")
                .password(passwordEncoder.encode("test1234"))
                .name("박병호")
                .role(USER)
                .age(35)
                .sex(M)
                .build();
        userRepository.save(user);

        // 로그인
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
        String refreshToken = rootNode.path("data").path("refreshToken").asText();
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        //Authentication 객체 넣기
        SecurityContextHolder.getContext().setAuthentication(authentication);
//        UserRequestDTO.SignIn signIn = UserRequestDTO.SignIn.builder()
//                .userId("bhkim62")
//                .password("test1234")
//                .build();
//        authService.signIn(signIn);
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
    void 멤버_정보_수정_성공() throws Exception {
        //given
        String userId = "bhkim62";
        UpdateUser updateUser = new UpdateUser("김병호", 30, M, "01029292020");
        UserRequestDTO.UpdateUserInfo updateUserInfo = UserRequestDTO.UpdateUserInfo.builder()
                .name(updateUser.name())
                .age(updateUser.age())
                .sex(updateUser.sex())
                .phoneNumber(updateUser.phoneNumber())
                .build();

        //when
        ApiResponseResult<Boolean> result = userService.updateUser(updateUserInfo, userId);

        em.flush();
        //then
        assertThat(result.getData()).isTrue();
    }

    @Test
    void 멤버_비밀번호_수정_성공() throws Exception {
        //given
        String userId = "bhkim62";
        UserRequestDTO.UpdatePassword requestUserInfo = UserRequestDTO.UpdatePassword.builder()
                .password("qwer1234") // 비밀번호 암호화
                .build();

        //when
        ApiResponseResult<Boolean> result = userService.changePassword(requestUserInfo, userId);

        em.flush();
        //then
        assertThat(result.getData()).isTrue();
    }

    @Test
    void 멤버_비밀번호_이전과_동일_값_오류() throws Exception {
        String userId = "bhkim62";
        UserRequestDTO.UpdatePassword requestUserInfo = UserRequestDTO.UpdatePassword.builder()
                .password("test1234") // 비밀번호 암호화
                .build();

        //when, then
        assertThatThrownBy(() -> userService.changePassword(requestUserInfo, userId)).isInstanceOf(ApiException.class).hasMessage("이전 비밀번호와 다른 비밀번호를 입력해주세요");
    }

    @Test
    void 로그아웃() throws Exception {
        // when
        userService.signOut();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // then
        assertThat(authentication).isNull();
    }

    @Test
    void 토큰재발급_성공() throws Exception {
        // given
        String requestJson = "{\"userId\":\"bhkim62\", \"password\": \"test1234\"}";

        //토큰 값 추출
        String contentAsString = mvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //accessToken 값만 빼내오기
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(contentAsString);
        String accessToken = rootNode.path("data").path("accessToken").asText();
        String refreshToken = rootNode.path("data").path("refreshToken").asText();
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        //Authentication 객체 넣기
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Thread.sleep(1000);

        UserRequestDTO.RefreshToken rt = UserRequestDTO.RefreshToken.builder()
                .refreshToken(refreshToken)
                .build();

        // MockHttpServletRequest 생성
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();

        // 헤더 값 추가
        mockRequest.addHeader("Authorization", refreshToken);

        // MockHttpServletRequest를 ServletRequestAttributes에 설정
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);

        // RequestContextHolder에 해당 요청 속성 설정
        RequestContextHolder.setRequestAttributes(attributes, true);

        // when
        AuthResponseDTO.Token reissueToken = userService.reissueToken();

        // then
        assertThat(accessToken).isNotEqualTo(reissueToken.getAccessToken());
        assertThat(refreshToken).isNotEqualTo(reissueToken.getRefreshToken());
    }

    @Test
    void 토큰재발급시_refreshToken의_유효하지_않을때() throws Exception {
        // given
        String requestJson = "{\"userId\":\"bhkim62\", \"password\": \"test1234\"}";

        //토큰 값 추출
        String contentAsString = mvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //accessToken 값만 빼내오기
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(contentAsString);
        String accessToken = rootNode.path("data").path("accessToken").asText();
        String refreshToken = rootNode.path("data").path("refreshToken").asText();
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        //Authentication 객체 넣기
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // MockHttpServletRequest 생성
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();

        // 헤더 값 추가
        mockRequest.addHeader("Authorization", refreshToken + "test");

        // MockHttpServletRequest를 ServletRequestAttributes에 설정
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);

        // RequestContextHolder에 해당 요청 속성 설정
        RequestContextHolder.setRequestAttributes(attributes, true);

        Thread.sleep(1000);


        // when, then
        assertThatThrownBy(() -> userService.reissueToken()).isInstanceOf(ApiException.class).hasMessage("유효하지 않은 토큰 입니다");
    }
}