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
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class AddEdgeCommandTest extends AbstractGraphCommandTest {

    private static final String NODE_UUID = "nodeUUID";
    private static final String EDGE_UUID = "edgeUUID";

    private Node node;
    @Mock private Edge edge;

    private AddEdgeCommand tested;

    @Before
    public void setup() throws Exception {
        super.init( 500, 500 );
        node = mockNode( NODE_UUID );
        when( edge.getUUID() ).thenReturn( EDGE_UUID );
        when( graphIndex.getNode( eq( NODE_UUID ) )).thenReturn( node );
        this.tested = new AddEdgeCommand( NODE_UUID, edge );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        verify( containmentRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
        verify( cardinalityRuleManager, times( 0 ) ).evaluate( any( Graph.class ), any( Node.class ), any( RuleManager.Operation.class ) );
        verify( connectionRuleManager, times( 0 ) ).evaluate( any( Edge.class ), any( Node.class ), any( Node.class ) );
        verify( edgeCardinalityRuleManager, times( 0 ) ).evaluate( any( Edge.class ), any( Node.class ), any( Node.class ),
                any( List.class ), any( List.class ), any( RuleManager.Operation.class ) );
        verify( dockingRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecute() {
        CommandResult<RuleViolation> result = tested.execute( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        assertTrue( node.getOutEdges().size() == 1 );
        assertEquals( edge, node.getOutEdges().get( 0 ) );
        verify( graphIndex, times( 1 ) ).addEdge( eq( edge ) );
        verify( graphIndex, times( 0 ) ).addNode( any( Node.class ) );
    }


}
