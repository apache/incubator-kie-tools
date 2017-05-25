/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.SetConnectionSourceNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Magnet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

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
    private Edge<View<?>, Node> edge;
    @Mock
    private Magnet magnet;

    private ConnectionAcceptorControlImpl tested;
    private SetConnectionSourceNodeCommand setConnectionSourceNodeCommand;
    private SetConnectionTargetNodeCommand setConnectionTargetNodeCommand;

    private final CommandResult<CanvasViolation> result = CanvasCommandResultBuilder.SUCCESS;

    @Before
    @SuppressWarnings("uncheecked")
    public void setup() {
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        when(canvas.getView()).thenReturn(canvasView);
        doAnswer(invocationOnMock -> {
            final Node node = (Node) invocationOnMock.getArguments()[0];
            final Edge edge = (Edge) invocationOnMock.getArguments()[1];
            final Magnet magnet = (Magnet) invocationOnMock.getArguments()[2];
            final Boolean isNewConnection = (Boolean) invocationOnMock.getArguments()[3];
            setConnectionSourceNodeCommand = new SetConnectionSourceNodeCommand(node,
                                                                                edge,
                                                                                magnet,
                                                                                isNewConnection);
            return setConnectionSourceNodeCommand;
        }).when(canvasCommandFactory).setSourceNode(any(Node.class),
                                                    any(Edge.class),
                                                    any(Magnet.class),
                                                    anyBoolean());
        doAnswer(invocationOnMock -> {
            final Node node = (Node) invocationOnMock.getArguments()[0];
            final Edge edge = (Edge) invocationOnMock.getArguments()[1];
            final Magnet magnet = (Magnet) invocationOnMock.getArguments()[2];
            final Boolean isNewConnection = (Boolean) invocationOnMock.getArguments()[3];
            setConnectionTargetNodeCommand = new SetConnectionTargetNodeCommand(node,
                                                                                edge,
                                                                                magnet,
                                                                                isNewConnection);
            return setConnectionTargetNodeCommand;
        }).when(canvasCommandFactory).setTargetNode(any(Node.class),
                                                    any(Edge.class),
                                                    any(Magnet.class),
                                                    anyBoolean());
        when(commandManager.allow(eq(canvasHandler),
                                  eq(setConnectionSourceNodeCommand))).thenReturn(result);
        when(commandManager.execute(eq(canvasHandler),
                                    eq(setConnectionSourceNodeCommand))).thenReturn(result);
        when(commandManager.allow(eq(canvasHandler),
                                  eq(setConnectionTargetNodeCommand))).thenReturn(result);
        when(commandManager.execute(eq(canvasHandler),
                                    eq(setConnectionTargetNodeCommand))).thenReturn(result);
        this.tested = new ConnectionAcceptorControlImpl(canvasCommandFactory);
        this.tested.setCommandManagerProvider(() -> commandManager);
    }

    @Test
    public void testEnable() {
        tested.enable(canvasHandler);
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
    public void testDisable() {
        tested.enable(canvasHandler);
        tested.disable();
        assertNull(tested.getCanvasHandler());
        verify(canvasView,
               times(1)).setConnectionAcceptor(eq(IConnectionAcceptor.NONE));
        verify(canvasView,
               never()).setDockingAcceptor(any(IDockingAcceptor.class));
        verify(canvasView,
               never()).setContainmentAcceptor(any(IContainmentAcceptor.class));
    }

    @Test
    public void testAllowSource() {
        tested.enable(canvasHandler);
        final boolean allow = tested.allowSource(node,
                                                 edge,
                                                 magnet);
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
        assertEquals(magnet,
                     setConnectionSourceNodeCommand.getMagnet());
        assertTrue(setConnectionSourceNodeCommand.isNewConnection());
    }

    @Test
    public void testAllowTarget() {
        tested.enable(canvasHandler);
        final boolean allow = tested.allowTarget(node,
                                                 edge,
                                                 magnet);
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
        assertEquals(magnet,
                     setConnectionTargetNodeCommand.getMagnet());
        assertTrue(setConnectionTargetNodeCommand.isNewConnection());
    }

    @Test
    public void testAcceptSource() {
        tested.enable(canvasHandler);
        final boolean allow = tested.acceptSource(node,
                                                  edge,
                                                  magnet);
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
        assertEquals(magnet,
                     setConnectionSourceNodeCommand.getMagnet());
        assertTrue(setConnectionSourceNodeCommand.isNewConnection());
    }

    @Test
    public void testAcceptTarget() {
        tested.enable(canvasHandler);
        final boolean allow = tested.acceptTarget(node,
                                                  edge,
                                                  magnet);
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
        assertEquals(magnet,
                     setConnectionTargetNodeCommand.getMagnet());
        assertTrue(setConnectionTargetNodeCommand.isNewConnection());
    }

    @Test
    public void testAcceptSourceNotNewConnection() {
        when(edge.getSourceNode()).thenReturn(node);
        tested.enable(canvasHandler);
        final boolean allow = tested.acceptSource(node,
                                                  edge,
                                                  magnet);
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
        assertEquals(magnet,
                     setConnectionSourceNodeCommand.getMagnet());
        assertFalse(setConnectionSourceNodeCommand.isNewConnection());
    }

    @Test
    public void testAcceptTargetNotNewConnection() {
        when(edge.getTargetNode()).thenReturn(node);
        tested.enable(canvasHandler);
        final boolean allow = tested.acceptTarget(node,
                                                  edge,
                                                  magnet);
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
        assertEquals(magnet,
                     setConnectionTargetNodeCommand.getMagnet());
        assertFalse(setConnectionTargetNodeCommand.isNewConnection());
    }
}
