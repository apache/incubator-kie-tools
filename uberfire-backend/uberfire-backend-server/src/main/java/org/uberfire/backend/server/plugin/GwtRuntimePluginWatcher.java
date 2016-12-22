/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.backend.server.plugin;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.Dependent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Monitors the plugin directory for changes and loads/removes plugins using the
 * {@link GwtRuntimePluginLoader}.
 */
@Dependent
public class GwtRuntimePluginWatcher {

    private static final Logger LOG = LoggerFactory.getLogger( GwtRuntimePluginWatcher.class );

    volatile boolean active;
    private ExecutorService executor;
    private GwtRuntimePluginLoader pluginLoader;

    /**
     * Starts the plugin watcher iff the provided plugin directory exists and
     * the watcher hasn't already been started.
     * 
     * @param pluginDir
     *            the plugin directory to monitor
     * @param executor
     *            the executor service to submit the watch thread to
     * @param pluginLoader
     *            the plugin loader for registering and removing plugins
     */
    void start( final String pluginDir,
                final ExecutorService executor,
                final GwtRuntimePluginLoader pluginLoader ) throws IOException {

        final Path pluginRootPath = Paths.get( pluginDir );
        if ( active || !Files.exists( pluginRootPath ) ) {
            return;
        }

        this.active = true;
        this.executor = executor;
        this.pluginLoader = pluginLoader;

        final WatchService watchService = FileSystems.getDefault().newWatchService();
        pluginRootPath.register( watchService,
                                 ENTRY_CREATE,
                                 ENTRY_MODIFY,
                                 ENTRY_DELETE );

        startWatchService( watchService );
    }

    private void startWatchService( final WatchService watchService ) {
        executor.submit( () -> {
            while ( active ) {
                try {
                    final WatchKey watchKey = watchService.poll( 5, TimeUnit.SECONDS );
                    
                    if ( watchKey != null && active ) {
                        final List<WatchEvent<?>> events = watchKey.pollEvents();
                        for ( WatchEvent<?> event : events ) {
                            final Kind<?> kind = event.kind();
                            if ( kind == OVERFLOW ) {
                                continue;
                            }

                            final Path file = (Path) event.context();
                            if ( kind == ENTRY_CREATE || kind == ENTRY_MODIFY ) {
                                loadPlugin( file );
                            } 
                            else if ( kind == ENTRY_DELETE ) {
                                reloadPlugins( file );
                            }
                        }
                        boolean valid = watchKey.reset();
                        if ( !valid ) {
                            break;
                        }
                    }
                } catch ( InterruptedException e ) {
                    active = false;
                    Thread.currentThread().interrupt();
                }
            }
        } );
    }

    void stop() {
        active = false;
        
        if (executor != null) {
            executor.shutdown();
        }
    }

    void loadPlugin( final Path file ) {
        if ( file.getFileName().toString().endsWith( ".jar" ) ) {
            try {
                pluginLoader.loadPlugin( file, true );
            } catch ( Exception e ) {                
                logPluginWatcherError( "Failed to process new plugin " + file.getFileName().toString(), e, !active );
            }
        }
    }

    void reloadPlugins( final Path file ) {
        try {
            pluginLoader.reload();
        } catch ( Exception e ) {
            logPluginWatcherError( "Failed to delete plugin " + file.getFileName().toString(), e, !active );
        }
    }
    
    void logPluginWatcherError (final String message, final Exception e, final boolean debug) {
        if (debug) {
            // Debug level is sufficient in case application is stopping
            LOG.debug( message );
        }
        else {
            LOG.error( message );
        }
    }

}