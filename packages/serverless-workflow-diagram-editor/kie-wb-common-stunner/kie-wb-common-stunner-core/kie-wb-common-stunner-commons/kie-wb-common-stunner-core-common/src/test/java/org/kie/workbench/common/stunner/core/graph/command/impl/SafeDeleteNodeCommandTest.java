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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * This test case uses a valid graph structure with different nodes and connectors
 * and checks that when removing a certain node, the right commands are being accumulated
 * in the right order.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class SafeDeleteNodeCommandTest {

    private TestingGraphMockHandler graphTestHandler;
    private TestingGraphInstanceBuilder.TestGraph2 graphHolder;
    private SafeDeleteNodeCommand tested;

    @Before
    public void setup() throws Exception {
        this.graphTestHandler = new TestingGraphMockHandler();
        this.graphHolder = TestingGraphInstanceBuilder.newGraph2(graphTestHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteStartNode() {
        this.tested = new SafeDeleteNodeCommand(graphHolder.startNode);
        final CommandResult<RuleViolation> result = tested.allow(graphTestHandler.graphCommandExecutionContext);
        final List<Command<GraphCommandExecutionContext, RuleViolation>> commands = tested.getCommands();
        assertNotNull(commands);
        assertTrue(3 == commands.size());
        final DeleteConnectorCommand delete1 = (DeleteConnectorCommand) commands.get(0);
        assertNotNull(delete1);
        assertEquals(graphHolder.edge1,
                     delete1.getEdge());
        final RemoveChildrenCommand removeChild = (RemoveChildrenCommand) commands.get(1);
        assertNotNull(removeChild);
        assertEquals(graphHolder.parentNode,
                     removeChild.getParent());
        assertEquals(graphHolder.startNode,
                     removeChild.getCandidates().iterator().next());
        final DeregisterNodeCommand deleteNode = (DeregisterNodeCommand) commands.get(2);
        assertNotNull(deleteNode);
        assertEquals(graphHolder.startNode,
                     deleteNode.getNode());
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteEndNode() {
        this.tested = new SafeDeleteNodeCommand(graphHolder.endNode);
        final CommandResult<RuleViolation> result = tested.allow(graphTestHandler.graphCommandExecutionContext);
        final List<Command<GraphCommandExecutionContext, RuleViolation>> commands = tested.getCommands();
        assertNotNull(commands);
        assertTrue(3 == commands.size());
        final DeleteConnectorCommand delete2 = (DeleteConnectorCommand) commands.get(0);
        assertNotNull(delete2);
        assertEquals(graphHolder.edge2,
                     delete2.getEdge());
        final RemoveChildrenCommand removeChild = (RemoveChildrenCommand) commands.get(1);
        assertNotNull(removeChild);
        assertEquals(graphHolder.parentNode,
                     removeChild.getParent());
        assertEquals(graphHolder.endNode,
                     removeChild.getCandidates().iterator().next());
        final DeregisterNodeCommand deleteNode = (DeregisterNodeCommand) commands.get(2);
        assertNotNull(deleteNode);
        assertEquals(graphHolder.endNode,
                     deleteNode.getNode());
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteIntermediateNode() {
        // Expect the connector for edge1 to be shortcut in this test case.
        this.tested = new SafeDeleteNodeCommand(graphHolder.intermNode);
        final CommandResult<RuleViolation> result = tested.allow(graphTestHandler.graphCommandExecutionContext);
        final List<Command<GraphCommandExecutionContext, RuleViolation>> commands = tested.getCommands();
        assertNotNull(commands);
        assertTrue(4 == commands.size());
        final DeleteConnectorCommand delete2 = (DeleteConnectorCommand) commands.get(0);
        assertNotNull(delete2);
        assertEquals(graphHolder.edge2,
                     delete2.getEdge());
        final SetConnectionTargetNodeCommand setConnectionNewTarget = (SetConnectionTargetNodeCommand) commands.get(1);
        assertNotNull(setConnectionNewTarget);
        assertEquals(graphHolder.edge1,
                     setConnectionNewTarget.getEdge());
        assertEquals(graphHolder.startNode,
                     setConnectionNewTarget.getSourceNode());
        assertEquals(graphHolder.endNode,
                     setConnectionNewTarget.getTargetNode());
        final RemoveChildrenCommand removeChild = (RemoveChildrenCommand) commands.get(2);
        assertNotNull(removeChild);
        assertEquals(graphHolder.parentNode,
                     removeChild.getParent());
        assertEquals(graphHolder.intermNode,
                     removeChild.getCandidates().iterator().next());
        final DeregisterNodeCommand deleteNode = (DeregisterNodeCommand) commands.get(3);
        assertNotNull(deleteNode);
        assertEquals(graphHolder.intermNode,
                     deleteNode.getNode());
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteIntermediateNodeExcludingTheConnectors() {
        final Set<String> excluded = new HashSet<String>() {{
            add(graphHolder.edge1.getUUID());
            add(graphHolder.edge2.getUUID());
        }};
        SafeDeleteNodeCommand.SafeDeleteNodeCommandCallback callback =
                mock(SafeDeleteNodeCommand.SafeDeleteNodeCommandCallback.class);
        this.tested = new SafeDeleteNodeCommand(graphHolder.intermNode,
                                                callback,
                                                SafeDeleteNodeCommand.Options.exclude(excluded));
        final CommandResult<RuleViolation> result = tested.allow(graphTestHandler.graphCommandExecutionContext);
        final List<Command<GraphCommandExecutionContext, RuleViolation>> commands = tested.getCommands();
        assertNotNull(commands);
        assertTrue(2 == commands.size());
        final RemoveChildrenCommand removeChild = (RemoveChildrenCommand) commands.get(0);
        assertNotNull(removeChild);
        assertEquals(graphHolder.parentNode,
                     removeChild.getParent());
        assertEquals(graphHolder.intermNode,
                     removeChild.getCandidates().iterator().next());
        final DeregisterNodeCommand deleteNode = (DeregisterNodeCommand) commands.get(1);
        assertNotNull(deleteNode);
        assertEquals(graphHolder.intermNode,
                     deleteNode.getNode());
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(callback, times(1)).deleteNode(eq(graphHolder.intermNode));
        verify(callback, never()).deleteConnector(eq(graphHolder.edge1));
        verify(callback, never()).deleteConnector(eq(graphHolder.edge2));
    }

    @Test
    public void testGetSafeDeleteCallback() {

        final Node node = mock(Node.class);
        when(node.getUUID()).thenReturn("uuid");
        final SafeDeleteNodeCommand.SafeDeleteNodeCommandCallback callback = mock(SafeDeleteNodeCommand.SafeDeleteNodeCommandCallback.class);
        final SafeDeleteNodeCommand command = new SafeDeleteNodeCommand(node,
                                                                        callback,
                                                                        SafeDeleteNodeCommand.Options.defaults());

        final Optional<SafeDeleteNodeCommand.SafeDeleteNodeCommandCallback> actual = command.getSafeDeleteCallback();

        assertTrue(actual.isPresent());
        assertEquals(callback, actual.get());
    }

    @Test
    public void testCreateChangeParentCommands() {

        final Node node = mock(Node.class);
        final SafeDeleteNodeCommand.SafeDeleteNodeCommandCallback callback = mock(SafeDeleteNodeCommand.SafeDeleteNodeCommandCallback.class);
        final Element<?> canvas = mock(Element.class);
        final Node canvasNode = mock(Node.class);
        final Node<?, Edge> candidate = mock(Node.class);
        final List<Edge> outEdges = new ArrayList<>();
        final Edge e1 = mock(Edge.class);
        final Child child = mock(Child.class);
        final Node targetNode = mock(Node.class);
        when(node.getUUID()).thenReturn("uuid");
        when(canvas.asNode()).thenReturn(canvasNode);
        when(e1.getContent()).thenReturn(child);
        when(e1.getTargetNode()).thenReturn(targetNode);
        when(candidate.getOutEdges()).thenReturn(outEdges);
        outEdges.add(e1);

        final SafeDeleteNodeCommand command = new SafeDeleteNodeCommand(node,
                                                                        callback,
                                                                        SafeDeleteNodeCommand.Options.defaults());
        command.createChangeParentCommands(canvas, candidate);

        verify(callback).moveChildToCanvasRoot(canvasNode, targetNode);
    }
}
