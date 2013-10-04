package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;

import static org.kie.commons.validation.PortablePreconditions.checkNotNull;

/**
 * An Event indicating a Resource has been added
 */
@Portable
public class ResourceAddedEvent {

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

    public Path getPath() {
        return this.path;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

}
