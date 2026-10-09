package com.pushkar.ratelimiter.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pushkar.ratelimiter.core.TokenBucket;

@Service
public class RateLimiterService {
    private final long capacity;
    private final double refillRate;

    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public RateLimiterService(@Value("${ratelimiter.capacity}") long capacity, @Value("${ratelimiter.refill-rate}") double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
    }

    public Boolean isAllowed(String cliendId) {
        TokenBucket bucket = buckets.computeIfAbsent(cliendId, id -> new TokenBucket(capacity, refillRate));
        return bucket.tryConsume();
    }
}
