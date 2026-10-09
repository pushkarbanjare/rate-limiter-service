package com.pushkar.ratelimiter.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.pushkar.ratelimiter.core.TokenBucket;

@Service
public class RateLimiterService {
    private static final long DEFAULT_CAPACITY = 5;
    private static final double DEFAULT_REFILL_RATE = 1;

    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public Boolean isAllowed(String cliendId) {
        TokenBucket bucket = buckets.computeIfAbsent(cliendId,
                id -> new TokenBucket(DEFAULT_CAPACITY, DEFAULT_REFILL_RATE));
        return bucket.tryConsume();
    }
}
