/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ElementCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.NodeDockingContext;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.TestingGraphUtils.verifyCardinality;
import static org.kie.workbench.common.stunner.core.TestingGraphUtils.verifyDocking;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AddDockedNodeCommandTest extends AbstractGraphCommandTest {

    private static final String PARENT_UUID = "parentUUID";
    private static final String CANDIDATE_UUID = "candidateUUID";

    private Node parent;
    private Node candidate;

    private AddDockedNodeCommand tested;

    @Before
    public void setup() throws Exception {
        super.init();
        this.parent = mockNode(PARENT_UUID);
        this.candidate = mockNode(CANDIDATE_UUID);
        when(graphIndex.getNode(eq(PARENT_UUID))).thenReturn(parent);
        when(graphIndex.getNode(eq(CANDIDATE_UUID))).thenReturn(candidate);
        this.tested = new AddDockedNodeCommand(PARENT_UUID,
                                               candidate);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInitializeCommands() {
        this.tested = spy(tested);
        tested.initialize(graphCommandExecutionContext);
        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        verify(tested,
               times(2)).addCommand(commandArgumentCaptor.capture());
        List<Command> commands = commandArgumentCaptor.getAllValues();
        assertNotNull(commands);
        assertTrue(commands.size() == 2);
        assertTrue(commands.get(0) instanceof RegisterNodeCommand);
        assertTrue(commands.get(1) instanceof DockNodeCommand);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        final ArgumentCaptor<RuleEvaluationContext> contextCaptor = ArgumentCaptor.forClass(RuleEvaluationContext.class);
        verify(ruleManager,
               times(2)).evaluate(eq(ruleSet),
                                  contextCaptor.capture());
        final List<RuleEvaluationContext> contexts = contextCaptor.getAllValues();
        assertEquals(2,
                     contexts.size());
        verifyCardinality((ElementCardinalityContext) contexts.get(0),
                          graph,
                          candidate,
                          CardinalityContext.Operation.ADD);
        verifyDocking((NodeDockingContext) contexts.get(1),
                      parent,
                      candidate);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllowNoRules() {
        useAllowedExecutionContext();
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(ruleManager,
               times(0)).evaluate(any(RuleSet.class),
                                  any(RuleEvaluationContext.class));
    }
}
