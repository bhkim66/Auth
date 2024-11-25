package com.bhkim.auth.controller.api;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.AuthDTO;
import com.bhkim.auth.dto.UserRequestDTO;
import com.bhkim.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;


    @PostMapping("/sign-in")
    public ApiResponseResult<AuthDTO.Token> signIn(@RequestBody UserRequestDTO.Signup signup) {
        ApiResponseResult<AuthDTO.Token> result = authService.signIn(signup);
        return result;
    }

    @GetMapping("/role")
    public ApiResponseResult<String> hasRole() {
        ApiResponseResult<HttpStatus> result = authService.signOut();
        return ApiResponseResult.success("test");
    }
}
