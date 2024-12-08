package com.bhkim.auth.controller.api;

import com.bhkim.auth.common.RoleEnum;
import com.bhkim.auth.config.security.JwtTokenProvider;
import com.bhkim.auth.config.security.WebSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest
@WebMvcTest(controllers = TestController.class)
@Import({WebSecurityConfig.class})
class TestControllerTest {
    @Autowired
    private MockMvc mvc;


    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = "USER")
    void ROLE이_맞는경우() throws Exception {
        mvc.perform(get("/test/health-check").header(
                        "X-AUTH-TOKEN", ""))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void ROLE이_다를경우() throws Exception {
        System.out.println("RoleEnum.USER.name() = " + RoleEnum.USER.name());
        System.out.println("RoleEnum.USER.name() = " + RoleEnum.USER);
        System.out.println("RoleEnum.USER.name() = " + RoleEnum.USER.getValue());
        mvc.perform(get("/test/health-check").header(
                        "X-AUTH-TOKEN", ""))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }
}