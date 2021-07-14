package com.gabriele.sctest.scheduer;

import org.junit.Test;

import java.util.function.Consumer;

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