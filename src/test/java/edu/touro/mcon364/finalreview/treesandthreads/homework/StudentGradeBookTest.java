package edu.touro.mcon364.finalreview.treesandthreads.homework;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static org.junit.jupiter.api.Assertions.*;

class StudentGradeBookTest {
    private StudentGradeBook bookOf(Map<String, Double> grades) {
        return new StudentGradeBook(grades);
    }
    private Map<String, Double> sampleGrades() {
        Map<String, Double> g = new HashMap<>();
        g.put("Alice", 95.0);  // A
        g.put("Bob",   82.0);  // B
        g.put("Carol", 73.0);  // C
        g.put("Dan",   65.0);  // D
        g.put("Eve",   40.0);  // F
        return g;
    }
    // constructor
    @Test void constructorThrowsOnNull() {
        assertThrows(Exception.class, () -> new StudentGradeBook(null));
    }
    @Test void constructorDefendsAgainstExternalMutation() {
        Map<String, Double> mutable = new HashMap<>(Map.of("Alice", 90.0));
        StudentGradeBook book = new StudentGradeBook(mutable);
        mutable.clear();
        assertEquals(1, book.buildSortedGradeBook().size());
    }
    // empty
    @Test void buildSortedGradeBookIsEmptyForEmptyInput() {
        assertTrue(bookOf(Map.of()).buildSortedGradeBook().isEmpty());
    }
    @Test void getLetterGradeDistributionIsEmptyForEmptyInput() {
        assertTrue(bookOf(Map.of()).getLetterGradeDistribution().isEmpty());
    }
    @Test void getTopStudentsIsEmptyForEmptyInput() {
        assertTrue(bookOf(Map.of()).getTopStudents(5).isEmpty());
    }
    @Test void getStudentsInScoreRangeIsEmptyForEmptyInput() {
        assertTrue(bookOf(Map.of()).getStudentsInScoreRange(0, 100).isEmpty());
    }
    // buildSortedGradeBook
    @Test void buildSortedGradeBookIsSortedAlphabetically() {
        List<String> names = new ArrayList<>(bookOf(sampleGrades()).buildSortedGradeBook().keySet());
        assertEquals("Alice", names.get(0));
        assertEquals("Bob",   names.get(1));
        assertEquals("Carol", names.get(2));
        assertEquals("Dan",   names.get(3));
        assertEquals("Eve",   names.get(4));
    }
    @Test void buildSortedGradeBookContainsAllEntries() {
        TreeMap<String, Double> result = bookOf(sampleGrades()).buildSortedGradeBook();
        assertEquals(5, result.size());
        assertEquals(95.0, result.get("Alice"), 0.001);
    }
    // getStatistics
    @Test void getStatisticsCountIsCorrect() {
        assertEquals(5, bookOf(sampleGrades()).getStatistics().getCount());
    }
    @Test void getStatisticsMinAndMaxAreCorrect() {
        DoubleSummaryStatistics stats = bookOf(sampleGrades()).getStatistics();
        assertEquals(40.0,  stats.getMin(), 0.001);
        assertEquals(95.0,  stats.getMax(), 0.001);
    }
    @Test void getStatisticsAverageIsCorrect() {
        // (95 + 82 + 73 + 65 + 40) / 5 = 71.0
        assertEquals(71.0, bookOf(sampleGrades()).getStatistics().getAverage(), 0.001);
    }
    @Test void getStatisticsForSingleStudentIsExact() {
        DoubleSummaryStatistics stats = bookOf(Map.of("Alice", 88.0)).getStatistics();
        assertEquals(1, stats.getCount());
        assertEquals(88.0, stats.getAverage(), 0.001);
    }
    // getLetterGradeDistribution
    @Test void getLetterGradeDistributionCountsCorrectly() {
        Map<String, Long> dist = bookOf(sampleGrades()).getLetterGradeDistribution();
        assertEquals(1L, dist.get("A"));
        assertEquals(1L, dist.get("B"));
        assertEquals(1L, dist.get("C"));
        assertEquals(1L, dist.get("D"));
        assertEquals(1L, dist.get("F"));
    }
    @Test void getLetterGradeDistributionGroupsMultipleStudentsInSameBucket() {
        Map<String, Double> g = Map.of("Alice", 91.0, "Bob", 95.0, "Carol", 55.0);
        Map<String, Long> dist = bookOf(g).getLetterGradeDistribution();
        assertEquals(2L, dist.get("A"));
        assertEquals(1L, dist.get("F"));
    }
    @Test void getLetterGradeDistributionIsSortedAlphabetically() {
        List<String> keys = new ArrayList<>(bookOf(sampleGrades()).getLetterGradeDistribution().keySet());
        assertEquals("A", keys.get(0));
        assertEquals("B", keys.get(1));
        assertEquals("C", keys.get(2));
    }
    // getTopStudents
    @Test void getTopStudentsReturnsHighestScorerFirst() {
        List<String> top = bookOf(sampleGrades()).getTopStudents(1);
        assertEquals(1, top.size());
        assertEquals("Alice", top.get(0));
    }
    @Test void getTopStudentsRespectsLimit() {
        List<String> top = bookOf(sampleGrades()).getTopStudents(3);
        assertEquals(3, top.size());
        assertEquals("Alice", top.get(0));
        assertEquals("Bob",   top.get(1));
        assertEquals("Carol", top.get(2));
    }
    @Test void getTopStudentsWithNLargerThanSizeReturnsAll() {
        assertEquals(5, bookOf(sampleGrades()).getTopStudents(100).size());
    }
    @Test void getTopStudentsWithZeroReturnsEmpty() {
        assertTrue(bookOf(sampleGrades()).getTopStudents(0).isEmpty());
    }
    // getStudentsInScoreRange
    @Test void getStudentsInScoreRangeIsInclusiveOnBothEnds() {
        List<String> result = bookOf(sampleGrades()).getStudentsInScoreRange(82.0, 95.0);
        assertTrue(result.contains("Alice"));
        assertTrue(result.contains("Bob"));
        assertFalse(result.contains("Carol"));
    }
    @Test void getStudentsInScoreRangeIsSortedAlphabetically() {
        Map<String, Double> g = Map.of("Zara", 90.0, "Adam", 92.0);
        List<String> result = bookOf(g).getStudentsInScoreRange(85.0, 100.0);
        assertEquals("Adam", result.get(0));
        assertEquals("Zara", result.get(1));
    }
    @Test void getStudentsInScoreRangeExcludesOutsideBounds() {
        List<String> result = bookOf(sampleGrades()).getStudentsInScoreRange(90.0, 100.0);
        assertEquals(List.of("Alice"), result);
    }
}

