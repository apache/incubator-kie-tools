package org.drools.workbench.screens.testscenario.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Failure {

    private String message;
    private String displayName;

    public Failure() {

    }

    public Failure(String displayName, String message) {
        this.displayName = displayName;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "Failure{" +
                "message='" + message + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
