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

package com.ait.lienzo.client.widget.panel.mediators;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.mediator.IEventFilter;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.UIEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class RestrictedMousePanMediatorTest {

    @Mock
    private LienzoBoundsPanel panel;

    private RestrictedMousePanMediator mediator;

    @Before
    public void setUp() {
        mediator = spy(new RestrictedMousePanMediator(panel));
    }

    @Test
    public void testGetLayerViewport() {

        final Layer layer = mock(Layer.class);
        final Viewport expectedViewport = mock(Viewport.class);
        mediator.setViewport(expectedViewport);
        doReturn(expectedViewport).when(layer).getViewport();
        doReturn(layer).when(mediator).getLayer();

        final Viewport actualViewport = mediator.getViewport();

        assertEquals(expectedViewport,
                     actualViewport);
    }

    @Test
    public void testHandleEventMouseMoveDragging() throws Exception {
        final Viewport viewport = mock(Viewport.class);
        final HTMLDivElement element = mock(HTMLDivElement.class);
        final NodeMouseMoveEvent moveEvent = spy(new NodeMouseMoveEvent(element));
        final Transform transform = mock(Transform.class);
        final Transform inverseTransform = mock(Transform.class);
        final Scene scene = mock(Scene.class);
        final UIEvent uiEvent = mock(UIEvent.class);
        uiEvent.type = "mouseMove";

        doReturn(transform).when(transform).copy();
        doReturn(NodeMouseMoveEvent.getType()).when(moveEvent).getAssociatedType();
        doReturn(transform).when(viewport).getTransform();
        doReturn(scene).when(viewport).getScene();
        doReturn(viewport).when(mediator).getViewport();
        doReturn(true).when(mediator).isDragging();
        doReturn(inverseTransform).when(mediator).inverseTransform();

        mediator.handleEvent(moveEvent.getAssociatedType(), uiEvent, 100, 100);

        verify(mediator).onMouseMove(Matchers.eq(100), Matchers.eq(100));
    }

    @Test
    public void testHandleEventMouseMoveNotDragging() {
        final HTMLDivElement element = mock(HTMLDivElement.class);
        final NodeMouseMoveEvent moveEvent = spy(new NodeMouseMoveEvent(element));

        final UIEvent uiEvent = mock(UIEvent.class);
        uiEvent.type = "mouseMove";

        doReturn(NodeMouseMoveEvent.getType()).when(moveEvent).getAssociatedType();

        doReturn(false).when(mediator).isDragging();

        mediator.handleEvent(moveEvent.getAssociatedType(), uiEvent, 100, 100);

        verify(mediator,
               never()).onMouseMove(Matchers.eq(100), Matchers.eq(100));
    }


    @Test
    public void testHandleEventMouseDownNoFilter() {
        final HTMLDivElement element = mock(HTMLDivElement.class);
        final NodeMouseDownEvent downEvent = spy(new NodeMouseDownEvent(element));
        final Viewport viewport = mock(Viewport.class);
        final UIEvent uiEvent = mock(UIEvent.class);
        uiEvent.type  = "mouseDown";

        doReturn(NodeMouseDownEvent.getType()).when(downEvent).getAssociatedType();

        doReturn(element).when(viewport).getElement();

        doReturn(viewport).when(mediator).getViewport();

        mediator.handleEvent(downEvent.getAssociatedType(), uiEvent, 100, 100);
        verify(mediator).onMouseDown(Matchers.eq(100), Matchers.eq(100));



    }

    @Test
    public void testHandleEventMouseDownDisabledFilter() {
        final HTMLDivElement element = mock(HTMLDivElement.class);
        final NodeMouseDownEvent downEvent = spy(new NodeMouseDownEvent(element));
        final Viewport viewport = mock(Viewport.class);
        final IEventFilter iEventFilter = mock(IEventFilter.class);
        final UIEvent uiEvent = mock(UIEvent.class);
        uiEvent.type = "mouseDown";

        when(mediator.inverseTransform()).thenReturn(new Transform());
        when(mediator.getViewport()).thenReturn(viewport);
        when(viewport.getTransform()).thenReturn(new Transform());

        doReturn(NodeMouseDownEvent.getType()).when(downEvent).getAssociatedType();
        doReturn(element).when(viewport).getElement();
        doReturn(false).when(iEventFilter).isEnabled();
        doReturn(viewport).when(mediator).getViewport();
        doReturn(iEventFilter).when(mediator).getEventFilter();
        doReturn(true).when(mediator).isDragging();

        mediator.handleEvent(downEvent.getAssociatedType(), uiEvent, 100, 100);
        verify(mediator).onMouseDown(Matchers.eq(100), Matchers.eq(100));
    }

    @Test
    public void testHandleEventMouseDownEnabledFilterTestPassed() {
        final HTMLDivElement element = mock(HTMLDivElement.class);
        final NodeMouseDownEvent downEvent = spy(new NodeMouseDownEvent(element));
        final Viewport viewport = mock(Viewport.class);
        final IEventFilter iEventFilter = mock(IEventFilter.class);
        final UIEvent uiEvent = mock(UIEvent.class);
        uiEvent.type = "mouseDown";
        doReturn(NodeMouseDownEvent.getType()).when(downEvent).getAssociatedType();

        doReturn(element).when(viewport).getElement();

        doReturn(true).when(iEventFilter).isEnabled();
        doReturn(true).when(iEventFilter).test(Matchers.any(UIEvent.class));
        doReturn(viewport).when(mediator).getViewport();
        doReturn(iEventFilter).when(mediator).getEventFilter();

        mediator.handleEvent(downEvent.getAssociatedType(), uiEvent, 100, 100);

        verify(mediator).onMouseDown(Matchers.eq(100), Matchers.eq(100));
    }

    @Test
    public void testHandleEventMouseDownEnabledFilterTestNotPassed() throws Exception {
        final HTMLDivElement element = mock(HTMLDivElement.class);
        final NodeMouseDownEvent downEvent = spy(new NodeMouseDownEvent(element));
        final Viewport viewport = mock(Viewport.class);
        final IEventFilter iEventFilter = mock(IEventFilter.class);
        final UIEvent uiEvent = mock(UIEvent.class);
        uiEvent.type = "mouseDown";

        doReturn(NodeMouseDownEvent.getType()).when(downEvent).getAssociatedType();

        doReturn(element).when(viewport).getElement();

        doReturn(true).when(iEventFilter).isEnabled();
        doReturn(false).when(iEventFilter).test(Matchers.any(UIEvent.class));

        doReturn(viewport).when(mediator).getViewport();
        doReturn(iEventFilter).when(mediator).getEventFilter();

        mediator.handleEvent(downEvent.getAssociatedType(), uiEvent, 100, 100);

        verify(mediator,
               never()).onMouseDown(Matchers.eq(100), Matchers.eq(100));
    }

    @Test
    public void testHandleEventMouseUpNotDragging() {
        final UIEvent uiEvent = mock(UIEvent.class);
        final HTMLDivElement element = mock(HTMLDivElement.class);
        final NodeMouseUpEvent upEvent = spy(new NodeMouseUpEvent(element));
        uiEvent.type = "mouseUp";

        doReturn(NodeMouseUpEvent.getType()).when(upEvent).getAssociatedType();

        doReturn(false).when(mediator).isDragging();

        mediator.handleEvent(upEvent.getAssociatedType(), uiEvent, 100, 100);

        verify(mediator,
               never()).onMouseUp();
        verify(mediator,
               never()).cancel();
    }

    @Test
    public void testHandleEventMouseUpDragging() {
        final HTMLDivElement element = mock(HTMLDivElement.class);
        final NodeMouseUpEvent upEvent = spy(new NodeMouseUpEvent(element));
        final Viewport viewport = mock(Viewport.class);
        final UIEvent uiEvent = mock(UIEvent.class);
        uiEvent.type = "mouseUp";
        doReturn(NodeMouseUpEvent.getType()).when(upEvent).getAssociatedType();

        doReturn(element).when(viewport).getElement();

        doReturn(viewport).when(mediator).getViewport();
        doReturn(true).when(mediator).isDragging();

        mediator.handleEvent(upEvent.getAssociatedType(), uiEvent, 100, 100);

        verify(mediator).onMouseUp();
        verify(mediator).cancel();
    }

    @Test
    public void testOnMouseDown() {
        final HTMLDivElement element = mock(HTMLDivElement.class);
        final NodeMouseDownEvent downEvent = spy(new NodeMouseDownEvent(element));
        final Transform transform = mock(Transform.class);
        final Viewport viewport = mock(Viewport.class);
        final ArgumentCaptor<Point2D> point = ArgumentCaptor.forClass(Point2D.class);

        doReturn(123).when(downEvent).getX();
        doReturn(987).when(downEvent).getY();
        doReturn(NodeMouseDownEvent.getType()).when(downEvent).getAssociatedType();

        doReturn(transform).when(transform).getInverse();

        doReturn(transform).when(viewport).getTransform();
        doReturn(element).when(viewport).getElement();

        doReturn(viewport).when(mediator).getViewport();

        mediator.onMouseDown(downEvent.getX(), downEvent.getY());

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
        final HTMLDivElement element = mock(HTMLDivElement.class);
        final NodeMouseMoveEvent moveEvent = spy(new NodeMouseMoveEvent(element));
        final Transform transform = mock(Transform.class);
        final Transform translated = mock(Transform.class);
        final Viewport viewport = mock(Viewport.class);
        final Scene scene = mock(Scene.class);

        doReturn(xCoordinate).when(moveEvent).getX();
        doReturn(yCoordinate).when(moveEvent).getY();
        doReturn(NodeMouseMoveEvent.getType()).when(moveEvent).getAssociatedType();

        doReturn(translated).when(transform).translate(Matchers.anyDouble(),
                                                       Matchers.anyDouble());
        doReturn(transform).when(transform).copy();

        doReturn(scene).when(viewport).getScene();
        doReturn(transform).when(viewport).getTransform();

        doReturn(viewport).when(mediator).getViewport();
        doReturn(transform).when(mediator).inverseTransform();
        doReturn(batchDrawing).when(mediator).isBatchDraw();

        mediator.onMouseMove(xCoordinate, yCoordinate);

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

        mediator.onMouseMove(xMovement, yMovement);

        verify(transform).translate(xCoordinate,
                                    yCoordinate);

        verify(viewport, times(2)).setTransform(translated);

        if (batchDrawing) {
            verify(scene, times(2)).batch();
        } else {
            verify(scene, times(2)).draw();
        }
    }
}
