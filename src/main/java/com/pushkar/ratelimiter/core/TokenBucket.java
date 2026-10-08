package com.pushkar.ratelimiter.core;

public class TokenBucket {
    private final long capacity;
    private final double refillTokensPerSecond;
    private double availableTokens;
    private long lastRefillTimestamp;

    public TokenBucket(long capacity, double refillTokensPerSecond) {
        if(capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        if(refillTokensPerSecond <= 0) throw new IllegalArgumentException("refillTokensPerSecond must be positive");

        this.capacity = capacity;
        this.refillTokensPerSecond = refillTokensPerSecond;
        this.availableTokens = capacity;
        this.lastRefillTimestamp = System.nanoTime();
    }

    public synchronized boolean tryConsume() {
        refill();
        if (availableTokens >= 1) {
            availableTokens -= 1;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.nanoTime();
        double secondsElapsed = (now - lastRefillTimestamp) / 1_000_000_000.0;
        double tokensToAdd = secondsElapsed * refillTokensPerSecond;
        
        if(tokensToAdd > 0) {
            availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
            lastRefillTimestamp = now;
        }
    }

    synchronized double getAvailableTokens() {
        refill();
        return availableTokens;
    }
}
