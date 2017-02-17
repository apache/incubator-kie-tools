package org.uberfire.backend.server.plugins.processors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.plugins.engine.AbstractPluginsTest;
import org.uberfire.workbench.events.PluginAddedEvent;
import org.uberfire.workbench.events.PluginUpdatedEvent;

import javax.enterprise.event.Event;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HTMLPluginProcessorTest extends AbstractPluginsTest {

    @Mock
    private Event<PluginAddedEvent> pluginAddedEvent;

    @Mock
    private Event<PluginUpdatedEvent> pluginUpdatedEvent;

    private HTMLPluginProcessor processor;

    public void setup() {
        processor = new HTMLPluginProcessor(pluginAddedEvent, pluginUpdatedEvent){
            @Override
            String getPluginContent(String pluginName, String pluginDeploymentDir) throws IOException {
                return "mock";
            }
        };
    }

    @Test
    public void processTest() {

        assertFalse(processor.isRegistered("dora.html"));
        processor.process("dora.html", pluginDeploymentDir, true);

        assertTrue(processor.isRegistered("dora.html"));
        verify(pluginAddedEvent, times(1)).fire(any());

        processor.process("dora.html", pluginDeploymentDir, true);
        verify(pluginUpdatedEvent, times(1)).fire(any());

        assertTrue(processor.lookupForTemplate("dora.html").isPresent());

    }

    @Test
    public void shouldProcessTest() throws Exception {

        assertFalse(processor.shouldProcess("pluginname.nocache.js"));
        assertTrue(processor.shouldProcess("pluginname.html"));

    }


}