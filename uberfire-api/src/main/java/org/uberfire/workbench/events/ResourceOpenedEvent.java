package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An Event indicating a Resource has been opened
 */
@Portable
public class ResourceOpenedEvent {

    private Path path;

    public ResourceOpenedEvent() {
        //Empty constructor for Errai marshalling
    }

    public ResourceOpenedEvent( final Path path ) {
        this.path = PortablePreconditions.checkNotNull( "path", path );
    }

    public Path getPath() {
        return this.path;
    }

}
