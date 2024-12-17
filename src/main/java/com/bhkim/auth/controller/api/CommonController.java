package com.bhkim.auth.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common")
public class CommonController {
    @GetMapping("health-test")
    public ResponseEntity<String> healthTest() {
        return ResponseEntity.ok("ok");
    }
}
