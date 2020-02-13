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
import java.util.Arrays;
import java.util.Collections;
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
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    private BaseHeaderMetaData header;

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

    private double originalColumnWidth = 200;
    private double originalRightColumnWidth = 300;
    private BaseGridColumn<String> column;
    private BaseGridColumn<String> rightColumn;

    @Before
    public void setup() {
        this.uiColumn1 = new RowNumberColumn();
        this.uiColumn2 = new BaseGridColumn<>(Arrays.asList(new BaseHeaderMetaData("title1"), new BaseHeaderMetaData("title2")),
                                              columnRenderer,
                                              100.0);
        this.uiModel = new BaseGridData();
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

        final Point2D computedLocation = new Point2D(100.0, 100.0);
        when(gridWidget.getComputedLocation()).thenReturn(computedLocation);

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
                                                                                                                   add(20.0);
                                                                                                                   add(20.0);
                                                                                                               }},
                                                                                                               new ArrayList<Double>() {{
                                                                                                                   add(20.0);
                                                                                                                   add(40.0);
                                                                                                                   add(60.0);
                                                                                                               }},
                                                                                                               false,
                                                                                                               false,
                                                                                                               2,
                                                                                                               0,
                                                                                                               0,
                                                                                                               0);
        when(helper.getRenderingInformation()).thenReturn(ri);

        final GridWidgetDnDHandlersState wrappedState = new GridWidgetDnDHandlersState();
        this.state = spy(wrappedState);

        final GridWidgetDnDMouseMoveHandler wrapped = new GridWidgetDnDMouseMoveHandler(layer,
                                                                                        state);
        this.handler = spy(wrapped);

        column = new BaseGridColumn<>(header, columnRenderer, originalColumnWidth);
        rightColumn = new BaseGridColumn<>(header, columnRenderer, originalRightColumnWidth);
    }

    @Test
    public void findGridColumnWithEmptyLayer() {
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.NONE);

        handler.onNodeMouseMove(event);

        verify(handler,
               times(1)).findGridColumn(eq(event));

        verify(handler,
               never()).findMovableColumns(any(GridWidget.class),
                                           any(BaseGridRendererHelper.RenderingInformation.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class));
        verify(handler,
               never()).findMovableRows(any(GridWidget.class),
                                        any(BaseGridRendererHelper.RenderingInformation.class),
                                        any(Double.class),
                                        any(Double.class));
        verify(handler,
               never()).findResizableColumn(any(GridWidget.class),
                                            any(BaseGridRendererHelper.RenderingInformation.class),
                                            any(Double.class));
    }

    @Test
    public void findGridColumnWithInvisibleGridWidgets() {
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.NONE);
        when(gridWidget.isVisible()).thenReturn(false);
        when(layer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));

        handler.onNodeMouseMove(event);

        verify(handler,
               times(1)).findGridColumn(eq(event));

        verify(handler,
               never()).findMovableColumns(any(GridWidget.class),
                                           any(BaseGridRendererHelper.RenderingInformation.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class));
        verify(handler,
               never()).findMovableRows(any(GridWidget.class),
                                        any(BaseGridRendererHelper.RenderingInformation.class),
                                        any(Double.class),
                                        any(Double.class));
        verify(handler,
               never()).findResizableColumn(any(GridWidget.class),
                                            any(BaseGridRendererHelper.RenderingInformation.class),
                                            any(Double.class));
    }

    @Test
    public void findMovableGridWhenNoColumnOrRowOperationIsDetected() {
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.NONE);
        when(gridWidget.isVisible()).thenReturn(true);
        when(layer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));

        //This location is top-left of the GridWidget; not within a column move/resize or row move hot-spot
        when(event.getX()).thenReturn(100);
        when(event.getY()).thenReturn(100);

        handler.onNodeMouseMove(event);

        verify(handler,
               times(1)).findGridColumn(eq(event));

        verify(handler,
               times(1)).findMovableColumns(any(GridWidget.class),
                                            any(BaseGridRendererHelper.RenderingInformation.class),
                                            any(Double.class),
                                            any(Double.class),
                                            any(Double.class),
                                            any(Double.class));
        verify(handler,
               never()).findMovableRows(any(GridWidget.class),
                                        any(BaseGridRendererHelper.RenderingInformation.class),
                                        any(Double.class),
                                        any(Double.class));
        verify(handler,
               times(1)).findResizableColumn(any(GridWidget.class),
                                             any(BaseGridRendererHelper.RenderingInformation.class),
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
        when(layer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));
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
    public void findMovableGridWhenOverDragHandleWhenIsPinned() {
        doFindMovableGridWhenOverDragHandle(true,
                                            () -> {
                                                verify(state,
                                                       never()).setActiveGridWidget(any(GridWidget.class));
                                                verify(state,
                                                       never()).setOperation(eq(GridWidgetHandlersOperation.GRID_MOVE_PENDING));
                                                assertNull(state.getActiveGridWidget());
                                                assertEquals(GridWidgetHandlersOperation.NONE,
                                                             state.getOperation());
                                            });
    }

    @Test
    public void findMovableGridWhenOverDragHandleWhenNotPinned() {
        doFindMovableGridWhenOverDragHandle(false,
                                            () -> {
                                                verify(state).setActiveGridWidget(eq(gridWidget));
                                                verify(state).setOperation(eq(GridWidgetHandlersOperation.GRID_MOVE_PENDING));
                                                assertEquals(gridWidget,
                                                             state.getActiveGridWidget());
                                                assertEquals(GridWidgetHandlersOperation.GRID_MOVE_PENDING,
                                                             state.getOperation());
                                            });
    }

    private void doFindMovableGridWhenOverDragHandle(final boolean isPinned,
                                                     final Runnable assertion) {
        state.setOperation(GridWidgetHandlersOperation.NONE);
        when(gridWidget.isVisible()).thenReturn(true);
        when(gridWidget.onDragHandle(any(INodeXYEvent.class))).thenReturn(true);
        when(layer.isGridPinned()).thenReturn(isPinned);
        when(layer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));

        //This location is top-left of the GridWidget; not within a column move/resize or row move hot-spot
        when(event.getX()).thenReturn(100);
        when(event.getY()).thenReturn(100);

        handler.onNodeMouseMove(event);

        verify(handler,
               times(1)).findGridColumn(eq(event));

        assertion.run();
    }

    @Test
    public void findMovableColumns() {
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.NONE);
        when(gridWidget.isVisible()).thenReturn(true);
        when(layer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));

        //This location is in the GridWidget's header; within a column move hot-spot, but not within a column resize or row move hot-spot
        when(event.getX()).thenReturn(160);
        when(event.getY()).thenReturn(100);

        handler.onNodeMouseMove(event);

        verify(handler,
               times(1)).findGridColumn(eq(event));

        verify(handler,
               times(1)).findMovableColumns(any(GridWidget.class),
                                            any(BaseGridRendererHelper.RenderingInformation.class),
                                            any(Double.class),
                                            any(Double.class),
                                            any(Double.class),
                                            any(Double.class));
        verify(handler,
               never()).findMovableRows(any(GridWidget.class),
                                        any(BaseGridRendererHelper.RenderingInformation.class),
                                        any(Double.class),
                                        any(Double.class));
        verify(handler,
               times(1)).findResizableColumn(any(GridWidget.class),
                                             any(BaseGridRendererHelper.RenderingInformation.class),
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
    public void findResizableColumnsInHeader() {
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.NONE);
        when(gridWidget.isVisible()).thenReturn(true);
        when(layer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));

        //This location is in the GridWidget's header; within a column resize hot-spot, but not within a column move.
        when(event.getX()).thenReturn(246);
        when(event.getY()).thenReturn(132);

        //Both COLUMN_MOVE_PENDING and COLUMN_RESIZE_PENDING are detected; however COLUMN_RESIZE_PENGING takes precedence.
        final InOrder inOrder = Mockito.inOrder(state);

        handler.onNodeMouseMove(event);

        verify(handler,
               times(1)).findGridColumn(eq(event));

        verify(handler,
               times(1)).findMovableColumns(any(GridWidget.class),
                                            any(BaseGridRendererHelper.RenderingInformation.class),
                                            any(Double.class),
                                            any(Double.class),
                                            any(Double.class),
                                            any(Double.class));
        inOrder.verify(state,
                       times(1)).setActiveGridWidget(eq(gridWidget));
        inOrder.verify(state,
                       times(1)).setActiveGridColumns(uiColumnsArgumentCaptor.capture());
        inOrder.verify(state,
                       times(1)).setOperation(eq(GridWidgetHandlersOperation.COLUMN_MOVE_PENDING));

        verify(handler,
               never()).findMovableRows(any(GridWidget.class),
                                        any(BaseGridRendererHelper.RenderingInformation.class),
                                        any(Double.class),
                                        any(Double.class));

        verify(handler,
               times(1)).findResizableColumn(any(GridWidget.class),
                                             any(BaseGridRendererHelper.RenderingInformation.class),
                                             any(Double.class));
        inOrder.verify(state,
                       times(1)).setActiveGridWidget(eq(gridWidget));
        inOrder.verify(state,
                       times(1)).setActiveGridColumns(uiColumnsArgumentCaptor.capture());
        inOrder.verify(state,
                       times(1)).setOperation(eq(GridWidgetHandlersOperation.COLUMN_RESIZE_PENDING));

        final List<GridColumn<?>> uiColumns = uiColumnsArgumentCaptor.getValue();
        assertNotNull(uiColumns);
        assertEquals(1,
                     uiColumns.size());
        assertTrue(uiColumns.contains(uiColumn2));
    }

    @Test
    public void findResizableColumnsInBody() {
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.NONE);
        when(gridWidget.isVisible()).thenReturn(true);
        when(layer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));

        //This location is in the GridWidget's body; within a column resize hot-spot, but not within a column move or row move hot-spot
        when(event.getX()).thenReturn(246);
        when(event.getY()).thenReturn(180);

        handler.onNodeMouseMove(event);

        verify(handler,
               times(1)).findGridColumn(eq(event));

        verify(handler,
               never()).findMovableColumns(any(GridWidget.class),
                                           any(BaseGridRendererHelper.RenderingInformation.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class));
        verify(handler,
               times(1)).findMovableRows(any(GridWidget.class),
                                         any(BaseGridRendererHelper.RenderingInformation.class),
                                         any(Double.class),
                                         any(Double.class));
        verify(handler,
               times(1)).findResizableColumn(any(GridWidget.class),
                                             any(BaseGridRendererHelper.RenderingInformation.class),
                                             any(Double.class));

        verify(state,
               times(1)).setActiveGridWidget(eq(gridWidget));
        verify(state,
               times(1)).setActiveGridColumns(uiColumnsArgumentCaptor.capture());
        verify(state,
               times(1)).setOperation(eq(GridWidgetHandlersOperation.COLUMN_RESIZE_PENDING));

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
        when(layer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));

        //This location is in the GridWidget's body; within row 1's move hot-spot, but not within a column move or resize hot-spot
        final int eventX = (int) (gridWidget.getComputedLocation().getX() + uiColumn1.getWidth() / 2);
        final int eventY = (int) (gridWidget.getComputedLocation().getY() + renderer.getHeaderHeight() + uiModel.getRow(0).getHeight() + uiModel.getRow(1).getHeight() / 2);
        when(event.getX()).thenReturn(eventX);
        when(event.getY()).thenReturn(eventY);

        handler.onNodeMouseMove(event);

        verify(handler,
               times(1)).findGridColumn(eq(event));

        verify(handler,
               never()).findMovableColumns(any(GridWidget.class),
                                           any(BaseGridRendererHelper.RenderingInformation.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class),
                                           any(Double.class));
        verify(handler,
               times(1)).findMovableRows(any(GridWidget.class),
                                         any(BaseGridRendererHelper.RenderingInformation.class),
                                         any(Double.class),
                                         any(Double.class));
        verify(handler,
               times(1)).findResizableColumn(any(GridWidget.class),
                                             any(BaseGridRendererHelper.RenderingInformation.class),
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
        assertTrue(uiRows.contains(uiModel.getRow(1)));
    }

    @Test
    public void handleRowMove() {
        final List<GridRow> existingRowOrder = new ArrayList<>(uiModel.getRows());
        final GridWidgetDnDProxy highlight = mock(GridWidgetDnDProxy.class);
        when(state.getEventColumnHighlight()).thenReturn(highlight);
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.NONE);
        when(gridWidget.isVisible()).thenReturn(true);
        when(layer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));

        //This location is in the GridWidget's body; within row 0's move hot-spot, but not within a column move or resize hot-spot
        final int eventX = (int) (gridWidget.getComputedLocation().getX() + uiColumn1.getWidth() / 2);
        final int eventY = (int) (gridWidget.getComputedLocation().getY() + renderer.getHeaderHeight() + uiModel.getRow(0).getHeight() / 2);
        when(event.getX()).thenReturn(eventX);
        when(event.getY()).thenReturn(eventY);

        //Mouse is moved over row and viable rows recorded
        handler.onNodeMouseMove(event);

        //Emulate row drag starting on a MouseDownEvent (see GridWidgetDnDMouseDownHandler)
        //This location is in the GridWidget's body; within row 1's move hot-spot
        final int eventNewY = (int) (gridWidget.getComputedLocation().getY() + renderer.getHeaderHeight() + uiModel.getRow(0).getHeight() + uiModel.getRow(1).getHeight() / 2);
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.ROW_MOVE);
        when(event.getY()).thenReturn(eventNewY);

        handler.onNodeMouseMove(event);

        verify(highlight).setY(gridWidget.getComputedLocation().getY() + renderer.getHeaderHeight());
        verify(layer).batch();

        assertEquals(existingRowOrder.get(0), uiModel.getRow(1));
        assertEquals(existingRowOrder.get(1), uiModel.getRow(0));
        assertEquals(existingRowOrder.get(2), uiModel.getRow(2));
    }

    @Test
    public void adjustColumnWidth() {
        double proposedNewWidth = 100;
        uiModel.setVisibleSizeAndRefresh(10, 0);
        assertEquals(proposedNewWidth, handler.adjustColumnWidth(proposedNewWidth, column, gridWidget), 0.1);

        uiModel.setVisibleSizeAndRefresh(1000, 0);
        proposedNewWidth = 300;
        assertEquals(proposedNewWidth, handler.adjustColumnWidth(proposedNewWidth, column, gridWidget), 0.1);

        proposedNewWidth = 50;
        uiColumn2.setColumnWidthMode(GridColumn.ColumnWidthMode.AUTO);
        int visibleWidth = gridWidget.getModel().getVisibleWidth();
        double adjustedWidth = handler.adjustColumnWidth(proposedNewWidth, uiColumn2, gridWidget);

        double widthWithoutColumn = gridWidget.getModel()
                .getColumns().stream()
                .filter(col -> !col.equals(uiColumn2))
                .mapToDouble(GridColumn::getWidth)
                .sum();

        assertEquals(visibleWidth - widthWithoutColumn, adjustedWidth, 0.1);

        column.setColumnWidthMode(GridColumn.ColumnWidthMode.FIXED);
        uiModel.appendColumn(column);
        uiModel.appendColumn(rightColumn);
        rightColumn.setColumnWidthMode(GridColumn.ColumnWidthMode.AUTO);
        assertEquals(proposedNewWidth, handler.adjustColumnWidth(proposedNewWidth, column, gridWidget), 0.1);
        double columnDelta = column.getWidth() - proposedNewWidth;
        double widthWithoutRightColumn = gridWidget.getWidth() - originalRightColumnWidth;
        double newRightColumnWidth = visibleWidth - widthWithoutRightColumn + columnDelta;
        assertEquals(newRightColumnWidth, rightColumn.getWidth(), 0.1);
    }

    @Test
    public void getFirstRightAutoColumn() {
        assertFalse(handler.getFirstRightAutoColumn(column, uiModel).isPresent());

        uiModel.appendColumn(column);
        assertFalse(handler.getFirstRightAutoColumn(column, uiModel).isPresent());

        uiModel.appendColumn(rightColumn);
        assertFalse(handler.getFirstRightAutoColumn(column, uiModel).isPresent());

        rightColumn.setColumnWidthMode(GridColumn.ColumnWidthMode.AUTO);
        assertEquals(rightColumn, handler.getFirstRightAutoColumn(column, uiModel).get());
    }
}
