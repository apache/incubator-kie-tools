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


package org.kie.workbench.common.stunner.core.client.api;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.api.AbstractFactoryManager;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.factory.definition.DefinitionFactory;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;

@ApplicationScoped
public class ClientFactoryManager extends AbstractFactoryManager implements FactoryManager {

    private final ManagedInstance<DefinitionFactory> definitionFactoryInstances;
    private final ManagedInstance<DiagramFactory> diagramFactoryInstances;
    private final ManagedInstance<GraphFactory> graphFactoryInstances;
    private final ManagedInstance<NodeFactory> nodeFactoryInstances;
    private final ManagedInstance<EdgeFactory> edgeFactoryInstances;

    protected ClientFactoryManager() {
        this(null,
             null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public ClientFactoryManager(final RegistryFactory registryFactory,
                                final DefinitionManager definitionManager,
                                final ManagedInstance<DefinitionFactory> definitionFactoryInstances,
                                final ManagedInstance<DiagramFactory> diagramFactoryInstances,
                                final ManagedInstance<GraphFactory> graphFactoryInstances,
                                final ManagedInstance<NodeFactory> nodeFactoryInstances,
                                final ManagedInstance<EdgeFactory> edgeFactoryInstances) {
        super(registryFactory,
              definitionManager);
        this.definitionFactoryInstances = definitionFactoryInstances;
        this.diagramFactoryInstances = diagramFactoryInstances;
        this.graphFactoryInstances = graphFactoryInstances;
        this.nodeFactoryInstances = nodeFactoryInstances;
        this.edgeFactoryInstances = edgeFactoryInstances;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        // Client definition factories.
        definitionFactoryInstances.forEach(factory -> registry().register(factory));
        // Client diagram factories..
        diagramFactoryInstances.forEach(factory -> registry().register(factory));
        // Graph factories.
        graphFactoryInstances.forEach(factory -> registry().register(factory));
        // Node factories.
        nodeFactoryInstances.forEach(factory -> registry().register(factory));
        // Edge factories.
        edgeFactoryInstances.forEach(factory -> registry().register(factory));
    }
}
