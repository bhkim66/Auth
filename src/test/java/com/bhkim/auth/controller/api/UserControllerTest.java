package com.bhkim.auth.controller.api;

import com.bhkim.auth.config.security.JwtTokenProvider;
import com.bhkim.auth.config.security.MockSecurityConfig;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.bhkim.auth.common.TypeEnum.M;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({MockSecurityConfig.class})
class UserControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    UserService userService;

    @Test
    void signOut() {
    }

    @Test
    void reissueToken() {
    }

    @Test
    @WithMockUser(username = "bhkim62", roles = "USER")
    void updateUser() throws Exception {
        UserRequestDTO.UpdateUserInfo resource = UserRequestDTO.UpdateUserInfo.builder()
                .userId("bhkim62")
                .name("정병호")
                .age(31)
                .sex(M)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String resourceJson = objectMapper.writeValueAsString(resource);

        mvc.perform(put("/user/update-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceJson))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void changePassword() {
    }
}