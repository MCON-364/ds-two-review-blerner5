package edu.touro.mcon364.finalreview.treesandthreads.model;
/**
 * A leaderboard entry used in the concurrent Trees and threads exercises.
 *
 * @param playerName name of the player
 * @param score      numeric score (higher is better)
 * @param timestamp  epoch millis when the score was recorded
 */
public record ScoreEntry(String playerName, int score, long timestamp)
        implements Comparable<ScoreEntry> {
    /**
     * Natural ordering: descending by score (highest first),
     * then ascending by playerName for a stable tie-break.
     */
    @Override
    public int compareTo(ScoreEntry other) {
        int s = Integer.compare(other.score, this.score); // descending
        if (s != 0) return s;
        return this.playerName.compareTo(other.playerName);
    }
}
