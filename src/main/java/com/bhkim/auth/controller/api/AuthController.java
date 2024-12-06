package com.bhkim.auth.controller.api;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.AuthResponseDTO;
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
    public ResponseEntity<ApiResponseResult<AuthResponseDTO.Token>> signIn(@RequestBody UserRequestDTO.SignIn signup) {
        return ResponseEntity.ok(ApiResponseResult.success(authService.signIn(signup)));
    }

    @GetMapping("/sign-out")
    public ResponseEntity<ApiResponseResult<Void>> signOut() {
        return ResponseEntity.ok(authService.signOut());
    }

    @PostMapping("/reissueToken")
    public ResponseEntity<ApiResponseResult<AuthResponseDTO.Token>> reissueToken() {
        return null;
//        return ResponseEntity.ok(ApiResponseResult.success(authService.reissueToken()));
    }

}
