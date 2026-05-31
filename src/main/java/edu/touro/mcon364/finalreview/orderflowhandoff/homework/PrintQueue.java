package edu.touro.mcon364.finalreview.orderflowhandoff.homework;

import edu.touro.mcon364.finalreview.model.PrintJob;

import java.util.Optional;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Homework 1 — PrintQueue.
 *
 * A small print room has one shared printer. Many print jobs can be submitted,
 * but the printer can only work on one job at a time.
 *
 * The print room should behave the way people expect a normal printer line to
 * behave: jobs wait until it is their turn, and the next job selected for
 * printing should be based on the order in which jobs arrived.
 *
 * This class is responsible for remembering the waiting print jobs and exposing
 * the operations that the rest of the program would need:
 * submitting a new job, printing the next job, checking what would print next,
 * and reporting how many jobs are still waiting.
 *
 * Before coding, think through the shape of the problem:
 * - What information does this object need to remember between method calls?
 * - When a new job is submitted, where should it be placed?
 * - When the printer is ready, which job should be selected?
 * - Is this problem about the most recent item, the oldest item, or all items?
 * - Which collection behavior matches that rule?
 * - What should the methods return when there are no waiting jobs?
 *
 * Requirements:
 * - submit(job) records a new print job as waiting.
 * - printNext() removes and returns the job that should be printed next.
 * - printNext() returns Optional.empty() if no jobs are waiting.
 * - peekNext() returns the job that would be printed next, but does not remove it.
 * - peekNext() returns Optional.empty() if no jobs are waiting.
 * - queuedJobs() returns the number of print jobs currently waiting.
 * - The class should not expose its internal collection directly.
 */
public class PrintQueue {

    // TODO: choose the field or fields needed to remember waiting print jobs
    private final Queue<PrintJob> queue = new LinkedList<>();
    /**
     * Records a new print job as waiting.
     *
     * @param job the print job to add
     */
    public void submit(PrintJob job) {
        // TODO: implement
        queue.add(job);
    }

    /**
     * Removes and returns the print job that should be handled next.
     *
     * @return the next print job, or Optional.empty() when no jobs are waiting
     */
    public Optional<PrintJob> printNext() {
        // TODO: implement
        return Optional.ofNullable(queue.poll());
    }

    /**
     * Returns the print job that would be handled next without removing it.
     *
     * @return the next print job, or Optional.empty() when no jobs are waiting
     */
    public Optional<PrintJob> peekNext() {
        // TODO: implement
        return Optional.ofNullable(queue.peek());
    }

    /**
     * Returns the number of jobs currently waiting to be printed.
     */
    public int queuedJobs() {
        // TODO: implement
        return queue.size();
    }
}
