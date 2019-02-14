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

import java.util.function.Function;

import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
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
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ControlPointControlImplTest {

    private static final String EDGE_UUID = "edge1";
    private static final ControlPoint CONTROL_POINT = ControlPoint.build(1, 1);

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private WiresCanvas canvas;

    @Mock
    private WiresManager wiresManager;

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

    @Mock
    private Graph graph;

    @Mock
    private DefinitionSet graphContent;

    @Mock
    private EventSourceMock<CanvasSelectionEvent> selectionEvent;

    private ControlPointControlImpl tested;
    private Layer layer;
    private Edge edge;
    private ViewConnectorImpl content;
    private ControlPoint[] controlPoints;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        layer = spy(new Layer());
        edge = new EdgeImpl<>(EDGE_UUID);
        content = new ViewConnectorImpl(mock(Object.class),
                                        Bounds.create());
        edge.setContent(content);
        controlPoints = new ControlPoint[]{CONTROL_POINT};
        content.setControlPoints(controlPoints);
        Group connectorGroup = new Group();
        layer.add(connectorGroup);
        when(connector.getGroup()).thenReturn(connectorGroup);
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
        when(canvas.getWiresManager()).thenReturn(wiresManager);
        when(canvas.getShape(EDGE_UUID)).thenReturn(connectorShape);
        when(connectorShape.getShapeView()).thenReturn(connector);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(graph.getContent()).thenReturn(graphContent);
        when(diagram.getGraph()).thenReturn(graph);
        when(commandManager.allow(eq(canvasHandler), any(Command.class)))
                .thenReturn(CanvasCommandResultBuilder.SUCCESS);
        tested = new ControlPointControlImpl(canvasCommandFactory, selectionEvent);
        tested.setCommandManagerProvider(() -> commandManager);
    }

    @Test
    public void testInit() {
        tested.init(canvasHandler);
        verify(wiresManager, times(1)).setControlPointsAcceptor(any(IControlPointsAcceptor.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddControlPoint() {
        final int index = 1;
        final ControlPoint controlPoint = ControlPoint.build(2, 2);

        CanvasCommand<AbstractCanvasHandler> addControlPointCommand = mock(CanvasCommand.class);
        doReturn(addControlPointCommand).when(canvasCommandFactory).addControlPoint(eq(edge),
                                                                                    eq(controlPoint),
                                                                                    eq(index));
        tested.init(canvasHandler);
        tested.addControlPoint(edge, controlPoint, index);
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
                                                                                          eq(0));
        tested.init(canvasHandler);
        tested.deleteControlPoint(edge, 0);
        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        verify(commandManager, times(1)).execute(eq(canvasHandler),
                                                 commandArgumentCaptor.capture());
        Command<AbstractCanvasHandler, CanvasViolation> command = commandArgumentCaptor.getValue();
        assertEquals(deleteControlPointCommand, command);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveControlPoint() {
        ControlPoint[] cps = new ControlPoint[]{ControlPoint.build(2, 2)};
        CanvasCommand<AbstractCanvasHandler> moveControlPointCommand = mock(CanvasCommand.class);
        doReturn(moveControlPointCommand).when(canvasCommandFactory).updateControlPointPosition(eq(edge),
                                                                                                eq(cps));
        tested.init(canvasHandler);
        tested.updateControlPoints(edge, cps);
        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        verify(commandManager, times(1)).execute(eq(canvasHandler),
                                                 commandArgumentCaptor.capture());
        Command<AbstractCanvasHandler, CanvasViolation> command = commandArgumentCaptor.getValue();
        assertEquals(moveControlPointCommand, command);
    }

    @Test
    public void testStunnerControlPointsAcceptorAdd() {
        final HandlerRegistration mouseUpHandlerRegistration = mock(HandlerRegistration.class);
        final NodeMouseUpHandler[] mouseUpHandlerCaptured = new NodeMouseUpHandler[1];
        doAnswer(invocationOnMock -> {
            mouseUpHandlerCaptured[0] = (NodeMouseUpHandler) invocationOnMock.getArguments()[0];
            mouseUpHandlerCaptured[0].onNodeMouseUp(mock(NodeMouseUpEvent.class));
            return mouseUpHandlerRegistration;
        }).when(layer).addNodeMouseUpHandler(any(NodeMouseUpHandler.class));
        ControlPointControl control = mock(ControlPointControl.class);
        ControlPointControlImpl.StunnerControlPointsAcceptor acceptor = createStunnerControlPointsAcceptor(control);

        boolean addResult = acceptor.add(connector, 1, new com.ait.lienzo.client.core.types.Point2D(2, 2));

        assertTrue(addResult);
        verify(connector, times(1)).addControlPoint(eq(2d),
                                                    eq(2d),
                                                    eq(1));
        verify(control, times(1)).addControlPoint(eq(edge),
                                                  eq(ControlPoint.build(2, 2)),
                                                  eq(0));
    }

    @Test
    public void testStunnerControlPointsAcceptorDelete() {
        ControlPointControl control = mock(ControlPointControl.class);
        ControlPointControlImpl.StunnerControlPointsAcceptor acceptor = createStunnerControlPointsAcceptor(control);
        final boolean deleteResult = acceptor.delete(connector, 1);
        assertTrue(deleteResult);
        verify(control, times(1)).deleteControlPoint(eq(edge),
                                                     eq(0));
    }

    @Test
    public void testStunnerControlPointsAcceptorMove() {
        ControlPointControl control = mock(ControlPointControl.class);
        ControlPointControlImpl.StunnerControlPointsAcceptor acceptor = createStunnerControlPointsAcceptor(control);
        Point2DArray locationArray = new Point2DArray(new com.ait.lienzo.client.core.types.Point2D(0, 0),
                                                      new com.ait.lienzo.client.core.types.Point2D(5, 5),
                                                      new com.ait.lienzo.client.core.types.Point2D(10, 10));
        final boolean moveResult = acceptor.move(connector, locationArray);
        assertTrue(moveResult);
        ArgumentCaptor<ControlPoint[]> controlPointsExpected = ArgumentCaptor.forClass(ControlPoint[].class);
        verify(control, times(1)).updateControlPoints(eq(edge),
                                                      controlPointsExpected.capture());
        ControlPoint[] cps = controlPointsExpected.getValue();
        assertNotNull(cps);
        assertEquals(1, cps.length);
        assertEquals(ControlPoint.build(5, 5), cps[0]);
    }

    private ControlPointControlImpl.StunnerControlPointsAcceptor createStunnerControlPointsAcceptor(ControlPointControl controlPointControl) {
        Function<String, Edge> connectorSupplier = uuid -> uuid.equals(EDGE_UUID) ? edge : null;
        return new ControlPointControlImpl.StunnerControlPointsAcceptor(controlPointControl,
                                                                        connectorSupplier);
    }
}