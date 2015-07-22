package org.uberfire.backend.server;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;

/**
 * Observes the creation and deletion of locks and notifies all connected
 * clients of the corresponding lock status changes.
 */
@ApplicationScoped
@Startup
public class LockClientNotifier {

    @Inject
    @Named("systemFS")
    private FileSystem fs;

    @Inject
    @Named("configIO")
    private IOService ioService;

    @Inject
    private Event<LockInfo> lockEvent;

    private WatchService ws;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private volatile boolean active = true;

    @PostConstruct
    private void init() {
        ws = fs.newWatchService();
        executorService.submit( new Runnable() {

            @Override
            public void run() {
                observeAndNotifyClients();
            }
        } );
    }

    @PreDestroy
    private void shutdown() {
        executorService.shutdown();

        active = false;

        if ( ws != null ) {
            ws.close();
        }
    }

    private void observeAndNotifyClients() {
        while ( active ) {
            try {

                final WatchKey wk;
                try {
                    wk = ws.take();
                } catch ( final Exception ex ) {
                    break;
                }

                final List<WatchEvent<?>> events = wk.pollEvents();
                for ( final WatchEvent<?> event : events ) {
                    final boolean created = event.kind().equals( StandardWatchEventKind.ENTRY_CREATE );
                    final boolean deleted = event.kind().equals( StandardWatchEventKind.ENTRY_DELETE );

                    final WatchContext context = (WatchContext) event.context();
                    final Path path = (created) ? context.getPath() : context.getOldPath();

                    if ( path != null && path.getFileName().toString().endsWith( PathFactory.LOCK_FILE_EXTENSION ) ) {
                        final org.uberfire.backend.vfs.Path vfsLockPath = Paths.convert( path );
                        final org.uberfire.backend.vfs.Path vfsPath = PathFactory.fromLock( vfsLockPath );

                        if ( created ) {
                            final String lockedBy = ioService.readAllString( path );
                            lockEvent.fire( new LockInfo( true,
                                                          lockedBy,
                                                          vfsPath,
                                                          vfsLockPath ) );
                        } else if ( deleted ) {
                            lockEvent.fire( new LockInfo( false,
                                                          null,
                                                          vfsPath,
                                                          vfsLockPath ) );
                        }
                    }
                }

                if ( !wk.reset() ) {
                    break;
                }
            } catch ( final Exception ignored ) {
            }
        }
    }
}