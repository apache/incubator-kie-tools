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
package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.List;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.animation.MergableGridWidgetCollapseRowsAnimation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.animation.MergableGridWidgetExpandRowsAnimation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;

/**
 * Base MouseClickHandler to handle clicks to either the GridWidgets Header or Body. This implementation
 * supports clicking on a "linked" column in the Header and delegating a response to the GridSelectionManager.
 */
public class BaseGridWidgetMouseClickHandler implements NodeMouseClickHandler {

    protected GridData gridModel;
    protected GridWidget gridWidget;
    protected BaseGridRendererHelper rendererHelper;
    protected GridSelectionManager selectionManager;
    protected GridRenderer renderer;

    public BaseGridWidgetMouseClickHandler(final GridWidget gridWidget,
                                           final GridSelectionManager selectionManager,
                                           final GridRenderer renderer) {
        this.gridWidget = gridWidget;
        this.gridModel = gridWidget.getModel();
        this.rendererHelper = gridWidget.getRendererHelper();
        this.selectionManager = selectionManager;
        this.renderer = renderer;
    }

    @Override
    public void onNodeMouseClick(final NodeMouseClickEvent event) {
        if (!gridWidget.isVisible()) {
            return;
        }
        if (!handleHeaderCellClick(event)) {
            if (!handleBodyCellClick(event)) {
                selectionManager.select(gridWidget);
            }
        }
    }

    /**
     * Check if a MouseClickEvent happened on a "linked" column. If it does then
     * delegate a response to GridSelectionManager.
     * @param event
     */
    protected boolean handleHeaderCellClick(final NodeMouseClickEvent event) {
        //Convert Canvas co-ordinate to Grid co-ordinate
        final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate(gridWidget,
                                                                          new Point2D(event.getX(),
                                                                                      event.getY()));
        final double cx = ap.getX();
        final double cy = ap.getY();

        final Group header = gridWidget.getHeader();
        final BaseGridRendererHelper.RenderingInformation ri = rendererHelper.getRenderingInformation();
        final double headerRowsYOffset = ri.getHeaderRowsYOffset();
        final double headerMinY = (header == null ? headerRowsYOffset : header.getY() + headerRowsYOffset);
        final double headerMaxY = (header == null ? renderer.getHeaderHeight() : renderer.getHeaderHeight() + header.getY());

        if (cx < 0 || cx > gridWidget.getWidth()) {
            return false;
        }
        if (cy < headerMinY || cy > headerMaxY) {
            return false;
        }

        //Get column index
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(cx);
        final GridColumn<?> column = ci.getColumn();
        if (column == null) {
            return false;
        }

        //If linked scroll it into view
        if (column.isLinked()) {
            final GridColumn<?> link = column.getLink();
            selectionManager.selectLinkedColumn(link);
            return true;
        }

        return false;
    }

    protected boolean handleBodyCellClick(final NodeMouseClickEvent event) {
        //Convert Canvas co-ordinate to Grid co-ordinate
        final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate(gridWidget,
                                                                          new Point2D(event.getX(),
                                                                                      event.getY()));
        final double cx = ap.getX();
        final double cy = ap.getY();

        final Group header = gridWidget.getHeader();
        final double headerMaxY = (header == null ? renderer.getHeaderHeight() : renderer.getHeaderHeight() + header.getY());

        if (cx < 0 || cx > gridWidget.getWidth()) {
            return false;
        }
        if (cy < headerMaxY || cy > gridWidget.getHeight()) {
            return false;
        }
        if (gridModel.getRowCount() == 0) {
            return false;
        }

        //Get row index
        GridRow row;
        int uiRowIndex = 0;
        double offsetY = cy - renderer.getHeaderHeight();
        while ((row = gridModel.getRow(uiRowIndex)).getHeight() < offsetY) {
            offsetY = offsetY - row.getHeight();
            uiRowIndex++;
        }
        if (uiRowIndex < 0 || uiRowIndex > gridModel.getRowCount() - 1) {
            return false;
        }

        //Get column index
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(cx);
        final double offsetX = ci.getOffsetX();
        final GridColumn<?> column = ci.getColumn();
        final List<GridColumn<?>> columns = gridModel.getColumns();

        if (column == null) {
            return false;
        }
        final int uiColumnIndex = ci.getUiColumnIndex();
        if (uiColumnIndex < 0 || uiColumnIndex > columns.size() - 1) {
            return false;
        }

        //Check if the cell can be Grouped
        final GridCell<?> cell = gridModel.getCell(uiRowIndex,
                                                   uiColumnIndex);
        if (cell == null) {
            return false;
        }
        if (cell.getMergedCellCount() < 2) {
            return false;
        }

        //Check if the Grouping control has been clicked
        final GridRow gridRow = gridModel.getRow(uiRowIndex);
        final GridColumn<?> gridColumn = columns.get(uiColumnIndex);
        final GridCell<?> nextRowCell = gridModel.getCell(uiRowIndex + 1,
                                                          uiColumnIndex);
        final double cellX = cx - offsetX;
        final double cellY = cy - rendererHelper.getRowOffset(uiRowIndex) - renderer.getHeaderHeight();
        if (!gridWidget.onGroupingToggle(cellX,
                                         cellY,
                                         gridColumn.getWidth(),
                                         gridRow.getHeight())) {
            return false;
        }

        //Collapse or expand rows as needed
        if (!nextRowCell.isCollapsed()) {
            collapseRows(uiRowIndex,
                         uiColumnIndex,
                         cell.getMergedCellCount());
        } else {
            expandRows(uiRowIndex,
                       uiColumnIndex,
                       cell.getMergedCellCount());
        }

        return true;
    }

    void collapseRows(final int uiRowIndex,
                      final int uiColumnIndex,
                      final int rowCount) {
        final MergableGridWidgetCollapseRowsAnimation a = new MergableGridWidgetCollapseRowsAnimation(gridWidget,
                                                                                                      uiRowIndex,
                                                                                                      uiColumnIndex,
                                                                                                      rowCount);
        a.run();
    }

    void expandRows(final int uiRowIndex,
                    final int uiColumnIndex,
                    final int rowCount) {
        final MergableGridWidgetExpandRowsAnimation a = new MergableGridWidgetExpandRowsAnimation(gridWidget,
                                                                                                  uiRowIndex,
                                                                                                  uiColumnIndex,
                                                                                                  rowCount);
        a.run();
    }
}
