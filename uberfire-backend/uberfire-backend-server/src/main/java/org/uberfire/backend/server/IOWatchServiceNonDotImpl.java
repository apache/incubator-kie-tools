package org.uberfire.backend.server;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;

@ApplicationScoped
public class IOWatchServiceNonDotImpl extends AbstractWatchService {

    @Override
    public boolean doFilter( WatchEvent<?> object ) {
        final WatchContext context = (WatchContext) object.context();
        if ( object.kind().equals( StandardWatchEventKind.ENTRY_MODIFY ) ) {
            if ( context.getOldPath().getFileName().toString().startsWith( "." ) ) {
                return true;
            }
        } else if ( object.kind().equals( StandardWatchEventKind.ENTRY_CREATE ) ) {
            if ( context.getPath().getFileName().toString().startsWith( "." ) ) {
                return true;
            }
        } else if ( object.kind().equals( StandardWatchEventKind.ENTRY_RENAME ) ) {
            if ( context.getOldPath().getFileName().toString().startsWith( "." ) ) {
                return true;
            }
        } else if ( object.kind().equals( StandardWatchEventKind.ENTRY_DELETE ) ) {
            if ( context.getOldPath().getFileName().toString().startsWith( "." ) ) {
                return true;
            }
        }
        return false;
    }

}
