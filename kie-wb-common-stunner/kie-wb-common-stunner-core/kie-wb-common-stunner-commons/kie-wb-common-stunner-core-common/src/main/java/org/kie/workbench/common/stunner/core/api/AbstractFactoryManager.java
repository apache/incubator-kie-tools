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

package org.kie.workbench.common.stunner.core.api;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.factory.definition.DefinitionFactory;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;
import org.kie.workbench.common.stunner.core.util.UUID;

import java.util.Iterator;

public abstract class AbstractFactoryManager {

    private final FactoryRegistry factoryRegistry;
    private final DefinitionManager definitionManager;
    private final DiagramFactory diagramFactory;

    protected AbstractFactoryManager() {
        this.factoryRegistry = null;
        this.definitionManager = null;
        this.diagramFactory = null;
    }

    public AbstractFactoryManager( final RegistryFactory registryFactory,
                                   final DefinitionManager definitionManager,
                                   final DiagramFactory diagramFactory ) {
        this.factoryRegistry = registryFactory.newFactoryRegistry();
        this.definitionManager = definitionManager;
        this.diagramFactory = diagramFactory;
    }

    @SuppressWarnings( "unchecked" )
    public <T> T newDefinition( final String id ) {
        final DefinitionFactory<T> factory = factoryRegistry.getDefinitionFactory( id );
        return factory.build( id );
    }

    public <T> T newDefinition( final Class<T> type ) {
        final String id = BindableAdapterUtils.getDefinitionId( type, definitionManager.adapters().registry() );
        return newDefinition( id );
    }

    public Element newElement( final String uuid,
                               final String id ) {
        return doBuild( uuid, id );
    }

    public Element newElement( final String uuid,
                               final Class<?> type ) {
        final String id = BindableAdapterUtils.getGenericClassName( type );
        return newElement( uuid, id );
    }

    private Object getDefinitionSet( final String id ) {
        return definitionManager.definitionSets().getDefinitionSetById( id );
    }

    @SuppressWarnings( "unchecked" )
    public <D extends Diagram> D newDiagram( final String name,
                                             final String id ) {
        final Graph graph = ( Graph ) newElement( UUID.uuid(), id );
        final Metadata metadata = buildMetadata( id, name );
        final String rootId = getCanvasRoot( graph );
        if ( null != rootId ) {
            metadata.setCanvasRootUUID( rootId );

        }
        return ( D ) diagramFactory.build( name, metadata, graph );
    }

    public <D extends Diagram> D newDiagram( final String uuid,
                                             final Class<?> type ) {
        final String id = BindableAdapterUtils.getDefinitionSetId( type, definitionManager.adapters().registry() );
        return newDiagram( uuid, id );
    }

    public FactoryRegistry registry() {
        return factoryRegistry;
    }

    protected Metadata buildMetadata( final String defSetId, final String title ) {
        return new MetadataImpl.MetadataImplBuilder( defSetId, definitionManager )
                .setTitle( title )
                .build();
    }

    protected DefinitionManager getDefinitionManager() {
        return definitionManager;
    }

    @SuppressWarnings( "unchecked" )
    private <T, C extends Definition<T>> Element<C> doBuild( final String uuid,
                                                             final String definitionId ) {
        final Object defSet = getDefinitionSet( definitionId );
        final boolean isDefSet = null != defSet;
        final Object definition = isDefSet ? defSet : newDefinition( definitionId );
        final Class<? extends ElementFactory> factoryType = isDefSet ?
                definitionManager.adapters().forDefinitionSet().getGraphFactoryType( definition ) :
                definitionManager.adapters().forDefinition().getGraphFactoryType( definition );
        final ElementFactory<Definition<Object>, Element<Definition<Object>>> factory = getGraphFactory( factoryType );
        final Element<Definition<Object>> element = factory.build( uuid, definition );
        return ( Element<C> ) element;
    }

    @SuppressWarnings( "unchecked" )
    private ElementFactory<Definition<Object>, Element<Definition<Object>>> getGraphFactory( final Class<? extends ElementFactory> type ) {
        return factoryRegistry.getGraphFactory( type );

    }

    // TODO: Refactor this - do not apply by default this behavior?
    private String getCanvasRoot( final Graph graph ) {
        final Node view = getFirstGraphViewNode( graph );
        if ( null != view ) {
            return view.getUUID();

        }
        return null;
    }

    @SuppressWarnings( "unchecked" )
    private Node getFirstGraphViewNode( final Graph graph ) {
        if ( null != graph ) {
            Iterable<Node> nodesIterable = graph.nodes();
            if ( null != nodesIterable ) {
                Iterator<Node> nodesIt = nodesIterable.iterator();
                if ( null != nodesIt ) {
                    while ( nodesIt.hasNext() ) {
                        Node node = nodesIt.next();
                        Object content = node.getContent();
                        if ( content instanceof View ) {
                            return node;
                        }

                    }
                }
            }

        }
        return null;
    }

}