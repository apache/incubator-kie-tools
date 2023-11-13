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


package org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.Style;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.MouseEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class BoundaryMousePanMediatorTest {

    private final Bounds bounds = new BaseBounds(-1000,
                                                 -1000,
                                                 2000,
                                                 2000);
    private final Bounds visibleBounds = new BaseBounds(-500,
                                                        -500,
                                                        1000,
                                                        1000);
    private RestrictedMousePanMediator mediator;
    @Mock
    private GridLayer viewLayer;

    @Mock
    private Viewport viewport;

    @Mock
    private Scene scene;

    private HTMLDivElement vpElement;

    private Transform transform;

    @Before
    public void setup() {
        this.transform = new Transform();
        this.vpElement = new HTMLDivElement();

        when(viewLayer.getVisibleBounds()).thenReturn(visibleBounds);
        when(viewLayer.getViewport()).thenReturn(viewport);
        when(viewport.getElement()).thenReturn(vpElement);
        when(viewport.getTransform()).thenReturn(transform);
        when(viewport.getScene()).thenReturn(scene);

        this.mediator = new RestrictedMousePanMediator(viewLayer);
        this.mediator.setTransformMediator(new BoundaryTransformMediator(bounds));
        this.mediator.setViewport(viewport);
    }

    @Test
    public void testMouseDownEvent() {
        final MouseEvent md0 = mock(MouseEvent.class);
        mediator.handleEvent(NodeMouseDownEvent.getType(), md0, 0, 0);
        assertEquals(Style.Cursor.MOVE.getCssName(), vpElement.style.cursor);
    }

    @Test
    public void testMouseUpEvent() {
        mediator.handleEvent(NodeMouseDownEvent.getType(), mock(MouseEvent.class), 0, 0);
        mediator.handleEvent(NodeMouseUpEvent.getType(), mock(MouseEvent.class), 0, 0);
        assertEquals(Style.Cursor.DEFAULT.getCssName(), vpElement.style.cursor);
    }

    @Test
    public void testMouseMoveEvent_LeftEdge() {
        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);

        mediator.handleEvent(NodeMouseDownEvent.getType(), mock(MouseEvent.class), 0, 0);
        mediator.handleEvent(NodeMouseMoveEvent.getType(), mock(MouseEvent.class), 1200, 0);

        verify(viewport,
               times(1)).setTransform(transformArgumentCaptor.capture());
        final Transform result = transformArgumentCaptor.getValue();
        assertNotNull(result);
        assertEquals(1000.0,
                     result.getTranslateX(),
                     0.0);
        assertEquals(0.0,
                     result.getTranslateY(),
                     0.0);
    }

    @Test
    public void testMouseMoveEvent_RightEdge() {
        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);

        mediator.handleEvent(NodeMouseDownEvent.getType(), mock(MouseEvent.class), 0, 0);
        mediator.handleEvent(NodeMouseMoveEvent.getType(), mock(MouseEvent.class), -200, 0);

        verify(viewport,
               times(1)).setTransform(transformArgumentCaptor.capture());
        final Transform result = transformArgumentCaptor.getValue();
        assertNotNull(result);
        assertEquals(0.0,
                     result.getTranslateX(),
                     0.0);
        assertEquals(0.0,
                     result.getTranslateY(),
                     0.0);
    }

    @Test
    public void testMouseMoveEvent_TopEdge() {
        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);

        mediator.handleEvent(NodeMouseDownEvent.getType(), mock(MouseEvent.class), 0, 0);
        mediator.handleEvent(NodeMouseMoveEvent.getType(), mock(MouseEvent.class), 0, 1200);

        verify(viewport, times(1)).setTransform(transformArgumentCaptor.capture());
        final Transform result = transformArgumentCaptor.getValue();
        assertNotNull(result);
        assertEquals(0.0,
                     result.getTranslateX(),
                     0.0);
        assertEquals(1000.0,
                     result.getTranslateY(),
                     0.0);
    }

    @Test
    public void testMouseMoveEvent_BottomEdge() {
        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);

        mediator.handleEvent(NodeMouseDownEvent.getType(), mock(MouseEvent.class), 0, 0);
        mediator.handleEvent(NodeMouseMoveEvent.getType(), mock(MouseEvent.class), 0, -200);

        verify(viewport,
               times(1)).setTransform(transformArgumentCaptor.capture());
        final Transform result = transformArgumentCaptor.getValue();
        assertNotNull(result);
        assertEquals(0.0,
                     result.getTranslateX(),
                     0.0);
        assertEquals(0.0,
                     result.getTranslateY(),
                     0.0);
    }

    @Test
    public void testMouseMoveEvent_LeftEdge_Scaled50pct() {
        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);

        transform.scaleWithXY(0.5,
                              0.5);

        mediator.handleEvent(NodeMouseDownEvent.getType(), mock(MouseEvent.class), 0, 0);
        mediator.handleEvent(NodeMouseMoveEvent.getType(), mock(MouseEvent.class), 1200, 0);

        verify(viewport,
               times(1)).setTransform(transformArgumentCaptor.capture());
        final Transform result = transformArgumentCaptor.getValue();
        assertNotNull(result);
        assertEquals(500.0,
                     result.getTranslateX(),
                     0.0);
        assertEquals(0.0,
                     result.getTranslateY(),
                     0.0);
    }

    @Test
    public void testMouseMoveEvent_RightEdge_Scaled50pct() {
        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);

        transform.scaleWithXY(0.5,
                              0.5);

        mediator.handleEvent(NodeMouseDownEvent.getType(), mock(MouseEvent.class), 0, 0);
        mediator.handleEvent(NodeMouseMoveEvent.getType(), mock(MouseEvent.class), -200, 0);

        verify(viewport,
               times(1)).setTransform(transformArgumentCaptor.capture());
        final Transform result = transformArgumentCaptor.getValue();
        assertNotNull(result);
        assertEquals(0.0,
                     result.getTranslateX(),
                     0.0);
        assertEquals(0.0,
                     result.getTranslateY(),
                     0.0);
    }

    @Test
    public void testMouseMoveEvent_TopEdge_Scaled50pct() {
        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);

        transform.scaleWithXY(0.5,
                              0.5);

        mediator.handleEvent(NodeMouseDownEvent.getType(), mock(MouseEvent.class), 0, 0);
        mediator.handleEvent(NodeMouseMoveEvent.getType(), mock(MouseEvent.class), 0, 1200);

        verify(viewport,
               times(1)).setTransform(transformArgumentCaptor.capture());
        final Transform result = transformArgumentCaptor.getValue();
        assertNotNull(result);
        assertEquals(0.0,
                     result.getTranslateX(),
                     0.0);
        assertEquals(500.0,
                     result.getTranslateY(),
                     0.0);
    }

    @Test
    public void testMouseMoveEvent_BottomEdge_Scaled50pct() {
        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);

        transform.scaleWithXY(0.5,
                              0.5);

        mediator.handleEvent(NodeMouseDownEvent.getType(), mock(MouseEvent.class), 0, 0);
        mediator.handleEvent(NodeMouseMoveEvent.getType(), mock(MouseEvent.class), 0, -200);

        verify(viewport,
               times(1)).setTransform(transformArgumentCaptor.capture());
        final Transform result = transformArgumentCaptor.getValue();
        assertNotNull(result);
        assertEquals(0.0,
                     result.getTranslateX(),
                     0.0);
        assertEquals(0.0,
                     result.getTranslateY(),
                     0.0);
    }
}
