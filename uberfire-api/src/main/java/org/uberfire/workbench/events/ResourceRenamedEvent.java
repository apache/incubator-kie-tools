package org.uberfire.workbench.events;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;

/**
 * An Event indicating a Resource has been renamed
 */
@Portable
public class ResourceRenamedEvent extends ResourceRenamed implements ResourceEvent {

    private Path sourcePath;
    private SessionInfo sessionInfo;

    public ResourceRenamedEvent() {
        //Empty constructor for Errai marshalling
    }

    public ResourceRenamedEvent( final Path sourcePath,
                                 final Path destinationPath,
                                 final SessionInfo sessionInfo ) {
        super( destinationPath );
        this.sourcePath = checkNotNull( "sourcePath", sourcePath );
        this.sessionInfo = checkNotNull( "sessionInfo", sessionInfo );
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    @Override
    public Path getPath() {
        return this.sourcePath;
    }

    @Override
    public String toString() {
      return "ResourceRenamedEvent [sourcePath=" + sourcePath + ", sessionInfo=" + sessionInfo + "]";
    }

}
