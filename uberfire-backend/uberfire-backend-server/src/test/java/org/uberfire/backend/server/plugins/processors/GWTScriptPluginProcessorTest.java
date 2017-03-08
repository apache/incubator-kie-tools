package org.uberfire.backend.server.plugins.processors;

import javax.enterprise.event.Event;

import org.jboss.errai.cdi.server.scripts.ScriptRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.plugins.engine.AbstractPluginsTest;
import org.uberfire.workbench.events.PluginAddedEvent;
import org.uberfire.workbench.events.PluginUpdatedEvent;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GWTScriptPluginProcessorTest extends AbstractPluginsTest {

    GWTScriptPluginProcessor processor;
    @Mock
    private ScriptRegistry scriptRegistry;
    @Mock
    private Event<PluginAddedEvent> pluginAddedEvent;
    @Mock
    private Event<PluginUpdatedEvent> pluginUpdatedEvent;

    @Before
    public void setup() {
        super.setup();
        processor = new GWTScriptPluginProcessor(scriptRegistry,
                                                 pluginAddedEvent,
                                                 pluginUpdatedEvent);
    }

    @Test
    public void processTest() {
        assertFalse(processor.isRegistered("test-app.nocache.js"));
        processor.process("test-app.nocache.js",
                          pluginDeploymentDir,
                          true);

        assertTrue(processor.isRegistered("test-app.nocache.js"));
        verify(scriptRegistry,
               times(1)).addScript(eq("UF"),
                                   anyString());
        verify(pluginAddedEvent,
               times(1)).fire(any());

        processor.process("test-app.nocache.js",
                          pluginDeploymentDir,
                          true);
        verify(pluginUpdatedEvent,
               times(1)).fire(any());
    }

    @Test
    public void shutDownShouldRemoveScripts() throws Exception {
        processor.shutDown();
        verify(scriptRegistry,
               times(1)).removeScripts("UF");
    }

    @Test
    public void removeAllShouldClearPluginsAndScriptRegistry() throws Exception {
        processor.availablePlugins.add("test");
        assertFalse(processor.availablePlugins.isEmpty());

        processor.removeAll();
        verify(scriptRegistry,
               times(1)).removeScripts("UF");

        assertTrue(processor.availablePlugins.isEmpty());
    }

    @Test
    public void shouldProcessTest() throws Exception {

        assertFalse(processor.shouldProcess("pluginname.html"));
        assertTrue(processor.shouldProcess("pluginname.nocache.js"));
    }
}