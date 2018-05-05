/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.Optional;

import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.SetConnectionSourceNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.CancelCanvasAction;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ConnectionAcceptorControlImplTest {

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;
    @Mock
    private AbstractCanvasHandler canvasHandler;
    @Mock
    private AbstractCanvas canvas;
    @Mock
    private WiresCanvas.View canvasView;
    @Mock
    private Diagram diagram;
    @Mock
    private Node node;
    @Mock
    private Edge<ViewConnector<?>, Node> edge;
    @Mock
    private ViewConnector edgeContent;
    @Mock
    private Connection connection;

    @Mock
    private EventSourceMock<CancelCanvasAction> cancelCanvasActionEvent;

    private ConnectionAcceptorControlImpl tested;
    private SetConnectionSourceNodeCommand setConnectionSourceNodeCommand;
    private SetConnectionTargetNodeCommand setConnectionTargetNodeCommand;

    private final CommandResult<CanvasViolation> result = CanvasCommandResultBuilder.SUCCESS;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        when(canvas.getView()).thenReturn(canvasView);
        when(edge.getContent()).thenReturn(edgeContent);
        when(edgeContent.getSourceConnection()).thenReturn(Optional.empty());
        when(edgeContent.getTargetConnection()).thenReturn(Optional.empty());
        doAnswer(invocationOnMock -> {
            final Node node = (Node) invocationOnMock.getArguments()[0];
            final Edge edge = (Edge) invocationOnMock.getArguments()[1];
            final Connection connection = (Connection) invocationOnMock.getArguments()[2];
            setConnectionSourceNodeCommand = new SetConnectionSourceNodeCommand(node,
                                                                                edge,
                                                                                connection);
            return setConnectionSourceNodeCommand;
        }).when(canvasCommandFactory).setSourceNode(any(Node.class),
                                                    any(Edge.class),
                                                    any(Connection.class));
        doAnswer(invocationOnMock -> {
            final Node node = (Node) invocationOnMock.getArguments()[0];
            final Edge edge = (Edge) invocationOnMock.getArguments()[1];
            final Connection connection = (Connection) invocationOnMock.getArguments()[2];
            setConnectionTargetNodeCommand = new SetConnectionTargetNodeCommand(node,
                                                                                edge,
                                                                                connection);
            return setConnectionTargetNodeCommand;
        }).when(canvasCommandFactory).setTargetNode(any(Node.class),
                                                    any(Edge.class),
                                                    any(Connection.class));
        when(commandManager.allow(eq(canvasHandler),
                                  eq(setConnectionSourceNodeCommand))).thenReturn(result);
        when(commandManager.execute(eq(canvasHandler),
                                    eq(setConnectionSourceNodeCommand))).thenReturn(result);
        when(commandManager.allow(eq(canvasHandler),
                                  eq(setConnectionTargetNodeCommand))).thenReturn(result);
        when(commandManager.execute(eq(canvasHandler),
                                    eq(setConnectionTargetNodeCommand))).thenReturn(result);
        this.tested = new ConnectionAcceptorControlImpl(canvasCommandFactory, cancelCanvasActionEvent);
        this.tested.setCommandManagerProvider(() -> commandManager);
    }

    @Test
    public void testInit() {
        tested.init(canvasHandler);
        assertEquals(canvasHandler,
                     tested.getCanvasHandler());
        verify(canvasView,
               times(1)).setConnectionAcceptor(any(IConnectionAcceptor.class));
        verify(canvasView,
               never()).setDockingAcceptor(any(IDockingAcceptor.class));
        verify(canvasView,
               never()).setContainmentAcceptor(any(IContainmentAcceptor.class));
    }

    @Test
    public void testAllowSource() {
        tested.init(canvasHandler);
        final boolean allow = tested.allowSource(node,
                                                 edge,
                                                 connection);
        assertTrue(allow);
        verify(commandManager,
               times(1)).allow(eq(canvasHandler),
                               eq(setConnectionSourceNodeCommand));
        verify(commandManager,
               never()).execute(any(AbstractCanvasHandler.class),
                                any(SetConnectionSourceNodeCommand.class));
        assertEquals(node,
                     setConnectionSourceNodeCommand.getNode());
        assertEquals(edge,
                     setConnectionSourceNodeCommand.getEdge());
        assertEquals(connection,
                     setConnectionSourceNodeCommand.getConnection());
    }

    @Test
    public void testSkipAllowSourceAsNoChanges() {
        when(edge.getSourceNode()).thenReturn(node);
        when(edgeContent.getSourceConnection()).thenReturn(Optional.of(connection));
        tested.init(canvasHandler);
        final boolean allow = tested.allowSource(node,
                                                 edge,
                                                 connection);
        assertTrue(allow);
        verify(commandManager,
               never()).allow(eq(canvasHandler),
                              eq(setConnectionSourceNodeCommand));
        verify(commandManager,
               never()).execute(any(AbstractCanvasHandler.class),
                                any(SetConnectionSourceNodeCommand.class));
    }

    @Test
    public void testAllowTarget() {
        tested.init(canvasHandler);
        final boolean allow = tested.allowTarget(node,
                                                 edge,
                                                 connection);
        assertTrue(allow);
        verify(commandManager,
               times(1)).allow(eq(canvasHandler),
                               eq(setConnectionTargetNodeCommand));
        verify(commandManager,
               never()).execute(any(AbstractCanvasHandler.class),
                                any(SetConnectionTargetNodeCommand.class));
        assertEquals(node,
                     setConnectionTargetNodeCommand.getNode());
        assertEquals(edge,
                     setConnectionTargetNodeCommand.getEdge());
        assertEquals(connection,
                     setConnectionTargetNodeCommand.getConnection());
    }

    @Test
    public void testSkipAllowTargetAsNoChanges() {
        when(edge.getTargetNode()).thenReturn(node);
        when(edgeContent.getTargetConnection()).thenReturn(Optional.of(connection));
        tested.init(canvasHandler);
        final boolean allow = tested.allowTarget(node,
                                                 edge,
                                                 connection);
        assertTrue(allow);
        verify(commandManager,
               never()).allow(eq(canvasHandler),
                              eq(setConnectionSourceNodeCommand));
        verify(commandManager,
               never()).execute(any(AbstractCanvasHandler.class),
                                any(SetConnectionSourceNodeCommand.class));
    }

    @Test
    public void testAcceptSource() {
        tested.init(canvasHandler);
        final boolean allow = tested.acceptSource(node,
                                                  edge,
                                                  connection);
        assertTrue(allow);
        verify(commandManager,
               times(1)).execute(eq(canvasHandler),
                                 eq(setConnectionSourceNodeCommand));
        verify(commandManager,
               never()).allow(any(AbstractCanvasHandler.class),
                              any(SetConnectionSourceNodeCommand.class));
        assertEquals(node,
                     setConnectionSourceNodeCommand.getNode());
        assertEquals(edge,
                     setConnectionSourceNodeCommand.getEdge());
        assertEquals(connection,
                     setConnectionSourceNodeCommand.getConnection());
    }

    @Test
    public void testAcceptTarget() {
        tested.init(canvasHandler);
        final boolean allow = tested.acceptTarget(node,
                                                  edge,
                                                  connection);
        assertTrue(allow);
        verify(commandManager,
               times(1)).execute(eq(canvasHandler),
                                 eq(setConnectionTargetNodeCommand));
        verify(commandManager,
               never()).allow(any(AbstractCanvasHandler.class),
                              any(SetConnectionSourceNodeCommand.class));
        assertEquals(node,
                     setConnectionTargetNodeCommand.getNode());
        assertEquals(edge,
                     setConnectionTargetNodeCommand.getEdge());
        assertEquals(connection,
                     setConnectionTargetNodeCommand.getConnection());
    }

    @Test
    public void testAcceptSourceAsNewConnection() {
        when(edge.getSourceNode()).thenReturn(node);
        tested.init(canvasHandler);
        final boolean allow = tested.acceptSource(node,
                                                  edge,
                                                  connection);
        assertTrue(allow);
        verify(commandManager,
               times(1)).execute(eq(canvasHandler),
                                 eq(setConnectionSourceNodeCommand));
        verify(commandManager,
               never()).allow(any(AbstractCanvasHandler.class),
                              any(SetConnectionSourceNodeCommand.class));
        assertEquals(node,
                     setConnectionSourceNodeCommand.getNode());
        assertEquals(edge,
                     setConnectionSourceNodeCommand.getEdge());
        assertEquals(connection,
                     setConnectionSourceNodeCommand.getConnection());
    }

    @Test
    public void testAcceptTargetNotNewConnection() {
        when(edge.getTargetNode()).thenReturn(node);
        tested.init(canvasHandler);
        final boolean allow = tested.acceptTarget(node,
                                                  edge,
                                                  connection);
        assertTrue(allow);
        verify(commandManager,
               times(1)).execute(eq(canvasHandler),
                                 eq(setConnectionTargetNodeCommand));
        verify(commandManager,
               never()).allow(any(AbstractCanvasHandler.class),
                              any(SetConnectionTargetNodeCommand.class));
        assertEquals(node,
                     setConnectionTargetNodeCommand.getNode());
        assertEquals(edge,
                     setConnectionTargetNodeCommand.getEdge());
        assertEquals(connection,
                     setConnectionTargetNodeCommand.getConnection());
    }

    @Test
    public void testSkipAcceptSourceAsNoChanges() {
        when(edge.getSourceNode()).thenReturn(node);
        when(edgeContent.getSourceConnection()).thenReturn(Optional.of(connection));
        tested.init(canvasHandler);
        final boolean accept = tested.acceptSource(node,
                                                   edge,
                                                   connection);
        assertTrue(accept);
        verify(commandManager,
               never()).allow(eq(canvasHandler),
                              eq(setConnectionSourceNodeCommand));
        verify(commandManager,
               never()).execute(any(AbstractCanvasHandler.class),
                                any(SetConnectionSourceNodeCommand.class));
    }

    @Test
    public void testSkipAcceptTargetAsNoChanges() {
        when(edge.getTargetNode()).thenReturn(node);
        when(edgeContent.getTargetConnection()).thenReturn(Optional.of(connection));
        tested.init(canvasHandler);
        final boolean accept = tested.acceptTarget(node,
                                                   edge,
                                                   connection);
        assertTrue(accept);
        verify(commandManager,
               never()).allow(eq(canvasHandler),
                              eq(setConnectionSourceNodeCommand));
        verify(commandManager,
               never()).execute(any(AbstractCanvasHandler.class),
                                any(SetConnectionSourceNodeCommand.class));
    }

    @Test
    public void testCreateConnections() {
        // New default connection for a graph element.
        Element element = mock(Element.class);
        View<?> content = mock(View.class);
        BoundsImpl bounds = new BoundsImpl(new BoundImpl(0d,
                                                         0d),
                                           new BoundImpl(10d,
                                                         20d));
        when(element.getContent()).thenReturn(content);
        when(content.getBounds()).thenReturn(bounds);
        MagnetConnection c1 =
                ConnectionAcceptorControlImpl.createConnection(element);
        assertEquals(5,
                     c1.getLocation().getX(),
                     0);
        assertEquals(10,
                     c1.getLocation().getY(),
                     0);
        assertEquals(MagnetConnection.MAGNET_CENTER,
                     c1.getMagnetIndex().getAsInt());
        assertFalse(c1.isAuto());

        // New default connection for wires.
        WiresConnection wiresConnection = mock(WiresConnection.class);
        when(wiresConnection.isAutoConnection()).thenReturn(true);
        WiresMagnet wiresMagnet = mock(WiresMagnet.class);
        when(wiresMagnet.getX()).thenReturn(122d);
        when(wiresMagnet.getY()).thenReturn(543d);
        when(wiresMagnet.getIndex()).thenReturn(7);
        MagnetConnection c2 =
                ConnectionAcceptorControlImpl.createConnection(wiresConnection,
                                                               wiresMagnet);
        assertEquals(122,
                     c2.getLocation().getX(),
                     0);
        assertEquals(543,
                     c2.getLocation().getY(),
                     0);
        assertEquals(7,
                     c2.getMagnetIndex().getAsInt());
        assertTrue(c2.isAuto());

        // Connections (view magnets) can be nullified.
        assertNull(ConnectionAcceptorControlImpl.createConnection(null));
        assertNull(ConnectionAcceptorControlImpl.createConnection(wiresConnection,
                                                                  null));
        assertNull(ConnectionAcceptorControlImpl.createConnection(null,
                                                                  null));
    }

    @Test
    public void onKeyDownEventTest() {
        InOrder inOrder = inOrder(cancelCanvasActionEvent);

        tested.init(canvasHandler);
        tested.onKeyDownEvent(KeyboardEvent.Key.ESC);
        tested.onKeyDownEvent(KeyboardEvent.Key.CONTROL);
        tested.onKeyDownEvent(KeyboardEvent.Key.ARROW_RIGHT);

        inOrder.verify(cancelCanvasActionEvent, times(1)).fire(any(CancelCanvasAction.class));
        inOrder.verify(cancelCanvasActionEvent, never()).fire(any(CancelCanvasAction.class));
        inOrder.verify(cancelCanvasActionEvent, never()).fire(any(CancelCanvasAction.class));
    }
}
