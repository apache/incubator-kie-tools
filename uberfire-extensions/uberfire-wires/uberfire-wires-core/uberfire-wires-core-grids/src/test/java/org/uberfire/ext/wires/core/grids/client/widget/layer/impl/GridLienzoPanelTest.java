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

package org.uberfire.ext.wires.core.grids.client.widget.layer.impl;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.INativeContext2D;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.scrollbars.GridLienzoScrollHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class GridLienzoPanelTest {

    private static final int WIDTH = 100;

    private static final int HEIGHT = 200;

    @Mock
    private AbsolutePanel rootPanel;

    @Mock
    private AbsolutePanel scrollPanel;

    @Mock
    private AbsolutePanel internalScrollPanel;

    @Mock
    private AbsolutePanel domElementContainer;

    @Mock
    private GridLienzoScrollHandler gridLienzoScrollHandler;

    @Mock
    private LienzoPanel lienzoPanel;

    @Mock
    private DefaultGridLayer gridLayer;

    @Mock
    private DivElement gridLayerDivElement;

    @Mock
    private Style gridLayerDivElementStyle;

    @Mock
    private CanvasElement gridLayerCanvasElement;

    @Mock
    private Node gridLayerNode;

    @Mock
    private Context2D context2D;

    @Mock
    private INativeContext2D nativeContext2D;

    private GridLienzoPanel gridLienzoPanel;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {

        gridLienzoPanel = spy(new GridLienzoPanel());

        when(gridLienzoPanel.getRootPanel()).thenReturn(rootPanel);
        when(gridLienzoPanel.getScrollPanel()).thenReturn(scrollPanel);
        when(gridLienzoPanel.getInternalScrollPanel()).thenReturn(internalScrollPanel);
        when(gridLienzoPanel.getDomElementContainer()).thenReturn(domElementContainer);
        when(gridLienzoPanel.getLienzoPanel()).thenReturn(lienzoPanel);
        when(gridLienzoPanel.getGridLienzoScrollHandler()).thenReturn(gridLienzoScrollHandler);

        when(gridLayer.getElement()).thenReturn(gridLayerDivElement);
        when(gridLayerDivElement.getStyle()).thenReturn(gridLayerDivElementStyle);
        when(gridLayer.getCanvasElement()).thenReturn(gridLayerCanvasElement);
        when(gridLayer.getContext()).thenReturn(context2D);
        when(gridLayer.asNode()).thenReturn(gridLayerNode);
        when(context2D.getNativeContext()).thenReturn(nativeContext2D);
    }

    @Test
    public void testSetupPanels() {

        doNothing().when(gridLienzoPanel).setupScrollPanel();
        doNothing().when(gridLienzoPanel).setupDomElementContainer();
        doNothing().when(gridLienzoPanel).setupRootPanel();

        gridLienzoPanel.setupPanels();

        final InOrder inOrder = inOrder(gridLienzoPanel);

        inOrder.verify(gridLienzoPanel).setupScrollPanel();
        inOrder.verify(gridLienzoPanel).setupDomElementContainer();
        inOrder.verify(gridLienzoPanel).setupRootPanel();
        inOrder.verify(gridLienzoPanel).add(rootPanel);
    }

    @Test
    public void testSetupScrollPanel() {

        gridLienzoPanel.setupScrollPanel();

        verify(scrollPanel).add(internalScrollPanel);
    }

    @Test
    public void testSetupDomElementContainer() {

        gridLienzoPanel.setupDomElementContainer();

        verify(domElementContainer).add(lienzoPanel);
    }

    @Test
    public void testSetupRootPanel() {

        gridLienzoPanel.setupRootPanel();

        verify(rootPanel).add(domElementContainer);
        verify(rootPanel).add(scrollPanel);
    }

    @Test
    public void testSetupScrollHandlers() {

        final GridLienzoScrollHandler lienzoScrollHandler = mock(GridLienzoScrollHandler.class);

        doReturn(lienzoScrollHandler).when(gridLienzoPanel).getGridLienzoScrollHandler();

        gridLienzoPanel.setupScrollHandlers();

        verify(lienzoScrollHandler).init();
        verify(gridLienzoPanel).addMouseUpHandler();
    }

    @Test
    public void testAddMouseUpHandler() {

        final ArgumentCaptor<MouseUpHandler> handler = ArgumentCaptor.forClass(MouseUpHandler.class);
        final MouseUpEvent mouseUpEvent = mock(MouseUpEvent.class);
        final HandlerRegistration registration = mock(HandlerRegistration.class);

        doReturn(registration).when(gridLienzoPanel).addMouseUpHandler(handler.capture());
        doNothing().when(gridLienzoPanel).refreshScrollPosition();

        gridLienzoPanel.addMouseUpHandler();

        handler.getValue().onMouseUp(mouseUpEvent);

        verify(gridLienzoPanel).refreshScrollPosition();
    }

    @Test
    public void testOnResize() {

        final ArgumentCaptor<Scheduler.ScheduledCommand> scheduledCommand = ArgumentCaptor.forClass(Scheduler.ScheduledCommand.class);

        doNothing().when(gridLienzoPanel).updatePanelSize();
        doNothing().when(gridLienzoPanel).refreshScrollPosition();
        doNothing().when(gridLienzoPanel).scheduleDeferred(scheduledCommand.capture());

        gridLienzoPanel.onResize();

        scheduledCommand.getValue().execute();

        verify(gridLienzoPanel).updatePanelSize();
        verify(gridLienzoPanel).refreshScrollPosition();
    }

    @Test
    public void testUpdatePanelSizeWhenWidthAndHeightAreGreaterThanZero() {

        final Element element = mock(Element.class);
        final Element parentElement = mock(Element.class);
        final Integer scrollWidth = 14;
        final Integer scrollHeight = 14;
        final Integer width = 800;
        final Integer height = 600;

        doReturn(element).when(gridLienzoPanel).getElement();
        doReturn(parentElement).when(element).getParentElement();
        doReturn(width).when(parentElement).getOffsetWidth();
        doReturn(height).when(parentElement).getOffsetHeight();
        doReturn(scrollWidth).when(gridLienzoScrollHandler).scrollbarWidth();
        doReturn(scrollHeight).when(gridLienzoScrollHandler).scrollbarHeight();

        gridLienzoPanel.updatePanelSize();

        verify(domElementContainer).setPixelSize(width - scrollWidth,
                                                 height - scrollHeight);
        verify(lienzoPanel).setPixelSize(width - scrollWidth,
                                         height - scrollHeight);
        verify(scrollPanel).setPixelSize(width,
                                         height);
    }

    @Test
    public void testUpdatePanelSizeWhenWidthAndHeightAreNotGreaterThanZero() {

        final Element element = mock(Element.class);
        final Element parentElement = mock(Element.class);
        final Integer width = 0;
        final Integer height = 0;

        doReturn(element).when(gridLienzoPanel).getElement();
        doReturn(parentElement).when(element).getParentElement();
        doReturn(width).when(parentElement).getOffsetWidth();
        doReturn(height).when(parentElement).getOffsetHeight();

        gridLienzoPanel.updatePanelSize();

        verify(domElementContainer,
               never()).setPixelSize(anyInt(),
                                     anyInt());
        verify(lienzoPanel,
               never()).setPixelSize(anyInt(),
                                     anyInt());
        verify(scrollPanel,
               never()).setPixelSize(anyInt(),
                                     anyInt());
    }

    @Test
    public void testRefreshScrollPosition() {

        final GridLienzoScrollHandler lienzoScrollHandler = mock(GridLienzoScrollHandler.class);

        doReturn(lienzoScrollHandler).when(gridLienzoPanel).getGridLienzoScrollHandler();

        gridLienzoPanel.refreshScrollPosition();

        verify(lienzoScrollHandler).refreshScrollPosition();
    }

    @Test
    public void propagateNewPanelSize() {
        int visibleWidth = 100;
        int visibleHeight = 100;
        GridData gridData = mock(GridData.class);
        GridWidget gridWidget = mock(GridWidget.class);
        DefaultGridLayer defaultGridLayer = new DefaultGridLayer();
        defaultGridLayer.register(gridWidget);
        when(gridWidget.getModel()).thenReturn(gridData);
        gridLienzoPanel.propagateNewPanelSize(visibleWidth, visibleHeight);
        verify(gridData, never()).setVisibleSizeAndRefresh(anyInt(), anyInt());

        gridLienzoPanel.add(defaultGridLayer);
        gridLienzoPanel.propagateNewPanelSize(visibleWidth, visibleHeight);

        verify(gridData, times(1)).setVisibleSizeAndRefresh(visibleWidth, visibleHeight);
    }

    @Test
    public void testConstructorWithSizeAndDefaultGridLayer() {
        final GridLienzoPanel gridPanel = new GridLienzoPanel(WIDTH, HEIGHT, gridLayer);

        assertThat(gridPanel.getDefaultGridLayer()).isEqualTo(gridLayer);
    }

    @Test
    public void testConstructor() {
        final GridLienzoPanel gridPanel = new GridLienzoPanel(gridLayer);

        assertThat(gridPanel.getDefaultGridLayer()).isEqualTo(gridLayer);
    }

    @Test
    public void testMouseDownHandlerDoesNotSetFocus() {
        gridLienzoPanel.setupDefaultHandlers();

        verify(gridLienzoPanel, never()).addMouseDownHandler(any(MouseDownHandler.class));
    }
}
