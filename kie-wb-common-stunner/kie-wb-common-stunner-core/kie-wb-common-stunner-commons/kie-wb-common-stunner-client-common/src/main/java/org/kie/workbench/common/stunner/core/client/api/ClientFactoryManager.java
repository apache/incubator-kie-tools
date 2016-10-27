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

package org.kie.workbench.common.stunner.core.client.api;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.stunner.core.api.AbstractFactoryManager;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.factory.definition.DefinitionFactory;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;

@ApplicationScoped
public class ClientFactoryManager extends AbstractFactoryManager implements FactoryManager {

    private final SyncBeanManager beanManager;
    private final ShapeManager shapeManager;

    protected ClientFactoryManager() {
        super();
        this.beanManager = null;
        this.shapeManager = null;
    }

    @Inject
    public ClientFactoryManager( final RegistryFactory registryFactory,
                                 final DefinitionManager definitionManager,
                                 final SyncBeanManager beanManager,
                                 final ShapeManager shapeManager,
                                 final DiagramFactory diagramFactory ) {
        super( registryFactory, definitionManager, diagramFactory );
        this.beanManager = beanManager;
        this.shapeManager = shapeManager;

    }

    @PostConstruct
    @SuppressWarnings( "unchecked" )
    public void init() {
        // Client definition factories..
        Collection<SyncBeanDef<DefinitionFactory>> beanDefSetAdapters = beanManager.lookupBeans( DefinitionFactory.class );
        for ( SyncBeanDef<DefinitionFactory> defSet : beanDefSetAdapters ) {
            DefinitionFactory factory = defSet.getInstance();
            registry().register( factory );
        }
        // Graph factories.
        Collection<SyncBeanDef<GraphFactory>> fAdapters = beanManager.lookupBeans( GraphFactory.class );
        for ( SyncBeanDef<GraphFactory> defSet : fAdapters ) {
            GraphFactory factory = defSet.getInstance();
            registry().register( factory );
        }
        // Node factories.
        Collection<SyncBeanDef<NodeFactory>> nAdapters = beanManager.lookupBeans( NodeFactory.class );
        for ( SyncBeanDef<NodeFactory> defSet : nAdapters ) {
            NodeFactory factory = defSet.getInstance();
            registry().register( factory );
        }
        // Edge factories.
        Collection<SyncBeanDef<EdgeFactory>> eAdapters = beanManager.lookupBeans( EdgeFactory.class );
        for ( SyncBeanDef<EdgeFactory> defSet : eAdapters ) {
            EdgeFactory factory = defSet.getInstance();
            registry().register( factory );
        }

    }

    @Override
    protected Metadata buildMetadata( final String defSetId, final String title ) {
        return new MetadataImpl.MetadataImplBuilder( defSetId, getDefinitionManager(), shapeManager )
                .setTitle( title )
                .build();
    }

}