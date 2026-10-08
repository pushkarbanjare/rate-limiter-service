package com.pushkar.ratelimiter.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TokenBucketTest {
    
    @Test 
    void allowsRequestsUpToCapacity() {
        TokenBucket bucket = new TokenBucket(5, 1);
        for (int i = 0; i < 5; i++) {
            assertTrue(bucket.tryConsume(), "request " + i + " should be allowed");
        }
    }

    @Test 
    void rejectsRequestsBeyondCapacity() {
        TokenBucket bucket = new TokenBucket(3, 1);
        for (int i = 0; i < 3; i++) {
            bucket.tryConsume();
        }
        assertFalse(bucket.tryConsume(), "4th request should be rejected");
    }

    @Test 
    void refillsTokensOverTime() throws InterruptedException {
        TokenBucket bucket = new TokenBucket(2, 10);
        bucket.tryConsume();
        bucket.tryConsume();
        assertFalse(bucket.tryConsume(), "bucket should be empty");
        Thread.sleep(150);
        assertTrue(bucket.tryConsume(), "token should have refilled");
    }

    @Test 
    void rejectsInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new TokenBucket(0, 1));
    }

    @Test 
    void rejectsInvalidRefillRate() {
        assertThrows(IllegalArgumentException.class, () -> new TokenBucket(5, 0));
    }
}
