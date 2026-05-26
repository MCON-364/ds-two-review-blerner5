package edu.touro.mcon364.finalreview.treesandthreads.homework;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.*;

/**
 * Homework 3 - Concurrent Event Log (ConcurrentSkipListMap + ExecutorService)
 *
 * Scenario: a distributed system fires events from many threads at once.
 * Each event has a timestamp (epoch millis) and a message string.
 * The log must store events in time order, be safe for concurrent writes,
 * and support efficient range queries after all threads have finished.
 *
 * This homework practises:
 * - ConcurrentSkipListMap as a thread-safe sorted map (the concurrent TreeMap).
 * - Why we need a unique key strategy when two events arrive at the same
 *   millisecond — use AtomicLong as a tie-breaker.
 * - ExecutorService to simulate concurrent event sources.
 * - Stream operations on the resulting sorted map.
 *
 * Before coding, think about:
 * - What happens if two events share the same Long key in the map? One overwrites the other!
 * - ConcurrentSkipListMap is sorted by Long key. What chronological ordering does that give?
 * - headMap, tailMap, and subMap return live views of the map.
 *   What does "live" mean here, and is that safe after all writers have stopped?
 *
 * Requirements:
 * - logEvent(timestamp, message) records the event. If two events share a
 *   timestamp, both must be stored. Use composite key:
 *     timestamp * 1_000_000L + sequence.getAndIncrement()
 * - runConcurrentSources(sources, eventsEach) uses an ExecutorService to have
 *   each source log eventsEach events, then waits for completion.
 * - getEventsAfter(timestamp) returns all events logged after the given timestamp, in order.
 * - getEventsBetween(from, to) returns events in [from, to] inclusive.
 * - getMostRecentN(n) returns the n most recent events as an immutable list, newest first.
 *
 * Do not use synchronized blocks. Rely on ConcurrentSkipListMap and AtomicLong.
 */
public class ConcurrentEventLog {

    // Thread-safe sorted map: composite Long key -> event message
    private final ConcurrentSkipListMap<Long, String> log = new ConcurrentSkipListMap<>();
    // Used to make keys unique when two events arrive at the same millisecond
    private final AtomicLong sequence = new AtomicLong(0);

    /**
     * Records an event with the given timestamp.
     *
     * Composite key = timestamp * 1_000_000L + sequence.getAndIncrement()
     * This guarantees key uniqueness without disturbing chronological ordering.
     *
     * @param timestamp epoch milliseconds of the event
     * @param message   event description
     */
    public void logEvent(long timestamp, String message) {
        // TODO
    }

    /**
     * Simulates concurrent event sources.
     *
     * Each source name in the list runs in its own thread and calls logEvent
     * eventsEach times (use System.currentTimeMillis() for the timestamp and
     * sourceName + "-" + i as the message).
     *
     * @param sources    list of source names
     * @param eventsEach number of events each source logs
     */
    public void runConcurrentSources(List<String> sources, int eventsEach)
            throws InterruptedException {
        // TODO
    }

    /**
     * Returns all events logged strictly after the given timestamp, in order.
     *
     */
    public List<String> getEventsAfter(long timestamp) {
        // TODO
        return List.of();
    }

    /**
     * Returns all events in the timestamp range [from, to] inclusive, in order.
     */
    public List<String> getEventsBetween(long from, long to) {
        // TODO
        return List.of();
    }

    /**
     * Returns the n most recent events as an immutable list, newest first.
     */
    public List<String> getMostRecentN(int n) {
        // TODO
        return List.of();
    }

    /** Returns the total number of logged events. */
    public int size() {
        return log.size();
    }
}

