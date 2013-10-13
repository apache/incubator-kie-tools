package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * An Event indicating a Resource has been opened
 */
@Portable
public class ResourceOpenedEvent {

    private Path path;
    private SessionInfo sessionInfo;

    public ResourceOpenedEvent() {
        //Empty constructor for Errai marshalling
    }

    public ResourceOpenedEvent( final Path path,
                                final SessionInfo sessionInfo ) {
        this.path = checkNotNull( "path", path );
        this.sessionInfo = checkNotNull( "sessionInfo", sessionInfo );
    }

    public Path getPath() {
        return this.path;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }
}
