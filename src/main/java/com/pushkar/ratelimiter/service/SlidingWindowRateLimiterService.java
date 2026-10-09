package com.pushkar.ratelimiter.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pushkar.ratelimiter.core.SlidingWindowCounter;

@Service 
public class SlidingWindowRateLimiterService {
    private final int maxRequests;
    private final long windowSizeMillis;
    private final ConcurrentHashMap<String, SlidingWindowCounter> counters = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiterService(@Value("${slidingwindow.max-requests}") int maxRequests, @Value("${slidingwindow.window-size-millis}") long windowSizeMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
    }

    public boolean isAllowed(String clientId) {
        SlidingWindowCounter counter = counters.computeIfAbsent(clientId, id -> new SlidingWindowCounter(maxRequests, windowSizeMillis));
        return counter.tryConsume();
    }
}
