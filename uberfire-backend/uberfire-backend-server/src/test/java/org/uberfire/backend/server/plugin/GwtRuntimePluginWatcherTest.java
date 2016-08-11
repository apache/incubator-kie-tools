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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ExecutorService;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GwtRuntimePluginWatcherTest extends AbstractGwtRuntimePluginTest {
    
    @InjectMocks
    private GwtRuntimePluginWatcher pluginWatcher;
    
    @Mock
    private GwtRuntimePluginLoader pluginLoader;
    
    @Mock
    private ExecutorService executor;
    
    @After
    public void tearDown() {
        pluginWatcher.stop();
    }
    
    @Test
    public void startSubmitsWatcherThread() throws Exception {
        pluginWatcher.start( pluginDir, executor, pluginLoader );
        verify(executor, times(1)).submit( any (Runnable.class));
    }
    
    @Test
    public void startDoesNotSubmitWatcherThreadIfPluginDirDoesNotExist() throws Exception {
        pluginWatcher.start( pluginDir + "invalid", executor, pluginLoader );
        verify(executor, never()).submit( any (Runnable.class));        
    }
    
    @Test
    public void startOnlyOnce() throws Exception {
        pluginWatcher.start( pluginDir, executor, pluginLoader );
        pluginWatcher.start( pluginDir, executor, pluginLoader );
        verify(executor, times(1)).submit( any (Runnable.class));
    }
    
    @Test
    public void stopEndsWatcherThread() throws Exception {
        pluginWatcher.start( pluginDir, executor, pluginLoader );
        assertTrue(pluginWatcher.active);
        pluginWatcher.stop();
        assertFalse(pluginWatcher.active);
        verify(executor, times(1)).shutdown( );
    }

}
