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

import static org.apache.commons.lang3.StringUtils.substringAfterLast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.workbench.events.PluginAddedEvent;
import org.uberfire.workbench.events.PluginUpdatedEvent;
import org.uberfire.workbench.events.PluginsReloadedEvent;

/**
 * Processes and activates deployed plugin jars. The corresponding .js files are
 * registered with the {@link PluginRegistry} and are added to the host page's
 * &lt;head&gt; element by Errai, so that all plugin scripts execute before the
 * web application's main script runs. This is required to ensure plugins and
 * their managed beans are discoverable by Errai's bean manager when the main
 * application bootstraps.
 */
@Dependent
public class GwtRuntimePluginLoader {
    private static final Logger LOG = LoggerFactory.getLogger( GwtRuntimePluginLoader.class );
    
    private PluginRegistry pluginRegistry;
    
    private Event<PluginAddedEvent> pluginAddedEvent;
    private Event<PluginUpdatedEvent> pluginUpdatedEvent;
    private Event<PluginsReloadedEvent> pluginsReloadedEvent;
    private String pluginDir;
    private String pluginDeploymentUrlPath;
    private String pluginDeploymentDir;

    @Inject
    public GwtRuntimePluginLoader(final Event<PluginAddedEvent> pluginAddedEvent,
                        final Event<PluginUpdatedEvent> pluginUpdatedEvent,
                        final Event<PluginsReloadedEvent> pluginsReloadedEvent) {
        
        this.pluginAddedEvent = pluginAddedEvent;
        this.pluginUpdatedEvent = pluginUpdatedEvent;
        this.pluginsReloadedEvent = pluginsReloadedEvent;
    }
    
    /**
     * Processes and loads the currently deployed plugins.
     * 
     * @param contextRootDir
     *            the web application's context root directory, must not be
     *            null.
     * @param pluginDir
     *            the plugin directory, must not be null.
     * @param pluginDeploymentDir
     *            the directory to deploy plugin contents to, must not be null.
     */
    public void init( final String contextRootDir,
                      final String pluginDir,
                      final String pluginDeploymentDir,
                      final PluginRegistry pluginRegistry ) throws IOException {
        
        this.pluginDeploymentUrlPath = substringAfterLast( pluginDeploymentDir, File.separator );
        this.pluginDeploymentDir = pluginDeploymentDir;
        this.pluginDir = pluginDir;
        this.pluginRegistry = pluginRegistry;
        loadPlugins();
    }
    
    void loadPlugins() throws IOException {
        pluginRegistry.removeAll();
        
        final File pluginRoot = new File( pluginDir );
        if ( pluginRoot.exists() ) {
            Collection<File> deployedPlugins = FileUtils.listFiles( pluginRoot,
                                                                    new String[]{"jar"},
                                                                    false );

            deployedPlugins.forEach( p -> loadPlugin( Paths.get( p.getAbsolutePath() ),
                                                         false ) );
        }
    }
    
    /**
     * Clears the plugin registry and reloads all currently deployed plugins.
     * Fires a {@link PluginsReloadedEvent} when done.
     */
    public void reload() throws IOException {
        loadPlugins();
        pluginsReloadedEvent.fire( new PluginsReloadedEvent() );
    }

    /**
     * Unpacks the provided plugin (path pointing to a JAR file), searches for
     * the corresponding JavaScript file and registers the plugin url with the
     * {@link PluginRegistry}.
     * 
     * @param path
     *            path to a deployed jar file.
     * @param notifyClients
     *            true if clients should be notified (of added and updated
     *            plugins) through CDI events, otherwise false.
     */
    public void loadPlugin( Path path, boolean notifyClients ) {
        final String pluginJs = processPluginJar( pluginDir + File.separator + path.toFile().getName() );

        if ( pluginJs != null ) {
            final String pluginDisplayName = pluginJs.replace( ".nocache.js", "" );

            if ( !pluginRegistry.isRegistered( pluginJs ) ) {
                final String url = pluginDeploymentUrlPath + "/" + pluginJs + "?nocache=" + System.currentTimeMillis();
                pluginRegistry.add( pluginJs, url );

                if ( notifyClients ) {
                    pluginAddedEvent.fire( new PluginAddedEvent( pluginDisplayName ) );
                }
            } else {
                if ( notifyClients ) {
                    pluginUpdatedEvent.fire( new PluginUpdatedEvent( pluginDisplayName ) );
                }
            }
        } else {
            LOG.warn( "Deployed plugin " + path.toFile().getName() + " does not contain a nocache.js file!" );
        }
    }

    String processPluginJar( String jarFileName ) {
        String pluginScriptFileName = null;

        try ( JarFile jar = new JarFile( jarFileName ) ) {
            final Enumeration<?> enumEntries = jar.entries();
            while ( enumEntries.hasMoreElements() ) {
                final JarEntry file = (JarEntry) enumEntries.nextElement();
                String fileName = StringUtils.substringAfterLast( file.getName(),
                                                                  File.separator );

                if ( fileName.endsWith( "cache.js" ) ) {
                    final File f = new File( pluginDeploymentDir + File.separator + fileName );
                    try ( InputStream is = jar.getInputStream( file );
                            FileOutputStream fos = new FileOutputStream( f ) ) {

                        while ( is.available() > 0 ) {
                            fos.write( is.read() );
                        }
                    }

                    if ( file.getName().endsWith( "nocache.js" ) ) {
                        pluginScriptFileName = fileName;
                    }
                }

            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        return pluginScriptFileName;
    }
    
}