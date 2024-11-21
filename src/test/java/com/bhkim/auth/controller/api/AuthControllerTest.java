package com.bhkim.auth.controller.api;

import com.bhkim.auth.config.JwtTokenProvider;
import com.bhkim.auth.dto.UserDto;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.mock.WithCustomMockUser;
import com.bhkim.auth.repository.AuthRepository;
import com.bhkim.auth.repository.UserRepository;
import com.bhkim.auth.service.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.bhkim.auth.common.TypeEnum.M;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ActiveProfiles("local") //테스트시 실행할 profile
@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    AuthServiceImpl authService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    AuthRepository authRepository; // AuthRepository를 모킹

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUpMember() {
        userRepository.save(getMember());
    }

    User getMember() {
        return User.builder()
                .id("bhkim")
                .name("김병호")
                .age(20)
                .sex(M)
                .build();
    }

    UserDto.UserInfo getMemberDto() {
        return UserDto.UserInfo.builder()
                .member(getMember())
                .build();
    }


    @Test
    @WithCustomMockUser // id = bhkim, role = USER
    void 룰_있는_경우() throws Exception {
        mvc.perform(get("/auth/role").header(
                        "X-AUTH-TOKEN", "TEST"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    void 룰_없는_경우() throws Exception {
        mvc.perform(get("/auth/other-role").header(
                        "X-AUTH-TOKEN", "TEST"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}