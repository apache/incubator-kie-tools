package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ResourceDeleted implements UberFireEvent,
                                        ResourceChange {

    private String message;

    public ResourceDeleted() {
    }

    public ResourceDeleted( final String message ) {
        this.message = message;
    }

    @Override
    public ResourceChangeType getType() {
        return ResourceChangeType.DELETE;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
