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
import java.util.Collections;
import java.util.List;

import com.google.gwt.dom.client.Style;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

/**
 * A container for the state of the MouseDown, MouseMove and MouseUp handlers during a drag operation.
 */
public class GridWidgetDnDHandlersState {

    private GridWidget activeGridWidget = null;
    private GridColumn.HeaderMetaData activeHeaderMetaData = null;
    private List<GridColumn<?>> activeGridColumns = new ArrayList<GridColumn<?>>();
    private List<GridRow> activeGridRows = new ArrayList<GridRow>();

    private GridWidgetHandlersOperation operation = GridWidgetHandlersOperation.NONE;
    private Style.Cursor cursor = Style.Cursor.DEFAULT;

    private double eventInitialX = 0;
    private double eventInitialColumnWidth = 0;
    private GridWidgetDnDProxy eventColumnHighlight = new GridWidgetDnDProxy();

    /**
     * Resets the state to no operation with no active columns or rows.
     */
    public void reset() {
        if (activeGridWidget != null) {
            activeGridWidget.setDraggable(false);
        }
        this.activeGridWidget = null;
        this.activeHeaderMetaData = null;
        this.activeGridRows.clear();
        setOperation(GridWidgetDnDHandlersState.GridWidgetHandlersOperation.NONE);
        setCursor(Style.Cursor.DEFAULT);
    }

    /**
     * Returns the active GridWidget. Can be null if no DnD operation has been registered over a GridWidget.
     * @return The active GridWidget or null.
     */
    public GridWidget getActiveGridWidget() {
        return activeGridWidget;
    }

    /**
     * Sets the active GridWidget. Can be null.
     * @param activeGridWidget
     */
    public void setActiveGridWidget(final GridWidget activeGridWidget) {
        this.activeGridWidget = activeGridWidget;
    }

    /**
     * Returns the active HeaderMetaData.
     * @return
     */
    public GridColumn.HeaderMetaData getActiveHeaderMetaData() {
        return activeHeaderMetaData;
    }

    /**
     * Sets the active HeaderMetaData.
     * @param activeHeaderMetaData
     */
    public void setActiveHeaderMetaData(final GridColumn.HeaderMetaData activeHeaderMetaData) {
        this.activeHeaderMetaData = activeHeaderMetaData;
    }

    /**
     * Returns the active columns being affected by the current the operation.
     * @return
     */
    public List<GridColumn<?>> getActiveGridColumns() {
        return Collections.unmodifiableList(activeGridColumns);
    }

    /**
     * Sets the active columns to be affected by the current the operation.
     */
    public void setActiveGridColumns(final List<GridColumn<?>> activeGridColumns) {
        this.activeGridColumns.clear();
        this.activeGridColumns.addAll(activeGridColumns);
    }

    /**
     * Returns the active rows being affected by the current the operation.
     * @return
     */
    public List<GridRow> getActiveGridRows() {
        return Collections.unmodifiableList(activeGridRows);
    }

    /**
     * Sets the active rows to be affected by the current the operation.
     */
    public void setActiveGridRows(final List<GridRow> activeGridRows) {
        this.activeGridRows.clear();
        this.activeGridRows.addAll(activeGridRows);
    }

    /**
     * The current drag operation in progress.
     * @return
     */
    public GridWidgetHandlersOperation getOperation() {
        return operation;
    }

    /**
     * Sets the current drag operation in progress.
     * @param operation
     */
    public void setOperation(final GridWidgetHandlersOperation operation) {
        this.operation = operation;
    }

    /**
     * Returns the Cursor type to be shown for the current operation. This primarily used in conjunction with DOMElement based cells.
     * When the pointer moves over a DOM element the browser determines the Cursor to show based on the DOM element's CSS. This
     * however can be different to the pointer required during, for example, a column resize operation. In such cases the
     * browser changes the pointer to that defined by CSS replacing that set by the MouseMove handler.
     * @return
     */
    public Style.Cursor getCursor() {
        return cursor;
    }

    /**
     * Sets the Cursor type to be shown for the current operation.
     * @param cursor
     */
    public void setCursor(Style.Cursor cursor) {
        this.cursor = cursor;
    }

    /**
     * Returns the grid-relative x-coordinate of the Mouse Event.
     * @return
     */
    public double getEventInitialX() {
        return eventInitialX;
    }

    /**
     * Sets the grid-relative x-coordinate of the Mouse Event.
     * @param eventInitialX
     */
    public void setEventInitialX(final double eventInitialX) {
        this.eventInitialX = eventInitialX;
    }

    /**
     * Returns the width of a column being re-sized at the commencement of the resize operation.
     * During a re-size operation the new width is determined by calculating the delta of
     * the MouseMoveEvent coordinates. The initial width is therefore required to apply
     * the same delta.
     * @return
     */
    public double getEventInitialColumnWidth() {
        return eventInitialColumnWidth;
    }

    /**
     * Sets the initial width of a column to be resized.
     * @param eventInitialColumnWidth
     */
    public void setEventInitialColumnWidth(final double eventInitialColumnWidth) {
        this.eventInitialColumnWidth = eventInitialColumnWidth;
    }

    /**
     * Returns  the Group representing the column during a drag operation of the column being moved
     * @return
     */
    public GridWidgetDnDProxy getEventColumnHighlight() {
        return eventColumnHighlight;
    }

    /**
     * The different states of the drag operation.
     */
    public enum GridWidgetHandlersOperation {
        NONE,                   // Nothing happening!
        COLUMN_RESIZE_PENDING,  // The ability to resize a column has been detected.
        COLUMN_RESIZE,          // A column is being resized
        COLUMN_MOVE_PENDING,    // The ability to move a column has been detected.
        COLUMN_MOVE_INITIATED,  // A column is able to move but has not _been_ moved to a new position.
        COLUMN_MOVE,            // A column is able to move and had been moved to a new position.
        ROW_MOVE_PENDING,       // The ability to move a row has been detected.
        ROW_MOVE_INITIATED,     // A row is able to move but has not _been_ moved to a new position.
        ROW_MOVE,               // A row is able to move and has been moved to a new position.
        GRID_MOVE_PENDING,      // The ability to move a grid has been detected.
        GRID_MOVE               // A grid has been moved.
    }
}
