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
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.rule.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.impl.violations.ContainmentRuleViolation;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class AddDockedNodeCommandTest extends AbstractGraphCommandTest {

    private static final String PARENT_UUID = "parentUUID";
    private static final String CANDIDATE_UUID = "candidateUUID";

    private Node parent;
    private Node candidate;

    private AddDockedNodeCommand tested;

    @Before
    public void setup() throws Exception {
        super.init( 500, 500 );
        this.parent = mockNode( PARENT_UUID );
        this.candidate = mockNode( CANDIDATE_UUID );
        when( graphIndex.getNode( eq( PARENT_UUID ) )).thenReturn( parent );
        when( graphIndex.getNode( eq( CANDIDATE_UUID ) )).thenReturn( candidate );
        this.tested = new AddDockedNodeCommand( PARENT_UUID, candidate );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testInitializeCommands() {
        this.tested = spy( tested );
        tested.initialize( graphCommandExecutionContext );
        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass( Command.class );
        verify( tested, times( 2 ) ).addCommand( commandArgumentCaptor.capture() );
        List<Command > commands = commandArgumentCaptor.getAllValues();
        assertNotNull( commands );
        assertTrue( commands.size() == 2 );
        assertTrue( commands.get( 0 ) instanceof AddNodeCommand );
        assertTrue( commands.get( 1 ) instanceof AddDockEdgeCommand );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        verify( containmentRuleManager, times( 1 ) ).evaluate( eq( graph ), eq( candidate ) );
        verify( cardinalityRuleManager, times( 1 ) ).evaluate( eq( graph ), eq( candidate ), eq( RuleManager.Operation.ADD ) );
        verify( dockingRuleManager, times( 1 ) ).evaluate( eq( parent ), eq( candidate ) );
        verify( connectionRuleManager, times( 0 ) ).evaluate( any( Edge.class ), any( Node.class ), any( Node.class ) );
        verify( edgeCardinalityRuleManager, times( 0 ) ).evaluate( any( Edge.class ), any( Node.class ), any( Node.class ),
                any( List.class ), any( List.class ), any( RuleManager.Operation.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testNotAllowed() {
        final RuleViolations FAILED_VIOLATIONS = new DefaultRuleViolations()
                .addViolation( new ContainmentRuleViolation( graph.getUUID(), PARENT_UUID ) );
        when( containmentRuleManager.evaluate( any( Element.class ), any( Element.class ) ) ).thenReturn( FAILED_VIOLATIONS );
        CommandResult<RuleViolation> result = tested.allow( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.ERROR, result.getType() );
    }

}
