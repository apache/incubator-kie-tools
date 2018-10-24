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

import java.util.ArrayList;
import java.util.Collections;

import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.Command;
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
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseGridWidgetMouseDoubleClickHandlerTest {

    @Mock
    private GridWidget gridWidget;

    @Mock
    private Group header;

    @Mock
    private Viewport viewport;

    @Mock
    private DefaultGridLayer layer;

    @Mock
    private GridSelectionManager selectionManager;

    @Mock
    private GridPinnedModeManager pinnedModeManager;

    @Mock
    private GridRenderer renderer;

    @Mock
    private NodeMouseDoubleClickEvent event;

    @Mock
    private GridData uiModel;

    @Mock
    private BaseGridRendererHelper helper;

    @Mock
    private GridColumn<String> uiColumn;

    private BaseGridWidgetMouseDoubleClickHandler handler;

    @Before
    public void setup() {
        when(gridWidget.getViewport()).thenReturn(viewport);
        when(gridWidget.getModel()).thenReturn(uiModel);
        when(gridWidget.getRenderer()).thenReturn(renderer);
        when(gridWidget.getRendererHelper()).thenReturn(helper);
        when(gridWidget.getLayer()).thenReturn(layer);
        when(gridWidget.getHeader()).thenReturn(header);
        when(renderer.getHeaderHeight()).thenReturn(64.0);
        when(renderer.getHeaderRowHeight()).thenReturn(32.0);
        when(uiModel.getHeaderRowCount()).thenReturn(2);
        when(uiModel.getColumnCount()).thenReturn(1);
        when(uiModel.getColumns()).thenReturn(new ArrayList<GridColumn<?>>() {{
            add(uiColumn);
        }});

        final BaseGridRendererHelper.RenderingInformation ri = BaseGridWidgetRenderingTestUtils.makeRenderingInformation(uiModel, Collections.emptyList());
        when(helper.getRenderingInformation()).thenReturn(ri);

        final BaseGridWidgetMouseDoubleClickHandler wrapped = new BaseGridWidgetMouseDoubleClickHandler(gridWidget,
                                                                                                        selectionManager,
                                                                                                        pinnedModeManager,
                                                                                                        renderer);
        handler = spy(wrapped);
    }

    @Test
    public void skipInvisibleGrid() {
        when(gridWidget.isVisible()).thenReturn(false);

        handler.onNodeMouseDoubleClick(event);

        verify(handler,
               never()).handleHeaderCellDoubleClick(any(NodeMouseDoubleClickEvent.class));
        verify(handler,
               never()).handleBodyCellDoubleClick(any(NodeMouseDoubleClickEvent.class));
        verify(selectionManager,
               never()).select(eq(gridWidget));
    }

    @Test
    public void enterPinnedMode() {
        when(gridWidget.isVisible()).thenReturn(true);

        when(event.getX()).thenReturn(100);
        when(event.getY()).thenReturn(100);

        when(gridWidget.getAbsoluteX()).thenReturn(100.0);
        when(gridWidget.getAbsoluteY()).thenReturn(100.0);

        handler.onNodeMouseDoubleClick(event);

        verify(handler,
               times(1)).handleHeaderCellDoubleClick(any(NodeMouseDoubleClickEvent.class));
        verify(handler,
               never()).handleBodyCellDoubleClick(any(NodeMouseDoubleClickEvent.class));
        verify(pinnedModeManager,
               times(1)).enterPinnedMode(eq(gridWidget),
                                         any(Command.class));
        verify(pinnedModeManager,
               never()).exitPinnedMode(any(Command.class));
    }

    @Test
    public void exitPinnedMode() {
        when(gridWidget.isVisible()).thenReturn(true);
        when(pinnedModeManager.isGridPinned()).thenReturn(true);

        when(event.getX()).thenReturn(100);
        when(event.getY()).thenReturn(100);

        when(gridWidget.getAbsoluteX()).thenReturn(100.0);
        when(gridWidget.getAbsoluteY()).thenReturn(100.0);

        handler.onNodeMouseDoubleClick(event);

        verify(handler,
               times(1)).handleHeaderCellDoubleClick(any(NodeMouseDoubleClickEvent.class));
        verify(handler,
               never()).handleBodyCellDoubleClick(any(NodeMouseDoubleClickEvent.class));
        verify(pinnedModeManager,
               never()).enterPinnedMode(any(GridWidget.class),
                                        any(Command.class));
        verify(pinnedModeManager,
               times(1)).exitPinnedMode(any(Command.class));
    }

    @Test
    public void basicCheckForBodyHandler() {
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

        handler.onNodeMouseDoubleClick(event);

        verify(handler,
               times(1)).handleHeaderCellDoubleClick(any(NodeMouseDoubleClickEvent.class));
        verify(handler,
               times(1)).handleBodyCellDoubleClick(any(NodeMouseDoubleClickEvent.class));
    }
}
