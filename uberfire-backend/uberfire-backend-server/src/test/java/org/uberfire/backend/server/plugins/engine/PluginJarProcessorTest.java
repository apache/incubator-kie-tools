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

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.plugin.PluginProcessor;
import org.uberfire.backend.server.plugins.processors.GWTScriptPluginProcessor;
import org.uberfire.backend.server.plugins.processors.HTMLPluginProcessor;
import org.uberfire.backend.server.plugins.processors.UFJSPluginProcessor;
import org.uberfire.workbench.events.PluginReloadedEvent;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PluginJarProcessorTest extends AbstractPluginsTest {

    @Mock
    Instance<PluginProcessor> pluginProcessors;
    PluginJarProcessor pluginJarProcessor;
    @Mock
    private Event<PluginReloadedEvent> pluginsReloadedEvent;
    @Mock
    private GWTScriptPluginProcessor gwtScriptPluginProcessor;
    @Mock
    private HTMLPluginProcessor htmlPluginProcessor;
    @Mock
    private UFJSPluginProcessor ufjsPluginProcessor;

    @Before
    public void setup() {
        super.setup();
        List<PluginProcessor> pluginProcessorsList = Arrays.asList(gwtScriptPluginProcessor,
                                                                   htmlPluginProcessor,
                                                                   ufjsPluginProcessor);
        when(this.pluginProcessors.iterator()).thenReturn(pluginProcessorsList.iterator());
        pluginJarProcessor = spy(new PluginJarProcessor(pluginProcessors,
                                                        pluginsReloadedEvent) {
            @Override
            List<String> extractFilesFromPluginsJar(String jarFileName) {
                return Arrays.asList("dora.html",
                                     "dora.txt");
            }
        });
    }

    @Test
    public void initLoadsDeployedPlugins() throws Exception {
        pluginJarProcessor.init(pluginDir,
                                pluginDeploymentDir);
        verify(pluginJarProcessor).loadPlugins();
    }

    @Test
    public void reloadClearsPluginRegistry() throws Exception {
        pluginJarProcessor.init(pluginDir,
                                pluginDeploymentDir);
        pluginJarProcessor.reload();

        verify(pluginJarProcessor,
               times(2)).removeAllPlugins();
    }

    @Test
    public void reloadFiresPluginsReloadedEvent() throws Exception {
        pluginJarProcessor.init(pluginDir,
                                pluginDeploymentDir);
        pluginJarProcessor.reload();

        verify(pluginsReloadedEvent).fire(any(PluginReloadedEvent.class));
    }

    @Test
    public void loadPlugins() throws Exception {
        Path path = mock(Path.class);
        when(path.toFile()).thenReturn(mock(File.class));

        when(htmlPluginProcessor.shouldProcess("dora.html")).thenReturn(true);

        pluginJarProcessor.loadPlugins(path,
                                       true);

        verify(htmlPluginProcessor,
               times(1)).process(eq("dora.html"),
                                 any(),
                                 eq(true));
    }
}