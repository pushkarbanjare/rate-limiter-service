package com.pushkar.ratelimiter.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SlidingWindowCounterTest {

    @Test
    void allowsRequestsUpToLimit() {
        SlidingWindowCounter counter = new SlidingWindowCounter(5, 1000);
        for (int i = 0; i < 5; i++) {
            assertTrue(counter.tryConsume(), "request " + i + " should be allowed");
        }
    }

    @Test
    void rejectsRequestsBeyondLimit() {
        SlidingWindowCounter counter = new SlidingWindowCounter(3, 1000);

        for (int i = 0; i < 3; i++)
            counter.tryConsume();

        assertFalse(counter.tryConsume(), "4th request should be rejected");
    }

    @Test
    void slidesWindowAndAllowsAgainAfterExpiry() throws InterruptedException {
        SlidingWindowCounter counter = new SlidingWindowCounter(2, 200);

        counter.tryConsume();
        counter.tryConsume();
        assertFalse(counter.tryConsume(), "window should be full");

        Thread.sleep(250);

        assertTrue(counter.tryConsume(), "old requests should have expired, allowing a new one");
    }

    @Test
    void currentCountReflectsOnlyRequestsWithinWindow() throws InterruptedException {
        SlidingWindowCounter counter = new SlidingWindowCounter(5, 200);

        counter.tryConsume();
        counter.tryConsume();
        assertEquals(2, counter.currentCount());

        Thread.sleep(250);

        assertEquals(0, counter.currentCount(), "expired timestamps should not count");
    }

    @Test
    void rejectsInvalidMaxRequests() {
        assertThrows(IllegalArgumentException.class, () -> new SlidingWindowCounter(0, 1000));
    }

    @Test
    void rejectsInvalidWindowSize() {
        assertThrows(IllegalArgumentException.class, () -> new SlidingWindowCounter(5, 0));
    }
}