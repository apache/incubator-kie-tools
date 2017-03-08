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

package org.uberfire.backend.server.plugins.engine;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PluginWatcherTest extends AbstractPluginsTest {

    @Spy
    private PluginWatcher pluginWatcher;

    @Mock
    private PluginJarProcessor pluginJarProcessor;

    @Mock
    private ExecutorService executor;

    @Mock
    private Path plugin;

    @Mock
    private Path fileName;

    @After
    public void tearDown() {
        pluginWatcher.stop();
    }

    @Test
    public void startSubmitsWatcherThread() throws Exception {
        pluginWatcher.start(pluginDir,
                            executor,
                            pluginJarProcessor);
        verify(executor,
               times(1)).submit(any(Runnable.class));
    }

    @Test
    public void startDoesNotSubmitWatcherThreadIfPluginDirDoesNotExist() throws Exception {
        pluginWatcher.start(pluginDir + "invalid",
                            executor,
                            pluginJarProcessor);
        verify(executor,
               never()).submit(any(Runnable.class));
    }

    @Test
    public void startOnlyOnce() throws Exception {
        pluginWatcher.start(pluginDir,
                            executor,
                            pluginJarProcessor);
        pluginWatcher.start(pluginDir,
                            executor,
                            pluginJarProcessor);
        verify(executor,
               times(1)).submit(any(Runnable.class));
    }

    @Test
    public void stopEndsWatcherThread() throws Exception {
        pluginWatcher.start(pluginDir,
                            executor,
                            pluginJarProcessor);
        assertTrue(pluginWatcher.active);
        pluginWatcher.stop();
        assertFalse(pluginWatcher.active);
        verify(executor,
               times(1)).shutdown();
    }

    @Test
    public void loadPluginLogsError() throws Exception {
        pluginWatcher.start(pluginDir,
                            executor,
                            pluginJarProcessor);

        when(fileName.toString()).thenReturn("fileName.jar");
        when(plugin.getFileName()).thenReturn(fileName);

        Exception e = new RuntimeException();
        doThrow(e).when(pluginJarProcessor).loadPlugins(any(Path.class),
                                                        any(Boolean.class));
        pluginWatcher.loadPlugins(plugin);
        verify(pluginWatcher,
               times(1)).logPluginsWatcherError("Failed to process new plugin fileName.jar",
                                                e,
                                                false);

        pluginWatcher.stop();
        pluginWatcher.loadPlugins(plugin);
        verify(pluginWatcher,
               times(1)).logPluginsWatcherError("Failed to process new plugin fileName.jar",
                                                e,
                                                true);
    }

    @Test
    public void reloadPluginsLogsError() throws Exception {
        pluginWatcher.start(pluginDir,
                            executor,
                            pluginJarProcessor);

        when(fileName.toString()).thenReturn("fileName.js");
        when(plugin.getFileName()).thenReturn(fileName);

        Exception e = new RuntimeException();
        doThrow(e).when(pluginJarProcessor).reload();
        pluginWatcher.reloadPlugins(plugin);
        verify(pluginWatcher,
               times(1)).logPluginsWatcherError("Failed to delete plugin fileName.js",
                                                e,
                                                false);

        pluginWatcher.stop();
        pluginWatcher.reloadPlugins(plugin);
        verify(pluginWatcher,
               times(1)).logPluginsWatcherError("Failed to delete plugin fileName.js",
                                                e,
                                                true);
    }
}
