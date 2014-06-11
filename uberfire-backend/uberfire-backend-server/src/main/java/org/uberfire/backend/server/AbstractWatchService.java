package org.uberfire.backend.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.backend.server.util.Filter;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.async.DescriptiveThreadFactory;
import org.uberfire.commons.services.cdi.ApplicationStarted;
import org.uberfire.io.IOWatchService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;

public abstract class AbstractWatchService implements IOWatchService,
                                                      Filter<WatchEvent<?>> {

    @Inject
    private WatchServiceExecutorFactory factory;

    private final ExecutorService executorService = Executors.newCachedThreadPool( new DescriptiveThreadFactory() );

    private final List<FileSystem> fileSystems = new ArrayList<FileSystem>();
    private final List<WatchService> watchServices = new ArrayList<WatchService>();
    private boolean isDisposed = false;

    private boolean started;
    private Set<AsyncWatchService> watchThreads = new HashSet<AsyncWatchService>();
    private WatchServiceExecutor watchServiceExecutor = null;

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
            for ( final AsyncWatchService watchThread : watchThreads ) {
                executorService.execute( new DescriptiveRunnable() {
                    @Override
                    public String getDescription() {
                        return watchThread.getDescription();
                    }

                    @Override
                    public void run() {
                        watchThread.execute( getWatchServiceExecutor() );
                    }
                } );
            }
            watchServices.clear();
        }
    }

    @PreDestroy
    void dispose() {
        isDisposed = true;
        for ( final WatchService watchService : watchServices ) {
            watchService.close();
        }
        executorService.shutdown();
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
            public void execute( final WatchServiceExecutor wsExecutor ) {
                while ( !isDisposed ) {
                    final WatchKey wk;
                    try {
                        wk = ws.take();
                    } catch ( final Exception ex ) {
                        break;
                    }

                    wsExecutor.execute( wk, AbstractWatchService.this );

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
                return AbstractWatchService.this.getClass().getName() + "(" + ws.toString() + ")";
            }
        };

        if ( started ) {
            executorService.execute( new DescriptiveRunnable() {
                @Override
                public String getDescription() {
                    return asyncWatchService.getDescription();
                }

                @Override
                public void run() {
                    asyncWatchService.execute( getWatchServiceExecutor() );
                }
            } );
        } else {
            watchThreads.add( asyncWatchService );
        }
    }

    private WatchServiceExecutor getWatchServiceExecutor() {
        if ( watchServiceExecutor == null ) {
            watchServiceExecutor = factory.getWatchServiceExecutor();
        }
        return watchServiceExecutor;
    }
}
