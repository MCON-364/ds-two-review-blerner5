package edu.touro.mcon364.finalreview.orderflowhandoff.homework;

import edu.touro.mcon364.finalreview.model.SensorReading;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Homework 2 — Sensor reading processor.
 *
 * A monitoring system receives readings from sensors over time. One part of the
 * program submits readings as they arrive. Another part of the program processes
 * those readings using one or more background workers.
 *
 * This class is responsible for coordinating that handoff and for keeping a
 * summary of the readings that were actually processed.
 *
 * The important question is not only "How do we calculate the stats?" It is also:
 * "What happens when readings are being submitted and processed by different
 * threads at the same time?"
 *
 * Requirements:
 * - submit(reading) accepts one new sensor reading for later processing.
 * - start(workerCount) starts workerCount background workers.
 * - workerCount must be greater than 0.
 * - Workers should process submitted readings until the processor is stopped and
 *   all already-submitted readings have been handled.
 * - stop() tells the processor to stop accepting/processing future work and waits
 *   until the workers finish the remaining work.
 * - getTotalProcessed() returns how many readings have been processed so far.
 * - getStats() returns summary statistics for the processed reading values:
 *   count, minimum, maximum, sum, and average.
 * - Public reporting methods must not expose mutable internal state.
 *
 * Before coding, think about:
 * - Which object or objects represent work waiting to be processed?
 * - Which object or objects represent work that has already been processed?
 * - Which state can be accessed by more than one thread?
 * - How will workers know when to keep working and when to stop?
 * - What should happen if getStats() is called while workers are still running?
 * - Is it better to store all processed readings and calculate stats later, or
 *   update numeric summary state as each reading is processed?
 * - If several workers update the same stats, how will those updates stay correct?
 */
public class SensorProcessor {

    // Queue of readings waiting to be processed
    private final BlockingQueue<SensorReading> queue = new LinkedBlockingQueue<>();

    // Whether workers should continue running
    private final AtomicBoolean running = new AtomicBoolean(false);

    // Worker threads
    private final List<Thread> workers = new ArrayList<>();

    // Stats for processed readings
    private final DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
    private int totalProcessed = 0;

    // Lock for stats + totalProcessed
    private final Object statsLock = new Object();

    /**
     * Accept one sensor reading for processing.
     *
     * @param reading the reading to process later
     */
    public void submit(SensorReading reading) {
        if (running.get()) {
            queue.add(reading);
        }
    }

    /**
     * Start background workers that process submitted readings.
     *
     * @param workerCount number of worker threads to start
     * @throws IllegalArgumentException if workerCount is not positive
     */
    public void start(int workerCount) {
        if (workerCount <= 0) {
            throw new IllegalArgumentException("workerCount must be positive");
        }
        if (running.get()) {
            return; // already started
        }

        running.set(true);

        for (int i = 0; i < workerCount; i++) {
            Thread t = new Thread(this::workerLoop);
            workers.add(t);
            t.start();
        }
    }

    /**
     * Logic run by each worker.
     *
     * The worker should repeatedly look for work, process it when available, and
     * eventually exit when the processor is stopping and no work remains.
     */
    private void workerLoop() {
        try {
            while (true) {

                // If stopping AND no work left → exit
                if (!running.get() && queue.isEmpty()) {
                    return;
                }

                // Try to get work
                SensorReading reading = queue.poll();

                if (reading == null) {
                    // No work right now — small pause, then re-check
                    Thread.sleep(10);
                    continue;
                }

                // Process reading
                synchronized (statsLock) {
                    stats.accept(reading.value());
                    totalProcessed++;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Stop the processor and wait for workers to finish.
     *
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public void stop() throws InterruptedException {
        running.set(false);

        for (Thread t : workers) {
            t.join();
        }
    }

    /**
     * Return the number of readings processed so far.
     */
    public int getTotalProcessed() {
        synchronized (statsLock) {
            return totalProcessed;
        }
    }

    /**
     * Return summary statistics for the processed reading values.
     *
     * If no readings have been processed yet, return an empty
     * DoubleSummaryStatistics object.
     */
    public DoubleSummaryStatistics getStats() {
        synchronized (statsLock) {
            DoubleSummaryStatistics copy = new DoubleSummaryStatistics();
            copy.combine(stats);
            return copy;
        }
    }
}
