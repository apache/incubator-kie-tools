/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.concurrent.ExecutorService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PluginManagerTest extends AbstractPluginsTest {

    @Mock
    private PluginWatcher pluginWatcher;

    @Mock
    private PluginJarProcessor pluginJarProcessor;

    @InjectMocks
    private PluginManager manager;

    @Test
    public void initPluginLoader() throws Exception {
        manager.init(contextRootDir,
                     pluginDir);
        verify(pluginJarProcessor,
               times(1)).init(eq(pluginDir),
                              eq(pluginDeploymentDir));
    }

    @Test
    public void initStartsWatcher() throws Exception {
        manager.init(contextRootDir,
                     pluginDir);
        verify(pluginWatcher,
               times(1)).start(eq(pluginDir),
                               any(ExecutorService.class),
                               eq(pluginJarProcessor));
    }

    @Test
    public void shutdownStopsWatcher() throws Exception {
        manager.shutDown();
        verify(pluginWatcher,
               times(1)).stop();
    }

    @Test
    public void findPluginDeploymentDir() throws Exception {
        PluginManager managerSpy = spy(manager);
        final String pluginDeploymentDir = managerSpy.findPluginDeploymentDir(contextRootDir);
        verify(managerSpy).encodePath(contextRootDir);
        assertEquals(this.pluginDeploymentDir,
                     pluginDeploymentDir);
    }
}