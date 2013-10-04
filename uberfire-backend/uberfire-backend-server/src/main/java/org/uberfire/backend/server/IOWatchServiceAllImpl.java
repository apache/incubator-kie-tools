package org.uberfire.backend.server;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.commons.io.IOWatchService;
import org.kie.commons.java.nio.base.WatchContext;
import org.kie.commons.java.nio.file.StandardWatchEventKind;
import org.kie.commons.java.nio.file.WatchEvent;
import org.kie.commons.java.nio.file.WatchKey;
import org.kie.commons.java.nio.file.WatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.rpc.impl.SessionInfoImpl;
import org.uberfire.security.impl.IdentityImpl;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

@ApplicationScoped
public class IOWatchServiceAllImpl implements IOWatchService {

    private static final Logger LOGGER = LoggerFactory.getLogger( IOWatchServiceAllImpl.class );

    @Inject
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;

    @Inject
    private Event<ResourceRenamedEvent> resourceRenamedEvent;

    @Inject
    private Event<ResourceDeletedEvent> resourceDeletedEvent;

    @Inject
    private Event<ResourceAddedEvent> resourceAddedEvent;

    @Inject
    private Paths paths;

    private final List<WatchService> watchServices = new ArrayList<WatchService>();
    private boolean isDisposed = false;

    public void dispose() {
        isDisposed = true;
        for ( final WatchService watchService : watchServices ) {
            watchService.close();
        }
    }

    public void addWatchService( final WatchService ws ) {
        watchServices.add( ws );

        new Thread( "IOWatchServiceAllImpl(" + ws.toString() + ")" ) {
            @Override
            public void run() {
                while ( !isDisposed ) {
                    final WatchKey wk = ws.take();
                    if ( wk == null ) {
                        continue;
                    }

                    final List<WatchEvent<?>> events = wk.pollEvents();
                    for ( WatchEvent object : events ) {
                        try {
                            final WatchContext context = (WatchContext) object.context();
                            if ( object.kind().equals( StandardWatchEventKind.ENTRY_MODIFY ) ) {
                                resourceUpdatedEvent.fire( new ResourceUpdatedEvent( paths.convert( context.getOldPath() ), sessionInfo( context ) ) );
                            } else if ( object.kind().equals( StandardWatchEventKind.ENTRY_CREATE ) ) {
                                resourceAddedEvent.fire( new ResourceAddedEvent( paths.convert( context.getPath() ), sessionInfo( context ) ) );
                            } else if ( object.kind().equals( StandardWatchEventKind.ENTRY_RENAME ) ) {
                                resourceRenamedEvent.fire( new ResourceRenamedEvent( paths.convert( context.getOldPath(), false ), paths.convert( context.getPath() ), sessionInfo( context ) ) );
                            } else if ( object.kind().equals( StandardWatchEventKind.ENTRY_DELETE ) ) {
                                resourceDeletedEvent.fire( new ResourceDeletedEvent( paths.convert( context.getOldPath(), false ), sessionInfo( context ) ) );
                            }
                        } catch ( final Exception ex ) {
                            LOGGER.error( "Unexpected error during WatchService events fire.", ex );
                        }
                    }
                }
            }
        }.start();
    }

    private SessionInfo sessionInfo( final WatchContext context ) {
        final String sessionId;
        final String user;
        if ( context.getSessionId() == null ) {
            sessionId = "<system>";
        } else {
            sessionId = context.getSessionId();
        }
        if ( context.getUser() == null ) {
            user = "<system>";
        } else {
            user = context.getUser();
        }

        return new SessionInfoImpl( sessionId, new IdentityImpl( user ) );
    }
}
