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
import java.util.List;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
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
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState.GridWidgetHandlersOperation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class GridWidgetDnDMouseDownHandlerTest {

    @Mock
    private GridLayer layer;

    @Mock
    private Viewport viewport;

    @Mock
    private DivElement element;

    @Mock
    private Style style;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private BaseGridRendererHelper helper;

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
        when(layer.getViewport()).thenReturn(viewport);
        when(viewport.getElement()).thenReturn(element);
        when(element.getStyle()).thenReturn(style);
        when(gridWidget.getViewport()).thenReturn(viewport);
        when(gridWidget.getRendererHelper()).thenReturn(helper);
        when(gridWidget.getComputedLocation()).thenReturn(new Point2D(100,
                                                                      100));

        final GridWidgetDnDHandlersState wrappedState = new GridWidgetDnDHandlersState();
        this.state = spy(wrappedState);

        final GridWidgetDnDMouseDownHandler wrapped = new GridWidgetDnDMouseDownHandler(layer,
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
               times(1)).setOperation(GridWidgetHandlersOperation.COLUMN_MOVE);

        final List<GridColumn<?>> uiColumns = uiColumnsArgumentCaptor.getValue();
        assertNotNull(uiColumns);
        assertEquals(1,
                     uiColumns.size());
        assertTrue(uiColumns.contains(uiColumn));
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
               times(1)).setOperation(GridWidgetHandlersOperation.ROW_MOVE);

        final List<GridRow> uiRows = uiRowsArgumentCaptor.getValue();
        assertNotNull(uiRows);
        assertEquals(1,
                     uiRows.size());
        assertTrue(uiRows.contains(uiRow));
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
}
