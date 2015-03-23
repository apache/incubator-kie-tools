package org.uberfire.backend.server.io.watch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Filter;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.async.DescriptiveThreadFactory;
import org.uberfire.commons.services.cdi.ApplicationStarted;
import org.uberfire.io.IOWatchService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

public abstract class AbstractIOWatchService implements IOWatchService,
                                                        Filter<WatchEvent<?>> {

    private static final Logger LOG = LoggerFactory.getLogger( AbstractIOWatchService.class );

    private static final Integer AWAIT_TERMINATION_TIMEOUT = Integer.parseInt( System.getProperty( "org.uberfire.watcher.quitetimeout", "3" ) );

    private final ExecutorService executorService = Executors.newCachedThreadPool( new DescriptiveThreadFactory() );

    private final List<FileSystem> fileSystems = new ArrayList<FileSystem>();
    private final List<WatchService> watchServices = new ArrayList<WatchService>();
    protected boolean isDisposed = false;

    private boolean started;
    private final Set<AsyncWatchService> watchThreads = new HashSet<AsyncWatchService>();
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

    private IOWatchServiceExecutor executor = null;

    private final Set<Future<?>> jobs = new CopyOnWriteArraySet<Future<?>>();

    public AbstractIOWatchService() {
        final boolean autostart = Boolean.parseBoolean( System.getProperty( "org.uberfire.watcher.autostart", "true" ) );
        if ( autostart ) {
            start();
        }
    }

    public synchronized void start() {
        if ( !started ) {
            this.started = true;
            for ( final AsyncWatchService watchThread : watchThreads ) {
                final IOWatchServiceExecutor watchServiceExecutor = getWatchServiceExecutor();
                jobs.add( executorService.submit( new DescriptiveRunnable() {
                    @Override
                    public String getDescription() {
                        return watchThread.getDescription();
                    }

                    @Override
                    public void run() {
                        watchThread.execute( watchServiceExecutor );
                    }
                } ) );
            }
            watchThreads.clear();
        }
    }

    @PreDestroy
    protected void dispose() {
        isDisposed = true;
        for ( final WatchService watchService : watchServices ) {
            watchService.close();
        }
        for ( final Future<?> job : jobs ) {
            if ( !job.isCancelled() && !job.isDone() ) {
                job.cancel( true );
            }
        }
        executorService.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if ( !executorService.awaitTermination( AWAIT_TERMINATION_TIMEOUT, TimeUnit.SECONDS ) ) {
                executorService.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if ( !executorService.awaitTermination( AWAIT_TERMINATION_TIMEOUT, TimeUnit.SECONDS ) ) {
                    LOG.error( "Thread pool did not terminate" );
                }
            }
        } catch ( InterruptedException ie ) {
            // (Re-)Cancel if current thread also interrupted
            executorService.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public boolean hasWatchService( final FileSystem fs ) {
        return fileSystems.contains( fs );
    }

    @Override
    public void addWatchService( final FileSystem fs,
                                 final WatchService ws ) {
        fileSystems.add( fs );
        watchServices.add( ws );

        final AsyncWatchService asyncWatchService = new AsyncWatchService() {
            @Override
            public void execute( final IOWatchServiceExecutor wsExecutor ) {
                while ( !isDisposed ) {
                    final WatchKey wk;
                    try {
                        wk = ws.take();
                    } catch ( final Exception ex ) {
                        break;
                    }

                    wsExecutor.execute( wk, AbstractIOWatchService.this );

                    // Reset the key -- this step is critical if you want to
                    // receive further watch events.  If the key is no longer valid,
                    // the directory is inaccessible so exit the loop.
                    boolean valid = wk.reset();
                    if ( !valid ) {
                        break;
                    }
                }
            }

            @Override
            public String getDescription() {
                return AbstractIOWatchService.this.getClass().getName() + "(" + ws.toString() + ")";
            }
        };

        if ( started ) {
            final IOWatchServiceExecutor watchServiceExecutor = getWatchServiceExecutor();
            executorService.execute( new DescriptiveRunnable() {
                @Override
                public String getDescription() {
                    return asyncWatchService.getDescription();
                }

                @Override
                public void run() {
                    asyncWatchService.execute( watchServiceExecutor );
                }
            } );
        } else {
            watchThreads.add( asyncWatchService );
        }
    }

    public void configureOnEvent( @Observes ApplicationStarted applicationStartedEvent ) {
        start();
    }

    protected IOWatchServiceExecutor getWatchServiceExecutor() {
        if ( executor == null ) {
            IOWatchServiceExecutor _executor = null;
            try {
                _executor = InitialContext.doLookup( "java:module/IOWatchServiceExecutorImpl" );
            } catch ( final Exception ignored ) {
            }

            if ( _executor == null ) {
                _executor = new IOWatchServiceExecutorImpl();
                ( (IOWatchServiceExecutorImpl) _executor ).setEvents( resourceBatchChanges, resourceUpdatedEvent, resourceRenamedEvent, resourceDeletedEvent, resourceAddedEvent );
            }
            executor = _executor;
        }

        return executor;
    }
}
