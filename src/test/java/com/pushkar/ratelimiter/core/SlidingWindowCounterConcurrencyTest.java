package com.pushkar.ratelimiter.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class SlidingWindowCounterConcurrencyTest {

    @Test
    @Timeout(10)
    void exactlyLimitRequestsSucceedUnderConcurrency() throws InterruptedException {
        int maxRequests = 10;
        SlidingWindowCounter counter = new SlidingWindowCounter(maxRequests, 5000);

        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startSignal.await();
                    if (counter.tryConsume()) {
                        successCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneSignal.countDown();
                }
            });
        }

        startSignal.countDown();
        doneSignal.await();
        executor.shutdown();

        assertEquals(maxRequests, successCount.get(),
                "exactly " + maxRequests + " requests should succeed, no more, no less");
    }
}