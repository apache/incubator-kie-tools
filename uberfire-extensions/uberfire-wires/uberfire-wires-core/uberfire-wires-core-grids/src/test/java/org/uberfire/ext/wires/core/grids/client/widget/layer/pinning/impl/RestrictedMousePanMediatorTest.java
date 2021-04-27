/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.mediator.IEventFilter;
import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class RestrictedMousePanMediatorTest {

    private RestrictedMousePanMediator mediator;

    @Before
    public void setUp() {
        mediator = spy(new RestrictedMousePanMediator());
    }

    @Test
    public void testGetLayerViewport() {

        final GridLayer layer = mock(GridLayer.class);
        final Viewport expectedViewport = mock(Viewport.class);

        doReturn(expectedViewport).when(layer).getViewport();
        doReturn(layer).when(mediator).getGridLayer();

        final Viewport actualViewport = mediator.getLayerViewport();

        assertEquals(expectedViewport,
                     actualViewport);
    }

    @Test
    public void testSetCursor() {

        final Viewport viewport = mock(Viewport.class);
        final DivElement divElement = mock(DivElement.class);
        final Style.Cursor cursor = mock(Style.Cursor.class);
        final Style style = mock(Style.class);

        doReturn(style).when(divElement).getStyle();
        doReturn(divElement).when(viewport).getElement();
        doReturn(viewport).when(mediator).getLayerViewport();

        mediator.setCursor(cursor);

        verify(style).setCursor(cursor);
    }

    @Test
    public void testCancel() throws Exception {
        final Viewport viewport = mock(Viewport.class);
        final DivElement divElement = mock(DivElement.class);
        final Style style = mock(Style.class);

        doReturn(style).when(divElement).getStyle();
        doReturn(divElement).when(viewport).getElement();
        doReturn(viewport).when(mediator).getLayerViewport();

        mediator.cancel();

        verify(style).setCursor(Style.Cursor.DEFAULT);
    }

    @Test
    public void testHandleEventMouseMoveDragging() throws Exception {
        final NodeMouseMoveEvent moveEvent = mock(NodeMouseMoveEvent.class);
        final Viewport viewport = mock(Viewport.class);
        final Transform transform = mock(Transform.class);
        final Transform inverseTransform = mock(Transform.class);
        final Scene scene = mock(Scene.class);

        doReturn(transform).when(transform).copy();

        doReturn(NodeMouseMoveEvent.getType()).when(moveEvent).getAssociatedType();

        doReturn(transform).when(viewport).getTransform();
        doReturn(scene).when(viewport).getScene();

        doReturn(viewport).when(mediator).getViewport();
        doReturn(true).when(mediator).isDragging();
        doReturn(inverseTransform).when(mediator).inverseTransform();

        mediator.handleEvent(moveEvent);

        verify(mediator).onMouseMove(eq(moveEvent));
    }

    @Test
    public void testHandleEventMouseMoveNotDragging() throws Exception {
        final NodeMouseMoveEvent moveEvent = mock(NodeMouseMoveEvent.class);

        doReturn(NodeMouseMoveEvent.getType()).when(moveEvent).getAssociatedType();

        doReturn(false).when(mediator).isDragging();

        mediator.handleEvent(moveEvent);

        verify(mediator,
               never()).onMouseMove(any(NodeMouseMoveEvent.class));
    }

    @Test
    public void testHandleEventMouseDownNoFilter() throws Exception {
        final NodeMouseDownEvent downEvent = mock(NodeMouseDownEvent.class);
        final DivElement element = mock(DivElement.class);
        final Style style = mock(Style.class);
        final Viewport viewport = mock(Viewport.class);

        doReturn(NodeMouseDownEvent.getType()).when(downEvent).getAssociatedType();

        doReturn(style).when(element).getStyle();

        doReturn(element).when(viewport).getElement();

        doReturn(viewport).when(mediator).getLayerViewport();
        doReturn(viewport).when(mediator).getViewport();

        mediator.handleEvent(downEvent);

        verify(mediator).onMouseDown(eq(downEvent));
    }

    @Test
    public void testHandleEventMouseDownDisabledFilter() throws Exception {
        final NodeMouseDownEvent downEvent = mock(NodeMouseDownEvent.class);
        final DivElement element = mock(DivElement.class);
        final Style style = mock(Style.class);
        final Viewport viewport = mock(Viewport.class);
        final IEventFilter iEventFilter = mock(IEventFilter.class);

        doReturn(NodeMouseDownEvent.getType()).when(downEvent).getAssociatedType();

        doReturn(style).when(element).getStyle();

        doReturn(element).when(viewport).getElement();

        doReturn(false).when(iEventFilter).isEnabled();

        doReturn(viewport).when(mediator).getLayerViewport();
        doReturn(viewport).when(mediator).getViewport();
        doReturn(iEventFilter).when(mediator).getEventFilter();

        mediator.handleEvent(downEvent);

        verify(mediator).onMouseDown(eq(downEvent));
    }

    @Test
    public void testHandleEventMouseDownEnabledFilterTestPassed() throws Exception {
        final NodeMouseDownEvent downEvent = mock(NodeMouseDownEvent.class);
        final DivElement element = mock(DivElement.class);
        final Style style = mock(Style.class);
        final Viewport viewport = mock(Viewport.class);
        final IEventFilter iEventFilter = mock(IEventFilter.class);

        doReturn(NodeMouseDownEvent.getType()).when(downEvent).getAssociatedType();

        doReturn(style).when(element).getStyle();

        doReturn(element).when(viewport).getElement();

        doReturn(true).when(iEventFilter).isEnabled();
        doReturn(true).when(iEventFilter).test(any());

        doReturn(viewport).when(mediator).getLayerViewport();
        doReturn(viewport).when(mediator).getViewport();
        doReturn(iEventFilter).when(mediator).getEventFilter();

        mediator.handleEvent(downEvent);

        verify(mediator).onMouseDown(eq(downEvent));
    }

    @Test
    public void testHandleEventMouseDownEnabledFilterTestNotPassed() throws Exception {
        final NodeMouseDownEvent downEvent = mock(NodeMouseDownEvent.class);
        final DivElement element = mock(DivElement.class);
        final Style style = mock(Style.class);
        final Viewport viewport = mock(Viewport.class);
        final IEventFilter iEventFilter = mock(IEventFilter.class);

        doReturn(NodeMouseDownEvent.getType()).when(downEvent).getAssociatedType();

        doReturn(style).when(element).getStyle();

        doReturn(element).when(viewport).getElement();

        doReturn(true).when(iEventFilter).isEnabled();
        doReturn(false).when(iEventFilter).test(any());

        doReturn(viewport).when(mediator).getLayerViewport();
        doReturn(viewport).when(mediator).getViewport();
        doReturn(iEventFilter).when(mediator).getEventFilter();

        mediator.handleEvent(downEvent);

        verify(mediator,
               never()).onMouseDown(any(NodeMouseDownEvent.class));
    }

    @Test
    public void testHandleEventMouseUpNotDragging() throws Exception {
        final NodeMouseUpEvent upEvent = mock(NodeMouseUpEvent.class);

        doReturn(NodeMouseUpEvent.getType()).when(upEvent).getAssociatedType();

        doReturn(false).when(mediator).isDragging();

        mediator.handleEvent(upEvent);

        verify(mediator,
               never()).onMouseUp(any(NodeMouseUpEvent.class));
        verify(mediator,
               never()).cancel();
    }

    @Test
    public void testHandleEventMouseUpDragging() throws Exception {
        final NodeMouseUpEvent upEvent = mock(NodeMouseUpEvent.class);
        final DivElement element = mock(DivElement.class);
        final Style style = mock(Style.class);
        final Viewport viewport = mock(Viewport.class);

        doReturn(NodeMouseUpEvent.getType()).when(upEvent).getAssociatedType();

        doReturn(style).when(element).getStyle();

        doReturn(element).when(viewport).getElement();

        doReturn(viewport).when(mediator).getLayerViewport();
        doReturn(true).when(mediator).isDragging();

        mediator.handleEvent(upEvent);

        verify(mediator).onMouseUp(eq(upEvent));
        verify(mediator).cancel();
    }

    @Test
    public void testOnMouseDown() throws Exception {
        final NodeMouseDownEvent downEvent = mock(NodeMouseDownEvent.class);
        final Transform transform = mock(Transform.class);
        final Viewport viewport = mock(Viewport.class);
        final DivElement element = mock(DivElement.class);
        final Style style = mock(Style.class);
        final ArgumentCaptor<Point2D> point = ArgumentCaptor.forClass(Point2D.class);

        doReturn(123).when(downEvent).getX();
        doReturn(987).when(downEvent).getY();
        doReturn(NodeMouseDownEvent.getType()).when(downEvent).getAssociatedType();

        doReturn(style).when(element).getStyle();

        doReturn(transform).when(transform).getInverse();

        doReturn(transform).when(viewport).getTransform();
        doReturn(element).when(viewport).getElement();

        doReturn(viewport).when(mediator).getViewport();
        doReturn(viewport).when(mediator).getLayerViewport();

        mediator.onMouseDown(downEvent);

        verify(transform).transform(point.capture(),
                                    point.capture());

        assertEquals("Expected X coordinate of the event",
                     123.0,
                     point.getAllValues().get(0).getX(),
                     0.0);
        assertEquals("Expected Y coordinate of the event",
                     987.0,
                     point.getAllValues().get(0).getY(),
                     0.0);

        assertEquals("Expected the same point to be transformed",
                     point.getAllValues().get(0),
                     point.getAllValues().get(1));

        verify(mediator).setCursor(Style.Cursor.MOVE);
    }

    @Test
    public void testOnMouseMoveBatch() throws Exception {
        testOnMouseMove(true);
    }

    @Test
    public void testOnMouseMoveDraw() throws Exception {
        testOnMouseMove(false);
    }

    private void testOnMouseMove(boolean batchDrawing) {
        final int xCoordinate = 123;
        final int yCoordinate = 987;
        final NodeMouseMoveEvent moveEvent = mock(NodeMouseMoveEvent.class);
        final Transform transform = mock(Transform.class);
        final Transform translated = mock(Transform.class);
        final Viewport viewport = mock(Viewport.class);
        final Scene scene = mock(Scene.class);

        doReturn(xCoordinate).when(moveEvent).getX();
        doReturn(yCoordinate).when(moveEvent).getY();
        doReturn(NodeMouseMoveEvent.getType()).when(moveEvent).getAssociatedType();

        doReturn(translated).when(transform).translate(anyDouble(),
                                                       anyDouble());
        doReturn(transform).when(transform).copy();

        doReturn(scene).when(viewport).getScene();
        doReturn(transform).when(viewport).getTransform();

        doReturn(viewport).when(mediator).getViewport();
        doReturn(transform).when(mediator).inverseTransform();
        doReturn(batchDrawing).when(mediator).isBatchDraw();

        mediator.onMouseMove(moveEvent);

        verify(transform).translate(xCoordinate,
                                    yCoordinate);

        verify(viewport).setTransform(translated);

        if (batchDrawing) {
            verify(scene).batch();
        } else {
            verify(scene).draw();
        }

        final int xMovement = 10;
        final int yMovement = 20;
        final NodeMouseMoveEvent secondMoveEvent = mock(NodeMouseMoveEvent.class);

        doReturn(xCoordinate + xMovement).when(secondMoveEvent).getX();
        doReturn(yCoordinate + yMovement).when(secondMoveEvent).getY();

        mediator.onMouseMove(secondMoveEvent);

        verify(transform).translate(xMovement,
                                    yMovement);

        verify(viewport, times(2)).setTransform(translated);

        if (batchDrawing) {
            verify(scene, times(2)).batch();
        } else {
            verify(scene, times(2)).draw();
        }
    }
}
