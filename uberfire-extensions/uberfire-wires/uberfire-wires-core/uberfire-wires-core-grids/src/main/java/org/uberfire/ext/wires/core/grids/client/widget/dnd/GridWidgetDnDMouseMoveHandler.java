/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import java.util.Optional;

import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveHandler;
import com.ait.lienzo.client.core.mediator.IMediator;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.dom.client.Style;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.dom.HasDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

/**
 * MouseMoveHandler to handle potential drag operations and handle the drag itself; if required.
 */
public class GridWidgetDnDMouseMoveHandler implements NodeMouseMoveHandler {

    // How close the mouse pointer needs to be to the column separator to initiate a resize operation.
    private static final int COLUMN_RESIZE_HANDLE_SENSITIVITY = 5;

    protected final GridLayer layer;
    protected final GridWidgetDnDHandlersState state;

    public GridWidgetDnDMouseMoveHandler(final GridLayer layer,
                                         final GridWidgetDnDHandlersState state) {
        this.layer = layer;
        this.state = state;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onNodeMouseMove(final NodeMouseMoveEvent event) {
        switch (state.getOperation()) {
            case GRID_MOVE:
                //The grid is draggable. This is handled by Lienzo.
                break;

            case COLUMN_RESIZE:
                //If we're currently resizing a column we don't need to find a column
                handleColumnResize(event);
                break;

            case COLUMN_MOVE:
            case COLUMN_MOVE_INITIATED:
                //If we're currently moving a column we don't need to find a column
                handleColumnMove(event);
                break;

            case ROW_MOVE:
            case ROW_MOVE_INITIATED:
                //If we're currently moving a row we don't need to find a row
                handleRowMove(event);
                break;

            default:
                //Otherwise try to find a Grid and GridColumn(s)
                findGridColumn(event);
        }
    }

    protected void findGridColumn(final NodeMouseMoveEvent event) {
        state.reset();
        setCursor(state.getCursor());

        for (GridWidget gridWidget : layer.getGridWidgets()) {

            gridWidget.setDraggable(false);

            if (!gridWidget.isVisible()) {
                continue;
            }
            if (!gridWidget.getModel().isColumnDraggingEnabled()) {
                continue;
            }

            final Group header = gridWidget.getHeader();
            final GridRenderer renderer = gridWidget.getRenderer();
            final double headerHeight = renderer.getHeaderHeight();

            final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
            final BaseGridRendererHelper.RenderingInformation renderingInformation = rendererHelper.getRenderingInformation();
            if (renderingInformation == null) {
                continue;
            }

            final double headerRowsYOffset = renderingInformation.getHeaderRowsYOffset();
            final double headerMinY = (header == null ? headerRowsYOffset : header.getY() + headerRowsYOffset);
            final double headerMaxY = (header == null ? headerHeight : headerHeight + header.getY());

            final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate(gridWidget,
                                                                              new Point2D(event.getX(),
                                                                                          event.getY()));

            final double cx = ap.getX();
            final double cy = ap.getY();

            //If over Grid's drag handle prime for dragging
            if (!layer.isGridPinned()) {
                if (gridWidget.onDragHandle(event)) {
                    state.setActiveGridWidget(gridWidget);
                    state.setOperation(GridWidgetDnDHandlersState.GridWidgetHandlersOperation.GRID_MOVE_PENDING);
                    continue;
                }
            }

            //Check bounds
            if (cx < 0 || cx > gridWidget.getWidth()) {
                continue;
            }
            if (cy < headerMinY || cy > gridWidget.getHeight()) {
                continue;
            }

            if (cy < headerMaxY) {
                //Check for column moving
                findMovableColumns(gridWidget,
                                   renderingInformation,
                                   headerHeight - headerRowsYOffset,
                                   headerMinY,
                                   cx,
                                   cy);
            } else {
                //Check for movable rows
                findMovableRows(gridWidget,
                                renderingInformation,
                                cx,
                                cy);
            }

            //Check for column resizing
            findResizableColumn(gridWidget,
                                renderingInformation,
                                cx);

            //If over Grid but no operation has been identified default to Grid dragging; when unpinned
            if (!layer.isGridPinned()) {
                if (state.getActiveGridWidget() == null) {
                    state.setActiveGridWidget(gridWidget);
                    state.setOperation(GridWidgetDnDHandlersState.GridWidgetHandlersOperation.GRID_MOVE_PENDING);
                }
            }
        }

        for (IMediator mediator : layer.getViewport().getMediators()) {
            mediator.setEnabled(state.getActiveGridWidget() == null);
        }
    }

    protected void setCursor(final Style.Cursor cursor) {
        for (IMediator mediator : layer.getViewport().getMediators()) {
            if (mediator instanceof RestrictedMousePanMediator) {
                if (((RestrictedMousePanMediator) mediator).isDragging()) {
                    return;
                }
            }
        }
        layer.getViewport().getElement().getStyle().setCursor(cursor);
        state.setCursor(cursor);
    }

    protected void findResizableColumn(final GridWidget view,
                                       final BaseGridRendererHelper.RenderingInformation renderingInformation,
                                       final double cx) {
        //Gather information on columns
        if (renderingInformation == null) {
            return;
        }

        final BaseGridRendererHelper.RenderingBlockInformation bodyBlockInformation = renderingInformation.getBodyBlockInformation();
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final List<GridColumn<?>> bodyColumns = bodyBlockInformation.getColumns();
        final List<GridColumn<?>> floatingColumns = floatingBlockInformation.getColumns();
        final double bodyX = bodyBlockInformation.getX();
        final double floatingX = floatingBlockInformation.getX();
        final double floatingWidth = floatingBlockInformation.getWidth();

        //Check floating columns
        double offsetX = floatingX;
        GridColumn<?> column = null;
        for (GridColumn<?> gridColumn : floatingColumns) {
            if (gridColumn.isVisible()) {
                if (gridColumn.isResizable()) {
                    final double columnWidth = gridColumn.getWidth();
                    if (cx > columnWidth + offsetX - COLUMN_RESIZE_HANDLE_SENSITIVITY && cx < columnWidth + offsetX + COLUMN_RESIZE_HANDLE_SENSITIVITY) {
                        column = gridColumn;
                        break;
                    }
                }
                offsetX = offsetX + gridColumn.getWidth();
            }
        }

        //Check all other columns
        if (column == null) {
            offsetX = bodyX;
            for (GridColumn<?> gridColumn : bodyColumns) {
                if (gridColumn.isVisible()) {
                    if (gridColumn.isResizable()) {
                        final double columnWidth = gridColumn.getWidth();
                        if (offsetX + gridColumn.getWidth() > floatingX + floatingWidth) {
                            if (cx > columnWidth + offsetX - COLUMN_RESIZE_HANDLE_SENSITIVITY && cx < columnWidth + offsetX + COLUMN_RESIZE_HANDLE_SENSITIVITY) {
                                column = gridColumn;
                                break;
                            }
                        }
                    }
                    offsetX = offsetX + gridColumn.getWidth();
                }
            }
        }

        if (column != null) {
            final List<GridColumn<?>> activeColumns = new ArrayList<>();
            activeColumns.add(column);
            state.setActiveGridWidget(view);
            state.setActiveGridColumns(activeColumns);
            state.setOperation(GridWidgetDnDHandlersState.GridWidgetHandlersOperation.COLUMN_RESIZE_PENDING);
            setCursor(Style.Cursor.COL_RESIZE);
        }
    }

    protected void findMovableColumns(final GridWidget view,
                                      final BaseGridRendererHelper.RenderingInformation renderingInformation,
                                      final double headerRowsHeight,
                                      final double headerMinY,
                                      final double cx,
                                      final double cy) {
        //Gather information on columns
        if (renderingInformation == null) {
            return;
        }

        final BaseGridRendererHelper.RenderingBlockInformation bodyBlockInformation = renderingInformation.getBodyBlockInformation();
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final List<GridColumn<?>> allColumns = view.getModel().getColumns();
        final List<GridColumn<?>> bodyColumns = bodyBlockInformation.getColumns();
        final List<GridColumn<?>> floatingColumns = floatingBlockInformation.getColumns();
        final double bodyX = bodyBlockInformation.getX();
        final double floatingX = floatingBlockInformation.getX();
        final double floatingWidth = floatingBlockInformation.getWidth();

        //Check all other columns. Floating columns cannot be moved.
        double offsetX = bodyX;
        for (int headerColumnIndex = 0; headerColumnIndex < bodyColumns.size(); headerColumnIndex++) {
            final GridColumn<?> gridColumn = bodyColumns.get(headerColumnIndex);
            final double columnWidth = gridColumn.getWidth();

            if (gridColumn.isVisible()) {
                final List<GridColumn.HeaderMetaData> headerMetaData = gridColumn.getHeaderMetaData();
                final double headerRowHeight = headerRowsHeight / headerMetaData.size();

                for (int headerRowIndex = 0; headerRowIndex < headerMetaData.size(); headerRowIndex++) {
                    final GridColumn.HeaderMetaData md = headerMetaData.get(headerRowIndex);
                    if (gridColumn.isMovable()) {
                        if (cy < (headerRowIndex + 1) * headerRowHeight + headerMinY) {
                            if (cx > floatingX + floatingWidth) {
                                if (cx > offsetX && cx < offsetX + columnWidth) {
                                    //Get the block of columns to be moved.
                                    final List<GridColumn<?>> blockColumns = getBlockColumns(allColumns,
                                                                                             headerMetaData,
                                                                                             headerRowIndex,
                                                                                             allColumns.indexOf(gridColumn));
                                    //If the columns to move are split between body and floating we cannot move them.
                                    for (GridColumn<?> blockColumn : blockColumns) {
                                        if (floatingColumns.contains(blockColumn)) {
                                            return;
                                        }
                                    }

                                    state.setActiveGridWidget(view);
                                    state.setActiveGridColumns(blockColumns);
                                    state.setActiveHeaderMetaData(md);
                                    state.setOperation(GridWidgetDnDHandlersState.GridWidgetHandlersOperation.COLUMN_MOVE_PENDING);
                                    setCursor(Style.Cursor.MOVE);
                                    return;
                                }
                            }
                        }
                    }
                }
                offsetX = offsetX + columnWidth;
            }
        }
    }

    private List<GridColumn<?>> getBlockColumns(final List<GridColumn<?>> allColumns,
                                                final List<GridColumn.HeaderMetaData> headerMetaData,
                                                final int headerRowIndex,
                                                final int headerColumnIndex) {
        final int blockStartColumnIndex = ColumnIndexUtilities.getHeaderBlockStartColumnIndex(allColumns,
                                                                                              headerMetaData.get(headerRowIndex),
                                                                                              headerRowIndex,
                                                                                              headerColumnIndex);
        final int blockEndColumnIndex = ColumnIndexUtilities.getHeaderBlockEndColumnIndex(allColumns,
                                                                                          headerMetaData.get(headerRowIndex),
                                                                                          headerRowIndex,
                                                                                          headerColumnIndex);

        final List<GridColumn<?>> columns = new ArrayList<GridColumn<?>>();
        columns.addAll(allColumns.subList(blockStartColumnIndex,
                                          blockEndColumnIndex + 1));
        return columns;
    }

    protected void findMovableRows(final GridWidget view,
                                   final BaseGridRendererHelper.RenderingInformation renderingInformation,
                                   final double cx,
                                   final double cy) {
        if (!isOverRowDragHandleColumn(renderingInformation,
                                       cx)) {
            return;
        }

        final GridData gridModel = view.getModel();
        final GridRenderer renderer = view.getRenderer();
        final List<Double> allRowHeights = renderingInformation.getAllRowHeights();

        //Get row index
        if (gridModel.getRowCount() == 0) {
            return;
        }
        double rowHeight;
        int uiRowIndex = 0;
        double offsetY = cy - renderer.getHeaderHeight();
        while ((rowHeight = allRowHeights.get(uiRowIndex)) < offsetY) {
            offsetY = offsetY - rowHeight;
            uiRowIndex++;
        }
        if (uiRowIndex < 0 || uiRowIndex > gridModel.getRowCount() - 1) {
            return;
        }

        //Add row over which MouseEvent occurred
        final List<GridRow> rows = new ArrayList<>();
        rows.add(gridModel.getRow(uiRowIndex));

        //Add any other collapsed rows
        GridRow collapsedRow;
        while (uiRowIndex + 1 < gridModel.getRowCount() && (collapsedRow = gridModel.getRow(uiRowIndex + 1)).isCollapsed()) {
            rows.add(collapsedRow);
            uiRowIndex++;
        }

        state.setActiveGridWidget(view);
        state.setActiveGridRows(rows);
        state.setOperation(GridWidgetDnDHandlersState.GridWidgetHandlersOperation.ROW_MOVE_PENDING);
        setCursor(Style.Cursor.MOVE);
    }

    private boolean isOverRowDragHandleColumn(final BaseGridRendererHelper.RenderingInformation renderingInformation,
                                              final double cx) {
        //Gather information on columns
        if (renderingInformation == null) {
            return false;
        }

        final BaseGridRendererHelper.RenderingBlockInformation bodyBlockInformation = renderingInformation.getBodyBlockInformation();
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final List<GridColumn<?>> bodyColumns = bodyBlockInformation.getColumns();
        final List<GridColumn<?>> floatingColumns = floatingBlockInformation.getColumns();
        final double bodyX = bodyBlockInformation.getX();
        final double floatingX = floatingBlockInformation.getX();

        //Check floating columns
        if (findRowDragHandleColumn(floatingColumns,
                                    floatingX,
                                    cx) != null) {
            return true;
        }

        //Check all other columns
        return findRowDragHandleColumn(bodyColumns,
                                       bodyX,
                                       cx) != null;
    }

    private GridColumn<?> findRowDragHandleColumn(final List<GridColumn<?>> columns,
                                                  final double offsetX,
                                                  final double cx) {
        double _offsetX = offsetX;
        for (GridColumn<?> gridColumn : columns) {
            if (gridColumn.isVisible()) {
                if (gridColumn instanceof IsRowDragHandle) {
                    final double columnWidth = gridColumn.getWidth();
                    if (cx > _offsetX && cx < _offsetX + columnWidth) {
                        return gridColumn;
                    }
                }
                _offsetX = _offsetX + gridColumn.getWidth();
            }
        }
        return null;
    }

    protected void handleColumnResize(final NodeMouseMoveEvent event) {
        final GridWidget activeGridWidget = state.getActiveGridWidget();
        final List<GridColumn<?>> activeGridColumns = state.getActiveGridColumns();
        if (activeGridColumns.size() > 1) {
            return;
        }
        final GridColumn<?> activeGridColumn = activeGridColumns.get(0);
        final GridData activeGridModel = activeGridWidget.getModel();
        final List<GridColumn<?>> allGridColumns = activeGridModel.getColumns();

        final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate(activeGridWidget,
                                                                          new Point2D(event.getX(),
                                                                                      event.getY()));
        final double deltaX = ap.getX() - state.getEventInitialX();
        final Double columnMinimumWidth = activeGridColumn.getMinimumWidth();
        final Double columnMaximumWidth = activeGridColumn.getMaximumWidth();
        double columnNewWidth = state.getEventInitialColumnWidth() + deltaX;
        if (columnMinimumWidth != null) {
            if (columnNewWidth < columnMinimumWidth) {
                columnNewWidth = columnMinimumWidth;
            }
        }
        if (columnMaximumWidth != null) {
            if (columnNewWidth > columnMaximumWidth) {
                columnNewWidth = columnMaximumWidth;
            }
        }

        destroyColumns(allGridColumns);

        activeGridColumn.setWidth(adjustColumnWidth(columnNewWidth, activeGridColumn, activeGridWidget));
        layer.batch();
    }

    protected double adjustColumnWidth(double columnNewWidth, GridColumn<?> activeGridColumn, GridWidget activeGridWidget) {
        final GridData activeGridModel = activeGridWidget.getModel();

        double originalLeftColumnWidth = activeGridColumn.getWidth();
        double delta = originalLeftColumnWidth - columnNewWidth;

        int visibleWidth = activeGridModel.getVisibleWidth();
        double gridWidgetWidth = activeGridWidget.getWidth();
        double newGridWidth = gridWidgetWidth - delta;

        // if the grid is becoming less than 100% width
        if (newGridWidth < visibleWidth && delta > 0) {

            // look for a column with AUTO width on the right
            Optional<GridColumn<?>> rightGridColumn = getFirstRightAutoColumn(activeGridColumn, activeGridModel);
            if (rightGridColumn.isPresent()) {
                GridColumn<?> rightColumn = rightGridColumn.get();
                double originalRightColumnWidth = rightColumn.getWidth();
                double widthWithoutColumn = gridWidgetWidth - originalRightColumnWidth;

                double newWidth = visibleWidth - widthWithoutColumn + delta;

                rightColumn.setWidth(newWidth);
            }
            // or revert column resizing if the column itself has AUTO width
            else if (GridColumn.ColumnWidthMode.isAuto(activeGridColumn)) {
                double widthWithoutColumn = gridWidgetWidth - originalLeftColumnWidth;
                columnNewWidth = visibleWidth - widthWithoutColumn;
            }
        }
        return columnNewWidth;
    }

    Optional<GridColumn<?>> getFirstRightAutoColumn(GridColumn<?> target, GridData model) {
        List<GridColumn<?>> columns = model.getColumns();
        int targetIndex = columns.indexOf(target);

        for (int i = targetIndex + 1; i < columns.size(); i += 1) {
            GridColumn<?> gridColumn = columns.get(i);
            if (GridColumn.ColumnWidthMode.isAuto(gridColumn)) {
                return Optional.of(gridColumn);
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    protected void handleColumnMove(final NodeMouseMoveEvent event) {
        final GridWidget activeGridWidget = state.getActiveGridWidget();
        final List<GridColumn<?>> activeGridColumns = state.getActiveGridColumns();
        final GridColumn.HeaderMetaData activeHeaderMetaData = state.getActiveHeaderMetaData();

        final GridData activeGridModel = activeGridWidget.getModel();
        final List<GridColumn<?>> allGridColumns = activeGridModel.getColumns();
        final BaseGridRendererHelper rendererHelper = activeGridWidget.getRendererHelper();
        final BaseGridRendererHelper.RenderingInformation renderingInformation = rendererHelper.getRenderingInformation();
        if (renderingInformation == null) {
            return;
        }

        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final double floatingX = floatingBlockInformation.getX();
        final double floatingWidth = floatingBlockInformation.getWidth();

        final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate(activeGridWidget,
                                                                          new Point2D(event.getX(),
                                                                                      event.getY()));
        final double cx = ap.getX();
        if (cx < floatingX + floatingWidth) {
            return;
        }

        final double activeBlockWidth = getBlockWidth(allGridColumns,
                                                      allGridColumns.indexOf(activeGridColumns.get(0)),
                                                      allGridColumns.indexOf(activeGridColumns.get(activeGridColumns.size() - 1)));

        for (int headerColumnIndex = 0; headerColumnIndex < allGridColumns.size(); headerColumnIndex++) {
            final GridColumn<?> candidateGridColumn = allGridColumns.get(headerColumnIndex);
            if (candidateGridColumn.isVisible()) {
                if (!activeGridColumns.contains(candidateGridColumn)) {
                    for (int headerRowIndex = 0; headerRowIndex < candidateGridColumn.getHeaderMetaData().size(); headerRowIndex++) {
                        final GridColumn.HeaderMetaData candidateHeaderMetaData = candidateGridColumn.getHeaderMetaData().get(headerRowIndex);
                        if (candidateHeaderMetaData.getColumnGroup().equals(activeHeaderMetaData.getColumnGroup())) {
                            final int candidateBlockStartColumnIndex = ColumnIndexUtilities.getHeaderBlockStartColumnIndex(allGridColumns,
                                                                                                                           candidateHeaderMetaData,
                                                                                                                           headerRowIndex,
                                                                                                                           headerColumnIndex);
                            final int candidateBlockEndColumnIndex = ColumnIndexUtilities.getHeaderBlockEndColumnIndex(allGridColumns,
                                                                                                                       candidateHeaderMetaData,
                                                                                                                       headerRowIndex,
                                                                                                                       headerColumnIndex);
                            final double candidateBlockOffset = rendererHelper.getColumnOffset(candidateBlockStartColumnIndex);
                            final double candidateBlockWidth = getBlockWidth(allGridColumns,
                                                                             candidateBlockStartColumnIndex,
                                                                             candidateBlockEndColumnIndex);

                            final double minColX = Math.max(candidateBlockOffset,
                                                            candidateBlockOffset + (candidateBlockWidth - activeBlockWidth) / 2);
                            final double maxColX = Math.min(candidateBlockOffset + candidateBlockWidth,
                                                            candidateBlockOffset + (candidateBlockWidth + activeBlockWidth) / 2);
                            final double midColX = candidateBlockOffset + candidateBlockWidth / 2;
                            if (cx > minColX && cx < maxColX) {
                                if (cx < midColX) {
                                    destroyColumns(allGridColumns);
                                    activeGridModel.moveColumnsTo(candidateBlockEndColumnIndex,
                                                                  activeGridColumns);
                                    state.getEventColumnHighlight().setX(activeGridWidget.getComputedLocation().getX() + rendererHelper.getColumnOffset(activeGridColumns.get(0)));
                                    state.setOperation(GridWidgetDnDHandlersState.GridWidgetHandlersOperation.COLUMN_MOVE);
                                    layer.batch();
                                    return;
                                } else {
                                    destroyColumns(allGridColumns);
                                    activeGridModel.moveColumnsTo(candidateBlockStartColumnIndex,
                                                                  activeGridColumns);
                                    state.getEventColumnHighlight().setX(activeGridWidget.getComputedLocation().getX() + rendererHelper.getColumnOffset(activeGridColumns.get(0)));
                                    state.setOperation(GridWidgetDnDHandlersState.GridWidgetHandlersOperation.COLUMN_MOVE);
                                    layer.batch();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private double getBlockWidth(final List<? extends GridColumn> columns,
                                 final int blockStartColumnIndex,
                                 final int blockEndColumnIndex) {
        double blockWidth = 0;
        for (int blockColumnIndex = blockStartColumnIndex; blockColumnIndex <= blockEndColumnIndex; blockColumnIndex++) {
            final GridColumn column = columns.get(blockColumnIndex);
            if (column.isVisible()) {
                blockWidth = blockWidth + column.getWidth();
            }
        }
        return blockWidth;
    }

    protected void handleRowMove(final NodeMouseMoveEvent event) {
        final GridWidget activeGridWidget = state.getActiveGridWidget();
        final List<GridRow> activeGridRows = state.getActiveGridRows();

        final GridData activeGridModel = activeGridWidget.getModel();
        final List<GridColumn<?>> allGridColumns = activeGridModel.getColumns();

        final BaseGridRendererHelper rendererHelper = activeGridWidget.getRendererHelper();
        final GridRenderer renderer = activeGridWidget.getRenderer();
        final double headerHeight = renderer.getHeaderHeight();

        final GridRow leadRow = activeGridRows.get(0);
        final int leadRowIndex = activeGridModel.getRows().indexOf(leadRow);

        final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate(activeGridWidget,
                                                                          new Point2D(event.getX(),
                                                                                      event.getY()));
        final double cy = ap.getY();
        if (cy < headerHeight || cy > activeGridWidget.getHeight()) {
            return;
        }

        //Find new row index
        GridRow row;
        int uiRowIndex = 0;
        double offsetY = cy - headerHeight;
        while ((row = activeGridModel.getRow(uiRowIndex)).getHeight() < offsetY) {
            offsetY = offsetY - row.getHeight();
            uiRowIndex++;
        }
        if (uiRowIndex < 0 || uiRowIndex > activeGridModel.getRowCount() - 1) {
            return;
        }

        if (uiRowIndex == leadRowIndex) {
            //Don't move if the new rowIndex equals the index of the row(s) being moved
            return;
        } else if (uiRowIndex < activeGridModel.getRows().indexOf(leadRow)) {
            //Don't move up if the pointer is in the bottom half of the target row.
            if (offsetY > activeGridModel.getRow(uiRowIndex).getHeight() / 2) {
                return;
            }
        } else if (uiRowIndex > activeGridModel.getRows().indexOf(leadRow)) {
            //Don't move down if the pointer is in the top half of the target row.
            if (offsetY < activeGridModel.getRow(uiRowIndex).getHeight() / 2) {
                return;
            }
        }

        //Move row(s) and update highlight
        destroyColumns(allGridColumns);
        activeGridModel.moveRowsTo(uiRowIndex, activeGridRows);

        final double rowOffsetY = rendererHelper.getRowOffset(leadRow) + headerHeight;
        state.getEventColumnHighlight().setY(activeGridWidget.getComputedLocation().getY() + rowOffsetY);
        state.setOperation(GridWidgetDnDHandlersState.GridWidgetHandlersOperation.ROW_MOVE);
        layer.batch();
    }

    //Destroy all DOMElement based columns as their creation stores the GridBodyCellRenderContext
    //which is used to write updated GridCellValue(s) to the underlying GridData at the coordinate
    //at which the DOMElement was created. Moving Rows and Columns changes these coordinates and
    //hence the reference held in the DOMElement becomes out of date.
    private void destroyColumns(final List<GridColumn<?>> columns) {
        for (GridColumn<?> column : columns) {
            if (column instanceof HasDOMElementResources) {
                ((HasDOMElementResources) column).destroyResources();
            }
        }
    }
}
