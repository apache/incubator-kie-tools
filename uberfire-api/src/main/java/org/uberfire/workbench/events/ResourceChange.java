package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * A single Resource change in a batch
 */
@Portable
public class ResourceChange {

    private ChangeType type;
    private Path path;
    private SessionInfo sessionInfo;

    public ResourceChange() {
        //Empty constructor for Errai marshalling
    }

    public ResourceChange( final ChangeType type,
                           final Path path,
                           final SessionInfo sessionInfo ) {
        this.type = PortablePreconditions.checkNotNull( "type", type );
        this.path = PortablePreconditions.checkNotNull( "path", path );
        this.sessionInfo = checkNotNull( "sessionInfo", sessionInfo );
    }

    public ChangeType getType() {
        return this.type;
    }

    public Path getPath() {
        return this.path;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }
}
