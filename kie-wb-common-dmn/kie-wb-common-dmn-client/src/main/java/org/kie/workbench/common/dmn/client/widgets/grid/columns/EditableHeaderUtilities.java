/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

public class EditableHeaderUtilities {

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
        final double headerRowsHeight = renderer.getHeaderRowHeight();
        final double headerRowHeight = headerRowsHeight / column.getHeaderMetaData().size();
        while (headerRowHeight < offsetY) {
            offsetY = offsetY - headerRowHeight;
            uiHeaderRowIndex++;
        }
        if (uiHeaderRowIndex < 0 || uiHeaderRowIndex > column.getHeaderMetaData().size() - 1) {
            return null;
        }

        return uiHeaderRowIndex;
    }

    public static GridBodyCellRenderContext makeRenderContext(final GridWidget gridWidget,
                                                              final BaseGridRendererHelper.RenderingInformation ri,
                                                              final BaseGridRendererHelper.ColumnInformation ci,
                                                              final int uiHeaderRowIndex) {
        final GridColumn<?> column = ci.getColumn();
        final GridRenderer renderer = gridWidget.getRenderer();

        final Group header = gridWidget.getHeader();
        final double headerRowsYOffset = ri.getHeaderRowsYOffset();
        final double headerMinY = (header == null ? headerRowsYOffset : header.getY() + headerRowsYOffset);
        final double headerRowHeight = renderer.getHeaderRowHeight() / column.getHeaderMetaData().size();

        final double cellX = gridWidget.getAbsoluteX() + ci.getOffsetX();
        final double cellY = gridWidget.getAbsoluteY() + headerMinY + (headerRowHeight * uiHeaderRowIndex);

        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = ri.getFloatingBlockInformation();
        final double floatingX = floatingBlockInformation.getX();
        final double floatingWidth = floatingBlockInformation.getWidth();
        final double clipMinX = gridWidget.getAbsoluteX() + floatingX + floatingWidth;
        final double clipMinY = gridWidget.getAbsoluteY();

        return new GridBodyCellRenderContext(cellX,
                                             cellY,
                                             column.getWidth(),
                                             headerRowHeight,
                                             clipMinY,
                                             clipMinX,
                                             uiHeaderRowIndex,
                                             ci.getUiColumnIndex(),
                                             floatingBlockInformation.getColumns().contains(column),
                                             gridWidget.getViewport().getTransform(),
                                             renderer);
    }
}
