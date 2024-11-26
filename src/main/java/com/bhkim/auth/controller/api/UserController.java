package com.bhkim.auth.controller.api;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class UserController {
    private final UserService userService;

    @RequestMapping("/health-check")
    public ApiResponseResult<String> healthCheck() {
        return ApiResponseResult.success("ok");
    }

    @RequestMapping("/sign-up")
    public ApiResponseResult<HttpStatus> signUp(@RequestBody @Valid UserRequestDTO.Signup signup) {
        ApiResponseResult<HttpStatus> result = userService.signUp(signup);
        return result;
    }


}
