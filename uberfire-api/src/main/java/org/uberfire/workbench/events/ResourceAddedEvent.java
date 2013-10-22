package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * An Event indicating a Resource has been added
 */
@Portable
public class ResourceAddedEvent extends ResourceAdded implements ResourceEvent {

    private Path path;
    private SessionInfo sessionInfo;

    public ResourceAddedEvent() {
        //Empty constructor for Errai marshalling
    }

    public ResourceAddedEvent( final Path path,
                               final SessionInfo sessionInfo ) {
        this.path = checkNotNull( "path", path );
        this.sessionInfo = checkNotNull( "sessionInfo", sessionInfo );
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

}
