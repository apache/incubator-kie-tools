/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

public class CellContextUtilities {

    public static GridBodyCellEditContext makeCellRenderContext(final GridWidget gridWidget,
                                                                final BaseGridRendererHelper.RenderingInformation ri,
                                                                final BaseGridRendererHelper.ColumnInformation ci,
                                                                final int uiRowIndex) {
        final GridColumn<?> column = ci.getColumn();
        final GridRenderer renderer = gridWidget.getRenderer();

        final double cellX = getCellX(gridWidget, ci);
        final double cellY = getCellY(gridWidget, uiRowIndex);

        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = ri.getFloatingBlockInformation();
        final double clipMinX = getClipMinX(gridWidget, floatingBlockInformation);
        final double clipMinY = getClipMinY(gridWidget);

        final double blockCellWidth = column.getWidth();
        final double blockCellHeight = ri.getAllRowHeights().get(uiRowIndex);

        return new GridBodyCellEditContext(cellX,
                                           cellY,
                                           blockCellWidth,
                                           blockCellHeight,
                                           clipMinY,
                                           clipMinX,
                                           uiRowIndex,
                                           ci.getUiColumnIndex(),
                                           floatingBlockInformation.getColumns().contains(column),
                                           gridWidget.getViewport().getTransform(),
                                           renderer,
                                           Optional.empty());
    }

    public static GridBodyCellEditContext makeHeaderCellRenderContext(final GridWidget gridWidget,
                                                                      final BaseGridRendererHelper.RenderingInformation ri,
                                                                      final BaseGridRendererHelper.ColumnInformation ci,
                                                                      final int uiHeaderRowIndex) {
        return makeHeaderCellRenderContext(gridWidget,
                                           ri,
                                           ci,
                                           null,
                                           uiHeaderRowIndex);
    }

    public static GridBodyCellEditContext makeHeaderCellRenderContext(final GridWidget gridWidget,
                                                                      final BaseGridRendererHelper.RenderingInformation ri,
                                                                      final BaseGridRendererHelper.ColumnInformation ci,
                                                                      final Point2D rp,
                                                                      final int uiHeaderRowIndex) {
        final GridColumn<?> column = ci.getColumn();
        final GridRenderer renderer = gridWidget.getRenderer();
        final double headerRowHeight = getHeaderRowHeight(ri, column);

        final double cellX = getCellX(gridWidget, ci);
        final double cellY = getHeaderY(gridWidget, ri) + (headerRowHeight * uiHeaderRowIndex);

        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = ri.getFloatingBlockInformation();
        final double clipMinX = getClipMinX(gridWidget, floatingBlockInformation);
        final double clipMinY = getClipMinY(gridWidget);

        final List<GridColumn<?>> gridColumns = ri.getAllColumns();
        final GridColumn.HeaderMetaData headerMetaData = column.getHeaderMetaData().get(uiHeaderRowIndex);
        final int blockStartColumnIndex = ColumnIndexUtilities.getHeaderBlockStartColumnIndex(gridColumns,
                                                                                              headerMetaData,
                                                                                              uiHeaderRowIndex,
                                                                                              ci.getUiColumnIndex());
        final int blockEndColumnIndex = ColumnIndexUtilities.getHeaderBlockEndColumnIndex(gridColumns,
                                                                                          headerMetaData,
                                                                                          uiHeaderRowIndex,
                                                                                          ci.getUiColumnIndex());

        final double blockCellWidth = IntStream.rangeClosed(blockStartColumnIndex,
                                                            blockEndColumnIndex)
                .mapToDouble(uiHeaderColumnIndex -> gridColumns.get(uiHeaderColumnIndex).getWidth())
                .sum();

        return new GridBodyCellEditContext(cellX,
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

    public static void editSelectedCell(final GridWidget gridWidget) {
        editSelectedCell(gridWidget, null);
    }

    public static void editSelectedCell(final GridWidget gridWidget,
                                        final Point2D relativeLocation) {
        final GridData gridModel = gridWidget.getModel();

        if (gridModel.getSelectedHeaderCells().size() > 0) {
            final GridData.SelectedCell selectedCell = gridModel.getSelectedHeaderCells().get(0);
            final int uiHeaderRowIndex = selectedCell.getRowIndex();
            final int uiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(gridModel.getColumns(),
                                                                             selectedCell.getColumnIndex());

            final GridColumn<?> column = gridModel.getColumns().get(uiColumnIndex);
            final GridColumn.HeaderMetaData headerMetaData = column.getHeaderMetaData().get(uiHeaderRowIndex);

            final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
            final BaseGridRendererHelper.RenderingInformation ri = rendererHelper.getRenderingInformation();
            final double columnXCoordinate = rendererHelper.getColumnOffset(column) + column.getWidth() / 2;
            final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(columnXCoordinate);

            final GridBodyCellEditContext context = CellContextUtilities.makeHeaderCellRenderContext(gridWidget,
                                                                                                     ri,
                                                                                                     ci,
                                                                                                     relativeLocation,
                                                                                                     uiHeaderRowIndex);

            headerMetaData.edit(context);
        } else if (gridModel.getSelectedCells().size() > 0) {
            final GridData.SelectedCell origin = gridModel.getSelectedCellsOrigin();
            if (origin == null) {
                return;
            }
            gridWidget.startEditingCell(origin.getRowIndex(),
                                        ColumnIndexUtilities.findUiColumnIndex(gridModel.getColumns(),
                                                                               origin.getColumnIndex()));
        }
    }

    private static double getCellX(final GridWidget gridWidget,
                                   final BaseGridRendererHelper.ColumnInformation ci) {
        return gridWidget.getComputedLocation().getX() + ci.getOffsetX();
    }

    private static double getCellY(final GridWidget gridWidget,
                                   final int uiRowIndex) {
        final GridRenderer renderer = gridWidget.getRenderer();
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        return gridWidget.getComputedLocation().getY() + renderer.getHeaderHeight() + rendererHelper.getRowOffset(uiRowIndex);
    }

    private static double getHeaderY(final GridWidget gridWidget,
                                     final BaseGridRendererHelper.RenderingInformation ri) {
        final Group header = gridWidget.getHeader();
        final double headerRowsYOffset = ri.getHeaderRowsYOffset();
        final double headerMinY = (header == null ? headerRowsYOffset : header.getY() + headerRowsYOffset);
        return gridWidget.getComputedLocation().getY() + headerMinY;
    }

    private static double getHeaderRowHeight(final BaseGridRendererHelper.RenderingInformation ri,
                                             final GridColumn<?> column) {
        if (column.getHeaderMetaData() == null || column.getHeaderMetaData().size() == 0) {
            return 0.0;
        }
        return ri.getHeaderRowsHeight() / column.getHeaderMetaData().size();
    }

    private static double getClipMinX(final GridWidget gridWidget,
                                      final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation) {
        final double floatingX = floatingBlockInformation.getX();
        final double floatingWidth = floatingBlockInformation.getWidth();
        return gridWidget.getComputedLocation().getX() + floatingX + floatingWidth;
    }

    private static double getClipMinY(final GridWidget gridWidget) {
        return gridWidget.getComputedLocation().getY();
    }
}
