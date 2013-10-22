package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Portable
public class ResourceRenamed implements ResourceChange {

    private Path destinationPath;

    public ResourceRenamed() {
    }

    public ResourceRenamed( final Path destinationPath ) {
        this.destinationPath = checkNotNull( "destinationPath", destinationPath );
    }

    @Override
    public ResourceChangeType getType() {
        return ResourceChangeType.RENAME;
    }

    public Path getDestinationPath() {
        return this.destinationPath;
    }

}
