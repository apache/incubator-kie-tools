/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ControlPointControlImplTest {

    private static final String EDGE_UUID = "edge1";
    private static final ControlPoint CONTROL_POINT = ControlPoint.build(1, 3, 0);
    private static final Bounds GRAPH_BOUNDS = new BoundsImpl(new BoundImpl(0d, 0d), new BoundImpl(100d, 100d));

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
    private Metadata metadata;
    @Mock
    private WiresConnectorView connector;
    @Mock
    private IControlHandleList wiresPointHandles;
    @Mock
    private IControlHandle wiresPointHandle;
    @Mock
    private IPrimitive wiresPointPrimitive;
    @Mock
    private Shape connectorShape;

    private Edge edge;
    private ViewConnectorImpl content;
    private List<ControlPoint> controlPointList;

    private ControlPointControlImpl tested;

    @Mock
    private Graph graph;

    @Mock
    private DefinitionSet graphContent;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        edge = new EdgeImpl<>(EDGE_UUID);
        content = new ViewConnectorImpl(mock(Object.class),
                                        BoundsImpl.build());
        edge.setContent(content);
        controlPointList = Collections.singletonList(CONTROL_POINT);
        content.setControlPoints(controlPointList);
        when(connector.uuid()).thenReturn(EDGE_UUID);
        when(connector.getUUID()).thenReturn(EDGE_UUID);
        when(connector.getPointHandles()).thenReturn(wiresPointHandles);
        when(wiresPointHandles.isEmpty()).thenReturn(false);
        when(wiresPointHandles.size()).thenReturn(3);
        when(wiresPointHandles.getHandle(eq(0))).thenReturn(mock(IControlHandle.class));
        when(wiresPointHandles.getHandle(eq(1))).thenReturn(wiresPointHandle);
        when(wiresPointHandles.getHandle(eq(2))).thenReturn(mock(IControlHandle.class));
        when(wiresPointHandle.getControl()).thenReturn(wiresPointPrimitive);
        when(wiresPointPrimitive.getX()).thenReturn(CONTROL_POINT.getLocation().getX());
        when(wiresPointPrimitive.getY()).thenReturn(CONTROL_POINT.getLocation().getY());
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvas.getShape(EDGE_UUID)).thenReturn(connectorShape);
        when(connectorShape.getShapeView()).thenReturn(connector);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(graph.getContent()).thenReturn(graphContent);
        when(diagram.getGraph()).thenReturn(graph);
        when(graphContent.getBounds()).thenReturn(GRAPH_BOUNDS);
        when(commandManager.allow(eq(canvasHandler), any(Command.class)))
                .thenReturn(CanvasCommandResultBuilder.SUCCESS);
        tested = new ControlPointControlImpl(canvasCommandFactory);
        tested.setCommandManagerProvider(() -> commandManager);
    }

    @Test
    public void testInit() {
        tested.init(canvasHandler);
        verify(canvasView, times(1)).setControlPointsAcceptor(any(IControlPointsAcceptor.class));
    }

    @Test
    public void testRegister() {
        tested.init(canvasHandler);
        tested.register(edge);

        verify(connector).setDragBounds(GRAPH_BOUNDS.getUpperLeft().getX() + ControlPointControlImpl.DRAG_BOUNDS_MARGIN,
                                        GRAPH_BOUNDS.getUpperLeft().getY() + ControlPointControlImpl.DRAG_BOUNDS_MARGIN,
                                        GRAPH_BOUNDS.getLowerRight().getX() + ControlPointControlImpl.DRAG_BOUNDS_MARGIN,
                                        GRAPH_BOUNDS.getLowerRight().getY() + ControlPointControlImpl.DRAG_BOUNDS_MARGIN);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddControlPoint() {
        final ControlPoint controlPoint = ControlPoint.build(1, 3, 0);

        CanvasCommand<AbstractCanvasHandler> addControlPointCommand = mock(CanvasCommand.class);
        doReturn(addControlPointCommand).when(canvasCommandFactory).addControlPoint(eq(edge),
                                                                                    eq(controlPoint));
        tested.init(canvasHandler);
        tested.addControlPoints(edge, controlPoint);
        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        verify(commandManager, times(1)).execute(eq(canvasHandler),
                                                 commandArgumentCaptor.capture());
        Command<AbstractCanvasHandler, CanvasViolation> command = commandArgumentCaptor.getValue();
        assertEquals(addControlPointCommand, command);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteControlPoint() {
        CanvasCommand<AbstractCanvasHandler> deleteControlPointCommand = mock(CanvasCommand.class);
        doReturn(deleteControlPointCommand).when(canvasCommandFactory).deleteControlPoint(eq(edge),
                                                                                          eq(CONTROL_POINT));
        tested.init(canvasHandler);
        tested.removeControlPoint(edge, CONTROL_POINT);
        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        verify(commandManager, times(1)).execute(eq(canvasHandler),
                                                 commandArgumentCaptor.capture());
        Command<AbstractCanvasHandler, CanvasViolation> command = commandArgumentCaptor.getValue();
        assertEquals(deleteControlPointCommand, command);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveControlPoint() {
        Point2D location = Point2D.create(100, 200);
        CanvasCommand<AbstractCanvasHandler> moveControlPointCommand = mock(CanvasCommand.class);
        doReturn(moveControlPointCommand).when(canvasCommandFactory).updateControlPointPosition(eq(edge),
                                                                                                eq(CONTROL_POINT),
                                                                                                eq(location));
        tested.init(canvasHandler);
        tested.moveControlPoints(edge,
                                 Collections.singletonMap(CONTROL_POINT, location));
        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        verify(commandManager, times(1)).execute(eq(canvasHandler),
                                                 commandArgumentCaptor.capture());
        Command<AbstractCanvasHandler, CanvasViolation> command = commandArgumentCaptor.getValue();
        assertEquals(moveControlPointCommand, command);
    }

    @Test
    public void testStunnerControlPointsAcceptorAdd() {
        ControlPointControl control = mock(ControlPointControl.class);
        ControlPointControlImpl.StunnerControlPointsAcceptor acceptor = createStunnerControlPointsAcceptor(control);
        ControlPoint cp = ControlPoint.build(1, 3, 0);
        final boolean addResult = acceptor.add(connector, 1, new com.ait.lienzo.client.core.types.Point2D(1, 3));
        assertTrue(addResult);
        verify(control, times(1)).addControlPoints(eq(edge),
                                                   eq(cp));
    }

    @Test
    public void testStunnerControlPointsAcceptorDelete() {
        ControlPointControl control = mock(ControlPointControl.class);
        ControlPointControlImpl.StunnerControlPointsAcceptor acceptor = createStunnerControlPointsAcceptor(control);
        final boolean deleteResult = acceptor.delete(connector, 1);
        assertFalse(deleteResult);
        verify(control, times(1)).removeControlPoint(eq(edge),
                                                     eq(CONTROL_POINT));
    }

    @Test
    public void testStunnerControlPointsAcceptorMove() {
        ControlPointControl control = mock(ControlPointControl.class);
        ControlPointControlImpl.StunnerControlPointsAcceptor acceptor = createStunnerControlPointsAcceptor(control);
        Point2D location = new Point2D(200, 500);
        Point2DArray locationArray =
                new Point2DArray(new com.ait.lienzo.client.core.types.Point2D(0,
                                                                              0),
                                 new com.ait.lienzo.client.core.types.Point2D(location.getX(),
                                                                              location.getY()),
                                 new com.ait.lienzo.client.core.types.Point2D(1000,
                                                                              2000));
        final boolean moveResult = acceptor.move(connector, locationArray);
        assertTrue(moveResult);
        verify(control, times(1)).moveControlPoints(eq(edge),
                                                    eq(Collections.singletonMap(CONTROL_POINT, location)));
    }

    private ControlPointControlImpl.StunnerControlPointsAcceptor createStunnerControlPointsAcceptor(ControlPointControl controlPointControl) {
        Function<String, Edge> connectorSupplier = uuid -> uuid.equals(EDGE_UUID) ? edge : null;
        return new ControlPointControlImpl.StunnerControlPointsAcceptor(controlPointControl,
                                                                        connectorSupplier);
    }
}