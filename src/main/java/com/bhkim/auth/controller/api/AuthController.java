package com.bhkim.auth.controller.api;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.request.AuthRequestDTO;
import com.bhkim.auth.dto.response.AuthResponseDTO;
import com.bhkim.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponseResult<AuthResponseDTO.Token>> signIn(@RequestBody AuthRequestDTO.SignIn signIn) {
        return ResponseEntity.ok(ApiResponseResult.success(authService.signIn(signIn)));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponseResult<Void>> signUp(@RequestBody @Valid AuthRequestDTO.Signup signup) {
        return ResponseEntity.ok(authService.signUp(signup));
    }


}
