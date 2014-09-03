package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ResourceDeleted implements UberFireEvent,
                                        ResourceChange {

    @Override
    public ResourceChangeType getType() {
        return ResourceChangeType.DELETE;
    }
}
