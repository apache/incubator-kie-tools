package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ResourceAdded implements UberFireEvent,
                                      ResourceChange {

    @Override
    public ResourceChangeType getType() {
        return ResourceChangeType.ADD;
    }
}
