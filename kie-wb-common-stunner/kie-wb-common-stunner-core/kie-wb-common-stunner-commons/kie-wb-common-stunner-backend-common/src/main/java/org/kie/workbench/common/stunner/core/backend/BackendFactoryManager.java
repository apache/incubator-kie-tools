/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.backend;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.core.api.AbstractFactoryManager;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.factory.definition.DefinitionFactory;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.service.FactoryService;

@ApplicationScoped
@Service
public class BackendFactoryManager extends AbstractFactoryManager implements FactoryService,
                                                                             FactoryManager {

    private Instance<DefinitionFactory<?>> definitionFactoryInstances;
    private Instance<GraphFactory> graphFactoryInstances;
    private Instance<NodeFactory<?>> nodeFactoryInstances;
    private Instance<EdgeFactory<?>> edgeFactoryInstances;
    private Instance<DiagramFactory<?, ?>> diagramFactoryInstances;

    protected BackendFactoryManager() {
        this(null,
             null,
             null,
             null,
             null,
             null,
             null);
    }

    protected BackendFactoryManager(final DefinitionManager definitionManager) {
        super(definitionManager);
    }

    protected BackendFactoryManager(final DefinitionManager definitionManager,
                                    final RegistryFactory registryFactory) {
        super(registryFactory, definitionManager);
    }

    @Inject
    public BackendFactoryManager(final RegistryFactory registryFactory,
                                 final DefinitionManager definitionManager,
                                 final Instance<DefinitionFactory<?>> definitionFactoryInstances,
                                 final Instance<GraphFactory> graphFactoryInstances,
                                 final Instance<NodeFactory<?>> nodeFactoryInstances,
                                 final Instance<EdgeFactory<?>> edgeFactoryInstances,
                                 final Instance<DiagramFactory<?, ?>> diagramFactoryInstances) {
        super(registryFactory,
              definitionManager);
        this.definitionFactoryInstances = definitionFactoryInstances;
        this.graphFactoryInstances = graphFactoryInstances;
        this.nodeFactoryInstances = nodeFactoryInstances;
        this.edgeFactoryInstances = edgeFactoryInstances;
        this.diagramFactoryInstances = diagramFactoryInstances;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        // Definition factories.
        definitionFactoryInstances.forEach(registry()::register);
        // Graph factories.
        graphFactoryInstances.forEach(registry()::register);
        nodeFactoryInstances.forEach(registry()::register);
        edgeFactoryInstances.forEach(registry()::register);
        // Diagram factories.
        diagramFactoryInstances.forEach(registry()::register);
    }
}
