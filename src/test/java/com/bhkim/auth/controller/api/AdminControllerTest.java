package com.bhkim.auth.controller.api;

import com.bhkim.auth.config.security.JwtTokenProvider;
import com.bhkim.auth.config.security.MockSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
@Import({MockSecurityConfig.class})
class AdminControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("관리자 권한이 있는 사용자는 접근 가능")
    @WithMockUser(username="admin",roles={"ADMIN"})
    void adminAccessAllowed() throws Exception {
        mvc.perform(get("/admin/get"))
                .andExpect(status().isOk())
                .andDo(print()); // 200 응답을 기대
    }

    @Test
    @DisplayName("권한 없는 사용자는 접근 불가")
    @WithMockUser(username="admin",roles={"USER"})
    void adminAccessDenied() throws Exception {
        mvc.perform(get("/admin/get"))
                .andExpect(status().isForbidden())
                .andDo(print()); // 403 응답을 기대
    }
}