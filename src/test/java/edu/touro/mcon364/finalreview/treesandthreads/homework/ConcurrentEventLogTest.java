package edu.touro.mcon364.finalreview.treesandthreads.homework;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ConcurrentEventLogTest {

    // ── logEvent / size ───────────────────────────────────────────────────────

    @Test
    void sizeIsZeroInitially() {
        assertEquals(0, new ConcurrentEventLog().size());
    }

    @Test
    void logEventIncrementsSize() {
        ConcurrentEventLog log = new ConcurrentEventLog();
        log.logEvent(1000L, "first");
        log.logEvent(2000L, "second");
        assertEquals(2, log.size());
    }

    @Test
    void logEventStoresBothEventsWithSameTimestamp() {
        ConcurrentEventLog log = new ConcurrentEventLog();
        log.logEvent(1000L, "alpha");
        log.logEvent(1000L, "beta");
        assertEquals(2, log.size()); // composite key prevents overwrite
    }

    // ── getEventsAfter ────────────────────────────────────────────────────────

    @Test
    void getEventsAfterReturnsEventsStrictlyAfterTimestamp() {
        ConcurrentEventLog log = new ConcurrentEventLog();
        log.logEvent(1000L, "old");
        log.logEvent(2000L, "new");
        List<String> result = log.getEventsAfter(1000L);
        assertFalse(result.contains("old"));
        assertTrue(result.contains("new"));
    }

    @Test
    void getEventsAfterReturnsEmptyWhenNoneQualify() {
        ConcurrentEventLog log = new ConcurrentEventLog();
        log.logEvent(500L, "early");
        assertTrue(log.getEventsAfter(1000L).isEmpty());
    }

    @Test
    void getEventsAfterIsInChronologicalOrder() {
        ConcurrentEventLog log = new ConcurrentEventLog();
        log.logEvent(3000L, "third");
        log.logEvent(2000L, "second");
        List<String> result = log.getEventsAfter(1000L);
        assertEquals("second", result.get(0));
        assertEquals("third",  result.get(1));
    }

    // ── getEventsBetween ──────────────────────────────────────────────────────

    @Test
    void getEventsBetweenIsInclusiveOnBothEnds() {
        ConcurrentEventLog log = new ConcurrentEventLog();
        log.logEvent(1000L, "at-from");
        log.logEvent(2000L, "in-middle");
        log.logEvent(3000L, "at-to");
        log.logEvent(4000L, "outside");
        List<String> result = log.getEventsBetween(1000L, 3000L);
        assertTrue(result.contains("at-from"));
        assertTrue(result.contains("in-middle"));
        assertTrue(result.contains("at-to"));
        assertFalse(result.contains("outside"));
    }

    @Test
    void getEventsBetweenReturnsEmptyWhenNoneInRange() {
        ConcurrentEventLog log = new ConcurrentEventLog();
        log.logEvent(500L,  "too-early");
        log.logEvent(9000L, "too-late");
        assertTrue(log.getEventsBetween(1000L, 8000L).isEmpty());
    }

    // ── getMostRecentN ────────────────────────────────────────────────────────

    @Test
    void getMostRecentNReturnsNewestFirst() {
        ConcurrentEventLog log = new ConcurrentEventLog();
        log.logEvent(1000L, "old");
        log.logEvent(2000L, "middle");
        log.logEvent(3000L, "newest");
        List<String> result = log.getMostRecentN(2);
        assertEquals("newest", result.get(0));
        assertEquals("middle", result.get(1));
    }

    @Test
    void getMostRecentNRespectsLimit() {
        ConcurrentEventLog log = new ConcurrentEventLog();
        log.logEvent(1000L, "a");
        log.logEvent(2000L, "b");
        log.logEvent(3000L, "c");
        assertEquals(2, log.getMostRecentN(2).size());
    }

    @Test
    void getMostRecentNWithNLargerThanSizeReturnsAll() {
        ConcurrentEventLog log = new ConcurrentEventLog();
        log.logEvent(1000L, "only");
        assertEquals(1, log.getMostRecentN(100).size());
    }

    @Test
    void getMostRecentNIsImmutable() {
        ConcurrentEventLog log = new ConcurrentEventLog();
        log.logEvent(1000L, "event");
        assertThrows(Exception.class, () -> log.getMostRecentN(1).clear());
    }

    // ── runConcurrentSources ──────────────────────────────────────────────────

    @Test
    void runConcurrentSourcesStoresAllEvents() throws InterruptedException {
        ConcurrentEventLog log = new ConcurrentEventLog();
        List<String> sources = List.of("A", "B", "C", "D");
        int eventsEach = 100;
        log.runConcurrentSources(sources, eventsEach);
        assertEquals(sources.size() * eventsEach, log.size());
    }

    @Test
    void runConcurrentSourcesWithSingleSourceWorks() throws InterruptedException {
        ConcurrentEventLog log = new ConcurrentEventLog();
        log.runConcurrentSources(List.of("solo"), 10);
        assertEquals(10, log.size());
    }
}
