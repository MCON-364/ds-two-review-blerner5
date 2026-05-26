package edu.touro.mcon364.finalreview.treesandthreads.exercises;

import edu.touro.mcon364.finalreview.treesandthreads.model.ScoreEntry;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ConcurrentLeaderboardTest {

    // ── helpers ───────────────────────────────────────────────────────────────

    private ScoreEntry entry(String player, int score) {
        return new ScoreEntry(player, score, System.currentTimeMillis());
    }

    // ── submitScore / getTotalSubmissions ─────────────────────────────────────

    @Test
    void totalSubmissionsIsZeroInitially() {
        assertEquals(0, new ConcurrentLeaderboard().getTotalSubmissions());
    }

    @Test
    void submitScoreIncrementsTotalSubmissions() {
        ConcurrentLeaderboard lb = new ConcurrentLeaderboard();
        lb.submitScore(entry("Alice", 500));
        lb.submitScore(entry("Bob", 700));
        assertEquals(2, lb.getTotalSubmissions());
    }

    // ── getTopN ───────────────────────────────────────────────────────────────

    @Test
    void getTopNWhenEmptyReturnsEmptyList() {
        assertTrue(new ConcurrentLeaderboard().getTopN(5).isEmpty());
    }

    @Test
    void getTopNWithZeroReturnsEmptyList() {
        ConcurrentLeaderboard lb = new ConcurrentLeaderboard();
        lb.submitScore(entry("Alice", 100));
        assertTrue(lb.getTopN(0).isEmpty());
    }

    @Test
    void getTopNReturnsHighestScoresFirst() {
        ConcurrentLeaderboard lb = new ConcurrentLeaderboard();
        lb.submitScore(entry("Alice", 300));
        lb.submitScore(entry("Bob",   700));
        lb.submitScore(entry("Carol", 500));
        List<ScoreEntry> top = lb.getTopN(2);
        assertEquals(2, top.size());
        assertEquals(700, top.get(0).score());
        assertEquals(500, top.get(1).score());
    }

    @Test
    void getTopNWithNLargerThanSizeReturnsAll() {
        ConcurrentLeaderboard lb = new ConcurrentLeaderboard();
        lb.submitScore(entry("Alice", 100));
        lb.submitScore(entry("Bob",   200));
        assertEquals(2, lb.getTopN(100).size());
    }

    @Test
    void getTopNIsImmutable() {
        ConcurrentLeaderboard lb = new ConcurrentLeaderboard();
        lb.submitScore(entry("Alice", 100));
        List<ScoreEntry> top = lb.getTopN(1);
        assertThrows(Exception.class, () -> top.clear());
    }

    // ── runSimulation — concurrent correctness ────────────────────────────────

    @Test
    void runSimulationSubmitsCorrectTotalCount() throws InterruptedException {
        ConcurrentLeaderboard lb = new ConcurrentLeaderboard();
        List<String> players = List.of("Alice", "Bob", "Carol", "Dan");
        int scoresEach = 50;
        lb.runSimulation(players, scoresEach);
        assertEquals(players.size() * scoresEach, lb.getTotalSubmissions());
    }

    @Test
    void runSimulationWithSinglePlayerStillWorks() throws InterruptedException {
        ConcurrentLeaderboard lb = new ConcurrentLeaderboard();
        lb.runSimulation(List.of("Solo"), 10);
        assertEquals(10, lb.getTotalSubmissions());
    }
}
