package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ResourceUpdated implements UberFireEvent,
                                        ResourceChange {

    private String message;

    public ResourceUpdated() {
    }

    public ResourceUpdated( final String message ) {
        this.message = message;
    }

    @Override
    public ResourceChangeType getType() {
        return ResourceChangeType.UPDATE;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
