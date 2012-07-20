package org.drools.guvnor.client.editors.jbpm;

public interface LazyPanel {
    boolean isInitialized();

    /**
     * Expected to be an idempotent implementation
     */
    void initialize();
}
