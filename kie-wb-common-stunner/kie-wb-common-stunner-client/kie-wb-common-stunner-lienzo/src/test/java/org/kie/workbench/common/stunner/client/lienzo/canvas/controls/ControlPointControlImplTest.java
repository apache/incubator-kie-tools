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

import java.util.Arrays;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddControlPointCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteControlPointCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateControlPointPositionCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.controlpoint.CanvasControlPointDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.controlpoint.CanvasControlPointDragEndEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.controlpoint.CanvasControlPointDragStartEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPointImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ControlPointControlImplTest extends AbstractCanvasControlTest {

    private ControlPointControlImpl controlPointControl;

    private ControlPoint controlPoint1;

    private CanvasSelectionEvent canvasSelectionEvent;

    private CanvasControlPointDragStartEvent canvasControlPointDragStartEvent;

    private CanvasControlPointDragEndEvent canvasControlPointDragEndEvent;

    private CanvasControlPointDoubleClickEvent canvasControlPointDoubleClickEvent;

    private static final String EDGE_UUID = UUID.uuid();

    @Mock
    private Edge edge;

    @Mock
    private ConnectorShape connectorShape;

    @Mock
    private ViewConnector viewConnector;

    private Point2D initialControlPointPosition;

    private Point2D newControlPointPosition;

    @Before
    public void setUp() {
        super.setUp();

        initialControlPointPosition = new Point2D(0, 0);
        newControlPointPosition = new Point2D(10, 10);
        controlPoint1 = spy(new ControlPointImpl(initialControlPointPosition));
        canvasSelectionEvent = new CanvasSelectionEvent(canvasHandler, EDGE_UUID);
        canvasControlPointDragStartEvent = new CanvasControlPointDragStartEvent(initialControlPointPosition);
        canvasControlPointDragEndEvent = new CanvasControlPointDragEndEvent(newControlPointPosition);
        canvasControlPointDoubleClickEvent = new CanvasControlPointDoubleClickEvent(initialControlPointPosition);
        controlPointControl = spy(new ControlPointControlImpl(canvasCommandFactory));

        when(canvas.getShape(EDGE_UUID)).thenReturn(connectorShape);
        when(graphIndex.get(EDGE_UUID)).thenReturn(edge);
        when(graphIndex.getEdge(EDGE_UUID)).thenReturn(edge);
        when(edge.getContent()).thenReturn(viewConnector);
        when(edge.getUUID()).thenReturn(EDGE_UUID);
        when(connectorShape.getShapeView()).thenReturn(shapeView);
        when(viewConnector.getControlPoints()).thenReturn(Arrays.asList(controlPoint1));

        controlPointControl.init(canvasHandler);
        controlPointControl.setCommandManagerProvider(commandManagerProvider);
        controlPointControl.register(edge);
    }

    @Test
    public void testAddControlPoint() {
        controlPointControl.onCanvasSelectionEvent(canvasSelectionEvent);
        controlPointControl.addControlPoint(edge, controlPoint1);

        verify(canvasCommandFactory).addControlPoint(edge, controlPoint1);
        ArgumentCaptor<AddControlPointCommand> commandArgumentCaptor = ArgumentCaptor.forClass(AddControlPointCommand.class);
        verify(commandManager).execute(eq(canvasHandler), commandArgumentCaptor.capture());
    }

    @Test
    public void testMoveControlPoint() {
        controlPointControl.onCanvasSelectionEvent(canvasSelectionEvent);
        controlPointControl.onControlPointDragStartEvent(canvasControlPointDragStartEvent);
        controlPointControl.onCanvasControlPointDragEndEvent(canvasControlPointDragEndEvent);

        verify(canvasCommandFactory).updateControlPointPosition(edge, controlPoint1, newControlPointPosition);
        ArgumentCaptor<UpdateControlPointPositionCommand> commandArgumentCaptor = ArgumentCaptor.forClass(UpdateControlPointPositionCommand.class);
        verify(commandManager).execute(eq(canvasHandler), commandArgumentCaptor.capture());
    }

    @Test
    public void testMoveNullControlPoint() {
        controlPointControl.onCanvasControlPointDragEndEvent(canvasControlPointDragEndEvent);
        verify(canvasCommandFactory, never()).updateControlPointPosition(any(), any(), any());
        try {
            controlPointControl.moveControlPoint(null, newControlPointPosition);
        } catch (IllegalStateException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testRemoveControlPoint() {
        controlPointControl.onCanvasSelectionEvent(canvasSelectionEvent);
        controlPointControl.removeControlPoint(controlPoint1);

        verify(canvasCommandFactory).deleteControlPoint(edge, controlPoint1);
        ArgumentCaptor<DeleteControlPointCommand> commandArgumentCaptor = ArgumentCaptor.forClass(DeleteControlPointCommand.class);
        verify(commandManager).execute(eq(canvasHandler), commandArgumentCaptor.capture());
    }

    @Test
    public void testOnCanvasControlPointDoubleClickEvent() {
        controlPointControl.onCanvasSelectionEvent(canvasSelectionEvent);
        controlPointControl.onCanvasControlPointDoubleClickEvent(canvasControlPointDoubleClickEvent);

        verify(canvasCommandFactory).deleteControlPoint(edge, controlPoint1);
        ArgumentCaptor<DeleteControlPointCommand> commandArgumentCaptor = ArgumentCaptor.forClass(DeleteControlPointCommand.class);
        verify(commandManager).execute(eq(canvasHandler), commandArgumentCaptor.capture());
    }

    @Test
    public void testOnCanvasControlPointDragEndEvent() {
        controlPointControl.onCanvasSelectionEvent(canvasSelectionEvent);
        controlPointControl.onControlPointDragStartEvent(canvasControlPointDragStartEvent);
        controlPointControl.onCanvasControlPointDragEndEvent(canvasControlPointDragEndEvent);
        verify(controlPointControl).moveControlPoint(controlPoint1, newControlPointPosition);
    }

    @Test
    public void testOnCanvasSelectionEvent() {
        controlPointControl.onCanvasSelectionEvent(canvasSelectionEvent);
        assertEquals(controlPointControl.getSelectedEdge(), edge);
    }

    @Test
    public void testOnControlPointDragStartEvent() {
        controlPointControl.onCanvasSelectionEvent(canvasSelectionEvent);
        controlPointControl.onControlPointDragStartEvent(canvasControlPointDragStartEvent);
        assertEquals(controlPointControl.getSelectedControlPoint(), controlPoint1);
    }

    @Test
    public void testClear() {
        controlPointControl.clear();
        verify(controlPointControl).doClear();
        assertNull(controlPointControl.getSelectedEdge());
        assertNull(controlPointControl.getSelectedControlPoint());
        assertEquals(commandManagerProvider,
                     controlPointControl.getCommandManagerProvider());
    }

    @Test
    public void testDestroy() {
        controlPointControl.destroy();
        verify(controlPointControl).doDestroy();
        verify(controlPointControl).clear();
        assertNull(controlPointControl.getSelectedEdge());
        assertNull(controlPointControl.getSelectedControlPoint());
        assertNull(controlPointControl.getCommandManagerProvider());
    }

}