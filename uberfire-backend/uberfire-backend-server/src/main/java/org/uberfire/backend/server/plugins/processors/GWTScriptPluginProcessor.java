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
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.cdi.server.scripts.ScriptRegistry;
import org.uberfire.backend.plugin.PluginProcessor;
import org.uberfire.workbench.events.PluginAddedEvent;
import org.uberfire.workbench.events.PluginUpdatedEvent;

import static org.apache.commons.lang3.StringUtils.substringAfterLast;

@ApplicationScoped
public class GWTScriptPluginProcessor implements PluginProcessor {

    static final String SCRIPT_REGISTRY_KEY = "UF";
    final Set<String> availablePlugins = new HashSet<>();
    private ScriptRegistry scriptRegistry;
    private Event<PluginAddedEvent> pluginAddedEvent;
    private Event<PluginUpdatedEvent> pluginUpdatedEvent;

    public GWTScriptPluginProcessor() {
    }

    @Inject
    public GWTScriptPluginProcessor(final ScriptRegistry scriptRegistry,
                                    final Event<PluginAddedEvent> pluginAddedEvent,
                                    final Event<PluginUpdatedEvent> pluginUpdatedEvent) {
        this.scriptRegistry = scriptRegistry;
        this.pluginAddedEvent = pluginAddedEvent;
        this.pluginUpdatedEvent = pluginUpdatedEvent;
    }

    @PreDestroy
    void shutDown() {
        scriptRegistry.removeScripts(SCRIPT_REGISTRY_KEY);
    }

    private void add(String pluginName,
                     String scriptUrl) {
        availablePlugins.add(pluginName);
        scriptRegistry.addScript(SCRIPT_REGISTRY_KEY,
                                 scriptUrl);
    }

    @Override
    public void removeAll() {
        availablePlugins.clear();
        scriptRegistry.removeScripts(SCRIPT_REGISTRY_KEY);
    }

    @Override
    public boolean isRegistered(String pluginName) {
        return availablePlugins.contains(pluginName);
    }

    @Override
    public boolean shouldProcess(String pluginName) {
        return pluginName.endsWith(PluginProcessorType.GWT.getExtension());
    }

    @Override
    public void process(String pluginName,
                        String pluginDeploymentDir,
                        boolean notifyClients) {

        final String pluginDisplayName = pluginName.replace(PluginProcessorType.GWT.getExtension(),
                                                            "");
        if (!isRegistered(pluginName)) {
            final String url = resolveScriptUrl(pluginName,
                                                pluginDeploymentDir);
            add(pluginName,
                url);

            if (notifyClients) {
                pluginAddedEvent.fire(new PluginAddedEvent(pluginDisplayName));
            }
        } else {
            if (notifyClients) {
                pluginUpdatedEvent.fire(new PluginUpdatedEvent(pluginDisplayName));
            }
        }
    }

    String resolveScriptUrl(String pluginName,
                            String pluginDeploymentDir) {
        String pluginsDeploymentUrlPath = substringAfterLast(pluginDeploymentDir,
                                                             File.separator);
        return pluginsDeploymentUrlPath + "/" + pluginName + "?nocache=" + System.currentTimeMillis();
    }
}
