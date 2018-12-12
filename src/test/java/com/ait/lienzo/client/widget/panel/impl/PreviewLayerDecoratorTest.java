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
package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.common.api.java.util.function.Supplier;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class PreviewLayerDecoratorTest
{
    private static final Point2D LOCATION = new Point2D(120d, 330d);

    @Mock
    private HandlerRegistrationManager         handlers;

    @Mock
    private PreviewLayerDecorator.EventHandler eventHandler;

    @Mock
    private DragContext                        dragContext;

    @Mock
    private AbstractNodeDragEvent              dragEvent;

    private PreviewLayerDecorator              tested;

    private Bounds                             bgBounds;

    private Bounds                             visibleBounds;

    private Rectangle                          primitive;

    @Before
    public void setUp()
    {
        bgBounds = Bounds.build(0d, 0d, 800d, 1200d);
        visibleBounds = Bounds.build(0d, 0d, 400d, 800d);
        primitive = spy(new Rectangle(50, 50));
        when(dragEvent.getDragContext()).thenReturn(dragContext);
        when(primitive.getLocation()).thenReturn(LOCATION);
        this.tested = new PreviewLayerDecorator(handlers,
                                                new Supplier<Bounds>()
                                                {
                                                    @Override
                                                    public Bounds get()
                                                    {
                                                        return bgBounds;
                                                    }
                                                },
                                                new Supplier<Bounds>()
                                                {
                                                    @Override
                                                    public Bounds get()
                                                    {
                                                        return visibleBounds;
                                                    }
                                                },
                                                eventHandler,
                                                primitive);
    }

    @Test
    public void testInit()
    {
        verify(primitive, times(1)).addNodeDragStartHandler(any(NodeDragStartHandler.class));
        verify(primitive, times(1)).addNodeDragMoveHandler(any(NodeDragMoveHandler.class));
        verify(primitive, times(1)).addNodeDragEndHandler(any(NodeDragEndHandler.class));
        verify(primitive, times(1)).addNodeMouseEnterHandler(any(NodeMouseEnterHandler.class));
        verify(primitive, times(1)).addNodeMouseExitHandler(any(NodeMouseExitHandler.class));
        verify(handlers, times(5)).register(any(HandlerRegistration.class));
    }

    @Test
    public void testBuildDecorator()
    {
        Rectangle instance = PreviewLayerDecorator.buildDecorator();
        assertEquals(PreviewLayerDecorator.STROKE_COLOR, instance.getStrokeColor());
        assertEquals(PreviewLayerDecorator.STROKE_WIDTH, instance.getStrokeWidth(), 0);
        assertTrue(instance.isFillShapeForSelection());
        assertTrue(instance.isFillBoundsForSelection());
        assertTrue(instance.isDraggable());
        assertTrue(instance.isListening());
        assertNotNull(instance.getDragBounds());
        assertNotNull(instance.getDragConstraints());
    }

    @Test
    public void testUpdate()
    {
        DragBounds dragBounds = new DragBounds();
        primitive.setDragBounds(dragBounds);
        tested.update();
        assertEquals(0d, primitive.getX(), 0d);
        assertEquals(0d, primitive.getY(), 0d);
        assertEquals(400d, primitive.getWidth(), 0d);
        assertEquals(800d, primitive.getHeight(), 0d);
        assertEquals(1d, primitive.getStrokeAlpha(), 0d);
        assertTrue(primitive.isListening());
        assertEquals(0d, dragBounds.getX1(), 0d);
        assertEquals(0d, dragBounds.getY1(), 0d);
        assertEquals(400d, dragBounds.getX2(), 0d);
        assertEquals(400d, dragBounds.getY2(), 0d);
    }

    @Test
    public void testUpdateNothingVisible()
    {
        visibleBounds = Bounds.empty();
        tested.update();
        assertEquals(0d, primitive.getStrokeAlpha(), 0d);
        assertFalse(primitive.isListening());
    }

    @Test
    public void testOnMouseEnter()
    {
        tested.onMouseEnter();
        verify(eventHandler, times(1)).onMouseEnter();
        verify(eventHandler, never()).onMouseExit();
        verify(eventHandler, never()).onMove(any(Point2D.class));
    }

    @Test
    public void testOnMouseExit()
    {
        tested.onMouseExit();
        verify(eventHandler, times(1)).onMouseExit();
        verify(eventHandler, never()).onMouseEnter();
        verify(eventHandler, never()).onMove(any(Point2D.class));
    }

    @Test
    public void testOnDragStart()
    {
        when(primitive.getX()).thenReturn(10d);
        when(primitive.getY()).thenReturn(45d);
        assertFalse(tested.isDragging());
        tested.onDecoratorDragStart(dragEvent);
        assertTrue(tested.isDragging());
    }

    @Test
    public void testOnDragMove()
    {
        tested.onDecoratorDragStart(dragEvent);
        tested.onDecoratorDragMove();
        assertTrue(tested.isDragging());
        verify(eventHandler, times(1)).onMove(eq(LOCATION));
    }

    @Test
    public void testOnDragEnd()
    {
        tested.onDecoratorDragEnd();
        assertFalse(tested.isDragging());
    }
}
