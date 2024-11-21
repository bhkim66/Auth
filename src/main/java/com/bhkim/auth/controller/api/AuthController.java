package com.bhkim.auth.controller.api;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.AuthDto;
import com.bhkim.auth.dto.MemberDto;
import com.bhkim.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;


    @PostMapping("/sign-in")
    public ApiResponseResult<AuthDto.Token> signIn(@RequestBody MemberDto.MemberInfo memberInfo) {
        ApiResponseResult<AuthDto.Token> result = authService.signIn(memberInfo);
        return result;
    }

    @GetMapping("/role")
    public ApiResponseResult<String> hasRole() {
        ApiResponseResult<HttpStatus> result = authService.signOut();
        return ApiResponseResult.success("test");
    }
}
