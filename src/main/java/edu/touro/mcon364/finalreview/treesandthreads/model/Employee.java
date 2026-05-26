package edu.touro.mcon364.finalreview.treesandthreads.model;

/**
 * A simple employee record used in the Trees and threads exercises.
 *
 * @param name       full name of the employee
 * @param department department the employee belongs to
 * @param salary     annual salary in whole dollars
 */
public record Employee(String name, String department, int salary)
        implements Comparable<Employee> {

    /** Natural ordering: alphabetical by name, then department, then salary. */
    @Override
    public int compareTo(Employee other) {
        int n = this.name.compareTo(other.name);
        if (n != 0) return n;
        int d = this.department.compareTo(other.department);
        if (d != 0) return d;
        return Integer.compare(this.salary, other.salary);
    }
}

