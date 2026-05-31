package edu.touro.mcon364.finalreview.treesandthreads.exercises;

import edu.touro.mcon364.finalreview.treesandthreads.exercises.WordFrequencyCounter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class WordFrequencyCounterTest {

    // ── helpers ───────────────────────────────────────────────────────────────

    private WordFrequencyCounter of(String... words) {
        return new WordFrequencyCounter(List.of(words));
    }

    // ── constructor ───────────────────────────────────────────────────────────

    @Test
    void constructorThrowsOnNullList() {
        assertThrows(Exception.class, () -> new WordFrequencyCounter(null));
    }

    @Test
    void constructorDefendsAgainstExternalMutation() {
        List<String> words = new ArrayList<>(List.of("apple", "banana"));
        WordFrequencyCounter counter = new WordFrequencyCounter(words);
        words.clear();
        // internal copy is unaffected — map should still have 2 entries
        assertEquals(2, counter.buildFrequencyMap().size());
    }

    // ── empty list ────────────────────────────────────────────────────────────

    @Test
    void buildFrequencyMapIsEmptyForEmptyList() {
        assertTrue(new WordFrequencyCounter(List.of()).buildFrequencyMap().isEmpty());
    }

    @Test
    void getTopNReturnsEmptyListForEmptyInput() {
        assertTrue(new WordFrequencyCounter(List.of()).getTopN(5).isEmpty());
    }

    @Test
    void getWordsStartingWithReturnsEmptyForEmptyInput() {
        assertTrue(new WordFrequencyCounter(List.of()).getWordsStartingWith('a').isEmpty());
    }

    @Test
    void getMostFrequentInRangeIsEmptyForEmptyInput() {
        assertTrue(new WordFrequencyCounter(List.of()).getMostFrequentInRange("a", "z").isEmpty());
    }

    // ── buildFrequencyMap ─────────────────────────────────────────────────────

    @Test
    void buildFrequencyMapCountsCorrectly() {
        WordFrequencyCounter counter = of("apple", "banana", "apple", "cherry", "banana", "apple");
        Map<String, Long> map = counter.buildFrequencyMap();
        assertEquals(3L, map.get("apple"));
        assertEquals(2L, map.get("banana"));
        assertEquals(1L, map.get("cherry"));
    }

    @Test
    void buildFrequencyMapIsSortedAlphabetically() {
        WordFrequencyCounter counter = of("cherry", "apple", "banana");
        List<String> keys = new ArrayList<>(counter.buildFrequencyMap().keySet());
        assertEquals(List.of("apple", "banana", "cherry"), keys);
    }

    @Test
    void buildFrequencyMapCountsSingleWord() {
        assertEquals(1L, of("hello").buildFrequencyMap().get("hello"));
    }

    @Test
    void buildFrequencyMapTreatsEachOccurrenceAsSeparate() {
        WordFrequencyCounter counter = of("a", "a", "a", "b");
        assertEquals(3L, counter.buildFrequencyMap().get("a"));
        assertEquals(1L, counter.buildFrequencyMap().get("b"));
    }

    // ── getTopN ───────────────────────────────────────────────────────────────

    @Test
    void getTopNReturnsHighestFrequencyFirst() {
        WordFrequencyCounter counter = of("a", "b", "b", "c", "c", "c");
        List<String> top = counter.getTopN(3);
        assertEquals("c", top.get(0)); // most frequent
        assertEquals("b", top.get(1));
        assertEquals("a", top.get(2));
    }

    @Test
    void getTopNRespectsLimit() {
        WordFrequencyCounter counter = of("a", "b", "b", "c", "c", "c");
        assertEquals(2, counter.getTopN(2).size());
    }

    @Test
    void getTopNWithNLargerThanVocabularyReturnsAll() {
        WordFrequencyCounter counter = of("apple", "banana");
        assertEquals(2, counter.getTopN(100).size());
    }

    @Test
    void getTopNWithZeroReturnsEmptyList() {
        assertTrue(of("apple", "banana").getTopN(0).isEmpty());
    }

    // ── getWordsStartingWith ──────────────────────────────────────────────────

    @Test
    void getWordsStartingWithReturnsOnlyMatchingWords() {
        WordFrequencyCounter counter = of("apple", "avocado", "banana", "blueberry", "cherry");
        List<String> result = counter.getWordsStartingWith('b');
        assertEquals(List.of("banana", "blueberry"), result);
    }

    @Test
    void getWordsStartingWithIsSorted() {
        WordFrequencyCounter counter = of("cherry", "cat", "cabbage");
        List<String> result = counter.getWordsStartingWith('c');
        assertEquals(List.of("cabbage", "cat", "cherry"), result);
    }

    @Test
    void getWordsStartingWithReturnsEmptyWhenNoMatch() {
        WordFrequencyCounter counter = of("apple", "banana");
        assertTrue(counter.getWordsStartingWith('z').isEmpty());
    }

    @Test
    void getWordsStartingWithCountsEachWordOnce() {
        // "apple" appears 3 times but should appear once in the word list
        WordFrequencyCounter counter = of("apple", "apple", "avocado");
        List<String> result = counter.getWordsStartingWith('a');
        assertEquals(2, result.size());
        assertTrue(result.contains("apple"));
        assertTrue(result.contains("avocado"));
    }

    // ── getMostFrequentInRange ────────────────────────────────────────────────

    @Test
    void getMostFrequentInRangeReturnsMostFrequentWord() {
        WordFrequencyCounter counter = of("apple", "avocado", "avocado", "avocado", "banana");
        Optional<String> result = counter.getMostFrequentInRange("apple", "avocado");
        assertTrue(result.isPresent());
        assertEquals("avocado", result.get());
    }

    @Test
    void getMostFrequentInRangeIsInclusiveOnBothEnds() {
        WordFrequencyCounter counter = of("apple", "cherry", "banana");
        // "apple" to "cherry" includes all three
        Optional<String> result = counter.getMostFrequentInRange("apple", "cherry");
        assertTrue(result.isPresent());
    }

    @Test
    void getMostFrequentInRangeReturnsEmptyWhenRangeHasNoWords() {
        WordFrequencyCounter counter = of("apple", "banana");
        // range "m" to "z" has no words
        Optional<String> result = counter.getMostFrequentInRange("m", "z");
        assertTrue(result.isEmpty());
    }

    @Test
    void getMostFrequentInRangeExcludesWordsOutsideRange() {
        WordFrequencyCounter counter = of("apple", "apple", "apple", "cherry");
        // range only covers "banana" to "cherry" — excludes "apple"
        Optional<String> result = counter.getMostFrequentInRange("banana", "cherry");
        assertTrue(result.isPresent());
        assertEquals("cherry", result.get());
    }
}

