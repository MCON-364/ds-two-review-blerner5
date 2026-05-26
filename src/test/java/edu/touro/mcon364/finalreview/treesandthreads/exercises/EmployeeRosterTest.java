package edu.touro.mcon364.finalreview.treesandthreads.exercises;

import edu.touro.mcon364.finalreview.treesandthreads.model.Employee;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeSet;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeRosterTest {
    // helpers
    private static final Employee ALICE_ENG = new Employee("Alice", "Engineering", 90_000);
    private static final Employee BOB_ENG   = new Employee("Bob",   "Engineering", 80_000);
    private static final Employee CAROL_HR  = new Employee("Carol", "HR",          70_000);
    private static final Employee DAN_HR    = new Employee("Dan",   "HR",          75_000);
    private static final Employee EVE_SALES = new Employee("Eve",   "Sales",       85_000);
    private EmployeeRoster rosterOf(Employee... emps) {
        return new EmployeeRoster(List.of(emps));
    }
    // constructor
    @Test void constructorThrowsOnNullList() {
        assertThrows(Exception.class, () -> new EmployeeRoster(null));
    }
    @Test void constructorDefendsAgainstExternalMutation() {
        List<Employee> mutable = new ArrayList<>(List.of(ALICE_ENG, BOB_ENG));
        EmployeeRoster roster = new EmployeeRoster(mutable);
        mutable.clear();
        assertEquals(2, roster.buildRoster().get("Engineering").size());
    }
    // empty list
    @Test void buildRosterIsEmptyForEmptyList() {
        assertTrue(new EmployeeRoster(List.of()).buildRoster().isEmpty());
    }
    @Test void getAllEmployeesSortedIsEmptyForEmptyList() {
        assertTrue(new EmployeeRoster(List.of()).getAllEmployeesSorted().isEmpty());
    }
    @Test void getTopEarnerIsEmptyForEmptyList() {
        assertTrue(new EmployeeRoster(List.of()).getTopEarnerPerDepartment().isEmpty());
    }
    @Test void getDepartmentsInRangeIsEmptyForEmptyList() {
        assertTrue(new EmployeeRoster(List.of()).getDepartmentsInRange("A","Z").isEmpty());
    }
    // buildRoster
    @Test void buildRosterGroupsByDepartment() {
        Map<String, TreeSet<Employee>> result = rosterOf(ALICE_ENG, BOB_ENG, CAROL_HR).buildRoster();
        assertEquals(2, result.size());
        assertTrue(result.containsKey("Engineering"));
        assertTrue(result.containsKey("HR"));
    }
    @Test void buildRosterDepartmentsAreSortedAlphabetically() {
        List<String> depts = new ArrayList<>(rosterOf(EVE_SALES, CAROL_HR, ALICE_ENG).buildRoster().keySet());
        assertEquals(List.of("Engineering", "HR", "Sales"), depts);
    }
    @Test void buildRosterEmployeesWithinDepartmentAreSorted() {
        TreeSet<Employee> eng = rosterOf(BOB_ENG, ALICE_ENG).buildRoster().get("Engineering");
        assertEquals(ALICE_ENG, eng.first());
    }
    @Test void buildRosterCorrectSizes() {
        Map<String, TreeSet<Employee>> result =
            rosterOf(ALICE_ENG, BOB_ENG, CAROL_HR, DAN_HR, EVE_SALES).buildRoster();
        assertEquals(2, result.get("Engineering").size());
        assertEquals(2, result.get("HR").size());
        assertEquals(1, result.get("Sales").size());
    }
    // getTopEarnerPerDepartment
    @Test void topEarnerReturnsHighestSalary() {
        Map<String, Employee> top = rosterOf(ALICE_ENG, BOB_ENG, CAROL_HR, DAN_HR).getTopEarnerPerDepartment();
        assertEquals(ALICE_ENG, top.get("Engineering"));
        assertEquals(DAN_HR,    top.get("HR"));
    }
    @Test void topEarnerSingleEmployeeReturnsThatEmployee() {
        assertEquals(EVE_SALES, rosterOf(EVE_SALES).getTopEarnerPerDepartment().get("Sales"));
    }
    // getAllEmployeesSorted
    @Test void getAllEmployeesSortedReturnsSingleAlphabeticalList() {
        List<Employee> sorted = rosterOf(EVE_SALES, BOB_ENG, CAROL_HR, ALICE_ENG, DAN_HR).getAllEmployeesSorted();
        assertEquals(5, sorted.size());
        assertEquals(ALICE_ENG, sorted.get(0));
        assertEquals(BOB_ENG,   sorted.get(1));
        assertEquals(CAROL_HR,  sorted.get(2));
        assertEquals(DAN_HR,    sorted.get(3));
        assertEquals(EVE_SALES, sorted.get(4));
    }
    // getDepartmentsInRange
    @Test void getDepartmentsInRangeIncludesBothBoundaries() {
        NavigableMap<String, TreeSet<Employee>> result =
            rosterOf(ALICE_ENG, CAROL_HR, EVE_SALES).getDepartmentsInRange("Engineering", "HR");
        assertTrue(result.containsKey("Engineering"));
        assertTrue(result.containsKey("HR"));
        assertFalse(result.containsKey("Sales"));
    }
    @Test void getDepartmentsInRangeExcludesOutsideBounds() {
        NavigableMap<String, TreeSet<Employee>> result =
            rosterOf(ALICE_ENG, CAROL_HR, EVE_SALES).getDepartmentsInRange("HR", "HR");
        assertEquals(1, result.size());
        assertTrue(result.containsKey("HR"));
    }
    @Test void getDepartmentsInRangeEmptyWhenNoMatch() {
        assertTrue(rosterOf(ALICE_ENG, EVE_SALES).getDepartmentsInRange("L","R").isEmpty());
    }
}
