package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ResourceDeleted extends UberFireEvent implements ResourceChange {

    @Override
    public ResourceChangeType getType() {
        return ResourceChangeType.DELETE;
    }
}
