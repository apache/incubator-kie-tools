package org.uberfire.client.workbench.widgets.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An Event indicating a Resource has been deleted
 */
@Portable
public class ResourceDeletedEvent {

    private Path path;

    public ResourceDeletedEvent() {
        //Empty constructor for Errai marshalling
    }

    public ResourceDeletedEvent( final Path path ) {
        this.path = PortablePreconditions.checkNotNull( "path", path );
    }

    public Path getPath() {
        return this.path;
    }

}
