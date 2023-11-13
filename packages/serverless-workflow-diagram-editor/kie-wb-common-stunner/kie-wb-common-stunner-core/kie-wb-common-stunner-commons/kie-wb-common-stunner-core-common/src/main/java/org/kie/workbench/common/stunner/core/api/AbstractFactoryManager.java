/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.core.api;

import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.definition.DefinitionFactory;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.factory.impl.DiagramFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;
import org.kie.workbench.common.stunner.core.util.UUID;

public abstract class AbstractFactoryManager {

    private final FactoryRegistry factoryRegistry;
    private final DefinitionManager definitionManager;

    public AbstractFactoryManager(final RegistryFactory registryFactory,
                                  final DefinitionManager definitionManager) {
        this.factoryRegistry = null != registryFactory ? registryFactory.newFactoryRegistry() : null;
        this.definitionManager = definitionManager;
    }

    protected AbstractFactoryManager(final DefinitionManager definitionManager) {
        this.factoryRegistry = null;
        this.definitionManager = definitionManager;
    }

    @SuppressWarnings("unchecked")
    public <T> T newDefinition(final String id) {
        final DefinitionFactory<T> factory = factoryRegistry.getDefinitionFactory(id);
        return factory.build(id);
    }

    public Element<?> newElement(final String uuid,
                                 final String id) {
        return newElement(uuid, id, null);
    }

    private Element<?> newElement(final String uuid,
                                  final String id,
                                  final Metadata metadata) {
        final Object defSet = getDefinitionSet(id);
        final boolean isDefSet = null != defSet;
        return !isDefSet ? doBuildElement(uuid, id) : doBuildGraph(uuid, id, defSet, metadata);
    }

    private Object getDefinitionSet(final String id) {
        return definitionManager.definitionSets().getDefinitionSetById(id);
    }

    @SuppressWarnings("unchecked")
    public <M extends Metadata, D extends Diagram> D newDiagram(final String name,
                                                                final String id,
                                                                final M metadata) {
        final Graph<DefinitionSet, ?> graph = (Graph<DefinitionSet, ?>) newElement(UUID.uuid(), id, metadata);
        return (D) checkDiagramFactoryNotNull(graph.getContent().getDefinition(),
                                              metadata).build(name,
                                                              metadata,
                                                              graph);
    }

    @SuppressWarnings("unchecked")
    private <M extends Metadata> DiagramFactory<M, ?> checkDiagramFactoryNotNull(final String defSetid,
                                                                                 final M metadata) {
        final DiagramFactory<M, ?> factory = registry().getDiagramFactory(defSetid,
                                                                          metadata.getMetadataType());
        if (null == factory) {
            return (DiagramFactory<M, ?>) new DiagramFactoryImpl();
        }
        return factory;
    }

    public FactoryRegistry registry() {
        return factoryRegistry;
    }

    protected DefinitionManager getDefinitionManager() {
        return definitionManager;
    }

    @SuppressWarnings("unchecked")
    private <T, C extends Definition<T>> Element<C> doBuildElement(final String uuid,
                                                                   final String definitionId) {
        final T definition = newDefinition(definitionId);
        final Class<? extends ElementFactory> factoryType = definitionManager.adapters().forDefinition().getGraphFactoryType(definition);
        final ElementFactory<T, C, Element<C>> factory = factoryRegistry.getElementFactory(factoryType);
        return factory.build(uuid,
                             definition);
    }

    @SuppressWarnings("unchecked")
    private <C extends DefinitionSet> Element<C> doBuildGraph(final String uuid,
                                                              final String defSetId,
                                                              final Object defSet,
                                                              final Metadata metadata) {
        final Class<? extends ElementFactory> factoryType = definitionManager.adapters().forDefinitionSet().getGraphFactoryType(defSet);
        final ElementFactory<String, DefinitionSet, Element<DefinitionSet>> _factory = factoryRegistry.getElementFactory(factoryType);
        final ElementFactory<String, DefinitionSet, Element<DefinitionSet>> factory = null != _factory ? _factory :
                factoryRegistry.getElementFactory(GraphFactory.class);
        return (Element<C>) factory.build(uuid, defSetId, metadata);
    }
}
