package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * An Event indicating a Resource has been renamed
 */
@Portable
public class ResourceRenamedEvent {

    private Path sourcePath;
    private Path destinationPath;
    private SessionInfo sessionInfo;

    public ResourceRenamedEvent() {
        //Empty constructor for Errai marshalling
    }

    public ResourceRenamedEvent( final Path sourcePath,
                                 final Path destinationPath,
                                 final SessionInfo sessionInfo ) {
        this.sourcePath = checkNotNull( "sourcePath", sourcePath );
        this.destinationPath = checkNotNull( "destinationPath", destinationPath );
        this.sessionInfo = checkNotNull( "sessionInfo", sessionInfo );
    }

    public Path getSourcePath() {
        return this.sourcePath;
    }

    public Path getDestinationPath() {
        return this.destinationPath;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }
}
