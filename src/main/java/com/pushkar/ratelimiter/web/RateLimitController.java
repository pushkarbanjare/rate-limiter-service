package com.pushkar.ratelimiter.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RateLimitController {
    @GetMapping("/api/check")
    public ResponseEntity<String> check(@RequestParam String clientId) {
        return  ResponseEntity.ok("Request allowed");
    }
}
