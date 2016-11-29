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
import org.kie.workbench.common.stunner.core.command.exception.BadCommandArgumentsException;
import org.kie.workbench.common.stunner.core.command.exception.BoundsExceededException;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRule;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class UpdateElementPositionCommandTest extends AbstractGraphCommandTest {

    private static final String UUID = "testUUID";
    private static final Double X = 100d;
    private static final Double Y = 100d;
    private static final Double W = 50d;
    private static final Double H = 50d;
    private static final Double TX = 200d;
    private static final Double TY = 200d;

    @Mock Node candidate;
    private View content;
    private UpdateElementPositionCommand tested;

    @Before
    public void setup() throws Exception {
        super.init( 500, 500 );
        content = mockView( X, Y, W, H );
        when( candidate.getContent() ).thenReturn( content );
        when( graphIndex.getNode( eq( UUID ) )).thenReturn( candidate );
        this.tested = new UpdateElementPositionCommand( UUID, TX, TY );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        verify( containmentRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
        verify( cardinalityRuleManager, times( 0 ) ).evaluate( any( Graph.class ), any( Node.class ), any( RuleManager.Operation.class ) );
        verify( connectionRuleManager, times( 0 ) ).evaluate( any( Edge.class ), any( Node.class ), any( Node.class ) );
        verify( edgeCardinalityRuleManager, times( 0 ) ).evaluate( any( Edge.class ), any( Node.class ),
                any( List.class ), any( EdgeCardinalityRule.Type.class ), any( RuleManager.Operation.class ) );
        verify( dockingRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
    }

    @Test( expected = BadCommandArgumentsException.class )
    public void testAllowNodeNotFound() {
        when( graphIndex.getNode( eq( UUID ) )).thenReturn( null );
        tested.allow( graphCommandExecutionContext );
    }

    @Test
    public void testExecute() {
        CommandResult<RuleViolation> result = tested.execute( graphCommandExecutionContext );
        ArgumentCaptor<Bounds> bounds = ArgumentCaptor.forClass( Bounds.class );
        verify( content, times( 1 ) ).setBounds( bounds.capture() );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        Bounds b = bounds.getValue();
        assertEquals( UUID, tested.getUuid() );
        assertEquals( X, tested.getOldX() );
        assertEquals( Y, tested.getOldY() );
        assertEquals( TY, b.getUpperLeft().getY() );
        assertEquals( Double.valueOf( TX + W ), b.getLowerRight().getX() );
        assertEquals( Double.valueOf( TY + H ), b.getLowerRight().getY() );
    }

    @Test( expected = BadCommandArgumentsException.class )
    public void testExecuteNodeNotFound() {
        when( graphIndex.getNode( eq( UUID ) )).thenReturn( null );
        tested.execute( graphCommandExecutionContext );
    }

    @Test( expected = BoundsExceededException.class )
    public void testExecuteBadBounds() {
        this.tested = new UpdateElementPositionCommand( UUID, 600d, 600d );
        tested.execute( graphCommandExecutionContext );
    }

}
