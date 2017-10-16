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

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

public abstract class BaseKeyboardOperation implements KeyboardOperation {

    protected GridLayer gridLayer;

    public BaseKeyboardOperation(final GridLayer gridLayer) {
        this.gridLayer = PortablePreconditions.checkNotNull("gridLayer",
                                                            gridLayer);
    }

    @Override
    public TriStateBoolean isShiftKeyDown() {
        return TriStateBoolean.DONT_CARE;
    }

    @Override
    public TriStateBoolean isControlKeyDown() {
        return TriStateBoolean.DONT_CARE;
    }

    protected boolean scrollSelectedCellIntoView(final GridWidget gridWidget) {
        if (!isSelectionOriginSet(gridWidget)) {
            return false;
        }

        if (!(isGridWidgetRendered(gridWidget) || isGridColumnCandidateForScroll(gridWidget))) {
            return false;
        }

        final double dx = getCellScrollDeltaX(gridWidget);
        final double dy = getCellScrollDeltaY(gridWidget);

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

    private boolean isGridWidgetRendered(final GridWidget gridWidget) {
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final BaseGridRendererHelper.RenderingInformation renderingInformation = rendererHelper.getRenderingInformation();
        return renderingInformation != null;
    }

    private boolean isGridColumnCandidateForScroll(final GridWidget gridWidget) {
        final GridData gridModel = gridWidget.getModel();
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final BaseGridRendererHelper.RenderingInformation renderingInformation = rendererHelper.getRenderingInformation();
        if (renderingInformation == null) {
            return false;
        }

        final List<GridColumn<?>> columns = gridModel.getColumns();
        final GridData.SelectedCell origin = gridModel.getSelectedCellsOrigin();
        final int uiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(columns,
                                                                         origin.getColumnIndex());

        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final List<GridColumn<?>> floatingColumns = floatingBlockInformation.getColumns();
        final GridColumn<?> column = columns.get(uiColumnIndex);

        return !floatingColumns.contains(column);
    }

    private double getCellScrollDeltaX(final GridWidget gridWidget) {
        final GridData gridModel = gridWidget.getModel();
        final List<GridColumn<?>> columns = gridModel.getColumns();
        final GridData.SelectedCell origin = gridModel.getSelectedCellsOrigin();
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

    private double getCellScrollDeltaY(final GridWidget gridWidget) {
        final GridData gridModel = gridWidget.getModel();
        final GridData.SelectedCell origin = gridModel.getSelectedCellsOrigin();
        final int uiRowIndex = origin.getRowIndex();

        double dy = 0;
        final Bounds bounds = gridLayer.getVisibleBounds();
        final double rowHeight = gridModel.getRow(uiRowIndex).getHeight();
        final double headerHeight = gridWidget.getRenderer().getHeaderHeight();
        final double gridCellY = gridWidget.getY() + headerHeight + gridWidget.getRendererHelper().getRowOffset(uiRowIndex);

        if (gridCellY + rowHeight >= bounds.getY() + bounds.getHeight()) {
            dy = bounds.getY() + bounds.getHeight() - gridCellY - rowHeight;
        } else if (gridCellY <= bounds.getY() + headerHeight) {
            dy = bounds.getY() + headerHeight - gridCellY;
        }

        return dy;
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
