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

package org.kie.workbench.common.stunner.core.client.registry.impl;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.appformer.client.stateControl.registry.Registry;
import org.appformer.client.stateControl.registry.RegistryChangeListener;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@ApplicationScoped
public class RedoCommandRegistryProvider {

    private final ManagedInstance<GraphsProvider> graphsProviderInstances;
    private final SessionManager sessionManager;
    private final DefinitionUtils definitionUtils;
    private final ManagedInstance<CommandRegistryHolder> registryHolders;
    private final Map<String, Registry<Command<AbstractCanvasHandler, CanvasViolation>>> registryMap;
    private GraphsProvider graphsProvider;
    private RegistryChangeListener registryChangeListener;

    protected RedoCommandRegistryProvider() {
        this(null, null, null, null);
        // CDI proxy
    }

    @Inject
    public RedoCommandRegistryProvider(final @Any ManagedInstance<GraphsProvider> graphsProviderInstances,
                                       final @Any ManagedInstance<CommandRegistryHolder> registryHolders,
                                       final SessionManager sessionManager,
                                       final DefinitionUtils definitionUtils) {
        this.graphsProviderInstances = graphsProviderInstances;
        this.sessionManager = sessionManager;
        this.definitionUtils = definitionUtils;
        this.registryHolders = registryHolders;
        this.registryMap = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        final Diagram diagram = sessionManager.getCurrentSession()
                .getCanvasHandler()
                .getDiagram();
        final Annotation qualifier = definitionUtils.getQualifier(diagram.getMetadata().getDefinitionSetId());
        graphsProvider = InstanceUtils.lookup(graphsProviderInstances,
                                              GraphsProvider.class,
                                              qualifier);
    }

    @PreDestroy
    public void destroy() {
        graphsProviderInstances.destroyAll();
        registryHolders.destroyAll();
    }

    public GraphsProvider getGraphsProvider() {
        return graphsProvider;
    }

    public Registry<Command<AbstractCanvasHandler, CanvasViolation>> getCurrentCommandRegistry() {
        if (!getRegistryMap().containsKey(getGraphsProvider().getCurrentDiagramId())) {
            initializeRegistry(getGraphsProvider().getCurrentDiagramId());
        }

        return getRegistryMap().get(getGraphsProvider().getCurrentDiagramId());
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
