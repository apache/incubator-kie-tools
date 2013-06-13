package org.uberfire.workbench.events;

import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;

/**
 * An Event indicating a various changes to various Resources
 */
@Portable
public class ResourceBatchChangesEvent {

    private Set<ResourceChange> batch = new HashSet<ResourceChange>();

    public ResourceBatchChangesEvent() {
        //Empty constructor for Errai marshalling
    }

    public ResourceBatchChangesEvent( final Set<ResourceChange> batch ) {
        this.batch = PortablePreconditions.checkNotNull( "batch",
                                                         batch );
    }

    public Set<ResourceChange> getBatch() {
        return this.batch;
    }

}
