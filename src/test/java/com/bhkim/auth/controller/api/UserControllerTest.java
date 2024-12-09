package com.bhkim.auth.controller.api;

import com.bhkim.auth.common.RoleEnum;
import com.bhkim.auth.config.security.JwtTokenProvider;
import com.bhkim.auth.config.security.MockSecurityConfig;
import com.bhkim.auth.config.security.WebSecurityConfig;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.mock.WithCustomMockUser;
import com.bhkim.auth.security.CustomUserDetail;
import com.bhkim.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.bhkim.auth.common.TypeEnum.M;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@WebMvcTest(controllers = UserController.class)
@Import(MockSecurityConfig.class)
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

    @BeforeEach
    void setUp() {
//        CustomUserDetail userDetail = new CustomUserDetail(
//                User.builder()
//                        .id("bhkim62")
//                        .role(RoleEnum.USER)
//                        .build()
//        );
//        UsernamePasswordAuthenticationToken authentication =
//                new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
//
//        // SecurityContextHolder에 인증 정보 설정
//        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void signOut() {
    }

    @Test
    void reissueToken() {
    }

    @Test
    @WithCustomMockUser
    void 작성자가_자신의_데이터_수정() throws Exception {
        UserRequestDTO.UpdateUserInfo resource = UserRequestDTO.UpdateUserInfo.builder()
                .userId("bhkim62")
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
    @WithCustomMockUser
    void 다른_사람의_데이터를_수정_오류발생() throws Exception {
        UserRequestDTO.UpdateUserInfo resource = UserRequestDTO.UpdateUserInfo.builder()
                .userId("TAIN2")
                .name("박타인")
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

    @Test
    void changePassword() {
    }
}