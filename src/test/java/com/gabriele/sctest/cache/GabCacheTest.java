package com.gabriele.sctest.cache;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class GabCacheTest {

    private static int err = 0;

    @Test
    public void testMulti() throws InterruptedException {
        final Function<Integer, Integer> f = x -> {
            System.out.println("Computing for " + x);
            return x + 1;
        };

        final Cache<Integer, Integer> underTest = new GabCache<>(f);
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            service.execute(() -> {
                System.out.println("Computed value " + underTest.get(3));
                latch.countDown();
            });
        }
        latch.await();
    }

    @Test
    public void testMultiWithError() throws InterruptedException {
        final Function<Integer, Integer> f = x -> {
            if (err == 0) {
                err++;
                System.out.println("Throwing some error for " + x);
                throw new IllegalArgumentException("Some Error occourred!");
            }
            System.out.println("Computing for " + x);
            return x + 1;
        };

        final Cache<Integer, Integer> underTest = new GabCache<>(f);
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            service.execute(() -> {
                System.out.println("Computed value " + underTest.get(3));
                latch.countDown();
            });
        }
        latch.await();
        Thread.sleep(1000);
        for (int i = 0; i < 10; i++) {
            service.execute(() -> {
                System.out.println("Computed value " + underTest.get(3));
                latch.countDown();
            });
        }
        latch.await();
    }

}