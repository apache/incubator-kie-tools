/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.appformer.client.stateControl.registry.Registry;
import org.appformer.client.stateControl.registry.RegistryChangeListener;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNGraphsProvider;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;

@ApplicationScoped
public class RegistryProvider {

    private final ManagedInstance<CommandRegistryHolder> registryHolders;
    private final DMNGraphsProvider graphsProvider;
    private final Map<String, Registry<Command<AbstractCanvasHandler, CanvasViolation>>> registryMap;
    private RegistryChangeListener registryChangeListener;

    @Inject
    public RegistryProvider(final ManagedInstance<CommandRegistryHolder> registryHolders,
                            final DMNGraphsProvider graphsProvider) {
        this.registryHolders = registryHolders;
        this.graphsProvider = graphsProvider;
        this.registryMap = new HashMap<>();
    }

    public Registry<Command<AbstractCanvasHandler, CanvasViolation>> getCurrentCommandRegistry() {
        if (!getRegistryMap().containsKey(graphsProvider.getCurrentDiagramId())) {
            initializeRegistry(graphsProvider.getCurrentDiagramId());
        }

        return getRegistryMap().get(graphsProvider.getCurrentDiagramId());
    }

    public void setRegistryChangeListener(final RegistryChangeListener registryChangeListener) {
        this.registryChangeListener = registryChangeListener;
    }

    Map<String, Registry<Command<AbstractCanvasHandler, CanvasViolation>>> getRegistryMap() {
        return registryMap;
    }

    Registry<Command<AbstractCanvasHandler, CanvasViolation>> createRegistry() {
        final Registry<Command<AbstractCanvasHandler, CanvasViolation>> commandRegistry = registryHolders.get().getRegistry();
        if (!Objects.isNull(registryChangeListener)) {
            commandRegistry.setRegistryChangeListener(registryChangeListener);
        }
        return commandRegistry;
    }

    void initializeRegistry(final String diagramId) {
        getRegistryMap().putIfAbsent(diagramId, createRegistry());
    }
}
