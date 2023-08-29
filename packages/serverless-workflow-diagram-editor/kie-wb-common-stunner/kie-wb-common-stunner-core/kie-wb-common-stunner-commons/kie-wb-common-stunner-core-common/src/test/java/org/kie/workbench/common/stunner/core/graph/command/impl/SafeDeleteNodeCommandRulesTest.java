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

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ElementCardinalityContext;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.TestingGraphUtils.verifyCardinality;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * This test case mocks some nodes and connections for being able to
 * check the right rule contexts & evaluations are being performed.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class SafeDeleteNodeCommandRulesTest extends AbstractGraphCommandTest {

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
        super.init();
        node = mockNode(UUID);
        node1 = mockNode(UUID1);
        edge = mockEdge(EDGE_UUID);
        graph.addNode(node);
        graph.addNode(node1);
        when(graphIndex.getNode(eq(UUID))).thenReturn(node);
        when(graphIndex.getNode(eq(UUID))).thenReturn(node);
        when(graphIndex.getEdge(eq(EDGE_UUID))).thenReturn(edge);
        when(node.getOutEdges()).thenReturn(nodeOutEdges);
        when(node.getInEdges()).thenReturn(nodeInEdges);
        when(node1.getOutEdges()).thenReturn(nodeOutEdges1);
        when(node1.getInEdges()).thenReturn(nodeInEdges1);
        this.tested = new SafeDeleteNodeCommand(UUID,
                                                SafeDeleteNodeCommand.Options.defaults());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSingleNode() {
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        List<Command<GraphCommandExecutionContext, RuleViolation>> commands = tested.getCommands();
        assertNotNull(commands);
        assertTrue(1 == commands.size());
        Command command1 = commands.get(0);
        assertTrue(command1 instanceof DeregisterNodeCommand);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        final ArgumentCaptor<RuleEvaluationContext> contextCaptor = ArgumentCaptor.forClass(RuleEvaluationContext.class);
        verify(ruleManager,
               times(1)).evaluate(eq(ruleSet),
                                  contextCaptor.capture());
        final List<RuleEvaluationContext> contexts = contextCaptor.getAllValues();
        assertEquals(1,
                     contexts.size());
        verifyCardinality((ElementCardinalityContext) contexts.get(0),
                          graph,
                          node,
                          CardinalityContext.Operation.DELETE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultipleNodes() {
        initializeTheChildNode();
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        List<Command<GraphCommandExecutionContext, RuleViolation>> commands = tested.getCommands();
        assertNotNull(commands);
        assertTrue(3 == commands.size());
        final RemoveChildrenCommand removeChildCommand = (RemoveChildrenCommand) commands.get(0);
        assertNotNull(removeChildCommand);
        final DeregisterNodeCommand deregisterNode1Command = (DeregisterNodeCommand) commands.get(1);
        assertNotNull(deregisterNode1Command);
        final DeregisterNodeCommand deregisterNodeCommand = (DeregisterNodeCommand) commands.get(2);
        assertNotNull(deregisterNodeCommand);
        assertEquals(node1,
                     removeChildCommand.getCandidates().iterator().next());
        assertEquals(node,
                     removeChildCommand.getParent());
        assertEquals(node1,
                     deregisterNode1Command.getNode());
        assertEquals(node,
                     deregisterNodeCommand.getNode());
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        final ArgumentCaptor<RuleEvaluationContext> contextCaptor = ArgumentCaptor.forClass(RuleEvaluationContext.class);
        verify(ruleManager,
               times(2)).evaluate(eq(ruleSet),
                                  contextCaptor.capture());
        final List<RuleEvaluationContext> contexts = contextCaptor.getAllValues();
        assertEquals(2,
                     contexts.size());
        verifyCardinality((ElementCardinalityContext) contexts.get(1),
                          graph,
                          node,
                          CardinalityContext.Operation.DELETE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllowNoRules() {
        useAllowedExecutionContext();
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(ruleManager,
               times(0)).evaluate(eq(ruleSet),
                                  any(RuleEvaluationContext.class));
    }

    @SuppressWarnings("unchecked")
    private void initializeTheChildNode() {
        Child edgeContent = mock(Child.class);
        when(edge.getContent()).thenReturn(edgeContent);
        when(edge.getSourceNode()).thenReturn(node);
        when(edge.getTargetNode()).thenReturn(node1);
        nodeOutEdges.add(edge);
        nodeInEdges1.add(edge);
    }
}
