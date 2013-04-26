package org.uberfire.client.workbench.widgets.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * A single Resource change in a batch
 */
@Portable
public class ResourceChange {

    private ChangeType type;
    private Path path;

    public ResourceChange() {
        //Empty constructor for Errai marshalling
    }

    public ResourceChange( final ChangeType type,
                           final Path path ) {
        this.type = PortablePreconditions.checkNotNull( "type",
                                                        type );
        this.path = PortablePreconditions.checkNotNull( "path",
                                                        path );
    }

    public ChangeType getType() {
        return this.type;
    }

    public Path getPath() {
        return this.path;
    }

}
