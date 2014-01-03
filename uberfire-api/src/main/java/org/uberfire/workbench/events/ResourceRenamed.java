package org.uberfire.workbench.events;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class ResourceRenamed extends UberFireEvent implements ResourceChange {

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
