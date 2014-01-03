package org.uberfire.workbench.events;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;

/**
 * An Event indicating a Resource has been opened
 */
@Portable
public class ResourceOpenedEvent extends UberFireEvent {

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

    @Override
    public String toString() {
      return "ResourceOpenedEvent [path=" + path + ", sessionInfo=" + sessionInfo + "]";
    }

}
