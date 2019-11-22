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

package org.uberfire.ext.wires.core.grids.client.widget.scrollbars;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.uberfire.ext.wires.core.grids.client.widget.scrollbars.GridLienzoScrollHandler.DEFAULT_INTERNAL_SCROLL_HEIGHT;
import static org.uberfire.ext.wires.core.grids.client.widget.scrollbars.GridLienzoScrollHandler.DEFAULT_INTERNAL_SCROLL_WIDTH;

@RunWith(LienzoMockitoTestRunner.class)
public class GridLienzoScrollHandlerTest {

    @Mock
    private GridLienzoPanel gridLienzoPanel;

    @Mock
    private GridLienzoScrollPosition scrollPosition;

    @Mock
    private GridLienzoScrollBounds scrollBounds;

    @Mock
    private DefaultGridLayer defaultGridLayer;

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

    private GridLienzoScrollHandler gridLienzoScrollHandler;

    @Before
    public void setUp() {

        this.gridLienzoScrollHandler = spy(new GridLienzoScrollHandler(gridLienzoPanel));

        doReturn(transform).when(viewport).getTransform();
        doReturn(viewport).when(defaultGridLayer).getViewport();
        doReturn(scrollBounds).when(gridLienzoScrollHandler).scrollBounds();
        doReturn(scrollPosition).when(gridLienzoScrollHandler).scrollPosition();
        doReturn(defaultGridLayer).when(gridLienzoScrollHandler).getDefaultGridLayer();
    }

    @Test
    public void testInit() {

        doNothing().when(gridLienzoScrollHandler).setupGridLienzoScrollStyle();
        doNothing().when(gridLienzoScrollHandler).setupScrollBarSynchronization();
        doNothing().when(gridLienzoScrollHandler).setupMouseDragSynchronization();
        doNothing().when(gridLienzoScrollHandler).setupContextSwitcher();

        gridLienzoScrollHandler.init();

        verify(gridLienzoScrollHandler).setupGridLienzoScrollStyle();
        verify(gridLienzoScrollHandler).setupScrollBarSynchronization();
        verify(gridLienzoScrollHandler).setupMouseDragSynchronization();
        verify(gridLienzoScrollHandler).setupContextSwitcher();
    }

    @Test
    public void testSetupGridLienzoScrollStyle() {

        final GridLienzoScrollUI scrollUI = mock(GridLienzoScrollUI.class);

        doReturn(scrollUI).when(gridLienzoScrollHandler).gridLienzoScrollUI();

        gridLienzoScrollHandler.setupGridLienzoScrollStyle();

        verify(scrollUI).setup();
    }

    @Test
    public void testGridLienzoScrollUI() {
        final GridLienzoScrollUI scrollUI = gridLienzoScrollHandler.gridLienzoScrollUI();

        assertTrue(scrollUI != null);
    }

    @Test
    public void testSetupScrollBarSynchronization() {

        final AbsolutePanel scrollPanel = mock(AbsolutePanel.class);
        final ScrollHandler scrollHandler = mock(ScrollHandler.class);

        doReturn(scrollHandler).when(gridLienzoScrollHandler).onScroll();
        doReturn(scrollPanel).when(gridLienzoScrollHandler).getScrollPanel();
        doNothing().when(gridLienzoScrollHandler).synchronizeScrollSize();

        gridLienzoScrollHandler.setupScrollBarSynchronization();

        verify(gridLienzoScrollHandler).synchronizeScrollSize();
        verify(scrollPanel).addDomHandler(scrollHandler,
                                          ScrollEvent.getType());
    }

    @Test
    public void testSynchronizeScrollSize() {

        final AbsolutePanel panel = mock(AbsolutePanel.class);
        final Integer internalScrollPanelWidth = 42;
        final Integer internalScrollPanelHeight = 58;

        doReturn(internalScrollPanelWidth).when(gridLienzoScrollHandler).calculateInternalScrollPanelWidth();
        doReturn(internalScrollPanelHeight).when(gridLienzoScrollHandler).calculateInternalScrollPanelHeight();
        doReturn(panel).when(gridLienzoScrollHandler).getInternalScrollPanel();

        gridLienzoScrollHandler.synchronizeScrollSize();

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

        final Integer panelWidth = gridLienzoScrollHandler.calculateInternalScrollPanelWidth();
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

        final Integer panelWidth = gridLienzoScrollHandler.calculateInternalScrollPanelWidth();

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

        final Integer panelHeight = gridLienzoScrollHandler.calculateInternalScrollPanelHeight();
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

        final Integer panelHeight = gridLienzoScrollHandler.calculateInternalScrollPanelHeight();

        assertEquals(DEFAULT_INTERNAL_SCROLL_HEIGHT,
                     panelHeight,
                     0);
    }

    @Test
    public void testSetupMouseDragSynchronization() {

        final RestrictedMousePanMediator mediator = mock(RestrictedMousePanMediator.class);
        final LienzoPanel lienzoPanel = mock(LienzoPanel.class);
        final Viewport viewport = mock(Viewport.class);
        final Mediators mediators = mock(Mediators.class);

        doReturn(mediator).when(gridLienzoScrollHandler).makeRestrictedMousePanMediator();
        doReturn(lienzoPanel).when(gridLienzoScrollHandler).getLienzoPanel();
        doReturn(viewport).when(lienzoPanel).getViewport();
        doReturn(mediators).when(viewport).getMediators();

        gridLienzoScrollHandler.setupMouseDragSynchronization();

        verify(mediators).push(mediator);
    }

    @Test
    public void testOnScrollWhenMouseIsNotDragging() {

        final RestrictedMousePanMediator mediator = mock(RestrictedMousePanMediator.class);
        final ScrollEvent scrollEvent = mock(ScrollEvent.class);

        doReturn(false).when(mediator).isDragging();
        doReturn(mediator).when(gridLienzoScrollHandler).getMousePanMediator();
        doNothing().when(gridLienzoScrollHandler).updateGridLienzoPosition();

        final ScrollHandler scrollHandler = gridLienzoScrollHandler.onScroll();
        scrollHandler.onScroll(scrollEvent);

        verify(gridLienzoScrollHandler).updateGridLienzoPosition();
    }

    @Test
    public void testOnScrollWhenMouseIsDragging() {

        final RestrictedMousePanMediator mediator = mock(RestrictedMousePanMediator.class);
        final ScrollEvent scrollEvent = mock(ScrollEvent.class);

        doReturn(true).when(mediator).isDragging();
        doReturn(mediator).when(gridLienzoScrollHandler).getMousePanMediator();

        final ScrollHandler scrollHandler = gridLienzoScrollHandler.onScroll();
        scrollHandler.onScroll(scrollEvent);

        verify(gridLienzoScrollHandler,
               never()).updateGridLienzoPosition();
    }

    @Test
    public void testRefreshScrollPosition() {

        final GridLienzoScrollPosition scrollPosition = mock(GridLienzoScrollPosition.class);
        final Double internalScrollPanelWidth = 42d;
        final Double internalScrollPanelHeight = 58d;

        doReturn(internalScrollPanelWidth).when(scrollPosition).currentRelativeX();
        doReturn(internalScrollPanelHeight).when(scrollPosition).currentRelativeY();
        doReturn(scrollPosition).when(gridLienzoScrollHandler).scrollPosition();
        doNothing().when(gridLienzoScrollHandler).synchronizeScrollSize();
        doNothing().when(gridLienzoScrollHandler).setScrollBarsPosition(anyDouble(),
                                                                        anyDouble());

        gridLienzoScrollHandler.refreshScrollPosition();

        verify(gridLienzoScrollHandler).synchronizeScrollSize();
        verify(gridLienzoScrollHandler).setScrollBarsPosition(internalScrollPanelWidth,
                                                              internalScrollPanelHeight);
    }

    @Test
    public void testUpdateGridLienzoPosition() {

        final GridLienzoScrollBars scrollBars = mock(GridLienzoScrollBars.class);
        final GridLienzoScrollPosition scrollPosition = mock(GridLienzoScrollPosition.class);
        final Double percentageX = 40d;
        final Double percentageY = 60d;
        final Double currentXPosition = 400d;
        final Double currentYPosition = 600d;

        doReturn(scrollBars).when(gridLienzoScrollHandler).scrollBars();
        doReturn(scrollPosition).when(gridLienzoScrollHandler).scrollPosition();
        doReturn(percentageX).when(scrollBars).getHorizontalScrollPosition();
        doReturn(percentageY).when(scrollBars).getVerticalScrollPosition();
        doReturn(currentXPosition).when(scrollPosition).currentPositionX(percentageX);
        doReturn(currentYPosition).when(scrollPosition).currentPositionY(percentageY);
        doNothing().when(gridLienzoScrollHandler).updateGridLienzoTransform(anyDouble(),
                                                                            anyDouble());

        gridLienzoScrollHandler.updateGridLienzoPosition();

        verify(gridLienzoScrollHandler).updateGridLienzoTransform(currentXPosition,
                                                                  currentYPosition);
    }

    @Test
    public void testUpdateGridLienzoPositionWithPositions() {

        final DefaultGridLayer defaultGridLayer = mock(DefaultGridLayer.class);
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

        doReturn(defaultGridLayer).when(gridLienzoScrollHandler).getDefaultGridLayer();
        doReturn(viewport).when(defaultGridLayer).getViewport();
        doReturn(transform).when(viewport).getTransform();
        doReturn(oldTranslateX).when(transform).getTranslateX();
        doReturn(oldTranslateY).when(transform).getTranslateY();
        doReturn(scaleX).when(transform).getScaleX();
        doReturn(scaleY).when(transform).getScaleY();
        doReturn(copy).when(transform).copy();
        doReturn(translate).when(copy).translate(anyDouble(),
                                                 anyDouble());

        gridLienzoScrollHandler.updateGridLienzoTransform(currentXPosition,
                                                          currentYPosition);

        final Double deltaX = currentXPosition - (oldTranslateX / scaleX);
        final Double deltaY = currentYPosition - (oldTranslateY / scaleY);

        verify(defaultGridLayer).batch();
        verify(viewport).setTransform(translate);
        verify(copy).translate(deltaX,
                               deltaY);
    }

    @Test
    public void testSetScrollBarsPosition() {

        final GridLienzoScrollBars scrollBars = mock(GridLienzoScrollBars.class);
        final Double xPercentage = 42d;
        final Double yPercentage = 58d;

        doReturn(scrollBars).when(gridLienzoScrollHandler).scrollBars();

        gridLienzoScrollHandler.setScrollBarsPosition(xPercentage,
                                                      yPercentage);

        verify(scrollBars).setHorizontalScrollPosition(xPercentage);
        verify(scrollBars).setVerticalScrollPosition(yPercentage);
    }

    @Test
    public void testMakeRestrictedMousePanMediator() {

        final Viewport viewport = viewportMock();
        final DefaultGridLayer defaultGridLayer = mock(DefaultGridLayer.class);
        final RestrictedMousePanMediator restrictedMousePanMediator = spy(gridLienzoScrollHandler.makeRestrictedMousePanMediator());

        doNothing().when(gridLienzoScrollHandler).refreshScrollPosition();
        doReturn(viewport).when(restrictedMousePanMediator).getViewport();
        doReturn(defaultGridLayer).when(gridLienzoScrollHandler).getDefaultGridLayer();
        doReturn(viewport).when(defaultGridLayer).getViewport();

        restrictedMousePanMediator.handleEvent(mouseDownEventMock());
        restrictedMousePanMediator.handleEvent(mouseMoveEventMock());

        verify(gridLienzoScrollHandler).refreshScrollPosition();
    }

    @Test
    public void testGetMousePanMediator() {

        final RestrictedMousePanMediator expectedMediator = mock(RestrictedMousePanMediator.class);
        final LienzoPanel lienzoPanel = mock(LienzoPanel.class);
        final Viewport viewport = mock(Viewport.class);
        final Mediators mediators = mock(Mediators.class);

        doReturn(lienzoPanel).when(gridLienzoScrollHandler).getLienzoPanel();
        doReturn(viewport).when(lienzoPanel).getViewport();
        doReturn(mediators).when(viewport).getMediators();
        doReturn(expectedMediator).when(gridLienzoScrollHandler).makeRestrictedMousePanMediator();

        gridLienzoScrollHandler.setupMouseDragSynchronization();

        final RestrictedMousePanMediator actualMediator = gridLienzoScrollHandler.getMousePanMediator();

        assertEquals(expectedMediator,
                     actualMediator);
    }

    @Test
    public void testGetScrollPanel() {

        final AbsolutePanel expectedPanel = mock(AbsolutePanel.class);

        doReturn(expectedPanel).when(gridLienzoPanel).getScrollPanel();

        final AbsolutePanel actualPanel = gridLienzoScrollHandler.getScrollPanel();

        assertEquals(expectedPanel,
                     actualPanel);
    }

    @Test
    public void testGetInternalScrollPanel() {

        final AbsolutePanel expectedPanel = mock(AbsolutePanel.class);

        doReturn(expectedPanel).when(gridLienzoPanel).getInternalScrollPanel();

        final AbsolutePanel actualPanel = gridLienzoScrollHandler.getInternalScrollPanel();

        assertEquals(expectedPanel,
                     actualPanel);
    }

    @Test
    public void testGetDomElementContainer() {

        final AbsolutePanel expectedPanel = mock(AbsolutePanel.class);

        doReturn(expectedPanel).when(gridLienzoPanel).getDomElementContainer();

        final AbsolutePanel actualPanel = gridLienzoScrollHandler.getDomElementContainer();

        assertEquals(expectedPanel,
                     actualPanel);
    }

    @Test
    public void testGetLienzoPanel() {

        final LienzoPanel expectedPanel = mock(LienzoPanel.class);

        doReturn(expectedPanel).when(gridLienzoPanel).getLienzoPanel();

        final LienzoPanel actualPanel = gridLienzoScrollHandler.getLienzoPanel();

        assertEquals(expectedPanel,
                     actualPanel);
    }

    @Test
    public void testEmptyLayerReuse() {
        Assertions.assertThat(gridLienzoScrollHandler.emptyLayer()).isEqualTo(gridLienzoScrollHandler.emptyLayer());
    }

    @Test
    public void testScrollBarsReuse() {
        Assertions.assertThat(gridLienzoScrollHandler.scrollBars()).isEqualTo(gridLienzoScrollHandler.scrollBars());
    }

    @Test
    public void testScrollPositionReuse() {
        Assertions.assertThat(gridLienzoScrollHandler.scrollPosition()).isEqualTo(gridLienzoScrollHandler.scrollPosition());
    }

    @Test
    public void testScrollBoundsReuse() {
        Assertions.assertThat(gridLienzoScrollHandler.scrollBounds()).isEqualTo(gridLienzoScrollHandler.scrollBounds());
    }

    @Test
    public void testGetDefaultGridLayerWhenLienzoGridLayerIsNotNull() {
        final DefaultGridLayer actualLayer = gridLienzoScrollHandler.getDefaultGridLayer();

        verify(gridLienzoScrollHandler, never()).emptyLayer();

        assertEquals(defaultGridLayer,
                     actualLayer);
    }

    @Test
    public void testGetDefaultGridLayerWhenLienzoGridLayerIsNull() {

        final DefaultGridLayer expectedLayer = mock(DefaultGridLayer.class);

        doReturn(null).when(gridLienzoPanel).getDefaultGridLayer();
        doReturn(expectedLayer).when(gridLienzoScrollHandler).emptyLayer();
        doCallRealMethod().when(gridLienzoScrollHandler).getDefaultGridLayer();

        final DefaultGridLayer actualLayer = gridLienzoScrollHandler.getDefaultGridLayer();

        assertEquals(expectedLayer,
                     actualLayer);
    }

    @Test
    public void testEmptyLayer() {
        assertTrue(gridLienzoScrollHandler.emptyLayer() != null);
    }

    @Test
    public void testScrollBars() {
        assertTrue(gridLienzoScrollHandler.scrollBars() != null);
    }

    @Test
    public void testScrollPosition() {
        assertTrue(gridLienzoScrollHandler.scrollPosition() != null);
    }

    @Test
    public void testScrollBounds() {
        assertTrue(gridLienzoScrollHandler.scrollBounds() != null);
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
        doReturn(scrollPanel).when(gridLienzoScrollHandler).getScrollPanel();

        final Integer expectedScrollbarWidth = offsetWidth - clientWidth;
        final Integer actualScrollbarWidth = gridLienzoScrollHandler.scrollbarWidth();

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
        doReturn(scrollPanel).when(gridLienzoScrollHandler).getScrollPanel();

        final Integer expectedScrollbarHeight = offsetHeight - clientHeight;
        final Integer actualScrollbarHeight = gridLienzoScrollHandler.scrollbarHeight();

        assertEquals(expectedScrollbarHeight,
                     actualScrollbarHeight);
    }

    @Test
    public void testSetupContextSwitcher() {

        final AbsolutePanel domElementContainer = mock(AbsolutePanel.class);
        final LienzoPanel lienzoPanel = mock(LienzoPanel.class);
        final MouseWheelHandler wheelHandler = mock(MouseWheelHandler.class);
        final MouseMoveHandler moveHandler = mock(MouseMoveHandler.class);

        doReturn(domElementContainer).when(gridLienzoPanel).getDomElementContainer();
        doReturn(wheelHandler).when(gridLienzoScrollHandler).disablePointerEvents();
        doReturn(moveHandler).when(gridLienzoScrollHandler).enablePointerEvents();

        gridLienzoScrollHandler.setupContextSwitcher();

        verify(domElementContainer).addDomHandler(wheelHandler, MouseWheelEvent.getType());
        verify(gridLienzoPanel).addMouseMoveHandler(moveHandler);
    }

    @Test
    public void testEnablePointerEvents() {

        final MouseMoveEvent mouseMoveEvent = mock(MouseMoveEvent.class);
        final AbsolutePanel domElementContainer = mock(AbsolutePanel.class);
        final GridLienzoScrollUI scrollUI = mock(GridLienzoScrollUI.class);

        doReturn(domElementContainer).when(gridLienzoPanel).getDomElementContainer();
        doReturn(scrollUI).when(gridLienzoScrollHandler).gridLienzoScrollUI();
        doNothing().when(scrollUI).disablePointerEvents(any());

        gridLienzoScrollHandler.enablePointerEvents().onMouseMove(mouseMoveEvent);

        verify(scrollUI).enablePointerEvents(domElementContainer);
    }

    @Test
    public void testDisablePointerEvents() {

        final MouseWheelEvent mouseWheelEvent = mock(MouseWheelEvent.class);
        final AbsolutePanel domElementContainer = mock(AbsolutePanel.class);
        final GridLienzoScrollUI scrollUI = mock(GridLienzoScrollUI.class);

        doReturn(domElementContainer).when(gridLienzoPanel).getDomElementContainer();
        doReturn(scrollUI).when(gridLienzoScrollHandler).gridLienzoScrollUI();
        doNothing().when(scrollUI).disablePointerEvents(any());

        gridLienzoScrollHandler.disablePointerEvents().onMouseWheel(mouseWheelEvent);

        verify(scrollUI).disablePointerEvents(domElementContainer);
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
