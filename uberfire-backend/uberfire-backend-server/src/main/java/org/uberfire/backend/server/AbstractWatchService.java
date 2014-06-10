package org.uberfire.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.services.cdi.ApplicationStarted;
import org.uberfire.io.IOWatchService;
import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.rpc.impl.SessionInfoImpl;
import org.uberfire.security.impl.IdentityImpl;
import org.uberfire.workbench.events.ResourceAdded;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceDeleted;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceEvent;
import org.uberfire.workbench.events.ResourceRenamed;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdated;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import static org.uberfire.backend.server.util.Paths.*;

public abstract class AbstractWatchService implements IOWatchService {

    private static final Logger LOGGER = LoggerFactory.getLogger( AbstractWatchService.class );

    @Inject
    private Event<ResourceBatchChangesEvent> resourceBatchChanges;

    @Inject
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;

    @Inject
    private Event<ResourceRenamedEvent> resourceRenamedEvent;

    @Inject
    private Event<ResourceDeletedEvent> resourceDeletedEvent;

    @Inject
    private Event<ResourceAddedEvent> resourceAddedEvent;

    private final List<FileSystem> fileSystems = new ArrayList<FileSystem>();
    private final List<WatchService> watchServices = new ArrayList<WatchService>();
    private boolean isDisposed = false;

    private boolean started;
    private WatchServiceExecutorManager executorManager = null;
    private Set<AsyncWatchService> watchThreads = new HashSet<AsyncWatchService>();

    public AbstractWatchService() {
        final boolean autostart = Boolean.parseBoolean( System.getProperty( "org.uberfire.watcher.autostart", "true" ) );
        if ( autostart ) {
            start();
        }
    }

    public void configureOnEvent( @Observes ApplicationStarted applicationStartedEvent ) {
        start();
    }

    public synchronized void start() {
        if ( !started ) {
            this.started = true;
            for ( AsyncWatchService watchThread : watchThreads ) {
                getExecutorManager().execute( watchThread );
            }
            watchServices.clear();
        }
    }

    public void dispose() {
        isDisposed = true;
        for ( final WatchService watchService : watchServices ) {
            watchService.close();
        }
    }

    @Override
    public boolean hasWatchService( final FileSystem fs ) {
        return fileSystems.contains( fs );
    }

    public void addWatchService( final FileSystem fs,
                                 final WatchService ws ) {
        fileSystems.add( fs );
        watchServices.add( ws );

        final AsyncWatchService asyncWatchService = new AsyncWatchService() {
            @Override
            public void execute( final Event<ResourceBatchChangesEvent> resourceBatchChanges,
                                 final Event<ResourceUpdatedEvent> resourceUpdatedEvent,
                                 final Event<ResourceRenamedEvent> resourceRenamedEvent,
                                 final Event<ResourceDeletedEvent> resourceDeletedEvent,
                                 final Event<ResourceAddedEvent> resourceAddedEvent ) {
                while ( !isDisposed && !ws.isClose() ) {
                    final WatchKey wk = ws.take();
                    if ( wk == null ) {
                        continue;
                    }

                    final List<WatchEvent<?>> events = wk.pollEvents();
                    WatchContext firstContext = null;

                    if ( events.size() > 1 ) {
                        final Map<Path, Collection<ResourceChange>> changes = new HashMap<Path, Collection<ResourceChange>>();
                        for ( final WatchEvent event : events ) {
                            if ( !filterEvent( event ) ) {
                                if ( firstContext == null ) {
                                    firstContext = (WatchContext) event.context();
                                }
                                final Pair<Path, ResourceChange> result = buildChange( event );
                                if ( result != null ) {
                                    if ( !changes.containsKey( result.getK1() ) ) {
                                        changes.put( result.getK1(), new ArrayList<ResourceChange>() );
                                    }
                                    changes.get( result.getK1() ).add( result.getK2() );
                                }
                            }
                        }
                        if ( changes.size() == 1 && changes.values().size() == 1 ) {
                            final ResourceChange _event = changes.values().iterator().next().iterator().next();
                            if ( _event instanceof ResourceUpdated ) {
                                resourceUpdatedEvent.fire( (ResourceUpdatedEvent) toEvent( changes.keySet().iterator().next(), _event, firstContext ) );
                            } else if ( _event instanceof ResourceAdded ) {
                                resourceAddedEvent.fire( (ResourceAddedEvent) toEvent( changes.keySet().iterator().next(), _event, firstContext ) );
                            } else if ( _event instanceof ResourceRenamed ) {
                                resourceRenamedEvent.fire( (ResourceRenamedEvent) toEvent( changes.keySet().iterator().next(), _event, firstContext ) );
                            } else if ( _event instanceof ResourceDeleted ) {
                                resourceDeletedEvent.fire( (ResourceDeletedEvent) toEvent( changes.keySet().iterator().next(), _event, firstContext ) );
                            }
                        } else if ( changes.size() > 1 ) {
                            resourceBatchChanges.fire( new ResourceBatchChangesEvent( changes, sessionInfo( firstContext ) ) );
                        }
                    } else if ( events.size() == 1 ) {
                        try {
                            final WatchEvent<?> event = events.get( 0 );
                            if ( !filterEvent( event ) ) {
                                if ( event.kind().equals( StandardWatchEventKind.ENTRY_MODIFY ) ) {
                                    resourceUpdatedEvent.fire( buildEvent( ResourceUpdatedEvent.class, event ).getK2() );
                                } else if ( event.kind().equals( StandardWatchEventKind.ENTRY_CREATE ) ) {
                                    resourceAddedEvent.fire( buildEvent( ResourceAddedEvent.class, event ).getK2() );
                                } else if ( event.kind().equals( StandardWatchEventKind.ENTRY_RENAME ) ) {
                                    resourceRenamedEvent.fire( buildEvent( ResourceRenamedEvent.class, event ).getK2() );
                                } else if ( event.kind().equals( StandardWatchEventKind.ENTRY_DELETE ) ) {
                                    resourceDeletedEvent.fire( buildEvent( ResourceDeletedEvent.class, event ).getK2() );
                                }
                            }
                        } catch ( final Exception ex ) {
                            LOGGER.error( "Unexpected error during WatchService events fire.", ex );
                        }

                    }
                }
            }

            @Override
            public String getDescription() {
                return AbstractWatchService.this.getClass().getName() + "(" + ws.toString() + ")";
            }
        };

        if ( started ) {
            getExecutorManager().execute( asyncWatchService );
        } else {
            watchThreads.add( asyncWatchService );
        }
    }

    private WatchServiceExecutorManager getExecutorManager() {
        if ( executorManager == null ) {
            WatchServiceExecutorManager _executorManager = null;
            try {
                _executorManager = InitialContext.doLookup( "java:module/WatchServiceExecutorManager" );
            } catch ( final Exception ignored ) {
            }

            if ( _executorManager == null ) {
                executorManager = new WatchServiceExecutorManager();
                executorManager.setEvents( resourceBatchChanges, resourceUpdatedEvent, resourceRenamedEvent, resourceDeletedEvent, resourceAddedEvent );
            } else {
                executorManager = _executorManager;
            }
        }

        return executorManager;
    }

    private ResourceEvent toEvent( final Path path,
                                   final ResourceChange change,
                                   final WatchContext context ) {
        if ( change instanceof ResourceUpdated ) {
            return new ResourceUpdatedEvent( path, sessionInfo( context ) );
        } else if ( change instanceof ResourceAdded ) {
            return new ResourceAddedEvent( path, sessionInfo( context ) );
        } else if ( change instanceof ResourceRenamed ) {
            return new ResourceRenamedEvent( path, ( (ResourceRenamed) change ).getDestinationPath(), sessionInfo( context ) );
        } else if ( change instanceof ResourceDeleted ) {
            return new ResourceDeletedEvent( path, sessionInfo( context ) );
        }
        return null;
    }

    protected abstract boolean filterEvent( final WatchEvent<?> event );

    private <T extends ResourceEvent> Pair<Path, T> buildEvent( final Class<T> clazz,
                                                                final WatchEvent<?> event ) {
        final WatchContext context = (WatchContext) event.context();

        final Path _affectedPath;
        final T result;
        if ( event.kind().equals( StandardWatchEventKind.ENTRY_MODIFY ) ) {
            _affectedPath = convert( context.getOldPath() );
            result = (T) new ResourceUpdatedEvent( _affectedPath, sessionInfo( context ) );
        } else if ( event.kind().equals( StandardWatchEventKind.ENTRY_CREATE ) ) {
            _affectedPath = convert( context.getPath() );
            result = (T) new ResourceAddedEvent( _affectedPath, sessionInfo( context ) );
        } else if ( event.kind().equals( StandardWatchEventKind.ENTRY_RENAME ) ) {
            _affectedPath = convert( context.getOldPath() );
            result = (T) new ResourceRenamedEvent( _affectedPath, convert( context.getPath() ), sessionInfo( context ) );
        } else if ( event.kind().equals( StandardWatchEventKind.ENTRY_DELETE ) ) {
            _affectedPath = convert( context.getOldPath() );
            result = (T) new ResourceDeletedEvent( _affectedPath, sessionInfo( context ) );
        } else {
            _affectedPath = null;
            result = null;
        }
        if ( _affectedPath == null ) {
            return null;
        }

        return Pair.newPair( _affectedPath, result );
    }

    private Pair<Path, ResourceChange> buildChange( final WatchEvent<?> event ) {
        final WatchContext context = (WatchContext) event.context();

        final Path _affectedPath;
        final ResourceChange result;
        if ( event.kind().equals( StandardWatchEventKind.ENTRY_MODIFY ) ) {
            _affectedPath = convert( context.getOldPath() );
            result = new ResourceUpdated();
        } else if ( event.kind().equals( StandardWatchEventKind.ENTRY_CREATE ) ) {
            _affectedPath = convert( context.getPath() );
            result = new ResourceAdded();
        } else if ( event.kind().equals( StandardWatchEventKind.ENTRY_RENAME ) ) {
            _affectedPath = convert( context.getOldPath() );
            result = new ResourceRenamed( convert( context.getPath() ) );
        } else if ( event.kind().equals( StandardWatchEventKind.ENTRY_DELETE ) ) {
            _affectedPath = convert( context.getOldPath() );
            result = new ResourceDeleted();
        } else {
            _affectedPath = null;
            result = null;
        }
        if ( _affectedPath == null ) {
            return null;
        }

        return Pair.newPair( _affectedPath, result );
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
