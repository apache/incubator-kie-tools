/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.factory;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommandImpl;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.impl.AbstractElementFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.EmptyRulesCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSetImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.core.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * The custom factory for BPMN graphs.
 * It initializes the BPMN graph with a new Diagram node instance, which represents the main process.
 * This class uses the Commands API in order to avoid adding nodes/edges and setting bean values manually on the graph structure,
 * so this will avoid further errors, but in fact there should be not need to check runtime rules when executing
 * these commands.
 */
@ApplicationScoped
public class BPMNGraphFactoryImpl
        extends AbstractElementFactory<String, DefinitionSet, Graph<DefinitionSet, Node>>
        implements BPMNGraphFactory {

    private final DefinitionManager definitionManager;
    private final GraphCommandManager graphCommandManager;
    private final GraphCommandFactory graphCommandFactory;
    private final FactoryManager factoryManager;
    private final GraphIndexBuilder<?> indexBuilder;

    protected BPMNGraphFactoryImpl() {
        this( null, null, null, null, null );
    }

    @Inject
    public BPMNGraphFactoryImpl( final DefinitionManager definitionManager,
                                 final FactoryManager factoryManager,
                                 final GraphCommandManager graphCommandManager,
                                 final GraphCommandFactory graphCommandFactory,
                                 final GraphIndexBuilder<?> indexBuilder ) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.graphCommandManager = graphCommandManager;
        this.graphCommandFactory = graphCommandFactory;
        this.indexBuilder = indexBuilder;
    }

    @Override
    public Class<? extends ElementFactory> getFactoryType() {
        return BPMNGraphFactory.class;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Graph<DefinitionSet, Node> build( final String uuid,
                                             final String definitionSetId ) {
        final GraphImpl graph = new GraphImpl<>( uuid, new GraphNodeStoreImpl() );
        final DefinitionSet content = new DefinitionSetImpl( definitionSetId );
        graph.setContent( content );
        if ( null == content.getBounds() ) {
            content.setBounds( new BoundsImpl(
                    new BoundImpl( 0d, 0d ),
                    new BoundImpl( BPMNGraphFactory.GRAPH_DEFAULT_WIDTH, BPMNGraphFactory.GRAPH_DEFAULT_HEIGHT )
            ) );
        }
        // Add a BPMN diagram and a start event nodes by default.
        Node<Definition<BPMNDiagram>, Edge> diagramNode = ( Node<Definition<BPMNDiagram>, Edge> ) factoryManager.newElement( UUID.uuid(), BPMNDiagram.class );
        Node<Definition<StartNoneEvent>, Edge> startEventNode = ( Node<Definition<StartNoneEvent>, Edge> ) factoryManager.newElement( UUID.uuid(), StartNoneEvent.class );
        graphCommandManager.execute( createGraphContext( graph ),
                new CompositeCommandImpl.CompositeCommandBuilder()
                        .addCommand( graphCommandFactory.addNode( diagramNode ) )
                        .addCommand( graphCommandFactory.addChildNode( diagramNode, startEventNode, 100d, 100d ) )
                        .build()
        );

        return graph;
    }

    @SuppressWarnings( "unchecked" )
    private GraphCommandExecutionContext createGraphContext( final GraphImpl graph ) {
        Index<?, ?> index = indexBuilder.build( graph );
        return new EmptyRulesCommandExecutionContext(
                definitionManager,
                factoryManager, index );
    }
}
