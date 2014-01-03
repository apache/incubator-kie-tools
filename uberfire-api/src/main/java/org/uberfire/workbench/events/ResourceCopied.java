package org.uberfire.workbench.events;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class ResourceCopied extends UberFireEvent implements ResourceChange {

    private Path destinationPath;

    public ResourceCopied() {
        //Empty constructor for Errai marshalling
    }

    public ResourceCopied( final Path destinationPath ) {
        this.destinationPath = checkNotNull( "destinationPath", destinationPath );
    }

    @Override
    public ResourceChangeType getType() {
        return ResourceChangeType.COPY;
    }

    public Path getDestinationPath() {
        return this.destinationPath;
    }

}
