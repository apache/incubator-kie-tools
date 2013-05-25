package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An Event indicating a Resource has been copied
 */
@Portable
public class ResourceCopiedEvent {

    private Path sourcePath;
    private Path destinationPath;

    public ResourceCopiedEvent() {
        //Empty constructor for Errai marshalling
    }

    public ResourceCopiedEvent( final Path sourcePath,
                                final Path destinationPath ) {
        this.sourcePath = PortablePreconditions.checkNotNull( "sourcePath", sourcePath );
        this.destinationPath = PortablePreconditions.checkNotNull( "destinationPath", destinationPath );
    }

    public Path getSourcePath() {
        return this.sourcePath;
    }

    public Path getDestinationPath() {
        return this.destinationPath;
    }

}
