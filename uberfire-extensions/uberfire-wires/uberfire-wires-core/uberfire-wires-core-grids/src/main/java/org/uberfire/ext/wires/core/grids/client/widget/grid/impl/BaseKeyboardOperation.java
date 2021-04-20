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

package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.List;
import java.util.Objects;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper.RenderingBlockInformation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper.RenderingInformation;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static com.google.gwt.event.dom.client.KeyCodes.KEY_RIGHT;

public abstract class BaseKeyboardOperation implements KeyboardOperation {

    protected GridLayer gridLayer;

    public BaseKeyboardOperation(final GridLayer gridLayer) {
        this.gridLayer = Objects.requireNonNull(gridLayer, "gridLayer");
    }

    @Override
    public TriStateBoolean isShiftKeyDown() {
        return TriStateBoolean.DONT_CARE;
    }

    @Override
    public TriStateBoolean isControlKeyDown() {
        return TriStateBoolean.DONT_CARE;
    }

    @Override
    public boolean isExecutable(final GridWidget gridWidget) {
        final GridData gridModel = gridWidget.getModel();
        final int rowCount = gridModel.getRowCount();
        final int columnCount = gridModel.getColumnCount();
        if (rowCount == 0 || columnCount == 0) {
            return false;
        } else {
            return true;
        }
    }

    protected boolean scrollSelectedCellIntoView(final GridWidget gridWidget) {
        if (!isSelectionOriginSet(gridWidget) && !isHeaderSelectionOriginSet(gridWidget)) {
            return false;
        }

        boolean isHeaderCellSelected = false;
        if (!isSelectionOriginSet(gridWidget) && isHeaderSelectionOriginSet(gridWidget)) {
            isHeaderCellSelected = true;
        }

        if (!isGridColumnCandidateForScroll(gridWidget,
                                            isHeaderCellSelected)) {
            return false;
        }

        final double dx = getCellScrollDeltaX(gridWidget, isHeaderCellSelected);
        final double dy = getCellScrollDeltaY(gridWidget, isHeaderCellSelected);

        if (dx != 0 || dy != 0) {
            adjustViewportTransform(gridLayer.getViewport(),
                                    new Point2D(dx,
                                                dy));
        }
        return true;
    }

    private boolean isSelectionOriginSet(final GridWidget selectedGridWidget) {
        final GridData gridModel = selectedGridWidget.getModel();
        final GridData.SelectedCell origin = gridModel.getSelectedCellsOrigin();
        return origin != null;
    }

    private boolean isHeaderSelectionOriginSet(final GridWidget selectedGridWidget) {
        final GridData gridModel = selectedGridWidget.getModel();
        return !gridModel.getSelectedHeaderCells().isEmpty();
    }

    private boolean isGridColumnCandidateForScroll(final GridWidget gridWidget,
                                                   final boolean isHeaderCellSelected) {
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final RenderingInformation renderingInformation = rendererHelper.getRenderingInformation();

        if (Objects.isNull(renderingInformation)) {
            return false;
        }

        final GridData gridModel = gridWidget.getModel();

        final List<GridColumn<?>> columns = gridModel.getColumns();
        final GridData.SelectedCell origin = getSelectedCellOrigin(gridModel, isHeaderCellSelected);
        final int uiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(columns,
                                                                         origin.getColumnIndex());

        final RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final List<GridColumn<?>> floatingColumns = floatingBlockInformation.getColumns();
        final GridColumn<?> column = columns.get(uiColumnIndex);

        return !floatingColumns.contains(column);
    }

    private double getCellScrollDeltaX(final GridWidget gridWidget, final boolean isHeaderCellSelected) {
        final GridData gridModel = gridWidget.getModel();
        final List<GridColumn<?>> columns = gridModel.getColumns();
        final GridData.SelectedCell origin = getSelectedCellOrigin(gridModel, isHeaderCellSelected);
        final int uiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(columns,
                                                                         origin.getColumnIndex());

        double dx = 0;
        final Bounds bounds = gridLayer.getVisibleBounds();
        final double columnWidth = columns.get(uiColumnIndex).getWidth();
        final double gridCellX = gridWidget.getX() + gridWidget.getRendererHelper().getColumnOffset(uiColumnIndex);

        if (gridCellX + columnWidth >= bounds.getX() + bounds.getWidth()) {
            dx = bounds.getX() + bounds.getWidth() - gridCellX - columnWidth;
        } else if (gridCellX <= bounds.getX()) {
            dx = bounds.getX() - gridCellX;
        }

        return dx;
    }

    private double getCellScrollDeltaY(final GridWidget gridWidget, final boolean isHeaderCellSelected) {
        final GridData gridModel = gridWidget.getModel();
        final GridData.SelectedCell origin = getSelectedCellOrigin(gridModel, isHeaderCellSelected);
        final int uiRowIndex = origin.getRowIndex();

        double dy = 0;
        final Bounds bounds = gridLayer.getVisibleBounds();
        final int headerRowCount = gridModel.getHeaderRowCount();
        final double headerHeight = gridWidget.getRenderer().getHeaderHeight();
        final double rowHeight = isHeaderCellSelected ? gridWidget.getRenderer().getHeaderRowHeight() : gridModel.getRow(uiRowIndex).getHeight();
        final double headerYOffset = isHeaderCellSelected ? headerHeight - headerRowCount * rowHeight : headerHeight;
        final double rowOffset = isHeaderCellSelected ? rowHeight * uiRowIndex : gridWidget.getRendererHelper().getRowOffset(uiRowIndex);
        final double gridCellY = gridWidget.getY() + headerYOffset + rowOffset;

        if (gridCellY + rowHeight >= bounds.getY() + bounds.getHeight()) {
            dy = bounds.getY() + bounds.getHeight() - gridCellY - rowHeight;
        } else if (gridCellY <= bounds.getY() + headerYOffset) {
            dy = bounds.getY() + headerYOffset - gridCellY;
        }

        return dy;
    }

    /**
     * It retrieves the selected cell in <code>GridData</code> model, which could be an <b>header</b> cell or a
     * simple one. In case of an <b>header</b> cell, it manages a possible case where a cell is spanned over multiple
     * columns: when pressing <code>KEY_RIGHT</code>, it selected the last cell of the selected header cells group in
     * order to show all the spanned cell. The otherwise in all other cases
     * @param gridModel
     * @param isHeaderCellSelected
     * @return
     */
    protected GridData.SelectedCell getSelectedCellOrigin(final GridData gridModel, final boolean isHeaderCellSelected) {
        if (isHeaderCellSelected) {
            List<GridData.SelectedCell> selectedHeaderCells = gridModel.getSelectedHeaderCells();
            if (KEY_RIGHT == getKeyCode()) {
                return selectedHeaderCells.get(selectedHeaderCells.size() - 1);
            } else {
                return selectedHeaderCells.get(0);
            }
        } else {
            return gridModel.getSelectedCellsOrigin();
        }
    }

    private void adjustViewportTransform(final Viewport vp,
                                         final Point2D delta) {
        final Transform transform = vp.getTransform();
        final Transform t = transform.copy().getInverse();
        final Point2D translation = new Point2D(t.getTranslateX(),
                                                t.getTranslateY()).mul(-1.0);

        final double scaleX = transform.getScaleX();
        final double scaleY = transform.getScaleY();
        transform.reset();

        final Point2D frameLocation = translation.add(delta);
        transform.scale(scaleX,
                        scaleY).translate(frameLocation.getX(),
                                          frameLocation.getY());
    }
}
