package com.bhkim.auth.controller.api;

import com.bhkim.auth.entity.ApiResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping(value = "/get")
    public ResponseEntity<ApiResponseResult<String>> test() {
        log.info("test!");
        ApiResponseResult<String> result = ApiResponseResult.success("test");
        return ResponseEntity.ok(result);
    }
}
