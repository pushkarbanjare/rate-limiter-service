package com.pushkar.ratelimiter.core;

import java.util.ArrayDeque;
import java.util.Deque;

public class SlidingWindowCounter {
    private final int maxRequests;
    private final long windowSizeMillis;
    private final Deque<Long> requestTimestamps = new ArrayDeque<>();

    public SlidingWindowCounter(int maxRequests, long windowSizeMillis) {
        if(maxRequests <= 0) throw new IllegalArgumentException("maxRequests must be positive"); 
        if(windowSizeMillis <= 0) throw new IllegalArgumentException("windowSizeMillis must be positive");
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
    }

    public synchronized boolean tryConsume() {
        long now = System.currentTimeMillis();
        evictOldTimestamps(now);
        if(requestTimestamps.size() < maxRequests) {
            requestTimestamps.addLast(now);
            return true;
        }
        return false;
    }

    private void evictOldTimestamps(long now) {
        long windowStart = now - windowSizeMillis;
        while(!requestTimestamps.isEmpty() && requestTimestamps.peekFirst() < windowStart) {
            requestTimestamps.pollFirst();
        }
    }

    synchronized int currentCount() {
        evictOldTimestamps(System.currentTimeMillis());
        return requestTimestamps.size();
    }
}
