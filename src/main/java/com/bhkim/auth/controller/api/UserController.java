package com.bhkim.auth.controller.api;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.AuthResponseDTO;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.security.CustomUserDetail;
import com.bhkim.auth.service.AuthService;
import com.bhkim.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/test")
    public ResponseEntity<ApiResponseResult<Void>> test() {
        return ResponseEntity.ok(ApiResponseResult.success(null));
    }

    @GetMapping("/sign-out")
    public ResponseEntity<ApiResponseResult<Void>> signOut() {
        return ResponseEntity.ok(userService.signOut());
    }

    @PostMapping("/reissueToken")
    public ResponseEntity<ApiResponseResult<AuthResponseDTO.Token>> reissueToken(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponseResult.success(userService.reissueToken()));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseResult<Boolean>> updateUser(@RequestBody @Valid UserRequestDTO.UpdateUserInfo updateUserInfo, @AuthenticationPrincipal CustomUserDetail user) {
        return ResponseEntity.ok(userService.updateUser(updateUserInfo, user.getId()));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponseResult<Boolean>> changePassword(@RequestBody @Valid UserRequestDTO.UpdatePassword updatePassword, @AuthenticationPrincipal CustomUserDetail user) {
        return ResponseEntity.ok(userService.changePassword(updatePassword, user.getId()));
    }
}
