package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ResourceAdded implements ResourceChange,
                                      UberFireEvent {

    private String message;

    public ResourceAdded() {
    }

    public ResourceAdded( final String message ) {
        this.message = message;
    }

    @Override
    public ResourceChangeType getType() {
        return ResourceChangeType.ADD;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ResourceAdded{" +
                "message='" + message + '\'' +
                '}';
    }
}
