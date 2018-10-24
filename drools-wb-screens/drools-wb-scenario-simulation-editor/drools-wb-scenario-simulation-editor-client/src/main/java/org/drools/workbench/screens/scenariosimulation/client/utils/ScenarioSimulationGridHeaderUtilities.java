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

package org.drools.workbench.screens.scenariosimulation.client.utils;

import java.util.List;
import java.util.Optional;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

public class ScenarioSimulationGridHeaderUtilities {

    /**
     * Gets the header row index corresponding to the provided Canvas y-coordinate relative to the grid. Grid-relative coordinates
     * can be obtained from {@link INodeXYEvent} using {@link CoordinateUtilities#convertDOMToGridCoordinate(GridWidget, Point2D)}
     * @param gridWidget GridWidget to check.
     * @param column Column on which the even has occurred
     * @param cy y-coordinate relative to the GridWidget.
     * @return The header row index or null if the coordinate did not map to a header row.
     */
    public static Integer getUiHeaderRowIndex(final GridWidget gridWidget,
                                              final GridColumn<?> column,
                                              final double cy) {
        final Group header = gridWidget.getHeader();
        final GridRenderer renderer = gridWidget.getRenderer();
        final BaseGridRendererHelper.RenderingInformation ri = gridWidget.getRendererHelper().getRenderingInformation();
        final double headerRowsYOffset = ri.getHeaderRowsYOffset();
        final double headerMinY = (header == null ? headerRowsYOffset : header.getY() + headerRowsYOffset);
        final double headerMaxY = (header == null ? renderer.getHeaderHeight() : renderer.getHeaderHeight() + header.getY());

        if (cy < headerMinY || cy > headerMaxY) {
            return null;
        }

        //Get header row index
        int uiHeaderRowIndex = 0;
        double offsetY = cy - headerMinY;
        final int headerRowCount = gridWidget.getModel().getHeaderRowCount();
        final double headerRowHeight = renderer.getHeaderRowHeight();
        final double headerRowsHeight = headerRowHeight * headerRowCount;
        final double columnHeaderRowHeight = headerRowsHeight / column.getHeaderMetaData().size();
        while (columnHeaderRowHeight < offsetY) {
            offsetY = offsetY - columnHeaderRowHeight;
            uiHeaderRowIndex++;
        }
        if (uiHeaderRowIndex < 0 || uiHeaderRowIndex > column.getHeaderMetaData().size() - 1) {
            return null;
        }

        return uiHeaderRowIndex;
    }

    /**
     * Retrieve the  <code>ScenarioHeaderMetaData</code> from the <code>GridColumn</code> of a <code>GridWidget</code> at a given point x.
     * It returns <code>null</code> if none is present at that position.
     * @param gridWidget
     * @param cx
     * @param cy
     * @return
     */
    public static ScenarioHeaderMetaData getColumnScenarioHeaderMetaData(GridWidget gridWidget, double cx, double cy) {
        final GridColumn<?> column = getGridColumn(gridWidget, cx);
        if (column == null) {
            return null;
        }
        //Get row index
        final Integer uiHeaderRowIndex = ScenarioSimulationGridHeaderUtilities.getUiHeaderRowIndex(gridWidget,
                                                                                                   column,
                                                                                                   cy);
        return (ScenarioHeaderMetaData) column.getHeaderMetaData().get(uiHeaderRowIndex);
    }

    /**
     * Retrieve the  <code>ScenarioHeaderMetaData</code> from the <code>GridColumn</code> of a <code>GridWidget</code> at a given point.
     * It returns <code>null</code> if none is present at that position.
     * @param gridWidget
     * @param column
     * @param cy
     * @return
     */
    public static ScenarioHeaderMetaData getColumnScenarioHeaderMetaData(GridWidget gridWidget, GridColumn<?> column, double cy) {
        //Get row index
        final Integer uiHeaderRowIndex = ScenarioSimulationGridHeaderUtilities.getUiHeaderRowIndex(gridWidget,
                                                                                                   column,
                                                                                                   cy);
        return uiHeaderRowIndex == null ? null : (ScenarioHeaderMetaData) column.getHeaderMetaData().get(uiHeaderRowIndex);
    }

    /**
     * Retrieve the <code>GridColumn</code> of a <code>GridWidget</code> at a given point x.
     * It returns <code>null</code> if none is present at that position.
     * @param gridWidget
     * @param cx
     * @return
     */
    public static GridColumn<?> getGridColumn(GridWidget gridWidget, double cx) {
        //Get column information
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final BaseGridRendererHelper.RenderingInformation ri = rendererHelper.getRenderingInformation();
        if (ri == null) {
            return null;
        }
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(cx);
        return ci.getColumn();
    }

    public static boolean hasEditableHeader(final GridColumn<?> column) {
        return column.getHeaderMetaData().stream().anyMatch(md -> md instanceof ScenarioHeaderMetaData);
    }

    public static boolean isEditableHeader(final GridColumn<?> column,
                                           final Integer uiHeaderRowIndex) {
        GridColumn.HeaderMetaData headerMetaData = column.getHeaderMetaData().get(uiHeaderRowIndex);
        return headerMetaData instanceof ScenarioHeaderMetaData && !((ScenarioHeaderMetaData) headerMetaData).isReadOnly();
    }

    public static GridBodyCellEditContext makeRenderContext(final GridWidget gridWidget,
                                                            final BaseGridRendererHelper.RenderingInformation ri,
                                                            final BaseGridRendererHelper.ColumnInformation ci,
                                                            final int uiHeaderRowIndex) {
        return makeRenderContext(gridWidget,
                                 ri,
                                 ci,
                                 null,
                                 uiHeaderRowIndex);
    }

    public static GridBodyCellEditContext makeRenderContext(final GridWidget gridWidget,
                                                            final BaseGridRendererHelper.RenderingInformation ri,
                                                            final BaseGridRendererHelper.ColumnInformation ci,
                                                            final Point2D rp,
                                                            final int uiHeaderRowIndex) {
        final GridColumn<?> column = ci.getColumn();
        final GridRenderer renderer = gridWidget.getRenderer();

        final Group header = gridWidget.getHeader();
        final int headerRowCount = gridWidget.getModel().getHeaderRowCount();
        final double headerRowsYOffset = ri.getHeaderRowsYOffset();
        final double headerMinY = (header == null ? headerRowsYOffset : header.getY() + headerRowsYOffset);
        final double headerRowHeight = renderer.getHeaderRowHeight();
        final double headerRowsHeight = headerRowCount * headerRowHeight;
        final double columnHeaderRowHeight = headerRowsHeight / column.getHeaderMetaData().size();

        final double cellX = gridWidget.getAbsoluteX() + ci.getOffsetX();
        final double cellY = gridWidget.getAbsoluteY() + headerMinY + (columnHeaderRowHeight * uiHeaderRowIndex);

        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = ri.getFloatingBlockInformation();
        final double floatingX = floatingBlockInformation.getX();
        final double floatingWidth = floatingBlockInformation.getWidth();
        final double clipMinX = gridWidget.getAbsoluteX() + floatingX + floatingWidth;
        final double clipMinY = gridWidget.getAbsoluteY();

        //Check and adjust for blocks of columns sharing equal HeaderMetaData
        double blockCellX = cellX;
        double blockCellWidth = column.getWidth();
        final List<GridColumn<?>> gridColumns = ri.getAllColumns();
        final GridColumn.HeaderMetaData clicked = column.getHeaderMetaData().get(uiHeaderRowIndex);

        //Walk backwards to block start
        if (ci.getUiColumnIndex() > 0) {
            int uiLeadColumnIndex = ci.getUiColumnIndex() - 1;
            GridColumn<?> lead = gridColumns.get(uiLeadColumnIndex);
            while (uiLeadColumnIndex >= 0 && isSameHeaderMetaData(clicked,
                                                                  lead.getHeaderMetaData(),
                                                                  uiHeaderRowIndex)) {
                blockCellX = blockCellX - lead.getWidth();
                blockCellWidth = blockCellWidth + lead.getWidth();
                if (--uiLeadColumnIndex >= 0) {
                    lead = gridColumns.get(uiLeadColumnIndex);
                }
            }
        }

        //Walk forwards to block end
        if (ci.getUiColumnIndex() < gridColumns.size() - 1) {
            int uiTailColumnIndex = ci.getUiColumnIndex() + 1;
            GridColumn<?> tail = gridColumns.get(uiTailColumnIndex);
            while (uiTailColumnIndex < gridColumns.size() && isSameHeaderMetaData(clicked,
                                                                                  tail.getHeaderMetaData(),
                                                                                  uiHeaderRowIndex)) {
                blockCellWidth = blockCellWidth + tail.getWidth();
                tail = gridColumns.get(uiTailColumnIndex);
                if (++uiTailColumnIndex < gridColumns.size()) {
                    tail = gridColumns.get(uiTailColumnIndex);
                }
            }
        }

        return new GridBodyCellEditContext(blockCellX,
                                           cellY,
                                           blockCellWidth,
                                           headerRowHeight,
                                           clipMinY,
                                           clipMinX,
                                           uiHeaderRowIndex,
                                           ci.getUiColumnIndex(),
                                           floatingBlockInformation.getColumns().contains(column),
                                           gridWidget.getViewport().getTransform(),
                                           renderer,
                                           Optional.ofNullable(rp));
    }

    private static boolean isSameHeaderMetaData(final GridColumn.HeaderMetaData clickedHeaderMetaData,
                                                final List<GridColumn.HeaderMetaData> columnHeaderMetaData,
                                                final int uiHeaderRowIndex) {
        if (uiHeaderRowIndex > columnHeaderMetaData.size() - 1) {
            return false;
        }
        return clickedHeaderMetaData.equals(columnHeaderMetaData.get(uiHeaderRowIndex));
    }
}
