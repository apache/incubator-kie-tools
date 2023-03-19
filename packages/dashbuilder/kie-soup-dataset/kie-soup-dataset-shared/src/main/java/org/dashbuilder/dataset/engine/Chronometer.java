package org.dashbuilder.dataset.engine;

/**
 * The interface <code>Chronometer</code> permits to measure the time elapsed between two time snapshots.
 */
public interface Chronometer {

    /**
     * Start the timer.
     */
    long start();

    /**
     * Stop the timer.
     */
    long stop();

    /**
     * Return the elapsed time measured in nanoseconds since the very start.
     * @return  long, the time.
     */
    long elapsedTime();

    /**
     * Return the time specified in human readable format.
     * @param millis The time to format in milliseconds.
     * @return Examples: <i>2d 3h 44m 2s<i>  or  <i>20 weeks 3h 3s</i>
     */
    String formatElapsedTime(long millis);
}
