package org.uberfire.backend.server.plugins.processors;

import org.jboss.errai.cdi.server.scripts.ScriptRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.plugins.engine.AbstractPluginsTest;
import org.uberfire.workbench.events.PluginAddedEvent;
import org.uberfire.workbench.events.PluginUpdatedEvent;

import javax.enterprise.event.Event;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GWTScriptPluginProcessorTest extends AbstractPluginsTest {


    @Mock
    private ScriptRegistry scriptRegistry;
    @Mock
    private Event<PluginAddedEvent> pluginAddedEvent;
    @Mock
    private Event<PluginUpdatedEvent> pluginUpdatedEvent;

    GWTScriptPluginProcessor processor;

    @Before
    public void setup() {
        super.setup();
        processor = new GWTScriptPluginProcessor(scriptRegistry, pluginAddedEvent, pluginUpdatedEvent);
    }

    @Test
    public void processTest() {
        assertFalse(processor.isRegistered("test-app.nocache.js"));
        processor.process("test-app.nocache.js", pluginDeploymentDir, true);

        assertTrue(processor.isRegistered("test-app.nocache.js"));
        verify(scriptRegistry, times(1)).addScript(eq("UF"), anyString());
        verify(pluginAddedEvent, times(1)).fire(any());

        processor.process("test-app.nocache.js", pluginDeploymentDir, true);
        verify(pluginUpdatedEvent, times(1)).fire(any());

    }

    @Test
    public void shutDownShouldRemoveScripts() throws Exception {
        processor.shutDown();
        verify(scriptRegistry, times(1)).removeScripts("UF");
    }

    @Test
    public void removeAllShouldClearPluginsAndScriptRegistry() throws Exception {
        processor.availablePlugins.add("test");
        assertFalse(processor.availablePlugins.isEmpty());

        processor.removeAll();
        verify(scriptRegistry, times(1)).removeScripts("UF");

        assertTrue(processor.availablePlugins.isEmpty());
    }

    @Test
    public void shouldProcessTest() throws Exception {

        assertFalse(processor.shouldProcess("pluginname.html"));
        assertTrue(processor.shouldProcess("pluginname.nocache.js"));

    }

}