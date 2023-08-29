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


package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.Objects;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.ConnectorViewStub;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.command.impl.AbstractCompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test based on the graph {@link TestingGraphInstanceBuilder.TestGraph2}
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class CloneCanvasNodeCommandTest extends AbstractCanvasCommandTest {

    @Mock
    private Node parent;

    private Node candidate;

    private TestingGraphInstanceBuilder.TestGraph2 graphInstance;

    private CloneCanvasNodeCommand cloneCanvasNodeCommand;

    @Mock
    private ConnectorShape edgeShape;

    @Mock
    private ConnectorViewStub edgeShapeView;

    @Mock
    private ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessorManagedInstance;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();

        graphInstance = TestingGraphInstanceBuilder.newGraph2(new TestingGraphMockHandler());
        graph = graphInstance.graph;
        candidate = graphInstance.parentNode;
        when(graphIndex.getGraph()).thenReturn(graphInstance.graph);

        when(canvas.getShape(graphInstance.edge1.getUUID())).thenReturn(edgeShape);
        when(canvas.getShape(graphInstance.edge2.getUUID())).thenReturn(edgeShape);
        when(edgeShape.getShapeView()).thenReturn(edgeShapeView);
        when(childrenTraverseProcessorManagedInstance.get()).thenReturn(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()));

        this.cloneCanvasNodeCommand = new CloneCanvasNodeCommand(parent,
                                                                 candidate,
                                                                 SHAPE_SET_ID,
                                                                 childrenTraverseProcessorManagedInstance);
    }

    @Test
    public void testExecute() {
        cloneCanvasNodeCommand.execute(canvasHandler);

        AbstractCompositeCommand<AbstractCanvasHandler, CanvasViolation> commands = cloneCanvasNodeCommand.getCommands();

        assertEquals(commands.size(), 6);
        assertTrue(commands.getCommands().stream()
                           .filter(command -> command instanceof CloneCanvasNodeCommand)
                           .map(command -> (CloneCanvasNodeCommand) command)
                           .allMatch(command -> Objects.equals(command.getCandidate(), graphInstance.startNode) ||
                                   Objects.equals(command.getCandidate(), graphInstance.intermNode) ||
                                   Objects.equals(command.getCandidate(), graphInstance.endNode)));

        assertTrue(commands.getCommands().stream()
                           .filter(command -> command instanceof AddCanvasConnectorCommand)
                           .map(command -> (AddCanvasConnectorCommand) command)
                           .allMatch(command -> Objects.equals(command.getCandidate(), graphInstance.edge1) ||
                                   Objects.equals(command.getCandidate(), graphInstance.edge2)));
    }

    @Test
    public void testUndo() {
        testExecute();
        cloneCanvasNodeCommand.undo(canvasHandler);
        //nodes
        verify(canvasHandler, atLeastOnce()).removeChild(parent, candidate);
        verify(canvasHandler, atLeastOnce()).deregister(candidate);
        verify(canvasHandler, atLeastOnce()).removeChild(graphInstance.parentNode, graphInstance.startNode);
        verify(canvasHandler, atLeastOnce()).deregister(graphInstance.startNode);
        verify(canvasHandler, atLeastOnce()).removeChild(graphInstance.parentNode, graphInstance.intermNode);
        verify(canvasHandler, atLeastOnce()).deregister(graphInstance.intermNode);
        verify(canvasHandler, atLeastOnce()).removeChild(graphInstance.parentNode, graphInstance.endNode);
        verify(canvasHandler, atLeastOnce()).deregister(graphInstance.endNode);
        //edges
        verify(canvasHandler, atLeastOnce()).deregister(graphInstance.edge1);
        verify(canvasHandler, atLeastOnce()).deregister(graphInstance.edge2);
    }
}