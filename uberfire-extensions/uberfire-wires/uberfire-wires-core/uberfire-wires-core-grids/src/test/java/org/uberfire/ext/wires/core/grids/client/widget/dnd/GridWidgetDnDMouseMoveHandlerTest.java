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
import java.util.HashSet;
import java.util.List;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.mediator.Mediators;
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
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState.GridWidgetHandlersOperation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class GridWidgetDnDMouseMoveHandlerTest {

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
    private GridRenderer renderer;

    @Mock
    private GridColumnRenderer<String> columnRenderer;

    @Mock
    private BaseGridRendererHelper helper;

    @Mock
    private NodeMouseMoveEvent event;

    @Captor
    private ArgumentCaptor<List<GridColumn<?>>> uiColumnsArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<GridRow>> uiRowsArgumentCaptor;

    private BaseGridData uiModel;

    private BaseGridColumn uiColumn1;
    private BaseGridColumn<String> uiColumn2;

    private GridWidgetDnDHandlersState state;

    private Mediators mediators;

    private GridWidgetDnDMouseMoveHandler handler;

    @Before
    public void setup() {
        this.uiColumn1 = new RowNumberColumn();
        this.uiColumn2 = new BaseGridColumn<>(new BaseHeaderMetaData("title"),
                                              columnRenderer,
                                              100.0);
        this.uiModel = new BaseGridData() {{
            setHeaderRowCount(2);
        }};
        uiModel.appendColumn(uiColumn1);
        uiModel.appendColumn(uiColumn2);
        uiModel.appendRow(new BaseGridRow());
        uiModel.appendRow(new BaseGridRow());
        uiModel.appendRow(new BaseGridRow());

        this.mediators = new Mediators(viewport);

        when(renderer.getHeaderHeight()).thenReturn(64.0);
        when(renderer.getHeaderRowHeight()).thenReturn(32.0);

        when(layer.getViewport()).thenReturn(viewport);
        when(viewport.getElement()).thenReturn(element);
        when(viewport.getMediators()).thenReturn(mediators);
        when(element.getStyle()).thenReturn(style);
        when(gridWidget.getModel()).thenReturn(uiModel);
        when(gridWidget.getViewport()).thenReturn(viewport);
        when(gridWidget.getRenderer()).thenReturn(renderer);
        when(gridWidget.getRendererHelper()).thenReturn(helper);
        when(gridWidget.getWidth()).thenReturn(150.0);
        when(gridWidget.getHeight()).thenReturn(124.0);
        when(gridWidget.getLocation()).thenReturn(new Point2D(100,
                                                              100));

        final BaseGridRendererHelper.RenderingInformation ri = new BaseGridRendererHelper.RenderingInformation(mock(Bounds.class),
                                                                                                               uiModel.getColumns(),
                                                                                                               new BaseGridRendererHelper.RenderingBlockInformation(uiModel.getColumns(),
                                                                                                                                                                    0.0,
                                                                                                                                                                    0.0,
                                                                                                                                                                    0.0,
                                                                                                                                                                    100),
                                                                                                               new BaseGridRendererHelper.RenderingBlockInformation(Collections.emptyList(),
                                                                                                                                                                    0.0,
                                                                                                                                                                    0.0,
                                                                                                                                                                    0.0,
                                                                                                                                                                    0.0),
                                                                                                               0,
                                                                                                               2,
                                                                                                               new ArrayList<Double>() {{
                                                                                                                   add(20.0);
                                                                                                                   add(40.0);
                                                                                                                   add(60.0);
                                                                                                               }},
                                                                                                               false,
                                                                                                               false,
                                                                                                               0,
                                                                                                               2,
                                                                                                               0);
        when(helper.getRenderingInformation()).thenReturn(ri);

        final GridWidgetDnDHandlersState wrappedState = new GridWidgetDnDHandlersState();
        this.state = spy(wrappedState);

        final GridWidgetDnDMouseMoveHandler wrapped = new GridWidgetDnDMouseMoveHandler(layer,
                                                                                        state);
        this.handler = spy(wrapped);
    }

    @Test
    public void findGridColumnWithEmptyLayer() {
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.NONE);

        handler.onNodeMouseMove(event);

        verify(handler,
               times(1)).findGridColumn(eq(event));

        verify(handler,
               never()).findMovableColumns(any(GridWidget.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class));
        verify(handler,
               never()).findMovableRows(any(GridWidget.class),
                                        any(Double.class),
                                        any(Double.class));
        verify(handler,
               never()).findResizableColumn(any(GridWidget.class),
                                            any(Double.class));
    }

    @Test
    public void findGridColumnWithInvisibleGridWidgets() {
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.NONE);
        when(gridWidget.isVisible()).thenReturn(false);
        when(layer.getGridWidgets()).thenReturn(new HashSet<GridWidget>() {{
            add(gridWidget);
        }});

        handler.onNodeMouseMove(event);

        verify(handler,
               times(1)).findGridColumn(eq(event));

        verify(handler,
               never()).findMovableColumns(any(GridWidget.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class));
        verify(handler,
               never()).findMovableRows(any(GridWidget.class),
                                        any(Double.class),
                                        any(Double.class));
        verify(handler,
               never()).findResizableColumn(any(GridWidget.class),
                                            any(Double.class));
    }

    @Test
    public void findMovableGridWhenNoColumnOrRowOperationIsDetected() {
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.NONE);
        when(gridWidget.isVisible()).thenReturn(true);
        when(layer.getGridWidgets()).thenReturn(new HashSet<GridWidget>() {{
            add(gridWidget);
        }});

        //This location is top-left of the GridWidget; not within a column move/resize or row move hot-spot
        when(event.getX()).thenReturn(100);
        when(event.getY()).thenReturn(100);

        handler.onNodeMouseMove(event);

        verify(handler,
               times(1)).findGridColumn(eq(event));

        verify(handler,
               times(1)).findMovableColumns(any(GridWidget.class),
                                            any(Double.class),
                                            any(Double.class),
                                            any(Double.class),
                                            any(Double.class));
        verify(handler,
               never()).findMovableRows(any(GridWidget.class),
                                        any(Double.class),
                                        any(Double.class));
        verify(handler,
               never()).findResizableColumn(any(GridWidget.class),
                                            any(Double.class));

        verify(state,
               times(1)).setActiveGridWidget(eq(gridWidget));
        verify(state,
               times(1)).setOperation(eq(GridWidgetHandlersOperation.GRID_MOVE_PENDING));
    }

    @Test
    public void findMovableGridWhenNoColumnOrRowOperationIsDetectedAndGridIsPinned() {
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.NONE);
        when(gridWidget.isVisible()).thenReturn(true);
        when(layer.getGridWidgets()).thenReturn(new HashSet<GridWidget>() {{
            add(gridWidget);
        }});
        when(layer.isGridPinned()).thenReturn(true);

        //This location is top-left of the GridWidget; not within a column move/resize or row move hot-spot
        when(event.getX()).thenReturn(100);
        when(event.getY()).thenReturn(100);

        handler.onNodeMouseMove(event);

        verify(handler,
               times(1)).findGridColumn(eq(event));

        verify(state,
               never()).setActiveGridWidget(any(GridWidget.class));
        verify(state,
               times(1)).setOperation(eq(GridWidgetHandlersOperation.NONE));
    }

    @Test
    public void findMovableGridWhenOverDragHandle() {
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.NONE);
        when(gridWidget.isVisible()).thenReturn(true);
        when(gridWidget.onDragHandle(any(INodeXYEvent.class))).thenReturn(true);
        when(layer.getGridWidgets()).thenReturn(new HashSet<GridWidget>() {{
            add(gridWidget);
        }});
        //This location is top-left of the GridWidget; not within a column move/resize or row move hot-spot
        when(event.getX()).thenReturn(100);
        when(event.getY()).thenReturn(100);

        handler.onNodeMouseMove(event);

        verify(handler,
               times(1)).findGridColumn(eq(event));

        verify(handler,
               never()).findMovableColumns(any(GridWidget.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class));
        verify(handler,
               never()).findMovableRows(any(GridWidget.class),
                                        any(Double.class),
                                        any(Double.class));
        verify(handler,
               never()).findResizableColumn(any(GridWidget.class),
                                            any(Double.class));

        verify(state,
               times(1)).setActiveGridWidget(eq(gridWidget));
        verify(state,
               times(1)).setOperation(eq(GridWidgetHandlersOperation.GRID_MOVE_PENDING));
    }

    @Test
    public void findMovableColumns() {
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.NONE);
        when(gridWidget.isVisible()).thenReturn(true);
        when(layer.getGridWidgets()).thenReturn(new HashSet<GridWidget>() {{
            add(gridWidget);
        }});
        //This location is in the GridWidget's header; within a column move hot-spot, but not within a column resize or row move hot-spot
        when(event.getX()).thenReturn(160);
        when(event.getY()).thenReturn(100);

        handler.onNodeMouseMove(event);

        verify(handler,
               times(1)).findGridColumn(eq(event));

        verify(handler,
               times(1)).findMovableColumns(any(GridWidget.class),
                                            any(Double.class),
                                            any(Double.class),
                                            any(Double.class),
                                            any(Double.class));
        verify(handler,
               never()).findMovableRows(any(GridWidget.class),
                                        any(Double.class),
                                        any(Double.class));
        verify(handler,
               never()).findResizableColumn(any(GridWidget.class),
                                            any(Double.class));

        verify(state,
               times(1)).setActiveGridWidget(eq(gridWidget));
        verify(state,
               times(1)).setOperation(eq(GridWidgetHandlersOperation.COLUMN_MOVE_PENDING));
        verify(state,
               times(1)).setActiveGridColumns(uiColumnsArgumentCaptor.capture());

        final List<GridColumn<?>> uiColumns = uiColumnsArgumentCaptor.getValue();
        assertNotNull(uiColumns);
        assertEquals(1,
                     uiColumns.size());
        assertTrue(uiColumns.contains(uiColumn2));
    }

    @Test
    public void findResizableColumns() {
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.NONE);
        when(gridWidget.isVisible()).thenReturn(true);
        when(layer.getGridWidgets()).thenReturn(new HashSet<GridWidget>() {{
            add(gridWidget);
        }});
        //This location is in the GridWidget's body; within a column resize hot-spot, but not within a column move or row move hot-spot
        when(event.getX()).thenReturn(246);
        when(event.getY()).thenReturn(180);

        handler.onNodeMouseMove(event);

        verify(handler,
               times(1)).findGridColumn(eq(event));

        verify(handler,
               never()).findMovableColumns(any(GridWidget.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class));
        verify(handler,
               times(1)).findMovableRows(any(GridWidget.class),
                                         any(Double.class),
                                         any(Double.class));
        verify(handler,
               times(1)).findResizableColumn(any(GridWidget.class),
                                             any(Double.class));

        verify(state,
               times(1)).setActiveGridWidget(eq(gridWidget));
        verify(state,
               times(1)).setOperation(eq(GridWidgetHandlersOperation.COLUMN_RESIZE_PENDING));
        verify(state,
               times(1)).setActiveGridColumns(uiColumnsArgumentCaptor.capture());

        final List<GridColumn<?>> uiColumns = uiColumnsArgumentCaptor.getValue();
        assertNotNull(uiColumns);
        assertEquals(1,
                     uiColumns.size());
        assertTrue(uiColumns.contains(uiColumn2));
    }

    @Test
    public void findMovableRows() {
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.NONE);
        when(gridWidget.isVisible()).thenReturn(true);
        when(layer.getGridWidgets()).thenReturn(new HashSet<GridWidget>() {{
            add(gridWidget);
        }});
        //This location is in the GridWidget's body; within row 0's move hot-spot, but not within a column move or resize hot-spot
        when(event.getX()).thenReturn(125);
        when(event.getY()).thenReturn(180);

        handler.onNodeMouseMove(event);

        verify(handler,
               times(1)).findGridColumn(eq(event));

        verify(handler,
               never()).findMovableColumns(any(GridWidget.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class));
        verify(handler,
               times(1)).findMovableRows(any(GridWidget.class),
                                         any(Double.class),
                                         any(Double.class));
        verify(handler,
               times(1)).findResizableColumn(any(GridWidget.class),
                                             any(Double.class));

        verify(state,
               times(1)).setActiveGridWidget(eq(gridWidget));
        verify(state,
               times(1)).setOperation(eq(GridWidgetHandlersOperation.ROW_MOVE_PENDING));
        verify(state,
               times(1)).setActiveGridRows(uiRowsArgumentCaptor.capture());

        final List<GridRow> uiRows = uiRowsArgumentCaptor.getValue();
        assertNotNull(uiRows);
        assertEquals(1,
                     uiRows.size());
        assertTrue(uiRows.contains(uiModel.getRow(0)));
    }
}
