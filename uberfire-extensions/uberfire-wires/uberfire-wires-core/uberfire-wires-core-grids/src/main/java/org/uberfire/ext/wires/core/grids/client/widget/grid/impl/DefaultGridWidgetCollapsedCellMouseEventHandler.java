/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.List;
import java.util.Optional;

import com.ait.lienzo.client.core.event.AbstractNodeMouseEvent;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.animation.MergableGridWidgetCollapseRowsAnimation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.animation.MergableGridWidgetExpandRowsAnimation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

/**
 * A {@link NodeMouseEventHandler} to handle interaction with a "merged" {@link GridCell} "hot spot". The
 * {@link AbstractNodeMouseEvent} is checked to have happened over the {@link GridWidget#onGroupingToggle(double, double, double, double)}
 * in which case the applicable {@link GridRow}(s) are either collapsed or expanded; depending on their state.
 */
public class DefaultGridWidgetCollapsedCellMouseEventHandler implements NodeMouseEventHandler {

    protected GridRenderer renderer;

    public DefaultGridWidgetCollapsedCellMouseEventHandler(final GridRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public boolean onNodeMouseEvent(final GridWidget gridWidget,
                                    final Point2D relativeLocation,
                                    final Optional<Integer> uiHeaderRowIndex,
                                    final Optional<Integer> uiHeaderColumnIndex,
                                    final Optional<Integer> uiRowIndex,
                                    final Optional<Integer> uiColumnIndex,
                                    final AbstractNodeMouseEvent event) {
        if (isDNDOperationInProgress(gridWidget)) {
            return false;
        }

        boolean isHandled = false;
        if (uiRowIndex.isPresent() && uiColumnIndex.isPresent()) {
            isHandled = handleBodyCell(gridWidget,
                                       relativeLocation,
                                       uiRowIndex.get(),
                                       uiColumnIndex.get(),
                                       event);
        }

        return isHandled;
    }

    @Override
    public boolean handleBodyCell(final GridWidget gridWidget,
                                  final Point2D relativeLocation,
                                  final int uiRowIndex,
                                  final int uiColumnIndex,
                                  final AbstractNodeMouseEvent event) {
        final GridData gridData = gridWidget.getModel();
        final List<GridColumn<?>> gridColumns = gridData.getColumns();
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();

        //Check if the cell can be Grouped
        final GridCell<?> cell = gridData.getCell(uiRowIndex,
                                                  uiColumnIndex);
        if (cell == null) {
            return false;
        }
        if (cell.getMergedCellCount() < 2) {
            return false;
        }

        //Check if the Grouping control has been clicked
        final double cy = relativeLocation.getY();
        final double cx = relativeLocation.getX();
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(cx);
        final double offsetX = ci.getOffsetX();

        final GridRow gridRow = gridData.getRow(uiRowIndex);
        final GridColumn<?> gridColumn = gridColumns.get(uiColumnIndex);
        final GridCell<?> nextRowCell = gridData.getCell(uiRowIndex + 1,
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
            collapseRows(gridWidget,
                         uiRowIndex,
                         uiColumnIndex,
                         cell.getMergedCellCount());
        } else {
            expandRows(gridWidget,
                       uiRowIndex,
                       uiColumnIndex,
                       cell.getMergedCellCount());
        }

        return true;
    }

    void collapseRows(final GridWidget gridWidget,
                      final int uiRowIndex,
                      final int uiColumnIndex,
                      final int rowCount) {
        final MergableGridWidgetCollapseRowsAnimation a = new MergableGridWidgetCollapseRowsAnimation(gridWidget,
                                                                                                      uiRowIndex,
                                                                                                      uiColumnIndex,
                                                                                                      rowCount);
        a.run();
    }

    void expandRows(final GridWidget gridWidget,
                    final int uiRowIndex,
                    final int uiColumnIndex,
                    final int rowCount) {
        final MergableGridWidgetExpandRowsAnimation a = new MergableGridWidgetExpandRowsAnimation(gridWidget,
                                                                                                  uiRowIndex,
                                                                                                  uiColumnIndex,
                                                                                                  rowCount);
        a.run();
    }
}
