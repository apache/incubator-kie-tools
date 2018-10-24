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
package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.Collections;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseGridWidgetMouseClickHandlerTest {

    @Mock
    private GridWidget gridWidget;

    @Mock
    private Viewport viewport;

    @Mock
    private DefaultGridLayer layer;

    @Mock
    private GridSelectionManager selectionManager;

    @Mock
    private GridRenderer renderer;

    @Mock
    private NodeMouseClickEvent event;

    @Mock
    private GridData uiModel;

    @Mock
    private BaseGridRendererHelper helper;

    @Mock
    private GridColumn<String> uiColumn;

    @Mock
    private GridColumn<String> uiLinkedColumn;

    private BaseGridWidgetMouseClickHandler handler;

    @Before
    public void setup() {
        when(gridWidget.getViewport()).thenReturn(viewport);
        when(gridWidget.getModel()).thenReturn(uiModel);
        when(gridWidget.getRendererHelper()).thenReturn(helper);
        when(gridWidget.getLayer()).thenReturn(layer);
        when(renderer.getHeaderHeight()).thenReturn(64.0);
        when(renderer.getHeaderRowHeight()).thenReturn(32.0);
        when(uiModel.getHeaderRowCount()).thenReturn(2);

        final BaseGridRendererHelper.RenderingInformation ri = BaseGridWidgetRenderingTestUtils.makeRenderingInformation(uiModel, Collections.emptyList());
        when(helper.getRenderingInformation()).thenReturn(ri);

        final BaseGridWidgetMouseClickHandler wrapped = new BaseGridWidgetMouseClickHandler(gridWidget,
                                                                                            selectionManager,
                                                                                            renderer);
        handler = spy(wrapped);
    }

    @Test
    public void skipInvisibleGrid() {
        when(gridWidget.isVisible()).thenReturn(false);

        handler.onNodeMouseClick(event);

        verify(handler,
               never()).handleHeaderCellClick(any(NodeMouseClickEvent.class));
        verify(handler,
               never()).handleBodyCellClick(any(NodeMouseClickEvent.class));
        verify(selectionManager,
               never()).select(eq(gridWidget));
    }

    @Test
    public void selectVisibleGridHeaderNonLinkedColumn() {
        when(gridWidget.isVisible()).thenReturn(true);

        when(event.getX()).thenReturn(100);
        when(event.getY()).thenReturn(100);

        when(gridWidget.getLocation()).thenReturn(new Point2D(100,
                                                              100));
        when(uiColumn.isLinked()).thenReturn(false);

        final BaseGridRendererHelper.ColumnInformation ci = new BaseGridRendererHelper.ColumnInformation(uiColumn,
                                                                                                         0,
                                                                                                         0);
        when(helper.getColumnInformation(any(Double.class))).thenReturn(ci);

        handler.onNodeMouseClick(event);

        verify(handler,
               times(1)).handleHeaderCellClick(any(NodeMouseClickEvent.class));
        verify(handler,
               times(1)).handleBodyCellClick(any(NodeMouseClickEvent.class));
        verify(selectionManager,
               times(1)).select(eq(gridWidget));
        verify(selectionManager,
               never()).selectLinkedColumn(eq(uiColumn));
    }

    @Test
    public void selectVisibleGridHeaderLinkedColumn() {
        when(gridWidget.isVisible()).thenReturn(true);

        when(event.getX()).thenReturn(100);
        when(event.getY()).thenReturn(100);

        when(gridWidget.getAbsoluteX()).thenReturn(100.0);
        when(gridWidget.getAbsoluteY()).thenReturn(100.0);
        when(uiColumn.isLinked()).thenReturn(true);
        when(uiColumn.getLink()).thenAnswer(invocation -> uiLinkedColumn);

        final BaseGridRendererHelper.ColumnInformation ci = new BaseGridRendererHelper.ColumnInformation(uiColumn,
                                                                                                         0,
                                                                                                         0);
        when(helper.getColumnInformation(any(Double.class))).thenReturn(ci);

        handler.onNodeMouseClick(event);

        verify(handler,
               times(1)).handleHeaderCellClick(any(NodeMouseClickEvent.class));
        verify(handler,
               never()).handleBodyCellClick(any(NodeMouseClickEvent.class));
        verify(selectionManager,
               never()).select(eq(gridWidget));
        verify(selectionManager,
               times(1)).selectLinkedColumn(eq(uiLinkedColumn));
    }

    @Test
    public void selectVisibleGridBody() {
        when(gridWidget.isVisible()).thenReturn(true);

        when(event.getX()).thenReturn(100);
        when(event.getY()).thenReturn(200);

        when(gridWidget.getLocation()).thenReturn(new Point2D(100,
                                                              100));
        when(gridWidget.getHeight()).thenReturn(200.0);

        final BaseGridRendererHelper.ColumnInformation ci = new BaseGridRendererHelper.ColumnInformation(uiColumn,
                                                                                                         0,
                                                                                                         0);
        when(helper.getColumnInformation(any(Double.class))).thenReturn(ci);

        handler.onNodeMouseClick(event);

        verify(handler,
               times(1)).handleHeaderCellClick(any(NodeMouseClickEvent.class));
        verify(handler,
               times(1)).handleBodyCellClick(any(NodeMouseClickEvent.class));
        verify(selectionManager,
               times(1)).select(eq(gridWidget));
    }
}
