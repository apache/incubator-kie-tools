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


package org.kie.workbench.common.stunner.client.widgets.canvas;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.panel.BoundsProvider;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseDownEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseUpEvent;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class StunnerLienzoBoundsPanelTest {

    @Mock
    private EventSourceMock<CanvasMouseDownEvent> mouseDownEvent;

    @Mock
    private EventSourceMock<CanvasMouseUpEvent> mouseUpEvent;

    @Mock
    private TestBoundsLienzoPanelView view;

    @Mock
    private com.ait.lienzo.client.widget.panel.LienzoPanel lienzoPanel;

    @Mock
    private LienzoLayer lienzoLayer;

    @Mock
    private Layer layer;

    @Mock
    private HTMLDivElement panelElement;

    @Mock
    private HTMLDivElement viewElement;

    private StunnerLienzoBoundsPanel tested;

    static final String ON_MOUSE_DOWN = "mousedown";
    static final String ON_MOUSE_UP = "mouseup";

    @Before
    public void init() {
        this.tested = spy(new StunnerLienzoBoundsPanel(mouseDownEvent,
                                                   mouseUpEvent)
                .setPanelBuilder(() -> view));
        when(view.getLienzoPanel()).thenReturn(lienzoPanel);
        when(view.getElement()).thenReturn(viewElement);
        when(lienzoPanel.getElement()).thenReturn(panelElement);
        when(lienzoLayer.getLienzoLayer()).thenReturn(layer);
        doNothing().when(tested).broadcastBlurEvent(); // It's a native method.
    }

    @Test
    public void testShow() {
        tested.show(lienzoLayer);
        verify(view, times(1)).add(eq(layer));
        verify(panelElement, times(1)).addEventListener(eq(ON_MOUSE_DOWN), any(EventListener.class));
        verify(panelElement, times(1)).addEventListener(eq(ON_MOUSE_UP), any(EventListener.class));
    }

    @Test
    public void testFocus() {
        tested.setView(view)
                .focus();
        verify(viewElement, times(1)).focus();
    }

    @Test
    public void testSizeGetters() {
        tested.setView(view);
        when(lienzoPanel.getWidePx()).thenReturn(100);
        when(lienzoPanel.getHighPx()).thenReturn(450);
        assertEquals(100, tested.getWidthPx());
        assertEquals(450, tested.getHeightPx());
    }

    @Test
    public void testSetBackgroundLayer() {
        tested.setView(view);
        Layer bgLayer = mock(Layer.class);
        tested.setBackgroundLayer(bgLayer);
        verify(lienzoPanel, times(1)).setBackgroundLayer(eq(bgLayer));
    }

    @Test
    public void testDestroy() {
        tested.show(lienzoLayer);
        tested.addKeyDownHandler(mock(EventListener.class));
        tested.addKeyUpHandler(mock(EventListener.class));
        tested.addKeyPressHandler(mock(EventListener.class));
        tested.destroy();
        verify(panelElement, times(5))
                .removeEventListener(anyString(), any(EventListener.class));
        verify(view, times(1)).destroy();
        assertNull(tested.getView());
    }

    @Test
    public void testOnMouseDown() {
        tested.onMouseDown();
        verify(mouseDownEvent, times(1)).fire(any(CanvasMouseDownEvent.class));
    }

    @Test
    public void testOnMouseUp() {
        tested.onMouseUp();
        verify(mouseUpEvent, times(1)).fire(any(CanvasMouseUpEvent.class));
    }

    @Test
    public void testLocationConstraints() {
        Bounds bounds = tested.getLocationConstraints();
        assertNotNull(bounds);
        assertTrue(bounds.hasUpperLeft());
        assertEquals(0d, bounds.getUpperLeft().getX(), 0d);
        assertEquals(0d, bounds.getUpperLeft().getY(), 0d);
        assertFalse(bounds.hasLowerRight());
    }

    private static class TestBoundsLienzoPanelView extends LienzoBoundsPanel {

        public TestBoundsLienzoPanelView(LienzoPanel lienzoPanel, BoundsProvider boundsProvider) {
            super(lienzoPanel, boundsProvider);
        }

        @Override
        public LienzoBoundsPanel onRefresh() {
            return null;
        }

        @Override
        protected void doDestroy() {

        }

        @Override
        public HTMLDivElement getElement() {
            return null;
        }
    }
}
