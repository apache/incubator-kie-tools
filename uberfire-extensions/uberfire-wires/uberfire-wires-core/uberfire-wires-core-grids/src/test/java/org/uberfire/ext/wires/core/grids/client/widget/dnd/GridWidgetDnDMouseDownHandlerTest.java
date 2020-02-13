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

package org.uberfire.ext.wires.core.grids.client.widget.dnd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState.GridWidgetHandlersOperation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class GridWidgetDnDMouseDownHandlerTest {

    private static final double GRID_X = 10.0;

    private static final double GRID_Y = 20.0;

    private static final double GRID_WIDTH = 100.0;

    private static final double GRID_HEIGHT = 200.0;

    private static final double COLUMN_WIDTH = 25.0;

    private static final double HEADER_HEIGHT = 25.0;

    private static final double ROW_HEIGHT = 32.0;

    @Mock
    private GridLayer gridLayer;

    @Mock
    private Layer layer;

    @Mock
    private Viewport viewport;

    @Mock
    private DivElement element;

    @Mock
    private Style style;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private Group gridWidgetHeader;

    @Mock
    private GridRenderer gridRenderer;

    @Mock
    private BaseGridRendererHelper rendererHelper;

    @Mock
    private BaseGridRendererHelper.RenderingInformation renderingInformation;

    @Mock
    private GridColumn<String> uiColumn;

    @Mock
    private GridRow uiRow;

    @Mock
    private NodeMouseDownEvent event;

    @Captor
    private ArgumentCaptor<List<GridColumn<?>>> uiColumnsArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<GridRow>> uiRowsArgumentCaptor;

    private GridWidgetDnDHandlersState state;

    private GridWidgetDnDMouseDownHandler handler;

    @Before
    public void setup() {
        when(gridLayer.getLayer()).thenReturn(layer);
        when(gridLayer.getViewport()).thenReturn(viewport);
        when(viewport.getElement()).thenReturn(element);
        when(element.getStyle()).thenReturn(style);
        when(gridWidget.getViewport()).thenReturn(viewport);
        when(gridWidget.getRenderer()).thenReturn(gridRenderer);
        when(gridWidget.getRendererHelper()).thenReturn(rendererHelper);
        when(gridWidget.getComputedLocation()).thenReturn(new Point2D(GRID_X, GRID_Y));
        when(gridRenderer.getHeaderHeight()).thenReturn(HEADER_HEIGHT);

        final GridWidgetDnDHandlersState wrappedState = new GridWidgetDnDHandlersState();
        this.state = spy(wrappedState);

        final GridWidgetDnDMouseDownHandler wrapped = new GridWidgetDnDMouseDownHandler(gridLayer,
                                                                                        state);
        this.handler = spy(wrapped);
    }

    @Test
    public void skipNonActiveGrid() {
        when(state.getActiveGridWidget()).thenReturn(null);

        handler.onNodeMouseDown(event);

        // This is the only reasonable check that "nothing" happened; as the implementation calls
        // these directly after the check for an "active grid widget" has succeeded.
        verify(event,
               never()).getX();
        verify(event,
               never()).getY();
    }

    @Test
    public void stateColumnResizePendingMovesToColumnResize() {
        when(state.getActiveGridWidget()).thenReturn(gridWidget);
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.COLUMN_RESIZE_PENDING);
        when(state.getActiveGridColumns()).thenReturn(new ArrayList<GridColumn<?>>() {{
            add(uiColumn);
        }});

        handler.onNodeMouseDown(event);

        verify(state,
               times(1)).setOperation(GridWidgetHandlersOperation.COLUMN_RESIZE);
    }

    @Test
    public void stateColumnResizePendingWithNoActiveColumn() {
        when(state.getActiveGridWidget()).thenReturn(gridWidget);
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.COLUMN_RESIZE_PENDING);
        when(state.getActiveGridColumns()).thenReturn(Collections.emptyList());

        handler.onNodeMouseDown(event);

        verify(state, never()).setOperation(any(GridWidgetHandlersOperation.class));
    }

    @Test
    public void stateColumnMovePendingMovesToColumnMove() {
        when(state.getActiveGridWidget()).thenReturn(gridWidget);
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.COLUMN_MOVE_PENDING);
        when(state.getActiveGridColumns()).thenReturn(new ArrayList<GridColumn<?>>() {{
            add(uiColumn);
        }});

        handler.onNodeMouseDown(event);

        verify(handler,
               times(1)).showColumnHighlight(eq(gridWidget),
                                             uiColumnsArgumentCaptor.capture());
        verify(state,
               times(1)).setOperation(GridWidgetHandlersOperation.COLUMN_MOVE_INITIATED);

        final List<GridColumn<?>> uiColumns = uiColumnsArgumentCaptor.getValue();
        assertNotNull(uiColumns);
        assertEquals(1,
                     uiColumns.size());
        assertTrue(uiColumns.contains(uiColumn));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void stateColumnMovePendingWithNoActiveColumns() {
        when(state.getActiveGridWidget()).thenReturn(gridWidget);
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.COLUMN_MOVE_PENDING);
        when(state.getActiveGridColumns()).thenReturn(Collections.emptyList());

        handler.onNodeMouseDown(event);

        verify(handler, never()).showColumnHighlight(any(GridWidget.class),
                                                     any(List.class));
    }

    @Test
    public void stateRowMovePendingMovesToRowMove() {
        when(state.getActiveGridWidget()).thenReturn(gridWidget);
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.ROW_MOVE_PENDING);
        when(state.getActiveGridRows()).thenReturn(new ArrayList<GridRow>() {{
            add(uiRow);
        }});

        handler.onNodeMouseDown(event);

        verify(handler,
               times(1)).showRowHighlight(eq(gridWidget),
                                          uiRowsArgumentCaptor.capture());
        verify(state,
               times(1)).setOperation(GridWidgetHandlersOperation.ROW_MOVE_INITIATED);

        final List<GridRow> uiRows = uiRowsArgumentCaptor.getValue();
        assertNotNull(uiRows);
        assertEquals(1,
                     uiRows.size());
        assertTrue(uiRows.contains(uiRow));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void stateRowMovePendingWithNoActiveRows() {
        when(state.getActiveGridWidget()).thenReturn(gridWidget);
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.ROW_MOVE_PENDING);
        when(state.getActiveGridRows()).thenReturn(Collections.emptyList());

        handler.onNodeMouseDown(event);

        verify(handler, never()).showRowHighlight(any(GridWidget.class),
                                                  any(List.class));
    }

    @Test
    public void stateGridMovePendingMovesToGridMove() {
        when(state.getActiveGridWidget()).thenReturn(gridWidget);
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.GRID_MOVE_PENDING);

        handler.onNodeMouseDown(event);

        verify(state,
               times(1)).setOperation(GridWidgetHandlersOperation.GRID_MOVE);
        verify(gridWidget,
               times(1)).setDraggable(eq(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testColumnHighlight() {
        final GridWidgetDnDProxy highlight = mock(GridWidgetDnDProxy.class);
        final Bounds bounds = new BaseBounds(GRID_X, GRID_Y, GRID_WIDTH, GRID_HEIGHT);

        when(state.getEventColumnHighlight()).thenReturn(highlight);
        when(state.getActiveGridWidget()).thenReturn(gridWidget);
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.COLUMN_MOVE_PENDING);
        when(state.getActiveGridColumns()).thenReturn(Collections.singletonList(uiColumn));

        when(rendererHelper.getRenderingInformation()).thenReturn(renderingInformation);
        when(rendererHelper.getRowOffset(anyInt(), any(List.class))).thenCallRealMethod();
        when(renderingInformation.getBounds()).thenReturn(bounds);
        when(gridWidget.getHeader()).thenReturn(gridWidgetHeader);
        when(gridWidget.getHeight()).thenReturn(GRID_HEIGHT);
        when(uiColumn.getWidth()).thenReturn(COLUMN_WIDTH);

        handler.onNodeMouseDown(event);

        verify(highlight).setWidth(COLUMN_WIDTH);
        verify(highlight).setHeight(GRID_HEIGHT);
        verify(highlight).setX(GRID_X);
        verify(highlight).setY(GRID_Y);
        verify(layer).batch();
    }

    @Test
    public void testRowHighlight() {
        final GridWidgetDnDProxy highlight = mock(GridWidgetDnDProxy.class);
        final Bounds bounds = new BaseBounds(GRID_X, GRID_Y, GRID_WIDTH, GRID_HEIGHT);
        final GridData gridData = new BaseGridData();
        gridData.appendRow(uiRow);

        when(state.getEventColumnHighlight()).thenReturn(highlight);
        when(state.getActiveGridWidget()).thenReturn(gridWidget);
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.ROW_MOVE_PENDING);
        when(state.getActiveGridRows()).thenReturn(Collections.singletonList(uiRow));

        when(rendererHelper.getRenderingInformation()).thenReturn(renderingInformation);
        when(renderingInformation.getBounds()).thenReturn(bounds);
        when(renderingInformation.getAllRowHeights()).thenReturn(Collections.singletonList(ROW_HEIGHT));
        when(gridWidget.getModel()).thenReturn(gridData);
        when(gridWidget.getHeader()).thenReturn(gridWidgetHeader);
        when(gridWidget.getWidth()).thenReturn(GRID_WIDTH);
        when(uiRow.getHeight()).thenReturn(ROW_HEIGHT);

        handler.onNodeMouseDown(event);

        verify(highlight).setWidth(GRID_WIDTH);
        verify(highlight).setHeight(ROW_HEIGHT);
        verify(highlight).setX(GRID_X);
        verify(highlight).setY(GRID_Y + HEADER_HEIGHT);
        verify(layer).batch();
    }
}
