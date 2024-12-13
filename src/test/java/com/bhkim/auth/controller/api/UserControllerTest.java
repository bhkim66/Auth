package com.bhkim.auth.controller.api;

import com.bhkim.auth.config.security.JwtTokenProvider;
import com.bhkim.auth.config.security.WebSecurityConfig;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.mock.WithCustomMockUser;
import com.bhkim.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static com.bhkim.auth.common.RoleEnum.*;
import static com.bhkim.auth.common.TypeEnum.M;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@WebMvcTest(controllers = {UserController.class})
@Import(WebSecurityConfig.class)
@EnableMethodSecurity
class UserControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    UserService userService;

    @MockBean
    UserDetailsService userDetailsService; // MockBean 사용

//    @Test
    void 인증_없는_요청_제한() throws Exception {
        mvc.perform(get("/user/test"))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

//    @Test
    @WithCustomMockUser
    void 인증_정보가_있는_요청() throws Exception {
        UserRequestDTO.UpdateUserInfo resource = UserRequestDTO.UpdateUserInfo.builder()
                .name("정병호")
                .age(31)
                .sex(M)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String resourceJson = objectMapper.writeValueAsString(resource);

        mvc.perform(put("/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceJson))
                .andExpect(status().isOk())
                .andDo(print());
    }



    @Test
    @WithCustomMockUser(id = "bhkim622", role = USER)
    void USER_권한_유저_테스트() throws Exception {
        UserRequestDTO.UpdateUserInfo resource = UserRequestDTO.UpdateUserInfo.builder()
                .name("정병호")
                .age(31)
                .sex(M)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String resourceJson = objectMapper.writeValueAsString(resource);

        mvc.perform(put("/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceJson))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithCustomMockUser(id = "guest011", role = GUEST)
    void user만_접근_가능한_guest_권한이_접근() throws Exception {
        UserRequestDTO.UpdateUserInfo resource = UserRequestDTO.UpdateUserInfo.builder()
                .name("정병호")
                .age(31)
                .sex(M)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String resourceJson = objectMapper.writeValueAsString(resource);

        mvc.perform(put("/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceJson))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}