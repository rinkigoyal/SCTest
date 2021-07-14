package com.gabriele.sctest.scheduer;

import lombok.Getter;

@Getter
public class SchedulerObject implements Comparable<SchedulerObject> {
    private final long id;
    private final long deadlineMs;
    private static long currentId = 0;

    @Override
    public int compareTo(final SchedulerObject o) {
        return Long.compare(this.deadlineMs, o.deadlineMs);
    }

    SchedulerObject(long deadlineMs) {
        this.deadlineMs = deadlineMs;
        this.id = currentId++;
    }
}
