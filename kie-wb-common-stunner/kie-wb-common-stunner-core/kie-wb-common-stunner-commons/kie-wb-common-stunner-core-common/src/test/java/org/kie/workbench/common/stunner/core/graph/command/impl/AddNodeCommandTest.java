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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.rule.*;
import org.kie.workbench.common.stunner.core.rule.impl.violations.ContainmentRuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class AddNodeCommandTest extends AbstractGraphCommandTest {

    private static final String UUID = "nodeUUID";

    @Mock
    Node node;

    private AddNodeCommand tested;

    @Before
    public void setup() throws Exception {
        super.init( 500, 500 );
        when( node.getUUID() ).thenReturn( UUID );
        when( graph.getNode( eq( UUID ) ) ).thenReturn( node );
        when( graphIndex.getNode( eq( UUID ) ) ).thenReturn( node );
        this.tested = new AddNodeCommand( node );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        verify( containmentRuleManager, times( 1 ) ).evaluate( eq( graph ), eq( node ) );
        verify( cardinalityRuleManager, times( 1 ) ).evaluate( eq( graph ), eq( node ), eq( RuleManager.Operation.ADD ) );
        verify( connectionRuleManager, times( 0 ) ).evaluate( any( Edge.class ), any( Node.class ), any( Node.class ) );
        verify( edgeCardinalityRuleManager, times( 0 ) ).evaluate( any( Edge.class ), any( Node.class ),
                any( List.class ), any( EdgeCardinalityRule.Type.class ), any( RuleManager.Operation.class ) );
        verify( dockingRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testNotAllowed() {
        final RuleViolations FAILED_VIOLATIONS = new DefaultRuleViolations()
                .addViolation( new ContainmentRuleViolation( graph.getUUID(), UUID ) );
        when( containmentRuleManager.evaluate( any( Element.class ), any( Element.class ) ) ).thenReturn( FAILED_VIOLATIONS );
        CommandResult<RuleViolation> result = tested.allow( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.ERROR, result.getType() );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecute() {
        CommandResult<RuleViolation> result = tested.execute( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        verify( graph, times( 1 ) ).addNode( eq( node ) );
        verify( graphIndex, times( 1 ) ).addNode( eq( node ) );
        verify( graphIndex, times( 0 ) ).addEdge( any( Edge.class ) );
        verify( graph, times( 0 ) ).removeNode( eq( UUID ) );
        verify( graphIndex, times( 0 ) ).removeNode( eq( node ) );
        verify( graphIndex, times( 0 ) ).removeEdge( any( Edge.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecuteCheckFailed() {
        final RuleViolations FAILED_VIOLATIONS = new DefaultRuleViolations()
                .addViolation( new ContainmentRuleViolation( graph.getUUID(), UUID ) );
        when( containmentRuleManager.evaluate( any( Element.class ), any( Element.class ) ) ).thenReturn( FAILED_VIOLATIONS );
        CommandResult<RuleViolation> result = tested.execute( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.ERROR, result.getType() );
        verify( graph, times( 0 ) ).addNode( eq( node ) );
        verify( graphIndex, times( 0 ) ).addNode( eq( node ) );
        verify( graphIndex, times( 0 ) ).addEdge( any( Edge.class ) );
        verify( graph, times( 0 ) ).removeNode( eq( UUID ) );
        verify( graphIndex, times( 0 ) ).removeNode( eq( node ) );
        verify( graphIndex, times( 0 ) ).removeEdge( any( Edge.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testUndo() {
        CommandResult<RuleViolation> result = tested.undo( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        verify( graph, times( 1 ) ).removeNode( eq( UUID ) );
        verify( graphIndex, times( 1 ) ).removeNode( eq( node ) );
        verify( graphIndex, times( 0 ) ).removeEdge( any( Edge.class ) );
        verify( graph, times( 0 ) ).addNode( eq( node ) );
        verify( graphIndex, times( 0 ) ).addNode( eq( node ) );
        verify( graphIndex, times( 0 ) ).addEdge( any( Edge.class ) );
    }
}
