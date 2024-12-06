package com.bhkim.auth.controller.api;

import com.bhkim.auth.config.TestSecurityConfiguration;
import com.bhkim.auth.config.security.JwtTokenProvider;
import com.bhkim.auth.config.security.WebSecurityConfig;
import com.bhkim.auth.service.impl.AuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ActiveProfiles("default") //테스트시 실행할 profile
@WebMvcTest(controllers = AuthController.class)
@Import({WebSecurityConfig.class})
//@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    AuthServiceImpl authService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;



//    @MockBean
//    UserRepository userRepository;

//    @BeforeEach
//    void setUpMember() {
//        userRepository.save(getUser());
//    }
//
//    User getUser() {
//        return User.builder()
//                .id("bhkim")
//                .name("김병호")
//                .age(20)
//                .sex(M)
//                .role(ADMIN)
//                .build();
//    }

    @Test
    void signIn() throws Exception {
//        log.info("권한 = {} ", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        String requestJson = "{\"userId\":\"bhkim62\", \"password\": \"1234\"}";
        mvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 인증요청_필요한_api() throws Exception {
        mvc.perform(get("/user/sign-out").header(
                        "X-AUTH-TOKEN", ""))
                .andExpect(status().isOk());
    }
}