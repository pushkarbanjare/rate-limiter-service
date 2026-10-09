package com.pushkar.ratelimiter.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class TokenBucketConcurrencyTest {

    @Test
    @Timeout(10)
    void exactlyCapacityRequestsSucceedUnderConcurrency() throws InterruptedException {
        int capacity = 10;
        // Refill rate set near-zero so no meaningful refill happens during the test
        TokenBucket bucket = new TokenBucket(capacity, 0.0001);

        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startSignal = new CountDownLatch(1); // holds all threads at the gate
        CountDownLatch doneSignal = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startSignal.await(); // wait for the "go" signal
                    if (bucket.tryConsume()) {
                        successCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneSignal.countDown();
                }
            });
        }

        startSignal.countDown(); // release all 50 threads at once
        doneSignal.await();      // wait for all to finish
        executor.shutdown();

        assertEquals(capacity, successCount.get(), "exactly " + capacity + " requests should succeed, no more, no less");
    }
}