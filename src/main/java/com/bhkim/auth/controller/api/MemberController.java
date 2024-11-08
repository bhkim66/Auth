package com.bhkim.auth.controller.api;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.MemberDto;
import com.bhkim.auth.service.MemberService;
import com.bhkim.auth.service.MemberServiceImpl;
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
public class MemberController {

    private final MemberService memberService;

    @RequestMapping("/health-check")
    public ApiResponseResult<String> healthCheck() {
        return ApiResponseResult.success("ok");
    }

    @RequestMapping("/sign-up")
    public ApiResponseResult<HttpStatus> signUp(@RequestBody @Valid MemberDto.MemberInfo memberInfo) {
        ApiResponseResult<HttpStatus> result = memberService.signUp(memberInfo);
        return result;
    }


}
