package edu.touro.mcon364.finalreview.treesandthreads.model;
/**
 * A book record used in the Trees and threads homework exercises.
 *
 * @param title  title of the book
 * @param author author's full name
 * @param year   publication year
 */
public record Book(String title, String author, int year)
        implements Comparable<Book> {
    /** Natural ordering: alphabetical by title, then author. */
    @Override
    public int compareTo(Book other) {
        int t = this.title.compareTo(other.title);
        if (t != 0) return t;
        return this.author.compareTo(other.author);
    }
}
