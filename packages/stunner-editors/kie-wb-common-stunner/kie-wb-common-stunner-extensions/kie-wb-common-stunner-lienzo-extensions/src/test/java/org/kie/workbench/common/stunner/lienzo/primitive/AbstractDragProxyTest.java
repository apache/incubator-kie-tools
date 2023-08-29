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


package org.kie.workbench.common.stunner.lienzo.primitive;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Timer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class AbstractDragProxyTest {

    @Mock
    private Layer layer;

    @Mock
    private MouseMoveEvent mouseMoveEvent;

    @Mock
    private MouseUpEvent mouseUpEvent;

    @Mock
    private Timer timer;

    @Mock
    private Transform transform;

    private Object shapeProxy;

    private AbstractDragProxy.Callback callback;

    private int timeout = 200;

    private AbstractDragProxy<Object> abstractDragProxy;

    @Before
    public void setup() {
        callback = spy(makeCallback());
        shapeProxy = spy(new Object());
        abstractDragProxy = spy(makeAbstractDragProxy());

        mockTransform(abstractDragProxy);
        mockTimer(abstractDragProxy);
    }

    @Test
    public void testGetMouseMoveHandlerWhenProxyIsNotAttached() {

        final int initialX = 100;
        final int initialY = 100;
        final MouseMoveHandler handler = abstractDragProxy.getMouseMoveHandler(initialX, initialY, timeout, callback);

        doReturn(false).when(abstractDragProxy).isAttached();

        handler.onMouseMove(mouseMoveEvent);

        verify(abstractDragProxy, never()).setLocation(any(), anyInt(), anyInt());
        verify(abstractDragProxy, never()).scheduleMove(any(), anyInt(), anyInt(), anyInt());
        verify(layer, never()).batch();
    }

    @Test
    public void testGetMouseMoveHandlerWhenProxyWhenXDiffAndYDiffAreNull() {

        final int initialX = 100;
        final int initialY = 100;
        final int expectedX = 100;
        final int expectedY = 100;
        final MouseMoveHandler handler = abstractDragProxy.getMouseMoveHandler(initialX, initialY, timeout, callback);

        when(mouseMoveEvent.getX()).thenReturn(75);
        when(mouseMoveEvent.getY()).thenReturn(75);
        doReturn(true).when(abstractDragProxy).isAttached();

        handler.onMouseMove(mouseMoveEvent);

        assertEquals(25, abstractDragProxy.xDiff().intValue());
        assertEquals(25, abstractDragProxy.yDiff().intValue());

        verify(abstractDragProxy).setLocation(shapeProxy, expectedX, expectedY);
        verify(abstractDragProxy).scheduleMove(callback, expectedX, expectedY, timeout);
        verify(layer, never()).batch();
    }

    @Test
    public void testGetMouseMoveHandler() {

        final int initialX = 100;
        final int initialY = 100;
        final int expectedX = 175;
        final int expectedY = 175;
        final MouseMoveHandler handler = abstractDragProxy.getMouseMoveHandler(initialX, initialY, timeout, callback);

        when(mouseMoveEvent.getX()).thenReturn(150);
        when(mouseMoveEvent.getY()).thenReturn(150);
        doReturn(0).when(abstractDragProxy).xDiff();
        doReturn(0).when(abstractDragProxy).yDiff();
        doReturn(25).when(abstractDragProxy).getXDiff();
        doReturn(25).when(abstractDragProxy).getYDiff();
        doReturn(true).when(abstractDragProxy).isAttached();

        handler.onMouseMove(mouseMoveEvent);

        verify(abstractDragProxy).setLocation(shapeProxy, expectedX, expectedY);
        verify(abstractDragProxy).scheduleMove(callback, expectedX, expectedY, timeout);
        verify(layer, never()).batch();
    }

    @Test
    public void testGetMouseUpHandlerWhenProxyIsNotAttached() {

        final MouseUpHandler handler = abstractDragProxy.getMouseUpHandler(callback);

        doReturn(false).when(abstractDragProxy).isAttached();

        handler.onMouseUp(mouseUpEvent);

        verify(abstractDragProxy, never()).clear();
        verify(callback, never()).onComplete(anyInt(), anyInt());
    }

    @Test
    public void testGetMouseUpHandler() {

        final MouseUpHandler handler = abstractDragProxy.getMouseUpHandler(callback);
        final int expectedX = 175;
        final int expectedY = 175;

        when(mouseUpEvent.getX()).thenReturn(150);
        when(mouseUpEvent.getY()).thenReturn(150);
        doReturn(25).when(abstractDragProxy).getXDiff();
        doReturn(25).when(abstractDragProxy).getYDiff();
        doNothing().when(abstractDragProxy).clear();
        doReturn(true).when(abstractDragProxy).isAttached();

        handler.onMouseUp(mouseUpEvent);

        verify(abstractDragProxy).clear();
        verify(callback).onComplete(expectedX, expectedY);
    }

    @Test
    public void testRelativeX() {

        final double transformTranslateX = 50d;
        final double transformScaleX = 1.5d;
        final int x = 100;
        final int expectedRelativeX = 33;

        when(transform.getTranslateX()).thenReturn(transformTranslateX);
        when(transform.getScaleX()).thenReturn(transformScaleX);

        final int actualRelativeX = abstractDragProxy.relativeX(x);

        assertEquals(expectedRelativeX, actualRelativeX);
    }

    @Test
    public void testRelativeY() {

        final double transformTranslateY = 50d;
        final double transformScaleY = 1.5d;
        final int y = 100;
        final int expectedRelativeY = 33;

        when(transform.getTranslateY()).thenReturn(transformTranslateY);
        when(transform.getScaleY()).thenReturn(transformScaleY);

        final int actualRelativeY = abstractDragProxy.relativeY(y);

        assertEquals(expectedRelativeY, actualRelativeY);
    }

    private AbstractDragProxy<Object> makeAbstractDragProxy() {

        final int defaultX = 1;
        final int defaultY = 1;

        return new AbstractDragProxy<Object>(layer, shapeProxy, defaultX, defaultY, timeout, callback) {
            @Override
            protected void addToLayer(final Layer layer,
                                      final Object shape) {
            }

            @Override
            protected void removeFromLayer(final Layer layer,
                                           final Object shape) {
            }

            @Override
            protected void setLocation(final Object shape,
                                       final int x,
                                       final int y) {
            }

            @Override
            void create(final int initialX,
                        final int initialY,
                        final int timeout,
                        final Callback callback) {
            }
        };
    }

    private AbstractDragProxy.Callback makeCallback() {
        return new AbstractDragProxy.Callback() {
            @Override
            public void onStart(final int x,
                                final int y) {
            }

            @Override
            public void onMove(final int x,
                               final int y) {
            }

            @Override
            public void onComplete(final int x,
                                   final int y) {
            }
        };
    }

    private void mockTimer(final AbstractDragProxy<Object> abstractDragProxy) {
        doReturn(timer).when(abstractDragProxy).makeTimer();
    }

    private void mockTransform(final AbstractDragProxy<Object> abstractDragProxy) {

        final double defaultScale = 1d;

        when(transform.getScaleX()).thenReturn(defaultScale);
        when(transform.getScaleY()).thenReturn(defaultScale);

        doReturn(transform).when(abstractDragProxy).getViewportTransform();
    }
}
