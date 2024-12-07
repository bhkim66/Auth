package com.bhkim.auth.controller.api;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.AuthResponseDTO;
import com.bhkim.auth.service.AuthService;
import com.bhkim.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/sign-out")
    public ResponseEntity<ApiResponseResult<Void>> signOut() {
        return ResponseEntity.ok(userService.signOut());
    }

    @PostMapping("/reissueToken")
    public ResponseEntity<ApiResponseResult<AuthResponseDTO.Token>> reissueToken() {
        return null;
//        return ResponseEntity.ok(ApiResponseResult.success(authService.reissueToken()));
    }

    @PutMapping("/update-user")
    public ResponseEntity<ApiResponseResult<Boolean>> updateUser(@RequestBody @Valid UserRequestDTO.UpdateUserInfo updateUserInfo) {
        return ResponseEntity.ok(userService.updateUser(updateUserInfo));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponseResult<Boolean>> changePassword(@RequestBody @Valid UserRequestDTO.UpdatePassword updatePassword) {
        return ResponseEntity.ok(userService.changePassword(updatePassword));
    }
}
