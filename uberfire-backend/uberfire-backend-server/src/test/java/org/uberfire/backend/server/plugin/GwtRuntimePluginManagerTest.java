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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ExecutorService;

import org.jboss.errai.cdi.server.scripts.ScriptRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GwtRuntimePluginManagerTest extends AbstractGwtRuntimePluginTest {
    
    @InjectMocks
    private GwtRuntimePluginManager manager;
    
    @Mock
    private GwtRuntimePluginWatcher pluginWatcher;
    
    @Mock
    private GwtRuntimePluginLoader pluginLoader;
    
    @Mock
    private ScriptRegistry scriptRegistry;
    
    @Test
    public void initPluginLoader() throws Exception {
        manager.init( contextRootDir, pluginDir );
        verify( pluginLoader,
                times( 1 ) ).init( eq( contextRootDir ),
                                   eq( pluginDir ),
                                   eq( pluginDeploymentDir ),
                                   any( PluginRegistry.class ) );
    }
    
    @Test
    public void initStartsWatcher() throws Exception {
        manager.init( contextRootDir, pluginDir );
        verify(pluginWatcher, times(1)).start(eq(pluginDir), any(ExecutorService.class), eq(pluginLoader));
    }
    
    @Test
    public void shutdownStopsWatcher() throws Exception {
        manager.shutDown( );
        verify(pluginWatcher, times(1)).stop();
    }
    
    @Test
    public void shutDownRemovesScripts() throws Exception {
        manager.shutDown( );
        verify(scriptRegistry, times(1)).removeScripts( "UF" );
    }
    
    @Test
    public void findPluginDeploymentDir() throws Exception {
        final String pluginDeploymentDir = manager.findPluginDeploymentDir( contextRootDir );
        assertEquals( this.pluginDeploymentDir, pluginDeploymentDir );
    }
    
    @Test
    public void pluginRegistryUsesErraiScriptRegistry() throws Exception {
        manager.init( contextRootDir, pluginDir );
        final ArgumentCaptor<PluginRegistry> pluginRegistry = ArgumentCaptor.forClass(PluginRegistry.class);
        
        verify( pluginLoader,
                times( 1 ) ).init( eq( contextRootDir ),
                                   eq( pluginDir ),
                                   eq( pluginDeploymentDir ),
                                   pluginRegistry.capture() );
        
        final PluginRegistry registry = pluginRegistry.getValue();
        registry.add( "test", "url");
        verify(scriptRegistry).addScript( GwtRuntimePluginManager.SCRIPT_REGISTRY_KEY, "url" );
        assertTrue(registry.isRegistered( "test" ));
        
        registry.remove( "test", "url" );
        verify(scriptRegistry).removeScript( GwtRuntimePluginManager.SCRIPT_REGISTRY_KEY, "url" );
        assertFalse(registry.isRegistered( "test" ));
        
        registry.add( "test", "url");
        assertTrue(registry.isRegistered( "test" ));
        
        registry.removeAll();
        verify(scriptRegistry).removeScripts( GwtRuntimePluginManager.SCRIPT_REGISTRY_KEY );
        assertFalse(registry.isRegistered( "test" ));
    }

}
