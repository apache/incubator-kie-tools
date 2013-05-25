package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An Event indicating a Resource has been updated
 */
@Portable
public class ResourceUpdatedEvent {

    private Path path;

    public ResourceUpdatedEvent() {
        //Empty constructor for Errai marshalling
    }

    public ResourceUpdatedEvent( final Path path ) {
        this.path = PortablePreconditions.checkNotNull( "path", path );
    }

    public Path getPath() {
        return this.path;
    }

}
