/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.backend;

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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
@Service
public class ApplicationFactoryManager extends AbstractFactoryManager implements FactoryService, FactoryManager {

    private Instance<DefinitionFactory<?>> definitionFactoryInstances;
    private Instance<GraphFactory<?>> graphFactoryInstances;
    private Instance<NodeFactory<?>> nodeFactoryInstances;
    private Instance<EdgeFactory<?>> edgeFactoryInstances;

    protected ApplicationFactoryManager() {
        super();
    }

    @Inject
    public ApplicationFactoryManager( final RegistryFactory registryFactory,
                                      final DefinitionManager definitionManager,
                                      final Instance<DefinitionFactory<?>> definitionFactoryInstances,
                                      final Instance<GraphFactory<?>> graphFactoryInstances,
                                      final Instance<NodeFactory<?>> nodeFactoryInstances,
                                      final Instance<EdgeFactory<?>> edgeFactoryInstances,
                                      final DiagramFactory diagramFactory ) {
        super( registryFactory, definitionManager, diagramFactory );
        this.definitionFactoryInstances = definitionFactoryInstances;
        this.graphFactoryInstances = graphFactoryInstances;
        this.nodeFactoryInstances = nodeFactoryInstances;
        this.edgeFactoryInstances = edgeFactoryInstances;
    }

    @PostConstruct
    public void init() {
        initDefinitionFactories();
        initGraphFactories();
    }

    @SuppressWarnings( "unchecked" )
    private void initDefinitionFactories() {
        for ( DefinitionFactory<?> factory : definitionFactoryInstances ) {
            registry().register( factory );
        }
    }

    @SuppressWarnings( "unchecked" )
    private void initGraphFactories() {
        for ( GraphFactory<?> factory : graphFactoryInstances ) {
            registry().register( factory );
        }
        for ( NodeFactory<?> factory : nodeFactoryInstances ) {
            registry().register( factory );
        }
        for ( EdgeFactory<?> factory : edgeFactoryInstances ) {
            registry().register( factory );
        }
    }

}
