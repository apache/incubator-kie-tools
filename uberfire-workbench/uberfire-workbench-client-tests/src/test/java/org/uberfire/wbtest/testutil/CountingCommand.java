package org.uberfire.wbtest.testutil;

import org.uberfire.mvp.Command;

/**
 * Implementation of Command that counts how many times it has been executed.
 */
public class CountingCommand implements Command {

    /**
     * Increments by 1 every time {@link #execute()} is called. You are free to set this to any value you like, and it
     * will continue to increment from there.
     */
    public int executeCount;

    @Override
    public void execute() {
        executeCount++;
    }

}
