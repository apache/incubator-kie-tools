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

import java.util.List;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.DragMode;
import com.google.gwt.dom.client.Style;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

/**
 * MouseDownHandler to handle the commencement of drag operations.
 */
public class GridWidgetDnDMouseDownHandler implements NodeMouseDownHandler {

    protected final GridLayer layer;
    protected final GridWidgetDnDHandlersState state;

    public GridWidgetDnDMouseDownHandler(final GridLayer layer,
                                         final GridWidgetDnDHandlersState state) {
        this.layer = layer;
        this.state = state;
    }

    @Override
    public void onNodeMouseDown(final NodeMouseDownEvent event) {
        //The Grid that the pointer is currently over is set by the MouseMoveHandler
        if (state.getActiveGridWidget() == null) {
            return;
        }

        //Get the GridWidget for the grid.
        final GridWidget activeGridWidget = state.getActiveGridWidget();
        final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate(activeGridWidget,
                                                                          new Point2D(event.getX(),
                                                                                      event.getY()));

        //Move from one of the pending operations to the actual operation, as appropriate.
        switch (state.getOperation()) {
            case COLUMN_RESIZE_PENDING:
                if (state.getActiveGridColumns().isEmpty()) {
                    return;
                }
                state.setEventInitialX(ap.getX());
                state.setEventInitialColumnWidth(state.getActiveGridColumns().get(0).getWidth());
                state.setOperation(GridWidgetDnDHandlersState.GridWidgetHandlersOperation.COLUMN_RESIZE);
                break;

            case COLUMN_MOVE_PENDING:
                if (state.getActiveGridColumns().isEmpty()) {
                    return;
                }

                state.setEventInitialX(ap.getX());
                state.setOperation(GridWidgetDnDHandlersState.GridWidgetHandlersOperation.COLUMN_MOVE_INITIATED);
                showColumnHighlight(state.getActiveGridWidget(), state.getActiveGridColumns());
                break;

            case ROW_MOVE_PENDING:
                if (state.getActiveGridRows().isEmpty()) {
                    return;
                }

                state.setEventInitialX(ap.getX());
                state.setOperation(GridWidgetDnDHandlersState.GridWidgetHandlersOperation.ROW_MOVE_INITIATED);
                showRowHighlight(state.getActiveGridWidget(), state.getActiveGridRows());
                break;

            case GRID_MOVE_PENDING:
                state.setOperation(GridWidgetDnDHandlersState.GridWidgetHandlersOperation.GRID_MOVE);
                activeGridWidget.setDragMode(DragMode.SAME_LAYER);
                activeGridWidget.setDraggable(true);
                setCursor(Style.Cursor.MOVE);
        }
    }

    private void setCursor(final Style.Cursor cursor) {
        layer.getViewport().getElement().getStyle().setCursor(cursor);
        state.setCursor(cursor);
    }

    @SuppressWarnings("unchecked")
    protected void showColumnHighlight(final GridWidget view,
                                       final List<GridColumn<?>> activeGridColumns) {
        final BaseGridRendererHelper rendererHelper = view.getRendererHelper();
        final BaseGridRendererHelper.RenderingInformation renderingInformation = rendererHelper.getRenderingInformation();
        if (renderingInformation == null) {
            return;
        }

        final Group header = view.getHeader();
        final double headerRowsYOffset = renderingInformation.getHeaderRowsYOffset();
        final double headerMinY = (header == null ? headerRowsYOffset : header.getY() + headerRowsYOffset);

        final Bounds bounds = renderingInformation.getBounds();
        final double activeColumnX = rendererHelper.getColumnOffset(activeGridColumns.get(0));
        final double highlightWidth = getHighlightWidth(activeGridColumns);
        final double highlightHeight = getHighlightHeight(bounds,
                                                          view,
                                                          headerMinY);

        final GridWidgetDnDProxy highlight = state.getEventColumnHighlight();
        highlight.setWidth(highlightWidth);
        highlight.setHeight(highlightHeight);
        highlight.setX(view.getComputedLocation().getX() + activeColumnX);
        highlight.setY(view.getComputedLocation().getY() + headerMinY);
        layer.add(state.getEventColumnHighlight());
        layer.getLayer().batch();
    }

    private double getHighlightWidth(final List<GridColumn<?>> activeGridColumns) {
        double highlightWidth = 0;
        for (GridColumn<?> activeGridColumn : activeGridColumns) {
            highlightWidth = highlightWidth + activeGridColumn.getWidth();
        }
        return highlightWidth;
    }

    private double getHighlightHeight(final Bounds bounds,
                                      final GridWidget view,
                                      final double headerMinY) {
        final double highlightHeight = Math.min(bounds.getY() + bounds.getHeight() - view.getComputedLocation().getY(),
                                                view.getHeight()) - headerMinY;
        return highlightHeight;
    }

    protected void showRowHighlight(final GridWidget view,
                                    final List<GridRow> activeGridRows) {
        final BaseGridRendererHelper rendererHelper = view.getRendererHelper();
        final BaseGridRendererHelper.RenderingInformation renderingInformation = rendererHelper.getRenderingInformation();
        if (renderingInformation == null) {
            return;
        }

        final Bounds bounds = renderingInformation.getBounds();
        final GridRow row = activeGridRows.get(0);
        final int rowIndex = view.getModel().getRows().indexOf(row);
        final List<Double> allRowHeights = renderingInformation.getAllRowHeights();
        final double rowOffsetY = rendererHelper.getRowOffset(rowIndex, allRowHeights) + view.getRenderer().getHeaderHeight();

        final double highlightWidth = Math.min(bounds.getX() + bounds.getWidth() - view.getComputedLocation().getX(),
                                               view.getWidth());
        final double highlightHeight = allRowHeights.get(rowIndex);

        final GridWidgetDnDProxy highlight = state.getEventColumnHighlight();
        highlight.setWidth(highlightWidth);
        highlight.setHeight(highlightHeight);
        highlight.setX(view.getComputedLocation().getX());
        highlight.setY(view.getComputedLocation().getY() + rowOffsetY);
        layer.add(state.getEventColumnHighlight());
        layer.getLayer().batch();
    }
}
