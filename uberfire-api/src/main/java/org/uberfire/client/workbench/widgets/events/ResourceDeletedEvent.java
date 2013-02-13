package org.uberfire.client.workbench.widgets.events;

import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An Event indicating a Resource has been deleted
 */
public class ResourceDeletedEvent {

    private final Path path;

    public ResourceDeletedEvent( final Path path ) {
        this.path = PortablePreconditions.checkNotNull( "path", path );
    }

    public Path getPath() {
        return this.path;
    }

}
