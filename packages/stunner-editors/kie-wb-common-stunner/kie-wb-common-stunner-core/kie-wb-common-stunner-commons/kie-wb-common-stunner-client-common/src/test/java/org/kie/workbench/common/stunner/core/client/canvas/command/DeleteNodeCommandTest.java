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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.AbstractCompositeCommand;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DeleteNodeCommandTest {

    private static final String SHAPE_SET_ID = "ss1";

    @Mock
    private AbstractCanvasHandler canvasHandler;
    @Mock
    private AbstractCanvas canvas;
    @Mock
    private Diagram diagram;
    @Mock
    private Metadata metadata;

    @Captor
    private ArgumentCaptor<AbstractCanvasGraphCommand> commandsCaptor;

    private TestingGraphInstanceBuilder.TestGraph4 graphHolder;
    private DeleteNodeCommand tested;

    @Before
    public void setup() throws Exception {
        TestingGraphMockHandler graphHandler = new TestingGraphMockHandler();
        this.graphHolder = TestingGraphInstanceBuilder.newGraph4(graphHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getGraphIndex()).thenReturn(graphHandler.graphIndex);
        when(canvasHandler.getGraphExecutionContext()).thenReturn(graphHandler.graphCommandExecutionContext);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(diagram.getGraph()).thenReturn(graphHandler.graph);
        when(metadata.getDefinitionSetId()).thenReturn(TestingGraphMockHandler.DEF_SET_ID);
        when(metadata.getShapeSetId()).thenReturn(SHAPE_SET_ID);
        when(metadata.getCanvasRootUUID()).thenReturn(graphHolder.parentNode.getUUID());
    }

    @Test
    public void startNodeTestGraphCommand() {
        this.tested = new DeleteNodeCommand(graphHolder.startNode);
        final org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand graphCommand =
                (org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand) tested.newGraphCommand(canvasHandler);
        assertNotNull(graphCommand);
        assertEquals(graphHolder.startNode,
                     graphCommand.getNode());
    }

    @Test
    public void startNodeTestCanvasCommands() {
        this.tested = new DeleteNodeCommand(graphHolder.startNode);
        final CommandResult<CanvasViolation> result = tested.allow(canvasHandler);
        final AbstractCompositeCommand<AbstractCanvasHandler, CanvasViolation> compositeCommand = tested.getCommand();
        assertNotNull(compositeCommand);
        assertTrue(3 == compositeCommand.size());
        final List<Command<AbstractCanvasHandler, CanvasViolation>> commands = compositeCommand.getCommands();
        assertNotNull(commands);
        final DeleteCanvasConnectorCommand delete1 = (DeleteCanvasConnectorCommand) commands.get(0);
        assertNotNull(delete1);
        assertEquals(graphHolder.edge1,
                     delete1.getCandidate());
        final RemoveCanvasChildrenCommand c2 = (RemoveCanvasChildrenCommand) commands.get(1);
        assertNotNull(c2);
        assertEquals(graphHolder.parentNode,
                     c2.getParent());
        assertEquals(graphHolder.startNode,
                     c2.getChildren().iterator().next());
        final DeleteCanvasNodeCommand c3 = (DeleteCanvasNodeCommand) commands.get(2);
        assertNotNull(c3);
        assertEquals(graphHolder.startNode,
                     c3.getCandidate());
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
    }

    @Test
    public void intermediateNodeTestGraphCommand() {
        this.tested = new DeleteNodeCommand(graphHolder.intermNode);
        final org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand graphCommand =
                (org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand) tested.newGraphCommand(canvasHandler);
        assertNotNull(graphCommand);
        assertEquals(graphHolder.intermNode,
                     graphCommand.getNode());
    }

    @Test
    public void intermediateNodeTestCanvasCommands() {
        this.tested = new DeleteNodeCommand(graphHolder.intermNode);
        final CommandResult<CanvasViolation> result = tested.allow(canvasHandler);
        assertEquals(CommandResult.Type.INFO, result.getType());

        final AbstractCompositeCommand<AbstractCanvasHandler, CanvasViolation> compositeCommand = tested.getCommand();
        assertNotNull(compositeCommand);
        assertEquals(7, compositeCommand.size());
        final List<Command<AbstractCanvasHandler, CanvasViolation>> commands = compositeCommand.getCommands();
        assertNotNull(commands);
        final CanvasUndockNodeCommand c1 = (CanvasUndockNodeCommand) commands.get(0);
        final RemoveCanvasChildrenCommand c2 = (RemoveCanvasChildrenCommand) commands.get(1);
        final DeleteCanvasNodeCommand c3 = (DeleteCanvasNodeCommand) commands.get(2);
        final DeleteCanvasConnectorCommand c5 = (DeleteCanvasConnectorCommand) commands.get(3);
        final SetCanvasConnectionCommand c6 = (SetCanvasConnectionCommand) commands.get(4);
        final RemoveCanvasChildrenCommand c4 = (RemoveCanvasChildrenCommand) commands.get(5);
        final DeleteCanvasNodeCommand c7 = (DeleteCanvasNodeCommand) commands.get(6);

        assertEquals(graphHolder.intermNode, c1.getParent());
        assertEquals(graphHolder.dockedNode, c1.getChild());
        assertEquals(graphHolder.dockedNode, c2.getChildren().iterator().next());
        assertEquals(graphHolder.parentNode, c2.getParent());
        assertEquals(graphHolder.dockedNode, c3.getCandidate());
        assertEquals(graphHolder.parentNode, c4.getParent());
        assertEquals(graphHolder.intermNode, c4.getChildren().iterator().next());
        assertEquals(graphHolder.edge2, c5.getCandidate());
        assertEquals(graphHolder.edge1, c6.getEdge());
        assertEquals(graphHolder.intermNode, c7.getCandidate());
    }

    @Test
    public void exclusionsTestCanvasCommands() {
        final SafeDeleteNodeCommand.Options options = SafeDeleteNodeCommand.Options.exclude(new HashSet<String>() {{
            add(graphHolder.edge2.getUUID());
        }});
        this.tested = new DeleteNodeCommand(graphHolder.intermNode,
                                            options);
        final CommandResult<CanvasViolation> result = tested.allow(canvasHandler);
        assertEquals(CommandResult.Type.INFO, result.getType());
        final AbstractCompositeCommand<AbstractCanvasHandler, CanvasViolation> compositeCommand = tested.getCommand();
        assertNotNull(compositeCommand);
        assertEquals(6, compositeCommand.size());
        final List<Command<AbstractCanvasHandler, CanvasViolation>> commands = compositeCommand.getCommands();
        assertNotNull(commands);

        final CanvasUndockNodeCommand c1 = (CanvasUndockNodeCommand) commands.get(0);
        final RemoveCanvasChildrenCommand c2 = (RemoveCanvasChildrenCommand) commands.get(1);
        final DeleteCanvasNodeCommand c3 = (DeleteCanvasNodeCommand) commands.get(2);
        final DeleteCanvasConnectorCommand c5 = (DeleteCanvasConnectorCommand) commands.get(3);
        final RemoveCanvasChildrenCommand c4 = (RemoveCanvasChildrenCommand) commands.get(4);
        final DeleteCanvasNodeCommand c6 = (DeleteCanvasNodeCommand) commands.get(5);

        assertEquals(graphHolder.intermNode, c1.getParent());
        assertEquals(graphHolder.dockedNode, c1.getChild());
        assertEquals(graphHolder.dockedNode, c2.getChildren().iterator().next());
        assertEquals(graphHolder.parentNode, c2.getParent());
        assertEquals(graphHolder.dockedNode, c3.getCandidate());
        assertEquals(graphHolder.parentNode, c4.getParent());
        assertEquals(graphHolder.intermNode, c4.getChildren().iterator().next());
        assertEquals(graphHolder.edge1, c5.getCandidate());
        assertEquals(graphHolder.intermNode, c6.getCandidate());
    }

    @Test
    public void testGetChildPosition() {
        final double parentX = 10;
        final double parentY = 5;
        final double nodeX = 7;
        final double nodeY = 22;
        final double expectedX = parentX + nodeX;
        final double expectedY = parentY + nodeY;

        final DeleteNodeCommand.CanvasDeleteProcessor processor = mock(DeleteNodeCommand.CanvasDeleteProcessor.class);

        final Node node = mock(Node.class);
        final Element parent = mock(Element.class);
        final View parentContent = mock(View.class);
        final View nodeContent = mock(View.class);

        final Bounds parentBounds = mock(Bounds.class);
        final Bound upParent = new Bound(parentX, parentY);
        final Bounds nodeBounds = mock(Bounds.class);
        final Bound upNode = new Bound(nodeX, nodeY);

        when(parentBounds.getUpperLeft()).thenReturn(upParent);
        when(parentContent.getBounds()).thenReturn(parentBounds);
        when(parent.getContent()).thenReturn(parentContent);
        when(nodeBounds.getUpperLeft()).thenReturn(upNode);
        when(nodeContent.getBounds()).thenReturn(nodeBounds);
        when(node.getContent()).thenReturn(nodeContent);
        when(processor.getChildPosition(node, parent)).thenCallRealMethod();

        final Point2D actualPosition = processor.getChildPosition(node, parent);

        assertEquals(expectedX, actualPosition.getX(), 0.01);
        assertEquals(expectedY, actualPosition.getY(), 0.01);
    }

    @Test
    public void testMoveChildToCanvasRoot() {
        final DeleteNodeCommand.CanvasDeleteProcessor processor = mock(DeleteNodeCommand.CanvasDeleteProcessor.class);
        final Node node = mock(Node.class);
        final Element elementCanvas = mock(Element.class);
        final Node canvasNode = mock(Node.class);
        final Element parentElement = mock(Element.class);
        final Node nodeParent = mock(Node.class);
        final Point2D newPosition = new Point2D(1, 2);
        final CompositeCommand command = mock(CompositeCommand.class);

        when(elementCanvas.asNode()).thenReturn(canvasNode);
        doCallRealMethod().when(processor).moveChildToCanvasRoot(elementCanvas, node);
        when(parentElement.asNode()).thenReturn(nodeParent);
        when(processor.getParent(node)).thenReturn(parentElement);
        when(processor.getCommand()).thenReturn(command);
        when(processor.getChildPosition(node, parentElement)).thenReturn(newPosition);

        processor.moveChildToCanvasRoot(elementCanvas, node);

        verify(command, times(3)).addCommand(commandsCaptor.capture());

        final List<AbstractCanvasGraphCommand> values = commandsCaptor.getAllValues();

        final AbstractCanvasGraphCommand removeChildrenCmd = values.get(0);
        final AbstractCanvasGraphCommand setChildrenCmd = values.get(1);
        final AbstractCanvasGraphCommand updatePosition = values.get(2);

        assertTrue(removeChildrenCmd instanceof RemoveChildrenCommand);
        assertTrue(setChildrenCmd instanceof SetChildrenCommand);
        assertTrue(updatePosition instanceof UpdateElementPositionCommand);

        assertEquals(nodeParent, ((RemoveChildrenCommand) removeChildrenCmd).getParent());
        assertEquals(canvasNode, ((SetChildrenCommand) setChildrenCmd).getParent());
        assertEquals(node, ((UpdateElementPositionCommand) updatePosition).getElement());

        assertEquals(node, getNode(((RemoveChildrenCommand) removeChildrenCmd).getChildren()));
        assertEquals(node, getNode(((SetChildrenCommand) setChildrenCmd).getCandidates()));
        assertEquals(newPosition, ((UpdateElementPositionCommand) updatePosition).getLocation());
    }

    private Node getNode(final Collection<Node<?, Edge>> collection) {
        return collection.stream().findFirst().get();
    }
}
