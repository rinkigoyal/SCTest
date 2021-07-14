package com.gabriele.sctest.scheduer;

import lombok.extern.slf4j.Slf4j;

import java.util.PriorityQueue;
import java.util.function.Consumer;

@Slf4j
public class GabDeadlineEngine implements DeadlineEngine {

    private final PriorityQueue<SchedulerObject> queue;

    GabDeadlineEngine() {
        queue = new PriorityQueue<>();
    }

    /**
     * Time complexity is O(log n) (where n is the number of elements already in the queue)
     *
     * @param deadlineMs the millis
     * @return id of the scheduled object
     */
    @Override
    public long schedule(long deadlineMs) {
        final SchedulerObject schedulerObject = new SchedulerObject(deadlineMs);
        queue.add(schedulerObject);
        return schedulerObject.getId();
    }

    /**
     * Time complexity is O(n), if called often we should probably use another data structure
     *
     * @param requestId identifier to cancel.
     * @return true if element found and removed, false otherwise
     */
    @Override
    public boolean cancel(long requestId) {
        return queue.removeIf(e -> e.getId() == requestId);
    }

    /**
     * Time complexity for maxPoll elements is O(maxPoll) - using priorityQueue
     *
     * @param nowMs   time in millis since epoch to check deadlines against.
     * @param handler to call with identifier of expired deadlines.
     * @param maxPoll count of maximum number of expired deadlines to process.
     * @return number of elements polled
     */
    @Override
    public int poll(long nowMs, Consumer<Long> handler, int maxPoll) {
        int currentPoll = 0;
        while (queue.peek() != null && nowMs >= queue.peek().getDeadlineMs() && currentPoll < maxPoll) {
            final SchedulerObject currentElement = queue.poll();
            handler.accept(currentElement.getId());
            currentPoll++;
        }
        return currentPoll;
    }

    @Override
    public int size() {
        return queue.size();
    }
}
