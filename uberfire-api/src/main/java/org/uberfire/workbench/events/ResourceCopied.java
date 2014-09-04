package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Portable
public class ResourceCopied implements UberFireEvent,
                                       ResourceChange {

    private Path destinationPath;
    private String message;

    public ResourceCopied() {
    }

    public ResourceCopied( final Path destinationPath,
                           final String message ) {
        this.destinationPath = checkNotNull( "destinationPath", destinationPath );
        this.message = message;
    }

    @Override
    public ResourceChangeType getType() {
        return ResourceChangeType.COPY;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Path getDestinationPath() {
        return this.destinationPath;
    }

}
