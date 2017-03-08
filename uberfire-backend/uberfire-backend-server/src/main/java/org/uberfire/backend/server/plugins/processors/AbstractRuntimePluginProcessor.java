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
package org.uberfire.backend.server.plugins.processors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.event.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.plugin.RuntimePlugin;
import org.uberfire.backend.plugin.RuntimePluginProcessor;
import org.uberfire.workbench.events.PluginAddedEvent;
import org.uberfire.workbench.events.PluginUpdatedEvent;

public abstract class AbstractRuntimePluginProcessor implements RuntimePluginProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRuntimePluginProcessor.class);

    final Map<String, RuntimePlugin> availableRuntimePlugins = new HashMap<>();

    private Event<PluginAddedEvent> pluginAddedEvent;

    private Event<PluginUpdatedEvent> pluginUpdatedEvent;

    public AbstractRuntimePluginProcessor() {

    }

    public AbstractRuntimePluginProcessor(Event<PluginAddedEvent> pluginAddedEvent,
                                          Event<PluginUpdatedEvent> pluginUpdatedEvent) {
        this.pluginAddedEvent = pluginAddedEvent;
        this.pluginUpdatedEvent = pluginUpdatedEvent;
    }

    @Override
    public Collection<RuntimePlugin> getAvailableRuntimePlugins() {
        return availableRuntimePlugins.values();
    }

    @Override
    public void removeAll() {
        availableRuntimePlugins.clear();
    }

    @Override
    public boolean isRegistered(String pluginName) {
        return availableRuntimePlugins.containsKey(pluginName);
    }

    @Override
    public boolean shouldProcess(String pluginName) {
        return pluginName.endsWith(getType().getExtension());
    }

    @Override
    public void process(String pluginName,
                        String pluginDeploymentDir,
                        boolean notifyClients) {

        if (!isRegistered(pluginName)) {
            loadPlugin(pluginName,
                       pluginDeploymentDir);

            if (notifyClients) {
                pluginAddedEvent.fire(new PluginAddedEvent(pluginName));
            }
        } else {
            if (notifyClients) {
                pluginUpdatedEvent.fire(new PluginUpdatedEvent(pluginName));
            }
        }
    }

    private void loadPlugin(String pluginName,
                            String pluginDeploymentDir) {
        try {
            String pluginContent = getPluginContent(pluginName,
                                                    pluginDeploymentDir);
            availableRuntimePlugins.put(pluginName,
                                        new RuntimePlugin(getType(),
                                                          pluginName,
                                                          pluginContent));
        } catch (IOException e) {
            LOG.error("Failed to initialize " + pluginDeploymentDir,
                      e);
            throw new RuntimeException(e);
        }
    }

    String getPluginContent(String pluginName,
                            String pluginDeploymentDir) throws IOException {
        Path path = Paths.get(pluginDeploymentDir + File.separator + pluginName);
        return new String(Files.readAllBytes(path));
    }

    abstract PluginProcessorType getType();
}
