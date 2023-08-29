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


package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILineSpliceAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddControlPointCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.CloneConnectorCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteControlPointCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateChildrenCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasHighlight;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LineSpliceAcceptorControlImplTest {

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private CanvasHighlight highlight;

    @Mock
    private WiresCanvas canvas;

    @Mock
    private WiresCanvasView canvasView;

    @Mock
    private WiresManager wiresManager;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Node spliceNode;

    @Mock
    private Node parentNode;

    @Mock
    private Node sourceNode;

    @Mock
    private Node targetNode;

    @Mock
    private View sourceContent;

    @Mock
    private View spliceContent;

    @Mock
    private Edge<ViewConnector<?>, Node> connector;

    @Captor
    private ArgumentCaptor<CompositeCommand<AbstractCanvasHandler, CanvasViolation>> commandsCapture;

    private double[] location = new double[]{20, 20};

    private LineSpliceAcceptorControlImpl tested;

    private CloneConnectorCommand cloneConnectorCommand;

    private DeleteControlPointCommand deleteControlPointCommand;

    private AddControlPointCommand addControlPointCommand;

    private SetConnectionTargetNodeCommand setConnectionTargetNodeCommand;

    private UpdateChildrenCommand updateChildrenCommand;

    private UpdateElementPositionCommand updateElementPositionCommand;

    private final CommandResult<CanvasViolation> resultSuccess = CanvasCommandResultBuilder.SUCCESS;

    private final CommandResult<CanvasViolation> resultFailed = CanvasCommandResultBuilder.failed();

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(canvas.getWiresManager()).thenReturn(wiresManager);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn(null);
        when(metadata.getShapeSetId()).thenReturn("shapeSetId");
        when(sourceContent.getBounds()).thenReturn(Bounds.create(0d, 0d, 10d, 10d));
        when(spliceContent.getBounds()).thenReturn(Bounds.create(0d, 0d, 10d, 10d));
        when(sourceNode.getContent()).thenReturn(sourceContent);
        when(spliceNode.getContent()).thenReturn(spliceContent);
        when(spliceNode.getUUID()).thenReturn("spliceNode");
        when(sourceNode.getUUID()).thenReturn("sourceNode");
        when(targetNode.getUUID()).thenReturn("targetNode");
        when(connector.getSourceNode()).thenReturn(sourceNode);
        when(connector.getTargetNode()).thenReturn(targetNode);

        this.tested = spy(new LineSpliceAcceptorControlImpl(canvasCommandFactory, highlight));
        this.tested.setCommandManagerProvider(() -> commandManager);
    }

    @Test
    public void testOnDestroy() {
        tested.init(canvasHandler);

        tested.destroy();

        verify(highlight, times(1)).destroy();
        verify(wiresManager, times(1)).setLineSpliceAcceptor(ILineSpliceAcceptor.NONE);
    }

    @Test
    public void testInit() {
        tested.init(canvasHandler);

        assertEquals(canvasHandler, tested.getCanvasHandler());
        verify(highlight, times(1)).setCanvasHandler(eq(canvasHandler));
        verify(wiresManager,
               times(1)).setSpliceEnabled(true);
        verify(wiresManager,
               times(1)).setLineSpliceAcceptor(any(ILineSpliceAcceptor.class));
        verify(wiresManager,
               never()).setDockingAcceptor(any(IDockingAcceptor.class));
        verify(wiresManager,
               never()).setContainmentAcceptor(any(IContainmentAcceptor.class));
    }

    @Test
    public void testAllowTrue() {
        setCloneConnectorCommand();
        setDeleteControlPointCommand();
        setAddControlPointCommand();
        setSetConnectionTargetNodeCommand();
        setUpdateChildrenCommand();
        setUpdateElementPositionCommand();
        setAllowTrue();

        tested.init(canvasHandler);
        assertTrue(tested.allow(spliceNode, location, parentNode, connector));

        verify(highlight, times(1)).unhighLight();
        verify(highlight, times(1)).highLight(connector);
        verify(commandManager, times(1))
                .allow(eq(canvasHandler), any(CompositeCommand.class));

        verify(tested).executeCommands(any(Function.class), commandsCapture.capture());
        final CompositeCommand<AbstractCanvasHandler, CanvasViolation> commands = commandsCapture.getValue();
        final List<Command<AbstractCanvasHandler, CanvasViolation>> commandList = commands.getCommands();

        assertEquals(commands.size(), 4);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof CloneConnectorCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof DeleteControlPointCommand).count(), 0);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof AddControlPointCommand).count(), 0);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof SetConnectionTargetNodeCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof UpdateChildrenCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof UpdateElementPositionCommand).count(), 1);
        assertEquals(parentNode, updateChildrenCommand.getParent());
        assertEquals(spliceNode, updateChildrenCommand.getCandidates().iterator().next());
    }

    @Test
    public void testAllowFalse() {
        setCloneConnectorCommand();
        setDeleteControlPointCommand();
        setAddControlPointCommand();
        setSetConnectionTargetNodeCommand();
        setUpdateChildrenCommand();
        setUpdateElementPositionCommand();
        setAllowFalse();

        tested.init(canvasHandler);
        assertFalse(tested.allow(spliceNode, location, parentNode, connector));

        verify(highlight, times(1)).unhighLight();
        verify(highlight, never()).highLight(connector);
        verify(commandManager, times(1))
                .allow(eq(canvasHandler), any(CompositeCommand.class));

        verify(tested).executeCommands(any(Function.class), commandsCapture.capture());
        final CompositeCommand<AbstractCanvasHandler, CanvasViolation> commands = commandsCapture.getValue();
        final List<Command<AbstractCanvasHandler, CanvasViolation>> commandList = commands.getCommands();

        assertEquals(commands.size(), 4);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof CloneConnectorCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof DeleteControlPointCommand).count(), 0);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof AddControlPointCommand).count(), 0);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof SetConnectionTargetNodeCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof UpdateChildrenCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof UpdateElementPositionCommand).count(), 1);
        assertEquals(parentNode, updateChildrenCommand.getParent());
        assertEquals(spliceNode, updateChildrenCommand.getCandidates().iterator().next());
    }

    @Test
    public void testAcceptTrue() {
        setCloneConnectorCommand();
        setDeleteControlPointCommand();
        setAddControlPointCommand();
        setSetConnectionTargetNodeCommand();
        setUpdateChildrenCommand();
        setUpdateElementPositionCommand();
        setExecuteTrue();

        tested.init(canvasHandler);
        List<double[]> firstHalfPoints = new ArrayList<>();
        List<double[]> secondHalfPoints = new ArrayList<>();
        double[] CP0 = new double[]{10, 10};
        double[] CP1 = new double[]{20, 10};
        double[] CP2 = new double[]{30, 10};
        double[] CP3 = new double[]{40, 10};
        firstHalfPoints.add(CP0);
        firstHalfPoints.add(CP1);
        secondHalfPoints.add(CP2);
        secondHalfPoints.add(CP3);

        assertTrue(tested.accept(spliceNode,
                                 location,
                                 parentNode,
                                 connector,
                                 2,
                                 firstHalfPoints,
                                 secondHalfPoints));

        verify(highlight, times(2)).unhighLight();
        verify(highlight, times(1)).highLight(connector);
        verify(commandManager, times(1))
                .execute(eq(canvasHandler), any(CompositeCommand.class));

        verify(tested).executeCommands(any(Function.class), commandsCapture.capture());
        final CompositeCommand<AbstractCanvasHandler, CanvasViolation> commands = commandsCapture.getValue();
        final List<Command<AbstractCanvasHandler, CanvasViolation>> commandList = commands.getCommands();

        assertEquals(commands.size(), 5);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof CloneConnectorCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof DeleteControlPointCommand).count(), 0);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof AddControlPointCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof SetConnectionTargetNodeCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof UpdateChildrenCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof UpdateElementPositionCommand).count(), 1);
        assertEquals(parentNode, updateChildrenCommand.getParent());
        assertEquals(spliceNode, updateChildrenCommand.getCandidates().iterator().next());
    }

    @Test
    public void testAcceptTrueMultipleCPs() {
        setCloneConnectorCommand();
        setDeleteControlPointCommand();
        setAddControlPointCommand();
        setSetConnectionTargetNodeCommand();
        setUpdateChildrenCommand();
        setUpdateElementPositionCommand();
        setExecuteTrue();

        tested.init(canvasHandler);
        List<double[]> firstHalfPoints = new ArrayList<>();
        List<double[]> secondHalfPoints = new ArrayList<>();
        double[] CP0 = new double[]{10, 10};
        double[] CP1 = new double[]{20, 10};
        double[] CP2 = new double[]{30, 10};
        double[] CP3 = new double[]{40, 10};
        double[] CP4 = new double[]{50, 10};
        double[] CP5 = new double[]{60, 10};
        firstHalfPoints.add(CP0);
        firstHalfPoints.add(CP1);
        firstHalfPoints.add(CP2);
        firstHalfPoints.add(CP3);
        secondHalfPoints.add(CP4);
        secondHalfPoints.add(CP5);

        assertTrue(tested.accept(spliceNode,
                                 location,
                                 parentNode,
                                 connector,
                                 6, // Points between the first and last are deleted
                                 firstHalfPoints,
                                 secondHalfPoints));

        verify(highlight, times(2)).unhighLight();
        verify(highlight, times(1)).highLight(connector);
        verify(commandManager, times(1))
                .execute(eq(canvasHandler), any(CompositeCommand.class));

        verify(tested).executeCommands(any(Function.class), commandsCapture.capture());
        final CompositeCommand<AbstractCanvasHandler, CanvasViolation> commands = commandsCapture.getValue();
        final List<Command<AbstractCanvasHandler, CanvasViolation>> commandList = commands.getCommands();

        assertEquals(commands.size(), 11);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof CloneConnectorCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof DeleteControlPointCommand).count(), 4);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof AddControlPointCommand).count(), 3);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof SetConnectionTargetNodeCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof UpdateChildrenCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof UpdateElementPositionCommand).count(), 1);
        assertEquals(parentNode, updateChildrenCommand.getParent());
        assertEquals(spliceNode, updateChildrenCommand.getCandidates().iterator().next());
    }

    @Test
    public void testAcceptFalse() {
        setCloneConnectorCommand();
        setDeleteControlPointCommand();
        setAddControlPointCommand();
        setSetConnectionTargetNodeCommand();
        setUpdateChildrenCommand();
        setUpdateElementPositionCommand();
        setExecuteFalse();

        tested.init(canvasHandler);
        List<double[]> firstHalfPoints = new ArrayList<>();
        List<double[]> secondHalfPoints = new ArrayList<>();
        double[] CP0 = new double[]{10, 10};
        double[] CP1 = new double[]{20, 10};
        double[] CP2 = new double[]{30, 10};
        double[] CP3 = new double[]{40, 10};
        firstHalfPoints.add(CP0);
        firstHalfPoints.add(CP1);
        secondHalfPoints.add(CP2);
        secondHalfPoints.add(CP3);

        assertFalse(tested.accept(spliceNode,
                                  location,
                                  parentNode,
                                  connector,
                                  2,
                                  firstHalfPoints,
                                  secondHalfPoints));

        verify(highlight, times(2)).unhighLight();
        verify(highlight, never()).highLight(connector);
        verify(commandManager, times(1))
                .execute(eq(canvasHandler), any(CompositeCommand.class));

        verify(tested).executeCommands(any(Function.class), commandsCapture.capture());
        final CompositeCommand<AbstractCanvasHandler, CanvasViolation> commands = commandsCapture.getValue();
        final List<Command<AbstractCanvasHandler, CanvasViolation>> commandList = commands.getCommands();

        assertEquals(commands.size(), 5);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof CloneConnectorCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof DeleteControlPointCommand).count(), 0);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof AddControlPointCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof SetConnectionTargetNodeCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof UpdateChildrenCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof UpdateElementPositionCommand).count(), 1);
        assertEquals(parentNode, updateChildrenCommand.getParent());
        assertEquals(spliceNode, updateChildrenCommand.getCandidates().iterator().next());
    }

    private void setUpdateElementPositionCommand() {
        doAnswer(invocationOnMock -> {
            final Node<View<?>, Edge> node = (Node<View<?>, Edge>) invocationOnMock.getArguments()[0];
            final org.kie.workbench.common.stunner.core.graph.content.view.Point2D point = (org.kie.workbench.common.stunner.core.graph.content.view.Point2D) invocationOnMock.getArguments()[1];
            updateElementPositionCommand = new UpdateElementPositionCommand(node, point);
            return updateElementPositionCommand;
        }).when(canvasCommandFactory).updatePosition(any(Node.class),
                                                     any(org.kie.workbench.common.stunner.core.graph.content.view.Point2D.class));
    }

    private void setUpdateChildrenCommand() {
        doAnswer(invocationOnMock -> {
            final Node parent1 = (Node) invocationOnMock.getArguments()[0];
            final Collection candidates = Collections.singleton((Node) invocationOnMock.getArguments()[1]);
            updateChildrenCommand = new UpdateChildrenCommand(parent1,
                                                              candidates);
            return updateChildrenCommand;
        }).when(canvasCommandFactory).updateChildNode(any(Node.class),
                                                      any(Node.class));
    }

    private void setSetConnectionTargetNodeCommand() {
        doAnswer(invocationOnMock -> {
            final Node<? extends View<?>, Edge> node = (Node<? extends View<?>, Edge>) invocationOnMock.getArguments()[0];
            final Edge<? extends ViewConnector<?>, Node> edge = (Edge<? extends ViewConnector<?>, Node>) invocationOnMock.getArguments()[1];
            final Connection connection = (Connection) invocationOnMock.getArguments()[2];
            setConnectionTargetNodeCommand = new SetConnectionTargetNodeCommand(node,
                                                                                edge,
                                                                                connection);

            return setConnectionTargetNodeCommand;
        }).when(canvasCommandFactory).setTargetNode(any(Node.class),
                                                    any(Edge.class),
                                                    any(Connection.class));
    }

    private void setAddControlPointCommand() {
        doAnswer(invocationOnMock -> {
            final Edge<ViewConnector<?>, Node> connector = (Edge) invocationOnMock.getArguments()[0];
            final ControlPoint controlPoint = (ControlPoint) invocationOnMock.getArguments()[1];
            final int index = (int) invocationOnMock.getArguments()[2];
            addControlPointCommand = new AddControlPointCommand(connector, controlPoint, index);

            return addControlPointCommand;
        }).when(canvasCommandFactory).addControlPoint(any(Edge.class),
                                                      any(ControlPoint.class),
                                                      anyInt());
    }

    private void setDeleteControlPointCommand() {
        doAnswer(invocationOnMock -> {
            final Edge<ViewConnector<?>, Node> connector = (Edge) invocationOnMock.getArguments()[0];
            final int index = (int) invocationOnMock.getArguments()[1];
            deleteControlPointCommand = new DeleteControlPointCommand(connector, index);

            return deleteControlPointCommand;
        }).when(canvasCommandFactory).deleteControlPoint(any(Edge.class), anyInt());
    }

    private void setCloneConnectorCommand() {
        doAnswer(invocationOnMock -> {
            final Edge<ViewConnector<?>, Node> connector = (Edge) invocationOnMock.getArguments()[0];
            final String spliceNodeUUID = (String) invocationOnMock.getArguments()[1];
            final String targetNodeUUID = (String) invocationOnMock.getArguments()[2];
            final String shapeSetID = (String) invocationOnMock.getArguments()[3];
            final Consumer<Edge> callback = (Consumer<Edge>) invocationOnMock.getArguments()[4];

            cloneConnectorCommand = new CloneConnectorCommand(connector,
                                                              spliceNodeUUID,
                                                              targetNodeUUID,
                                                              shapeSetID,
                                                              callback);
            return cloneConnectorCommand;
        }).when(canvasCommandFactory).cloneConnector(any(Edge.class),
                                                     anyString(),
                                                     anyString(),
                                                     anyString(),
                                                     any(Consumer.class));
    }

    private void setAllowTrue() {
        when(commandManager.allow(eq(canvasHandler),
                                  any(CompositeCommand.class))).thenReturn(resultSuccess);
    }

    private void setAllowFalse() {
        when(commandManager.allow(eq(canvasHandler),
                                  any(CompositeCommand.class))).thenReturn(resultFailed);
    }

    private void setExecuteTrue() {
        when(commandManager.execute(eq(canvasHandler),
                                    any(CompositeCommand.class))).thenReturn(resultSuccess);
    }

    private void setExecuteFalse() {
        when(commandManager.execute(eq(canvasHandler),
                                    any(CompositeCommand.class))).thenReturn(resultFailed);
    }
}
