package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * An Event indicating a Resource has been copied
 */
@Portable
public class ResourceCopiedEvent extends ResourceCopied implements ResourceEvent {

    private Path sourcePath;
    private SessionInfo sessionInfo;

    public ResourceCopiedEvent() {
    }

    public ResourceCopiedEvent( final Path sourcePath,
                                final Path destinationPath,
                                final String message,
                                final SessionInfo sessionInfo ) {
        super( destinationPath, message );
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
        return "ResourceCopiedEvent{" +
                "sourcePath=" + sourcePath +
                ", destinationPath=" + getDestinationPath() +
                ", message=" + getMessage() +
                ", sessionInfo=" + sessionInfo +
                '}';
    }
}
