/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import com.ait.lienzo.client.core.shape.Attributes;
import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
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

    @Mock
    private Attributes attributes;

    @Mock
    private DivElement vpElement;

    @Mock
    private Style vpStyle;

    private Transform transform;

    @Before
    public void setup() {
        this.transform = new Transform();

        when(viewLayer.getVisibleBounds()).thenReturn(visibleBounds);
        when(viewLayer.getViewport()).thenReturn(viewport);
        when(viewport.getElement()).thenReturn(vpElement);
        when(vpElement.getStyle()).thenReturn(vpStyle);
        when(viewport.getTransform()).thenReturn(transform);
        when(viewport.getAttributes()).thenReturn(attributes);
        when(viewport.getScene()).thenReturn(scene);

        this.mediator = new RestrictedMousePanMediator(viewLayer);
        this.mediator.setTransformMediator(new BoundaryTransformMediator(bounds));
        this.mediator.setViewport(viewport);
    }

    @Test
    public void testMouseDownEvent() {
        final MouseDownEvent md0 = mock(MouseDownEvent.class);
        when(md0.getRelativeX(any(Element.class))).thenReturn(0);
        when(md0.getRelativeY(any(Element.class))).thenReturn(0);
        final NodeMouseDownEvent nmd0 = new NodeMouseDownEvent(md0);

        mediator.handleEvent(nmd0);

        verify(vpStyle,
               times(1)).setCursor(eq(Style.Cursor.MOVE));
    }

    @Test
    public void testMouseUpEvent() {
        final MouseDownEvent md0 = mock(MouseDownEvent.class);
        when(md0.getRelativeX(any(Element.class))).thenReturn(0);
        when(md0.getRelativeY(any(Element.class))).thenReturn(0);
        final NodeMouseDownEvent nmd0 = new NodeMouseDownEvent(md0);

        final MouseUpEvent mu0 = mock(MouseUpEvent.class);
        final NodeMouseUpEvent nmu0 = new NodeMouseUpEvent(mu0);

        mediator.handleEvent(nmd0);
        mediator.handleEvent(nmu0);

        verify(vpStyle,
               times(1)).setCursor(eq(Style.Cursor.MOVE));
        verify(vpStyle,
               times(1)).setCursor(eq(Style.Cursor.DEFAULT));
    }

    @Test
    public void testMouseMoveEvent_LeftEdge() {
        final MouseDownEvent md0 = mock(MouseDownEvent.class);
        when(md0.getRelativeX(any(Element.class))).thenReturn(0);
        when(md0.getRelativeY(any(Element.class))).thenReturn(0);
        final NodeMouseDownEvent nmd0 = new NodeMouseDownEvent(md0);

        final MouseMoveEvent mm1 = mock(MouseMoveEvent.class);
        when(mm1.getRelativeX(any(Element.class))).thenReturn(1200);
        when(mm1.getRelativeY(any(Element.class))).thenReturn(0);
        final NodeMouseMoveEvent nmm1 = new NodeMouseMoveEvent(mm1);

        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);

        mediator.handleEvent(nmd0);
        mediator.handleEvent(nmm1);

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
        final MouseDownEvent md0 = mock(MouseDownEvent.class);
        when(md0.getRelativeX(any(Element.class))).thenReturn(0);
        when(md0.getRelativeY(any(Element.class))).thenReturn(0);
        final NodeMouseDownEvent nmd0 = new NodeMouseDownEvent(md0);

        final MouseMoveEvent mm1 = mock(MouseMoveEvent.class);
        when(mm1.getRelativeX(any(Element.class))).thenReturn(-200);
        when(mm1.getRelativeY(any(Element.class))).thenReturn(0);
        final NodeMouseMoveEvent nmm1 = new NodeMouseMoveEvent(mm1);

        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);

        mediator.handleEvent(nmd0);
        mediator.handleEvent(nmm1);

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
        final MouseDownEvent md0 = mock(MouseDownEvent.class);
        when(md0.getRelativeX(any(Element.class))).thenReturn(0);
        when(md0.getRelativeY(any(Element.class))).thenReturn(0);
        final NodeMouseDownEvent nmd0 = new NodeMouseDownEvent(md0);

        final MouseMoveEvent mm1 = mock(MouseMoveEvent.class);
        when(mm1.getRelativeX(any(Element.class))).thenReturn(0);
        when(mm1.getRelativeY(any(Element.class))).thenReturn(1200);
        final NodeMouseMoveEvent nmm1 = new NodeMouseMoveEvent(mm1);

        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);

        mediator.handleEvent(nmd0);
        mediator.handleEvent(nmm1);

        verify(viewport,
               times(1)).setTransform(transformArgumentCaptor.capture());
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
        final MouseDownEvent md0 = mock(MouseDownEvent.class);
        when(md0.getRelativeX(any(Element.class))).thenReturn(0);
        when(md0.getRelativeY(any(Element.class))).thenReturn(0);
        final NodeMouseDownEvent nmd0 = new NodeMouseDownEvent(md0);

        final MouseMoveEvent mm1 = mock(MouseMoveEvent.class);
        when(mm1.getRelativeX(any(Element.class))).thenReturn(0);
        when(mm1.getRelativeY(any(Element.class))).thenReturn(-200);
        final NodeMouseMoveEvent nmm1 = new NodeMouseMoveEvent(mm1);

        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);

        mediator.handleEvent(nmd0);
        mediator.handleEvent(nmm1);

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
        final MouseDownEvent md0 = mock(MouseDownEvent.class);
        when(md0.getRelativeX(any(Element.class))).thenReturn(0);
        when(md0.getRelativeY(any(Element.class))).thenReturn(0);
        final NodeMouseDownEvent nmd0 = new NodeMouseDownEvent(md0);

        final MouseMoveEvent mm1 = mock(MouseMoveEvent.class);
        when(mm1.getRelativeX(any(Element.class))).thenReturn(1200);
        when(mm1.getRelativeY(any(Element.class))).thenReturn(0);
        final NodeMouseMoveEvent nmm1 = new NodeMouseMoveEvent(mm1);

        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);

        transform.scale(0.5,
                        0.5);
        mediator.handleEvent(nmd0);
        mediator.handleEvent(nmm1);

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
        final MouseDownEvent md0 = mock(MouseDownEvent.class);
        when(md0.getRelativeX(any(Element.class))).thenReturn(0);
        when(md0.getRelativeY(any(Element.class))).thenReturn(0);
        final NodeMouseDownEvent nmd0 = new NodeMouseDownEvent(md0);

        final MouseMoveEvent mm1 = mock(MouseMoveEvent.class);
        when(mm1.getRelativeX(any(Element.class))).thenReturn(-200);
        when(mm1.getRelativeY(any(Element.class))).thenReturn(0);
        final NodeMouseMoveEvent nmm1 = new NodeMouseMoveEvent(mm1);

        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);

        transform.scale(0.5,
                        0.5);
        mediator.handleEvent(nmd0);
        mediator.handleEvent(nmm1);

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
        final MouseDownEvent md0 = mock(MouseDownEvent.class);
        when(md0.getRelativeX(any(Element.class))).thenReturn(0);
        when(md0.getRelativeY(any(Element.class))).thenReturn(0);
        final NodeMouseDownEvent nmd0 = new NodeMouseDownEvent(md0);

        final MouseMoveEvent mm1 = mock(MouseMoveEvent.class);
        when(mm1.getRelativeX(any(Element.class))).thenReturn(0);
        when(mm1.getRelativeY(any(Element.class))).thenReturn(1200);
        final NodeMouseMoveEvent nmm1 = new NodeMouseMoveEvent(mm1);

        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);

        transform.scale(0.5,
                        0.5);
        mediator.handleEvent(nmd0);
        mediator.handleEvent(nmm1);

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
        final MouseDownEvent md0 = mock(MouseDownEvent.class);
        when(md0.getRelativeX(any(Element.class))).thenReturn(0);
        when(md0.getRelativeY(any(Element.class))).thenReturn(0);
        final NodeMouseDownEvent nmd0 = new NodeMouseDownEvent(md0);

        final MouseMoveEvent mm1 = mock(MouseMoveEvent.class);
        when(mm1.getRelativeX(any(Element.class))).thenReturn(0);
        when(mm1.getRelativeY(any(Element.class))).thenReturn(-200);
        final NodeMouseMoveEvent nmm1 = new NodeMouseMoveEvent(mm1);

        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);

        transform.scale(0.5,
                        0.5);
        mediator.handleEvent(nmd0);
        mediator.handleEvent(nmm1);

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
