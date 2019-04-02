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

package com.ait.lienzo.client.widget.panel.scrollbars;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.BoundsProvider;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.event.LienzoPanelBoundsChangedEvent;
import com.ait.lienzo.client.widget.panel.event.LienzoPanelBoundsChangedEventHandler;
import com.ait.lienzo.client.widget.panel.event.LienzoPanelResizeEvent;
import com.ait.lienzo.client.widget.panel.event.LienzoPanelResizeEventHandler;
import com.ait.lienzo.client.widget.panel.event.LienzoPanelScrollEvent;
import com.ait.lienzo.client.widget.panel.event.LienzoPanelScrollEventHandler;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScrollablePanelTest {

    @Mock
    private LienzoPanel lienzoPanel;

    @Mock
    private BoundsProvider layerBoundsProvider;

    @Mock
    private HandlerManager handlerManager;

    @Mock
    private ScrollablePanelHandler scrollHandler;

    @Mock
    private HandlerRegistrationManager handlers;

    private ScrollablePanel tested;

    @Before
    public void setUp() {
        this.tested = spy(new ScrollablePanel(lienzoPanel,
                                              layerBoundsProvider,
                                              handlerManager,
                                              scrollHandler,
                                              handlers));
    }

    @Test
    public void testSetLayer() {
        Layer layer = mock(Layer.class);
        tested.set(layer);
        verify(scrollHandler, times(1)).init();
    }

    @Test
    public void testSetCursor() {
        tested.setCursor(Style.Cursor.HELP);
        verify(lienzoPanel, times(1)).setCursor(eq(Style.Cursor.HELP));
    }

    @Test
    public void testGetVisibleBounds() {
        Layer layer = mock(Layer.class);
        Viewport viewport = mock(Viewport.class);
        Transform transform = mock(Transform.class);
        when(layer.getViewport()).thenReturn(viewport);
        when(viewport.getTransform()).thenReturn(transform);
        when(viewport.getHeight()).thenReturn(300);
        when(viewport.getWidth()).thenReturn(100);
        when(transform.getScaleX()).thenReturn(1d);
        when(transform.getScaleY()).thenReturn(1d);
        when(transform.getTranslateX()).thenReturn(0d);
        when(transform.getTranslateY()).thenReturn(0d);
        tested.set(layer);
        Bounds visibleBounds = tested.getVisibleBounds();
        assertEquals(0d, visibleBounds.getX(), 0);
        assertEquals(0d, visibleBounds.getY(), 0);
        assertEquals(100d, visibleBounds.getWidth(), 0);
        assertEquals(300d, visibleBounds.getHeight(), 0);
    }

    @Test
    public void testScrollHandlerRegistrations() {
        AbsolutePanel scrollPanel = mock(AbsolutePanel.class);
        when(tested.getScrollPanel()).thenReturn(scrollPanel);
        ScrollHandler scrollHandler = mock(ScrollHandler.class);
        tested.addScrollHandler(scrollHandler);
        verify(scrollPanel, times(1)).addDomHandler(eq(scrollHandler),
                                                    eq(ScrollEvent.getType()));
    }

    @Test
    public void testPanelScrollEventHandler() {
        LienzoPanelScrollEventHandler handler = mock(LienzoPanelScrollEventHandler.class);
        tested.addLienzoPanelScrollEventHandler(handler);
        verify(handlerManager, times(1)).addHandler(eq(LienzoPanelScrollEvent.TYPE),
                                                    eq(handler));
        tested.fireLienzoPanelScrollEvent(34.5d, 56.4d);
        ArgumentCaptor<LienzoPanelScrollEvent> eventCaptor = ArgumentCaptor.forClass(LienzoPanelScrollEvent.class);
        verify(handlerManager, times(1)).fireEvent(eventCaptor.capture());
        LienzoPanelScrollEvent event = eventCaptor.getValue();
        assertEquals(34.5d, event.getPctX(), 0);
        assertEquals(56.4d, event.getPctY(), 0);
    }

    @Test
    public void testPanelResizeEventHandler() {
        LienzoPanelResizeEventHandler handler = mock(LienzoPanelResizeEventHandler.class);
        tested.addLienzoPanelResizeEventHandler(handler);
        verify(handlerManager, times(1)).addHandler(eq(LienzoPanelResizeEvent.TYPE),
                                                    eq(handler));
        tested.fireLienzoPanelResizeEvent(34.5d, 56.4d);
        ArgumentCaptor<LienzoPanelResizeEvent> eventCaptor = ArgumentCaptor.forClass(LienzoPanelResizeEvent.class);
        verify(handlerManager, times(1)).fireEvent(eventCaptor.capture());
        LienzoPanelResizeEvent event = eventCaptor.getValue();
        assertEquals(34.5d, event.getWidth(), 0);
        assertEquals(56.4d, event.getHeight(), 0);
    }

    @Test
    public void testPanelBoundsChangedEventHandler() {
        LienzoPanelBoundsChangedEventHandler handler = mock(LienzoPanelBoundsChangedEventHandler.class);
        tested.addLienzoPanelBoundsChangedEventHandler(handler);
        verify(handlerManager, times(1)).addHandler(eq(LienzoPanelBoundsChangedEvent.TYPE),
                                                    eq(handler));
        tested.fireLienzoPanelBoundsChangedEvent();
        verify(handlerManager, times(1)).fireEvent(any(LienzoPanelBoundsChangedEvent.class));
    }

    @Test
    public void testUpdateSize() {
        AbsolutePanel scrollPanel = mock(AbsolutePanel.class);
        AbsolutePanel domContainerPanel = mock(AbsolutePanel.class);
        when(tested.getScrollPanel()).thenReturn(scrollPanel);
        when(tested.getDomElementContainer()).thenReturn(domContainerPanel);
        when(scrollHandler.scrollbarWidth()).thenReturn(32);
        when(scrollHandler.scrollbarHeight()).thenReturn(41);
        tested.updateSize(300, 500);
        verify(scrollPanel, times(1)).setPixelSize(300, 500);
        verify(domContainerPanel, times(1)).setPixelSize(268, 459);
        verify(lienzoPanel, times(1)).setPixelSize(268, 459);
        ArgumentCaptor<LienzoPanelResizeEvent> eventCaptor = ArgumentCaptor.forClass(LienzoPanelResizeEvent.class);
        verify(handlerManager, times(1)).fireEvent(eventCaptor.capture());
        LienzoPanelResizeEvent event = eventCaptor.getValue();
        assertEquals(300d, event.getWidth(), 0);
        assertEquals(500d, event.getHeight(), 0);
    }

    @Test
    public void testOnRefresh() {
        tested.onRefresh();
        verify(scrollHandler, times(1)).refresh();
    }

    @Test
    public void testSetupHandlers() {
        tested.setupHandlers();
        verify(tested, times(1)).addMouseDownHandler(any(MouseDownHandler.class));
        verify(tested, times(1)).addMouseUpHandler(any(MouseUpHandler.class));
        verify(tested, times(1)).addMouseOutHandler(any(MouseOutHandler.class));
        verify(tested, times(1)).addScrollHandler(any(ScrollHandler.class));
        verify(tested, times(1)).addAttachHandler(any(AttachEvent.Handler.class));
        verify(handlers, times(10)).register(any(HandlerRegistration.class));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(handlers, times(1)).removeHandler();
    }
}
