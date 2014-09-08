package org.uberfire.commons.services.cdi;

/**
 * Interface that allows to wrap startable behavior into single bean for further triggering
 */
public interface Startable {

    /**
     * Start the logic defined in the implementation
     */
    void start();
}
