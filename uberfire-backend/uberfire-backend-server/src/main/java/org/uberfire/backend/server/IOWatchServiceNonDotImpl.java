package org.uberfire.backend.server;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;

@ApplicationScoped
public class IOWatchServiceNonDotImpl extends AbstractWatchService {

    @Override
    protected boolean filterEvent( WatchEvent<?> object ) {
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

//    private static final Logger LOGGER = LoggerFactory.getLogger( IOWatchServiceNonDotImpl.class );
//
//    @Inject
//    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;
//
//    @Inject
//    private Event<ResourceRenamedEvent> resourceRenamedEvent;
//
//    @Inject
//    private Event<ResourceDeletedEvent> resourceDeletedEvent;
//
//    @Inject
//    private Event<ResourceAddedEvent> resourceAddedEvent;
//
//    @Inject
//    private Paths paths;
//
//    private final List<FileSystem> fileSystems = new ArrayList<FileSystem>();
//    private final List<WatchService> watchServices = new ArrayList<WatchService>();
//    private boolean isDisposed = false;
//
//    public void dispose() {
//        isDisposed = true;
//        for ( final WatchService watchService : watchServices ) {
//            watchService.close();
//        }
//    }
//
//    @Override
//    public boolean hasWatchService( final FileSystem fs ) {
//        return fileSystems.contains( fs );
//    }
//
//    public void addWatchService( final FileSystem fs,
//                                 final WatchService ws ) {
//        fileSystems.add( fs );
//        watchServices.add( ws );
//
//        new Thread( "IOWatchServiceNonDotImpl(" + ws.toString() + ")" ) {
//            @Override
//            public void run() {
//                while ( !isDisposed ) {
//                    final WatchKey wk = ws.take();
//                    if ( wk == null ) {
//                        continue;
//                    }
//
//                    final List<WatchEvent<?>> events = wk.pollEvents();
//                    for ( WatchEvent object : events ) {
//                        final WatchContext context = (WatchContext) object.context();
//                        try {
//                            if ( object.kind().equals( StandardWatchEventKind.ENTRY_MODIFY ) ) {
//                                if ( !context.getOldPath().getFileName().toString().startsWith( "." ) ) {
//                                    resourceUpdatedEvent.fire( new ResourceUpdatedEvent( paths.convert( context.getOldPath() ), sessionInfo( context ) ) );
//                                }
//                            } else if ( object.kind().equals( StandardWatchEventKind.ENTRY_CREATE ) ) {
//                                if ( !context.getPath().getFileName().toString().startsWith( "." ) ) {
//                                    resourceAddedEvent.fire( new ResourceAddedEvent( paths.convert( context.getPath() ), sessionInfo( context ) ) );
//                                }
//                            } else if ( object.kind().equals( StandardWatchEventKind.ENTRY_RENAME ) ) {
//                                if ( !context.getOldPath().getFileName().toString().startsWith( "." ) ) {
//                                    resourceRenamedEvent.fire( new ResourceRenamedEvent( paths.convert( context.getOldPath(), false ), paths.convert( context.getPath() ), sessionInfo( context ) ) );
//                                }
//                            } else if ( object.kind().equals( StandardWatchEventKind.ENTRY_DELETE ) ) {
//                                if ( !context.getOldPath().getFileName().toString().startsWith( "." ) ) {
//                                    resourceDeletedEvent.fire( new ResourceDeletedEvent( paths.convert( context.getOldPath(), false ), sessionInfo( context ) ) );
//                                }
//                            }
//                        } catch ( final Exception ex ) {
//                            LOGGER.error( "Unexpected error during WatchService events fire.", ex );
//                        }
//                    }
//                }
//            }
//        }.start();
//    }
//
//    private SessionInfo sessionInfo( final WatchContext context ) {
//        final String sessionId;
//        final String user;
//        if ( context.getSessionId() == null ) {
//            sessionId = "<system>";
//        } else {
//            sessionId = context.getSessionId();
//        }
//        if ( context.getUser() == null ) {
//            user = "<system>";
//        } else {
//            user = context.getUser();
//        }
//
//        return new SessionInfoImpl( sessionId, new IdentityImpl( user ) );
//    }
}
