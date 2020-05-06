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

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.ViewportTransformChangedEvent;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScrollablePanelHandlerTest {

    private static final int DEFAULT_INTERNAL_SCROLL_HEIGHT = 1;
    private static final int DEFAULT_INTERNAL_SCROLL_WIDTH = 1;

    @Mock
    private ScrollablePanel panel;

    @Mock
    private Layer layer;

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

    @Mock
    private ScrollBounds scrollBounds;

    @Mock
    private ScrollPosition scrollPosition;

    private ScrollablePanelHandler tested;

    @Before
    public void setUp() {
        this.tested = spy(new ScrollablePanelHandler(panel,
                                                     scrollBounds));
        when(panel.getLayer()).thenReturn(layer);
        when(layer.getViewport()).thenReturn(viewport);
        when(viewport.getTransform()).thenReturn(transform);
        when(tested.scrollPosition()).thenReturn(scrollPosition);
    }

    @Test
    public void testInit() {

        doNothing().when(tested).setupScrollBarSynchronization();
        doNothing().when(tested).setupContextSwitcher();
        doReturn(mock(ScrollUI.class)).when(tested).scrollUI();

        tested.init();

        verify(tested).setupScrollBarSynchronization();
        verify(tested).setupContextSwitcher();
    }

    @Test
    public void testScrollUI() {
        assertTrue(tested.scrollUI() != null);
    }

    @Test
    public void testSetupScrollBarSynchronization() {

        final AbsolutePanel scrollPanel = mock(AbsolutePanel.class);
        final ScrollHandler scrollHandler = mock(ScrollHandler.class);

        doReturn(scrollHandler).when(tested).onScroll();
        doReturn(scrollPanel).when(tested).getScrollPanel();
        doNothing().when(tested).synchronizeScrollSize();

        tested.setupScrollBarSynchronization();

        verify(tested).synchronizeScrollSize();
        verify(panel).addScrollHandler(any(ScrollHandler.class));
    }

    @Test
    public void testSynchronizeScrollSize() {

        final AbsolutePanel panel = mock(AbsolutePanel.class);
        final Integer internalScrollPanelWidth = 42;
        final Integer internalScrollPanelHeight = 58;

        doReturn(internalScrollPanelWidth).when(tested).calculateInternalScrollPanelWidth();
        doReturn(internalScrollPanelHeight).when(tested).calculateInternalScrollPanelHeight();
        doReturn(panel).when(tested).getInternalScrollPanel();

        tested.synchronizeScrollSize();

        verify(panel).setPixelSize(eq(internalScrollPanelWidth),
                                   eq(internalScrollPanelHeight));
    }

    @Test
    public void testCalculateInternalScrollPanelWidthWhenScrollbarXIsEnabled() {

        final Double maximumBoundX = +20d;
        final Double minimumBoundX = -20d;
        final Double zoomLevel = 0.75d;
        final Double currentScrollPosition = 10d;

        doReturn(maximumBoundX).when(scrollBounds).maxBoundX();
        doReturn(minimumBoundX).when(scrollBounds).minBoundX();
        doReturn(zoomLevel).when(transform).getScaleX();
        doReturn(currentScrollPosition).when(scrollPosition).deltaX();

        final Integer panelWidth = tested.calculateInternalScrollPanelWidth();
        final Integer scaledWidth = (int) ((maximumBoundX - minimumBoundX) * zoomLevel);

        assertEquals(scaledWidth,
                     panelWidth,
                     0);
    }

    @Test
    public void testCalculateInternalScrollPanelWidthWhenScrollbarXIsDisabled() {

        final Double maximumBoundX = +20d;
        final Double minimumBoundX = -20d;
        final Double zoomLevel = 0.75d;
        final Double currentScrollPosition = 0d;

        doReturn(maximumBoundX).when(scrollBounds).maxBoundX();
        doReturn(minimumBoundX).when(scrollBounds).minBoundX();
        doReturn(zoomLevel).when(transform).getScaleX();
        doReturn(currentScrollPosition).when(scrollPosition).deltaX();

        final Integer panelWidth = tested.calculateInternalScrollPanelWidth();

        assertEquals(DEFAULT_INTERNAL_SCROLL_WIDTH,
                     panelWidth,
                     0);
    }

    @Test
    public void testCalculateInternalScrollPanelHeightWhenScrollbarYIsEnabled() {

        final Double maximumBoundY = +20d;
        final Double minimumBoundY = -20d;
        final Double zoomLevel = 0.75d;
        final Double currentScrollPosition = 10d;

        doReturn(maximumBoundY).when(scrollBounds).maxBoundY();
        doReturn(minimumBoundY).when(scrollBounds).minBoundY();
        doReturn(zoomLevel).when(transform).getScaleY();
        doReturn(currentScrollPosition).when(scrollPosition).deltaY();

        final Integer panelHeight = tested.calculateInternalScrollPanelHeight();
        final Integer scaledHeight = (int) ((maximumBoundY - minimumBoundY) * zoomLevel);

        assertEquals(scaledHeight,
                     panelHeight,
                     0);
    }

    @Test
    public void testCalculateInternalScrollPanelHeightWhenScrollbarYIsDisabled() {

        final Double maximumBoundY = +20d;
        final Double minimumBoundY = -20d;
        final Double zoomLevel = 0.75d;
        final Double currentScrollPosition = 0d;

        doReturn(maximumBoundY).when(scrollBounds).maxBoundY();
        doReturn(minimumBoundY).when(scrollBounds).minBoundY();
        doReturn(zoomLevel).when(transform).getScaleY();
        doReturn(currentScrollPosition).when(scrollPosition).deltaY();

        final Integer panelHeight = tested.calculateInternalScrollPanelHeight();

        assertEquals(DEFAULT_INTERNAL_SCROLL_HEIGHT,
                     panelHeight,
                     0);
    }

    @Test
    public void testRefreshScrollPosition() {

        final ScrollPosition scrollPosition = mock(ScrollPosition.class);
        final Double internalScrollPanelWidth = 42d;
        final Double internalScrollPanelHeight = 58d;

        doReturn(internalScrollPanelWidth).when(scrollPosition).currentRelativeX();
        doReturn(internalScrollPanelHeight).when(scrollPosition).currentRelativeY();
        doReturn(scrollPosition).when(tested).scrollPosition();
        doNothing().when(tested).synchronizeScrollSize();
        doNothing().when(tested).setScrollBarsPosition(anyDouble(),
                                                       anyDouble());

        tested.refreshScrollPosition();

        verify(tested).setScrollBarsPosition(internalScrollPanelWidth,
                                             internalScrollPanelHeight);
    }

    @Test
    public void testUpdateLienzoPositionWithPositions() {

        final Layer layer = mock(Layer.class);
        final Viewport viewport = mock(Viewport.class);
        final Transform transform = mock(Transform.class);
        final Transform copy = mock(Transform.class);
        final Transform translate = mock(Transform.class);
        final Double oldTranslateX = 200d;
        final Double oldTranslateY = 200d;
        final Double scaleX = 2d;
        final Double scaleY = 2d;
        final Double currentXPosition = 500d;
        final Double currentYPosition = 500d;

        doReturn(layer).when(tested).getLayer();
        doReturn(viewport).when(layer).getViewport();
        doReturn(transform).when(viewport).getTransform();
        doReturn(oldTranslateX).when(transform).getTranslateX();
        doReturn(oldTranslateY).when(transform).getTranslateY();
        doReturn(scaleX).when(transform).getScaleX();
        doReturn(scaleY).when(transform).getScaleY();
        doReturn(copy).when(transform).copy();
        doReturn(translate).when(copy).translate(anyDouble(),
                                                 anyDouble());

        tested.updateLienzoPosition(currentXPosition,
                                    currentYPosition);

        verify(layer).batch();
        verify(viewport).setTransform(translate);
        verify(copy).translate(-100d,
                               -100d);
    }

    @Test
    public void testSetScrollBarsPosition() {

        final ScrollBars scrollBars = mock(ScrollBars.class);
        final Double xPercentage = 42d;
        final Double yPercentage = 58d;

        doReturn(scrollBars).when(tested).scrollBars();

        tested.setScrollBarsPosition(xPercentage,
                                     yPercentage);

        verify(scrollBars).setHorizontalScrollPosition(xPercentage);
        verify(scrollBars).setVerticalScrollPosition(yPercentage);
    }

    @Test
    public void testGetScrollPanel() {

        final AbsolutePanel expectedPanel = mock(AbsolutePanel.class);

        doReturn(expectedPanel).when(panel).getScrollPanel();

        final AbsolutePanel actualPanel = tested.getScrollPanel();

        assertEquals(expectedPanel,
                     actualPanel);
    }

    @Test
    public void testGetInternalScrollPanel() {

        final AbsolutePanel expectedPanel = mock(AbsolutePanel.class);

        doReturn(expectedPanel).when(panel).getInternalScrollPanel();

        final AbsolutePanel actualPanel = tested.getInternalScrollPanel();

        assertEquals(expectedPanel,
                     actualPanel);
    }

    @Test
    public void testGetDomElementContainer() {

        final AbsolutePanel expectedPanel = mock(AbsolutePanel.class);

        doReturn(expectedPanel).when(panel).getDomElementContainer();

        final AbsolutePanel actualPanel = tested.getDomElementContainer();

        assertEquals(expectedPanel,
                     actualPanel);
    }

    @Test
    public void testScrollbarWidth() {

        final AbsolutePanel scrollPanel = mock(AbsolutePanel.class);
        final Element element = mock(Element.class);
        final Integer offsetWidth = 1014;
        final Integer clientWidth = 1000;

        doReturn(offsetWidth).when(element).getOffsetWidth();
        doReturn(clientWidth).when(element).getClientWidth();
        doReturn(element).when(scrollPanel).getElement();
        doReturn(scrollPanel).when(tested).getScrollPanel();

        final Integer expectedScrollbarWidth = offsetWidth - clientWidth;
        final Integer actualScrollbarWidth = tested.scrollbarWidth();

        assertEquals(expectedScrollbarWidth,
                     actualScrollbarWidth);
    }

    @Test
    public void testScrollbarHeight() {

        final AbsolutePanel scrollPanel = mock(AbsolutePanel.class);
        final Element element = mock(Element.class);
        final Integer offsetHeight = 1014;
        final Integer clientHeight = 1000;

        doReturn(offsetHeight).when(element).getOffsetHeight();
        doReturn(clientHeight).when(element).getClientHeight();
        doReturn(element).when(scrollPanel).getElement();
        doReturn(scrollPanel).when(tested).getScrollPanel();

        final Integer expectedScrollbarHeight = offsetHeight - clientHeight;
        final Integer actualScrollbarHeight = tested.scrollbarHeight();

        assertEquals(expectedScrollbarHeight,
                     actualScrollbarHeight);
    }

    @Test
    public void testSetupContextSwitcher() {

        final AbsolutePanel domElementContainer = mock(AbsolutePanel.class);
        final LienzoPanel lienzoPanel = mock(LienzoPanel.class);
        final MouseWheelHandler wheelHandler = mock(MouseWheelHandler.class);
        final MouseMoveHandler moveHandler = mock(MouseMoveHandler.class);

        doReturn(domElementContainer).when(panel).getDomElementContainer();
        doReturn(wheelHandler).when(tested).disablePointerEvents();
        doReturn(moveHandler).when(tested).enablePointerEvents();

        tested.setupContextSwitcher();

        verify(domElementContainer).addDomHandler(wheelHandler, MouseWheelEvent.getType());
        verify(panel).addMouseMoveHandler(moveHandler);
    }

    @Test
    public void testEnablePointerEvents() {

        final MouseMoveEvent mouseMoveEvent = mock(MouseMoveEvent.class);
        final AbsolutePanel domElementContainer = mock(AbsolutePanel.class);
        final ScrollUI scrollUI = mock(ScrollUI.class);

        doReturn(domElementContainer).when(panel).getDomElementContainer();
        doReturn(scrollUI).when(tested).scrollUI();
        doNothing().when(scrollUI).disablePointerEvents(any(Widget.class));

        tested.enablePointerEvents().onMouseMove(mouseMoveEvent);

        verify(scrollUI).enablePointerEvents(domElementContainer);
    }

    @Test
    public void testDisablePointerEvents() {

        final MouseWheelEvent mouseWheelEvent = mock(MouseWheelEvent.class);
        final AbsolutePanel domElementContainer = mock(AbsolutePanel.class);
        final ScrollUI scrollUI = mock(ScrollUI.class);

        doReturn(domElementContainer).when(panel).getDomElementContainer();
        doReturn(scrollUI).when(tested).scrollUI();
        doNothing().when(scrollUI).disablePointerEvents(any(Widget.class));

        tested.disablePointerEvents().onMouseWheel(mouseWheelEvent);

        verify(scrollUI).disablePointerEvents(domElementContainer);
    }

    @Test
    public void testViewportScaleChangeHandler() {
        ScrollablePanel panel = mock(ScrollablePanel.class);
        ScrollablePanelHandler panelHandler = mock(ScrollablePanelHandler.class);
        when(panelHandler.getPanel()).thenReturn(panel);
        ScrollablePanelHandler.ViewportScaleChangeHandler scaleChangeHandler = new ScrollablePanelHandler.ViewportScaleChangeHandler(panelHandler);
        ViewportTransformChangedEvent event = mock(ViewportTransformChangedEvent.class);
        Viewport viewport = mock(Viewport.class);
        when(event.getViewport()).thenReturn(viewport);
        Transform viewportTransform = new Transform();
        viewportTransform.scale(0.1, 0.3);
        when(viewport.getTransform()).thenReturn(viewportTransform);
        ScrollBars scrollBars = mock(ScrollBars.class);
        when(panelHandler.scrollBars()).thenReturn(scrollBars);
        when(scrollBars.getHorizontalScrollPosition()).thenReturn(0.01);
        when(scrollBars.getVerticalScrollPosition()).thenReturn(0.03);
        scaleChangeHandler.onViewportTransformChanged(event);
        verify(panelHandler, times(1)).refresh();
        verify(panel, times(1)).fireLienzoPanelScaleChangedEvent();
        verify(panel, times(1)).fireLienzoPanelScrollEvent(eq(0.01), eq(0.03));
    }

    private Viewport viewportMock() {

        final Viewport viewport = mock(Viewport.class);
        final DivElement divElement = mock(DivElement.class);
        final Style style = mock(Style.class);

        doReturn(style).when(divElement).getStyle();
        doReturn(divElement).when(viewport).getElement();
        doReturn(transformMock()).when(viewport).getTransform();

        return viewport;
    }

    public Transform transformMock() {

        final Transform transform = mock(Transform.class);

        doReturn(transform).when(transform).getInverse();

        return transform;
    }

    private NodeMouseDownEvent mouseDownEventMock() {

        final NodeMouseDownEvent mouseDownEvent = mock(NodeMouseDownEvent.class);

        doReturn(NodeMouseDownEvent.getType()).when(mouseDownEvent).getAssociatedType();

        return mouseDownEvent;
    }

    private NodeMouseMoveEvent mouseMoveEventMock() {

        final NodeMouseMoveEvent mouseMoveEvent = mock(NodeMouseMoveEvent.class);

        doReturn(NodeMouseMoveEvent.getType()).when(mouseMoveEvent).getAssociatedType();

        return mouseMoveEvent;
    }
}
