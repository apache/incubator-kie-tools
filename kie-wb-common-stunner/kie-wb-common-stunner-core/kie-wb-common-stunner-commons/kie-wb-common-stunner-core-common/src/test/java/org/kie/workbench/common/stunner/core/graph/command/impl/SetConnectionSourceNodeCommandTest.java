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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class SetConnectionSourceNodeCommandTest extends AbstractGraphCommandTest {

    private static final String NODE_UUID = "nodeUUID";
    private static final String LAST_SOURCE_NODE_UUID = "lastSourceNodeUUID";
    private static final String TARGET_UUID = "nodeTargetUUID";
    private static final String EDGE_UUID = "edgeUUID";
    private static final Integer MAGNET_INDEX = 1;

    private Node node;
    private Node lastSourceNode;
    private Node target;
    private Edge edge;
    private ViewConnector edgeContent;
    private SetConnectionSourceNodeCommand tested;

    @Before
    @SuppressWarnings( "unchecked" )
    public void setup() throws Exception {
        super.init( 500, 500 );
        node = mockNode( NODE_UUID );
        lastSourceNode = mockNode( LAST_SOURCE_NODE_UUID );
        target = mockNode( TARGET_UUID );
        edge = mockEdge( EDGE_UUID );
        edgeContent = mock( ViewConnector.class );
        graphNodes.add( node );
        when( graphIndex.getNode( eq( NODE_UUID ) )).thenReturn( node );
        when( graphIndex.getNode( eq( LAST_SOURCE_NODE_UUID ) )).thenReturn( lastSourceNode );
        when( graphIndex.getNode( eq( TARGET_UUID ) )).thenReturn( target );
        when( graphIndex.getEdge( eq( EDGE_UUID ) )).thenReturn( edge );
        when( edge.getContent() ).thenReturn( edgeContent );
        when( edge.getSourceNode() ).thenReturn( lastSourceNode );
        when( edge.getTargetNode() ).thenReturn( target );
        this.tested = new SetConnectionSourceNodeCommand( node, edge,  MAGNET_INDEX );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        verify( connectionRuleManager, times( 1 ) ).evaluate( eq( edge ), eq( node ), eq( target ) );
        verify( edgeCardinalityRuleManager, times( 1 ) ).evaluate( eq( edge ), eq( node ), eq( target ),
                any( List.class ), any( List.class ), eq( RuleManager.Operation.ADD ) );
        verify( containmentRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
        verify( containmentRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
        verify( dockingRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testAllowNoSourceConnection() {
        this.tested = new SetConnectionSourceNodeCommand( null, edge,  MAGNET_INDEX );
        CommandResult<RuleViolation> result = tested.allow( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        verify( connectionRuleManager, times( 1 ) ).evaluate( eq( edge ), any( Node.class ), any( Node.class ) );
        verify( edgeCardinalityRuleManager, times( 1 ) ).evaluate( eq( edge ), any( Node.class ), any( Node.class ),
                any( List.class ), any( List.class ), eq( RuleManager.Operation.ADD ) );
        verify( containmentRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
        verify( containmentRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
        verify( dockingRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecute() {
        final List lastSourceOutEdges = mock( List.class );
        final List sourceOutEdges = mock( List.class );
        final List targetInEdges = mock( List.class );
        when( node.getOutEdges() ).thenReturn( sourceOutEdges );
        when( lastSourceNode.getOutEdges() ).thenReturn( lastSourceOutEdges );
        when( target.getInEdges() ).thenReturn( targetInEdges );
        CommandResult<RuleViolation> result = tested.execute( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        verify( lastSourceOutEdges, times( 1 ) ).remove( eq( edge ) );
        verify( sourceOutEdges, times( 1 ) ).add( eq( edge ) );
        verify( edgeContent, times( 1 ) ).setSourceMagnetIndex( eq( MAGNET_INDEX ) );
        verify( edge, times( 1 ) ).setSourceNode( eq( node ) );
        verify( targetInEdges, times( 0 ) ).remove( any( Edge.class ) );
        verify( targetInEdges, times( 0 ) ).add( any( Edge.class ) );
        verify( graphIndex, times( 0 ) ).removeEdge( any( Edge.class ) );
        verify( graphIndex, times( 0 ) ).addEdge( any( Edge.class ) );
        verify( graphIndex, times( 0 ) ).addNode( any( Node.class ) );
        verify( graphIndex, times( 0 ) ).removeNode(  any( Node.class ) );
    }


}
