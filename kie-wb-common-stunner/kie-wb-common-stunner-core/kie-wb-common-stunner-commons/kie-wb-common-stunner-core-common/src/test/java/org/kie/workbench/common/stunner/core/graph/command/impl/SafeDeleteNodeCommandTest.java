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
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRule;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class SafeDeleteNodeCommandTest extends AbstractGraphCommandTest {

    private static final String UUID = "nodeUUID";
    private static final String UUID1 = "node1UUID";
    private static final String EDGE_UUID = "edgeUUID";

    private Node node;
    private Node node1;
    private Edge edge;
    private final List nodeOutEdges = new LinkedList();
    private final List nodeInEdges = new LinkedList();
    private final List nodeOutEdges1 = new LinkedList();
    private final List nodeInEdges1 = new LinkedList();
    private SafeDeleteNodeCommand tested;

    @Before
    public void setup() throws Exception {
        super.init( 500, 500 );
        node = mockNode( UUID );
        node1 = mockNode( UUID1 );
        edge = mockEdge( EDGE_UUID );
        graphNodes.add( node );
        graphNodes.add( node1 );
        when( graphIndex.getNode( eq( UUID ) )).thenReturn( node );
        when( graphIndex.getNode( eq( UUID ) )).thenReturn( node );
        when( graphIndex.getEdge( eq( EDGE_UUID ) )).thenReturn( edge );
        when( node.getOutEdges() ).thenReturn( nodeOutEdges );
        when( node.getInEdges() ).thenReturn( nodeInEdges );
        when( node1.getOutEdges() ).thenReturn( nodeOutEdges1 );
        when( node1.getInEdges() ).thenReturn( nodeInEdges1 );
        this.tested = new SafeDeleteNodeCommand( UUID );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testSingleNode() {
        CommandResult<RuleViolation> result = tested.allow( graphCommandExecutionContext );
        List<Command<GraphCommandExecutionContext, RuleViolation>> commands = tested.getCommands();
        assertNotNull( commands );
        assertTrue( 1 == commands.size() );
        Command command1 = commands.get( 0 );
        assertTrue( command1 instanceof DeregisterNodeCommand );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        verify( cardinalityRuleManager, times( 2 ) ).evaluate( eq( graph ), eq( node ), eq( RuleManager.Operation.DELETE ) );
        verify( edgeCardinalityRuleManager, times( 0 ) ).evaluate( any( Edge.class ), any( Node.class ),
                any( List.class ), any( EdgeCardinalityRule.Type.class ), any( RuleManager.Operation.class ) );
        verify( containmentRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
        verify( connectionRuleManager, times( 0 ) ).evaluate( any( Edge.class ), any( Node.class ), any( Node.class ) );
        verify( dockingRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testMultipleNodes() {
        initializeTheChildNode();
        CommandResult<RuleViolation> result = tested.allow( graphCommandExecutionContext );
        List<Command<GraphCommandExecutionContext, RuleViolation>> commands = tested.getCommands();
        assertNotNull( commands );
        assertTrue( 3 == commands.size() );
        assertTrue( contains( commands, DeregisterNodeCommand.class ) );
        assertTrue( contains( commands, DeleteConnectorCommand.class ) );
        assertTrue( contains( commands, SafeDeleteNodeCommand.class ) );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        verify( cardinalityRuleManager, times( 4 ) ).evaluate( eq( graph ), eq( node ), eq( RuleManager.Operation.DELETE ) );
        verify( edgeCardinalityRuleManager, times( 2 ) ).evaluate( any( Edge.class ), any( Node.class ),
                any( List.class ), any( EdgeCardinalityRule.Type.class ), any( RuleManager.Operation.class ) );
        verify( connectionRuleManager, times( 2 ) ).evaluate( any( Edge.class ), any( Node.class ), any( Node.class ) );
        verify( containmentRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
        verify( dockingRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testAllowNoRules() {
        when( graphCommandExecutionContext.getRulesManager() ).thenReturn( null );
        CommandResult<RuleViolation> result = tested.allow( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        verify( cardinalityRuleManager, times( 0 ) ).evaluate( eq( graph ), eq( node ), eq( RuleManager.Operation.DELETE ) );
        verify( edgeCardinalityRuleManager, times( 0 ) ).evaluate( any( Edge.class ), any( Node.class ),
                any( List.class ), any( EdgeCardinalityRule.Type.class ), any( RuleManager.Operation.class ) );
        verify( containmentRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
        verify( connectionRuleManager, times( 0 ) ).evaluate( any( Edge.class ), any( Node.class ), any( Node.class ) );
        verify( dockingRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
    }

    @SuppressWarnings( "unchecked" )
    private void initializeTheChildNode() {
        Child edgeContent = mock ( Child.class );
        when( edge.getContent() ).thenReturn( edgeContent );
        when( edge.getSourceNode() ).thenReturn( node );
        when( edge.getTargetNode() ).thenReturn( node1 );
        nodeOutEdges.add( edge );
        nodeInEdges1.add( edge );
    }

    @SuppressWarnings( "unchecked" )
    private boolean contains( List commands, Class<?> clazz ) {
        return commands.stream().filter( command -> command.getClass().isAssignableFrom( clazz ) ).findFirst().isPresent();
    }

}
