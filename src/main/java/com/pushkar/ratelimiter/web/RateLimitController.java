package com.pushkar.ratelimiter.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pushkar.ratelimiter.service.RateLimiterService;

@RestController
public class RateLimitController {
    private final RateLimiterService rateLimiterService;

    public RateLimitController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/api/check")
    public ResponseEntity<String> check(@RequestParam String clientId) {
        boolean allowed = rateLimiterService.isAllowed(clientId);
        if (allowed)
            return ResponseEntity.ok("Request allowed");
        else
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded. Try again later");
    }
}
