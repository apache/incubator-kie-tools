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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.workbench.events.PluginAddedEvent;
import org.uberfire.workbench.events.PluginUpdatedEvent;
import org.uberfire.workbench.events.PluginsReloadedEvent;

@RunWith(MockitoJUnitRunner.class)
public class GwtRuntimePluginLoaderTest extends AbstractGwtRuntimePluginTest {
    
    private GwtRuntimePluginLoader pluginLoader;
    
    @Mock
    private Event<PluginAddedEvent> pluginAddedEvent;
    
    @Mock
    private Event<PluginUpdatedEvent> pluginUpdatedEvent;
    
    @Mock
    private Event<PluginsReloadedEvent> pluginsReloadedEvent;
    
    @Mock
    private PluginRegistry pluginRegistry;
    
    @Before
    public void setup() {
        super.setup();
        
        pluginLoader = spy( new GwtRuntimePluginLoader( pluginAddedEvent,
                                                        pluginUpdatedEvent,
                                                        pluginsReloadedEvent) );
        
    }
    
    @Test
    public void initLoadsDeployedPlugins() throws Exception {
        pluginLoader.init( contextRootDir, pluginDir, pluginDeploymentDir, pluginRegistry );
        verify(pluginLoader).loadPlugins();
    }
    
    @Test
    public void reloadClearsPluginRegistry() throws Exception {
        pluginLoader.init( contextRootDir, pluginDir, pluginDeploymentDir, pluginRegistry );
        pluginLoader.reload();
        verify(pluginRegistry, times(2)).removeAll();
    }
    
    @Test
    public void reloadFiresPluginsReloadedEvent() throws Exception {
        pluginLoader.init( contextRootDir, pluginDir, pluginDeploymentDir, pluginRegistry );
        pluginLoader.reload();
        
        verify(pluginsReloadedEvent).fire( any(PluginsReloadedEvent.class) );
    }
    
    @Test
    public void loadPluginRegistersScript() throws Exception {
        pluginLoader.init( contextRootDir, pluginDir, pluginDeploymentDir, pluginRegistry );
        doReturn("test.nocache.js").when(pluginLoader).processPluginJar( any(String.class) );
     
        final ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> name = ArgumentCaptor.forClass(String.class);
        pluginLoader.loadPlugin( Paths.get( "test" ), false );
        verify(pluginRegistry).isRegistered("test.nocache.js");
        verify(pluginRegistry).add( name.capture(), url.capture() );
        
        assertEquals("test.nocache.js", name.getValue());
        assertNotNull(url.getValue());
        assertTrue(url.getValue().startsWith( "test-app/test.nocache.js?nocache=" ));
    }
    
    @Test
    public void loadPluginFiresPluginAddedEvent() throws Exception {
        pluginLoader.init( contextRootDir, pluginDir, pluginDeploymentDir, pluginRegistry );
        doReturn("test.nocache.js").when(pluginLoader).processPluginJar( any(String.class) );
        
        pluginLoader.loadPlugin( Paths.get( "test" ), true );
        verify(pluginRegistry).isRegistered("test.nocache.js");
        verify(pluginRegistry).add( any(String.class), any(String.class));
        verify(pluginAddedEvent).fire( any (PluginAddedEvent.class) );
    }
    
    @Test
    public void loadPluginFiresPluginUpdatedEvent() throws Exception {
        pluginLoader.init( contextRootDir, pluginDir, pluginDeploymentDir, pluginRegistry );
        doReturn("test.nocache.js").when(pluginLoader).processPluginJar( any(String.class) );
        when(pluginRegistry.isRegistered( "test.nocache.js" )).thenReturn( true );
        
        pluginLoader.loadPlugin( Paths.get( "test" ), true );
        verify(pluginRegistry).isRegistered("test.nocache.js");
        verify(pluginUpdatedEvent).fire( any (PluginUpdatedEvent.class) );
    }
    
}