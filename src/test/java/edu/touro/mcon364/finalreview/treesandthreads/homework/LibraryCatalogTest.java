package edu.touro.mcon364.finalreview.treesandthreads.homework;

import edu.touro.mcon364.finalreview.treesandthreads.model.Book;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import static org.junit.jupiter.api.Assertions.*;

class LibraryCatalogTest {
    private static final Book CLEAN_CODE      = new Book("Clean Code",           "Robert Martin", 2008);
    private static final Book CLEAN_ARCH      = new Book("Clean Architecture",   "Robert Martin", 2017);
    private static final Book PRAGMATIC_PROG  = new Book("Pragmatic Programmer", "David Thomas",  1999);
    private static final Book DESIGN_PATTERNS = new Book("Design Patterns",      "GoF",           1994);
    private static final Book REFACTORING     = new Book("Refactoring",          "Martin Fowler", 1999);

    private LibraryCatalog catalogOf(Book... books) {
        return new LibraryCatalog(List.of(books));
    }

    // constructor
    @Test void constructorThrowsOnNull() {
        assertThrows(Exception.class, () -> new LibraryCatalog(null));
    }
    @Test void constructorDefendsAgainstExternalMutation() {
        List<Book> mutable = new ArrayList<>(List.of(CLEAN_CODE));
        LibraryCatalog catalog = new LibraryCatalog(mutable);
        mutable.clear();
        assertEquals(1, catalog.buildTitleIndex().size());
    }

    // empty list
    @Test void buildTitleIndexIsEmptyForEmptyList() {
        assertTrue(new LibraryCatalog(List.of()).buildTitleIndex().isEmpty());
    }
    @Test void buildAuthorIndexIsEmptyForEmptyList() {
        assertTrue(new LibraryCatalog(List.of()).buildAuthorIndex().isEmpty());
    }
    @Test void getBooksPublishedBeforeIsEmptyForEmptyList() {
        assertTrue(new LibraryCatalog(List.of()).getBooksPublishedBefore(2025).isEmpty());
    }
    @Test void getAuthorsWithMoreThanIsEmptyForEmptyList() {
        assertTrue(new LibraryCatalog(List.of()).getAuthorsWithMoreThan(0).isEmpty());
    }
    @Test void findByTitlePrefixIsEmptyForEmptyList() {
        assertTrue(new LibraryCatalog(List.of()).findByTitlePrefix("C").isEmpty());
    }

    // buildTitleIndex
    @Test void buildTitleIndexKeyedByTitle() {
        Map<String, Book> index = catalogOf(CLEAN_CODE, PRAGMATIC_PROG).buildTitleIndex();
        assertEquals(CLEAN_CODE,     index.get("Clean Code"));
        assertEquals(PRAGMATIC_PROG, index.get("Pragmatic Programmer"));
    }
    @Test void buildTitleIndexIsSortedAlphabetically() {
        List<String> keys = new ArrayList<>(
            catalogOf(REFACTORING, CLEAN_CODE, DESIGN_PATTERNS).buildTitleIndex().keySet());
        assertTrue(keys.get(0).compareTo(keys.get(1)) < 0);
        assertTrue(keys.get(1).compareTo(keys.get(2)) < 0);
    }

    // buildAuthorIndex
    @Test void buildAuthorIndexGroupsByAuthor() {
        Map<String, TreeSet<Book>> index =
            catalogOf(CLEAN_CODE, CLEAN_ARCH, PRAGMATIC_PROG).buildAuthorIndex();
        assertEquals(2, index.get("Robert Martin").size());
        assertEquals(1, index.get("David Thomas").size());
    }
    @Test void buildAuthorIndexAuthorsSortedAlphabetically() {
        List<String> authors = new ArrayList<>(
            catalogOf(CLEAN_CODE, REFACTORING, DESIGN_PATTERNS).buildAuthorIndex().keySet());
        assertTrue(authors.get(0).compareTo(authors.get(1)) < 0);
    }

    // getBooksPublishedBefore
    @Test void getBooksPublishedBeforeFiltersCorrectly() {
        List<Book> result = catalogOf(CLEAN_CODE, PRAGMATIC_PROG, DESIGN_PATTERNS)
            .getBooksPublishedBefore(2000);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(b -> b.year() < 2000));
    }
    @Test void getBooksPublishedBeforeExcludesExactYear() {
        assertTrue(catalogOf(PRAGMATIC_PROG).getBooksPublishedBefore(1999).isEmpty());
    }
    @Test void getBooksPublishedBeforeIsSortedByTitle() {
        List<Book> result = catalogOf(REFACTORING, DESIGN_PATTERNS).getBooksPublishedBefore(2000);
        assertEquals("Design Patterns", result.get(0).title());
        assertEquals("Refactoring",     result.get(1).title());
    }

    // getAuthorsWithMoreThan
    @Test void getAuthorsWithMoreThanFiltersCorrectly() {
        List<String> result = catalogOf(CLEAN_CODE, CLEAN_ARCH, PRAGMATIC_PROG)
            .getAuthorsWithMoreThan(1);
        assertEquals(1, result.size());
        assertEquals("Robert Martin", result.get(0));
    }
    @Test void getAuthorsWithMoreThanIsSorted() {
        List<Book> books = List.of(
            new Book("A1", "Zara", 2000), new Book("A2", "Zara", 2001),
            new Book("B1", "Adam", 2000), new Book("B2", "Adam", 2001));
        List<String> result = new LibraryCatalog(books).getAuthorsWithMoreThan(1);
        assertEquals("Adam", result.get(0));
        assertEquals("Zara", result.get(1));
    }

    // findByTitlePrefix
    @Test void findByTitlePrefixReturnsMatchingBooks() {
        List<Book> result = catalogOf(CLEAN_CODE, CLEAN_ARCH, REFACTORING).findByTitlePrefix("Clean");
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(b -> b.title().startsWith("Clean")));
    }
    @Test void findByTitlePrefixIsAlphabetical() {
        List<Book> result = catalogOf(CLEAN_CODE, CLEAN_ARCH).findByTitlePrefix("Clean");
        assertEquals("Clean Architecture", result.get(0).title());
        assertEquals("Clean Code",         result.get(1).title());
    }
    @Test void findByTitlePrefixNoMatchReturnsEmpty() {
        assertTrue(catalogOf(CLEAN_CODE, REFACTORING).findByTitlePrefix("Z").isEmpty());
    }
}
