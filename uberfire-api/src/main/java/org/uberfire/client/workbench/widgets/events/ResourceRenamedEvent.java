package org.uberfire.client.workbench.widgets.events;

import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An Event indicating a Resource has been renamed
 */
public class ResourceRenamedEvent {

    private final Path sourcePath;
    private final Path destinationPath;

    public ResourceRenamedEvent( final Path sourcePath,
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
