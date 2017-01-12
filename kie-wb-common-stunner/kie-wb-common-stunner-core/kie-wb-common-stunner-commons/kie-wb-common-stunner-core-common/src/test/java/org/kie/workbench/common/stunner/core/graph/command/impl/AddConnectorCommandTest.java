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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRule;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class AddConnectorCommandTest extends AbstractGraphCommandTest {

    private static final String NODE_UUID = "nodeUUID";
    private static final String EDGE_UUID = "edgeUUID";

    private Node node;
    @Mock
    private Edge edge;
    @Mock
    private ViewConnector connContent;

    private AddConnectorCommand tested;

    @Before
    public void setup() throws Exception {
        super.init( 500,
                    500 );
        node = mockNode( NODE_UUID );
        when( edge.getUUID() ).thenReturn( EDGE_UUID );
        when( edge.getContent() ).thenReturn( connContent );
        when( connContent.getSourceMagnetIndex() ).thenReturn( 0 );
        when( connContent.getTargetMagnetIndex() ).thenReturn( 1 );
        when( graphIndex.getNode( eq( NODE_UUID ) ) ).thenReturn( node );
        this.tested = new AddConnectorCommand( NODE_UUID,
                                               edge,
                                               0 );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.INFO,
                      result.getType() );
        verify( containmentRuleManager,
                times( 0 ) ).evaluate( any( Element.class ),
                                       any( Element.class ) );
        verify( cardinalityRuleManager,
                times( 0 ) ).evaluate( any( Graph.class ),
                                       any( Node.class ),
                                       any( RuleManager.Operation.class ) );
        verify( connectionRuleManager,
                times( 1 ) ).evaluate( any( Edge.class ),
                                       eq( node ),
                                       any( Node.class ) );
        verify( edgeCardinalityRuleManager,
                times( 1 ) ).evaluate( eq( edge ),
                                       eq( node ),
                                       any( List.class ),
                                       any( EdgeCardinalityRule.Type.class ),
                                       any( RuleManager.Operation.class ) );
        verify( dockingRuleManager,
                times( 0 ) ).evaluate( any( Element.class ),
                                       any( Element.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testAllowNoRules() {
        when( graphCommandExecutionContext.getRulesManager() ).thenReturn( null );
        CommandResult<RuleViolation> result = tested.allow( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.INFO,
                      result.getType() );
        verify( containmentRuleManager,
                times( 0 ) ).evaluate( any( Element.class ),
                                       any( Element.class ) );
        verify( cardinalityRuleManager,
                times( 0 ) ).evaluate( any( Graph.class ),
                                       any( Node.class ),
                                       any( RuleManager.Operation.class ) );
        verify( connectionRuleManager,
                times( 0 ) ).evaluate( any( Edge.class ),
                                       eq( node ),
                                       any( Node.class ) );
        verify( edgeCardinalityRuleManager,
                times( 0 ) ).evaluate( eq( edge ),
                                       eq( node ),
                                       any( List.class ),
                                       any( EdgeCardinalityRule.Type.class ),
                                       any( RuleManager.Operation.class ) );
        verify( dockingRuleManager,
                times( 0 ) ).evaluate( any( Element.class ),
                                       any( Element.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecute() {
        CommandResult<RuleViolation> result = tested.execute( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.INFO,
                      result.getType() );
        assertTrue( node.getOutEdges().size() == 1 );
        assertEquals( edge,
                      node.getOutEdges().get( 0 ) );
        verify( graphIndex,
                times( 2 ) ).addEdge( eq( edge ) );
        verify( graphIndex,
                times( 0 ) ).addNode( any( Node.class ) );
    }
}
