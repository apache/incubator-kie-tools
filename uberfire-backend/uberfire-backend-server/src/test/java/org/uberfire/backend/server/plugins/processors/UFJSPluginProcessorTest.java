package org.uberfire.backend.server.plugins.processors;

import java.io.IOException;
import javax.enterprise.event.Event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.plugins.engine.AbstractPluginsTest;
import org.uberfire.workbench.events.PluginAddedEvent;
import org.uberfire.workbench.events.PluginUpdatedEvent;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class UFJSPluginProcessorTest extends AbstractPluginsTest {

    @Mock
    private Event<PluginAddedEvent> pluginAddedEvent;

    @Mock
    private Event<PluginUpdatedEvent> pluginUpdatedEvent;

    private UFJSPluginProcessor processor;

    public void setup() {
        processor = new UFJSPluginProcessor(pluginAddedEvent,
                                            pluginUpdatedEvent) {
            @Override
            String getPluginContent(String pluginName,
                                    String pluginDeploymentDir) throws IOException {
                return "mock";
            }
        };
    }

    @Test
    public void shouldProcessTest() throws Exception {

        assertTrue(processor.shouldProcess("pluginname.js"));
        assertFalse(processor.shouldProcess("pluginname.nocache.js"));
        assertFalse(processor.shouldProcess("pluginname.html"));
    }
}