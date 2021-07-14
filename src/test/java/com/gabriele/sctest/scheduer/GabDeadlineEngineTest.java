package com.gabriele.sctest.scheduer;

import org.junit.Test;

import java.util.function.Consumer;

/**
 * This test is not Prod ready (it wasn't part of the requirements) or covering all scenarios, I just wrote them to support the development,
 * and I committed the latest version - past versions covered other scenarios
 */
public class GabDeadlineEngineTest {

    private final DeadlineEngine underTest = new GabDeadlineEngine();

    @Test
    public void getPools() {
        final Consumer<Long> consumer = aLong -> System.out.println("Received event for id: " + aLong);
        underTest.schedule(10L);
        //underTest.schedule(5L);
        //underTest.schedule(2L);
        //underTest.cancel(2);
        underTest.poll(7L, consumer, 10);
    }

}