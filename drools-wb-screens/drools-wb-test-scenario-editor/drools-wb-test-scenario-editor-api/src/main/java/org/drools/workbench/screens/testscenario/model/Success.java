package org.drools.workbench.screens.testscenario.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Success {

    private int runCount;

    public Success() {
    }

    public Success(int runCount) {
        this.runCount = runCount;
    }

    public int getRunCount() {
        return runCount;
    }
}
