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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.jboss.errai.cdi.server.scripts.ScriptRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages GWT runtime plugins which involves monitoring the plugin directory
 * (see {@link GwtRuntimePluginWatcher}), and loading the deployed plugins (see
 * {@link GwtRuntimePluginLoader}).
 */
@Dependent
public class GwtRuntimePluginManager {

    private static final Logger LOG = LoggerFactory.getLogger( GwtRuntimePluginManager.class );
    static final String SCRIPT_REGISTRY_KEY = "UF";

    private GwtRuntimePluginWatcher pluginWatcher;
    private GwtRuntimePluginLoader pluginLoader;
    private ExecutorService executor;
    private ScriptRegistry scriptRegistry;

    @Inject
    public GwtRuntimePluginManager( final GwtRuntimePluginWatcher pluginWatcher,
                                    final GwtRuntimePluginLoader pluginLoader,
                                    final ScriptRegistry scriptRegistry) {

        this.pluginWatcher = pluginWatcher;
        this.pluginLoader = pluginLoader;
        this.scriptRegistry = scriptRegistry;
        this.executor = Executors.newSingleThreadExecutor();
    }

    @PreDestroy
    void shutDown() {
        pluginWatcher.stop();
        scriptRegistry.removeScripts( SCRIPT_REGISTRY_KEY );
    }

    /**
     * Initializes the {@link GwtRuntimePluginLoader} and
     * {@link GwtRuntimePluginWatcher} based on the provided parameters.
     * 
     * @param contextRootDir
     *            the web application's context root directory, must not be
     *            null.
     * @param pluginDir
     *            the plugin directory, must not be null.
     */
    public void init( final String contextRootDir,
                      final String pluginDir ) {
        try {
            pluginLoader.init( contextRootDir,
                               pluginDir,
                               findPluginDeploymentDir( contextRootDir ),
                               createPluginRegistry() );

            pluginWatcher.start( pluginDir,
                                 executor,
                                 pluginLoader );
        } catch ( Exception e ) {
            LOG.error( "Failed to initialize " + GwtRuntimePluginManager.class.getName(), e );
            throw new RuntimeException( e );
        }
    }

    /**
     * Finds the directory to deploy plugin contents to.
     * 
     * @param contextRootDir
     *            the web application's context root directory, must not be
     *            null.
     * 
     * @return the directory hosting the plugin's JS files or the provided
     *         context root directory if not found.
     */
    String findPluginDeploymentDir( String contextRootDir ) throws IOException {
        final Collection<File> gwtFiles = FileUtils.listFiles( new File( contextRootDir ),
                                                               new String[]{"nocache.js"},
                                                               true );
        if ( !gwtFiles.isEmpty() ) {
            final File gwtFile = gwtFiles.iterator().next();
            return gwtFile.getParentFile().getCanonicalPath();
        }
        return new File( contextRootDir ).getCanonicalPath();
    }
    
    private PluginRegistry createPluginRegistry() {
        final Set<String> availablePlugins = new HashSet<String>();
        return new PluginRegistry() {
            
            @Override
            public void add( String pluginName, String scriptUrl) {
                availablePlugins.add( pluginName );
                scriptRegistry.addScript( SCRIPT_REGISTRY_KEY, scriptUrl );
            }

            @Override
            public void remove( String pluginName, String scriptUrl ) {
                availablePlugins.remove( pluginName );
                scriptRegistry.removeScript( SCRIPT_REGISTRY_KEY, scriptUrl );
            }

            @Override
            public void removeAll() {
                availablePlugins.clear();
                scriptRegistry.removeScripts( SCRIPT_REGISTRY_KEY );
                
            }

            @Override
            public boolean isRegistered( String pluginName ) {
                return availablePlugins.contains( pluginName );
            }
        };
    }

}