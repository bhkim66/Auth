package com.bhkim.auth.controller.api;

import com.bhkim.auth.common.ApiResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping(value = "/get")
    public ResponseEntity<ApiResponseResult<String>> get() {
        ApiResponseResult<String> result = ApiResponseResult.success("test");
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/post")
    public ResponseEntity<ApiResponseResult<String>> post() {
        ApiResponseResult<String> result = ApiResponseResult.success("test");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health-check")
    public ResponseEntity<ApiResponseResult<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponseResult.success("ok"));
    }

}
