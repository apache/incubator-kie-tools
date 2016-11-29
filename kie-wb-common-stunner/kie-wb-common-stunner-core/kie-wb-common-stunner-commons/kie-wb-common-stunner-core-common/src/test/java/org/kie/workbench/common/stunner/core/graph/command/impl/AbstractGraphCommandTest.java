/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.rule.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRule;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.graph.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractGraphCommandTest {

    private static final String GRAPH_UUID = "graphUUID";
    protected final RuleViolations EMPTY_VIOLATIONS = new DefaultRuleViolations();

    @Mock protected GraphCommandExecutionContext graphCommandExecutionContext;
    @Mock protected DefinitionManager definitionManager;
    @Mock protected AdapterManager adapterManager;
    @Mock protected AdapterRegistry adapterRegistry;
    @Mock protected DefinitionAdapter definitionAdapter;
    @Mock protected PropertyAdapter propertyAdapter;
    @Mock protected FactoryManager factoryManager;
    @Mock protected GraphRulesManager graphRulesManager;
    @Mock protected GraphContainmentRuleManager containmentRuleManager;
    @Mock protected GraphConnectionRuleManager connectionRuleManager;
    @Mock protected GraphCardinalityRuleManager cardinalityRuleManager;
    @Mock protected GraphEdgeCardinalityRuleManager edgeCardinalityRuleManager;
    @Mock protected GraphDockingRuleManager dockingRuleManager;
    @Mock protected MutableIndex graphIndex;
    @Mock protected Graph<DefinitionSet, Node> graph;
    protected Collection<Node> graphNodes = new LinkedList<>();
    @Mock protected DefinitionSet graphContent;

    @SuppressWarnings( "unchecked" )
    public void init( final double width,
                      final double height ) {
        MockitoAnnotations.initMocks( this );
        Bounds bounds = mockBounds( 0, 0, width, height );
        when( graphContent.getBounds() ).thenReturn( bounds );
        when( graph.getUUID() ).thenReturn( GRAPH_UUID );
        when( graph.getContent() ).thenReturn( graphContent );
        when( graph.nodes() ).thenReturn( graphNodes );
        when( definitionManager.adapters() ).thenReturn( adapterManager );
        when( adapterManager.registry() ).thenReturn( adapterRegistry );
        when( adapterManager.forDefinition() ).thenReturn( definitionAdapter );
        when( adapterManager.forProperty() ).thenReturn( propertyAdapter );
        when( adapterRegistry.getDefinitionAdapter( any( Class.class ) ) ).thenReturn( definitionAdapter );
        when( adapterRegistry.getPropertyAdapter( any( Class.class ) ) ).thenReturn( propertyAdapter );
        when( graphCommandExecutionContext.getDefinitionManager() ).thenReturn( definitionManager );
        when( graphCommandExecutionContext.getFactoryManager() ).thenReturn( factoryManager );
        when( graphCommandExecutionContext.getRulesManager() ).thenReturn( graphRulesManager );
        when( graphCommandExecutionContext.getGraphIndex() ).thenReturn( graphIndex );
        when( graphIndex.getGraph() ).thenReturn( graph );
        when( graphRulesManager.containment() ).thenReturn( containmentRuleManager );
        when( graphRulesManager.connection() ).thenReturn( connectionRuleManager );
        when( graphRulesManager.cardinality() ).thenReturn( cardinalityRuleManager );
        when( graphRulesManager.edgeCardinality() ).thenReturn( edgeCardinalityRuleManager );
        when( graphRulesManager.docking() ).thenReturn( dockingRuleManager );
        when( containmentRuleManager.evaluate( any( Element.class ), any( Element.class ) ) ).thenReturn( EMPTY_VIOLATIONS );
        when( connectionRuleManager.evaluate( any( Edge.class ), any( Node.class ), any( Node.class ) ) ).thenReturn( EMPTY_VIOLATIONS );
        when( cardinalityRuleManager.evaluate( any( Graph.class ), any( Node.class ), any( RuleManager.Operation.class ) ) ).thenReturn( EMPTY_VIOLATIONS );
        when( edgeCardinalityRuleManager.evaluate( any( Edge.class ), any( Node.class ),
                any( List.class ), any( EdgeCardinalityRule.Type.class ), any( RuleManager.Operation.class ) ) ).thenReturn( EMPTY_VIOLATIONS );
        when( dockingRuleManager.evaluate( any( Element.class ), any( Element.class ) ) ).thenReturn( EMPTY_VIOLATIONS );
    }

    public static Node mockNode( String uuid ) {
        Node node = mock( Node.class );
        when( node.getUUID() ).thenReturn( uuid );
        when( node.getInEdges() ).thenReturn( new LinkedList() );
        when( node.getOutEdges() ).thenReturn( new LinkedList() );
        return node;
    }

    public static Edge mockEdge( String uuid ) {
        Edge edge = mock( Edge.class );
        when( edge.getUUID() ).thenReturn( uuid );
        return edge;
    }

    public static View mockView( final double x,
                                 final double y,
                                 final double w,
                                 final double h ) {
        View view = mock( View.class );
        Bounds bounds = mockBounds( x, y, w, h );
        when( view.getBounds() ).thenReturn( bounds );
        return view;
    }

    public static Bounds mockBounds( final double x,
                                 final double y,
                                 final double w,
                                 final double h ) {
        Bounds bounds = mock( Bounds.class );
        Bounds.Bound boundUL = mock( Bounds.Bound.class );
        Bounds.Bound boundLR = mock( Bounds.Bound.class );
        when( boundUL.getX() ).thenReturn( x );
        when( boundUL.getY() ).thenReturn( y );
        when( boundLR.getX() ).thenReturn( x + w );
        when( boundLR.getY() ).thenReturn( y + h );
        when( bounds.getLowerRight() ).thenReturn( boundLR );
        when( bounds.getUpperLeft() ).thenReturn( boundUL );
        return bounds;
    }

}
