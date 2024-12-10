package com.bhkim.auth.controller.api;

import com.bhkim.auth.config.security.JwtTokenProvider;
import com.bhkim.auth.config.security.MockSecurityConfig;
import com.bhkim.auth.mock.WithCustomMockUser;
import com.bhkim.auth.service.impl.AuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
//@ActiveProfiles("default") //테스트시 실행할 profile
@WebMvcTest(controllers = AuthController.class)
@Import({MockSecurityConfig.class})
class AuthControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    AuthServiceImpl authService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @Test
    @WithCustomMockUser
    void signIn() throws Exception {
        String requestJson = "{\"userId\":\"bhkim62\", \"password\": \"1234\"}";
        mvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andDo(print());
    }
}