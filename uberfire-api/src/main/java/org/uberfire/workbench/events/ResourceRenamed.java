package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Portable
public class ResourceRenamed implements UberFireEvent,
                                        ResourceChange {

    private Path destinationPath;
    private String message;

    public ResourceRenamed() {
    }

    public ResourceRenamed( final Path destinationPath,
                            final String message ) {
        this.destinationPath = checkNotNull( "destinationPath", destinationPath );
        this.message = message;
    }

    @Override
    public ResourceChangeType getType() {
        return ResourceChangeType.RENAME;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Path getDestinationPath() {
        return this.destinationPath;
    }

}
