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

package org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl;

import java.util.List;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.SelectionExtension;

/**
 * Manager for Cell selection operations.
 */
public class BaseCellSelectionManager implements CellSelectionManager {

    private final GridWidget gridWidget;
    private final GridData gridModel;

    public BaseCellSelectionManager(final GridWidget gridWidget) {
        this.gridWidget = PortablePreconditions.checkNotNull("gridWidget",
                                                             gridWidget);
        this.gridModel = PortablePreconditions.checkNotNull("gridModel",
                                                            gridWidget.getModel());
    }

    @Override
    public boolean selectCell(final Point2D ap,
                              final boolean isShiftKeyDown,
                              final boolean isControlKeyDown) {
        final Integer uiRowIndex = CoordinateUtilities.getUiRowIndex(gridWidget,
                                                                     ap.getY());
        final Integer uiColumnIndex = CoordinateUtilities.getUiColumnIndex(gridWidget,
                                                                           ap.getX());
        if (uiRowIndex == null || uiColumnIndex == null) {
            return false;
        }

        return selectCell(uiRowIndex,
                          uiColumnIndex,
                          isShiftKeyDown,
                          isControlKeyDown);
    }

    @Override
    public boolean selectCell(final int uiRowIndex,
                              final int uiColumnIndex,
                              final boolean isShiftKeyDown,
                              final boolean isControlKeyDown) {
        if (uiRowIndex < 0 || uiRowIndex > gridModel.getRowCount() - 1) {
            return false;
        }
        if (uiColumnIndex < 0 || uiColumnIndex > gridModel.getColumnCount() - 1) {
            return false;
        }

        CellSelectionStrategy strategy;
        final GridCell<?> cell = gridModel.getCell(uiRowIndex,
                                                   uiColumnIndex);
        if (cell == null) {
            strategy = RangeSelectionStrategy.INSTANCE;
        } else {
            strategy = cell.getSelectionManager();
        }
        if (strategy == null) {
            return false;
        }

        //Handle selection
        return strategy.handleSelection(gridModel,
                                        uiRowIndex,
                                        uiColumnIndex,
                                        isShiftKeyDown,
                                        isControlKeyDown);
    }

    @Override
    public boolean adjustSelection(final SelectionExtension direction,
                                   final boolean isShiftKeyDown) {
        final GridData.SelectedCell origin = gridModel.getSelectedCellsOrigin();
        if (origin == null) {
            return false;
        }

        if (isShiftKeyDown) {
            return extendSelection(origin,
                                   direction);
        } else {
            return moveSelection(origin,
                                 direction);
        }
    }

    private boolean extendSelection(final GridData.SelectedCell origin,
                                    final SelectionExtension direction) {
        if (gridModel.getSelectedCells().isEmpty()) {
            return false;
        }
        final int originUiRowIndex = origin.getRowIndex();
        final int originUiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(gridModel.getColumns(),
                                                                               origin.getColumnIndex());
        final int minUiRowIndex = findMinUiRowIndex(origin);
        final int maxUiRowIndex = findMaxUiRowIndex(origin);
        final int minUiColumnIndex = findMinUiColumnIndex(origin);
        final int maxUiColumnIndex = findMaxUiColumnIndex(origin);

        final int proposedUiColumnIndex = direction.getNextX(minUiColumnIndex,
                                                             maxUiColumnIndex,
                                                             originUiColumnIndex);
        final int proposedUiRowIndex = direction.getNextY(minUiRowIndex,
                                                          maxUiRowIndex,
                                                          originUiRowIndex);

        if (!isCoordinateWithinExtents(proposedUiRowIndex,
                                       proposedUiColumnIndex)) {
            return false;
        }

        return selectCell(proposedUiRowIndex,
                          proposedUiColumnIndex,
                          true,
                          false);
    }

    private int findMinUiRowIndex(final GridData.SelectedCell origin) {
        int minUiRowIndex = origin.getRowIndex();
        final List<GridData.SelectedCell> selectedCells = gridModel.getSelectedCells();
        for (GridData.SelectedCell selectedCell : selectedCells) {
            minUiRowIndex = Math.min(selectedCell.getRowIndex(),
                                     minUiRowIndex);
        }
        return minUiRowIndex;
    }

    private int findMaxUiRowIndex(final GridData.SelectedCell origin) {
        int maxUiRowIndex = origin.getRowIndex();
        final List<GridData.SelectedCell> selectedCells = gridModel.getSelectedCells();
        for (GridData.SelectedCell selectedCell : selectedCells) {
            maxUiRowIndex = Math.max(selectedCell.getRowIndex(),
                                     maxUiRowIndex);
        }
        return maxUiRowIndex;
    }

    private int findMinUiColumnIndex(final GridData.SelectedCell origin) {
        int minUiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(gridModel.getColumns(),
                                                                      origin.getColumnIndex());
        final List<GridData.SelectedCell> selectedCells = gridModel.getSelectedCells();
        for (GridData.SelectedCell selectedCell : selectedCells) {
            minUiColumnIndex = Math.min(ColumnIndexUtilities.findUiColumnIndex(gridModel.getColumns(),
                                                                               selectedCell.getColumnIndex()),
                                        minUiColumnIndex);
        }
        return minUiColumnIndex;
    }

    private int findMaxUiColumnIndex(final GridData.SelectedCell origin) {
        int maxUiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(gridModel.getColumns(),
                                                                      origin.getColumnIndex());
        final List<GridData.SelectedCell> selectedCells = gridModel.getSelectedCells();
        for (GridData.SelectedCell selectedCell : selectedCells) {
            maxUiColumnIndex = Math.max(ColumnIndexUtilities.findUiColumnIndex(gridModel.getColumns(),
                                                                               selectedCell.getColumnIndex()),
                                        maxUiColumnIndex);
        }
        return maxUiColumnIndex;
    }

    private boolean moveSelection(final GridData.SelectedCell origin,
                                  final SelectionExtension direction) {
        final int dx = direction.getDeltaX();
        final int dy = direction.getDeltaY();
        final int currentUiRowIndex = origin.getRowIndex();
        final int currentUiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(gridModel.getColumns(),
                                                                                origin.getColumnIndex());
        final int proposedUiRowIndex = currentUiRowIndex + dy;
        final int proposedUiColumnIndex = currentUiColumnIndex + dx;

        if (!isCoordinateWithinExtents(proposedUiRowIndex,
                                       proposedUiColumnIndex)) {
            return false;
        }

        return selectCell(proposedUiRowIndex,
                          proposedUiColumnIndex,
                          false,
                          false);
    }

    private boolean isCoordinateWithinExtents(final int proposedUiRowIndex,
                                              final int proposedUiColumnIndex) {
        if (proposedUiRowIndex < 0 || proposedUiRowIndex > gridModel.getRowCount() - 1) {
            return false;
        }
        if (proposedUiColumnIndex < 0 || proposedUiColumnIndex > gridModel.getColumnCount() - 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean startEditingCell(final Point2D ap) {
        //Get row information
        final Integer uiRowIndex = CoordinateUtilities.getUiRowIndex(gridWidget,
                                                                     ap.getY());
        if (uiRowIndex == null) {
            return false;
        }

        //Get column information
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(ap.getX());
        final GridColumn<?> column = ci.getColumn();
        if (column == null) {
            return false;
        }

        return edit(uiRowIndex,
                    ci);
    }

    @Override
    public boolean startEditingCell(final int uiRowIndex,
                                    final int uiColumnIndex) {
        if (!isCoordinateWithinExtents(uiRowIndex,
                                       uiColumnIndex)) {
            return false;
        }

        BaseGridRendererHelper.ColumnInformation ci = getFloatingColumnInformation(uiColumnIndex);
        if (ci == null) {
            ci = getBodyColumnInformation(uiColumnIndex);
        }
        if (ci == null) {
            return false;
        }

        return edit(uiRowIndex,
                    ci);
    }

    private BaseGridRendererHelper.ColumnInformation getFloatingColumnInformation(final int uiColumnIndex) {
        final GridColumn<?> column = gridModel.getColumns().get(uiColumnIndex);
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final BaseGridRendererHelper.RenderingInformation renderingInformation = rendererHelper.getRenderingInformation();
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final List<GridColumn<?>> floatingColumns = floatingBlockInformation.getColumns();

        if (!floatingColumns.contains(column)) {
            return null;
        }

        return new BaseGridRendererHelper.ColumnInformation(column,
                                                            uiColumnIndex,
                                                            floatingBlockInformation.getX() + rendererHelper.getColumnOffset(floatingColumns,
                                                                                                                             floatingColumns.indexOf(column)));
    }

    private BaseGridRendererHelper.ColumnInformation getBodyColumnInformation(final int uiColumnIndex) {
        final GridColumn<?> column = gridModel.getColumns().get(uiColumnIndex);
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final BaseGridRendererHelper.RenderingInformation renderingInformation = rendererHelper.getRenderingInformation();
        final BaseGridRendererHelper.RenderingBlockInformation bodyBlockInformation = renderingInformation.getBodyBlockInformation();
        final List<GridColumn<?>> bodyColumns = bodyBlockInformation.getColumns();

        if (!bodyColumns.contains(column)) {
            return null;
        }

        return new BaseGridRendererHelper.ColumnInformation(column,
                                                            uiColumnIndex,
                                                            bodyBlockInformation.getX() + rendererHelper.getColumnOffset(bodyColumns,
                                                                                                                         bodyColumns.indexOf(column)));
    }

    private boolean edit(final int uiRowIndex,
                         final BaseGridRendererHelper.ColumnInformation ci) {
        final GridColumn<?> column = ci.getColumn();
        final int uiColumnIndex = ci.getUiColumnIndex();
        final double offsetX = ci.getOffsetX();

        //Get rendering information
        final GridRenderer renderer = gridWidget.getRenderer();
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final BaseGridRendererHelper.RenderingInformation renderingInformation = rendererHelper.getRenderingInformation();
        if (renderingInformation == null) {
            return false;
        }

        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final double floatingX = floatingBlockInformation.getX();
        final double floatingWidth = floatingBlockInformation.getWidth();

        //Construct context of MouseEvent
        final double cellX = gridWidget.getX() + offsetX;
        final double cellY = gridWidget.getY() + renderer.getHeaderHeight() + getRowOffset(uiRowIndex,
                                                                                           uiColumnIndex,
                                                                                           rendererHelper);
        final double cellHeight = getCellHeight(uiRowIndex,
                                                uiColumnIndex);

        final Group header = gridWidget.getHeader();
        final double clipMinY = gridWidget.getY() + header.getY() + renderer.getHeaderHeight();
        final double clipMinX = gridWidget.getX() + floatingX + floatingWidth;

        final GridBodyCellRenderContext context = new GridBodyCellRenderContext(cellX,
                                                                                cellY,
                                                                                column.getWidth(),
                                                                                cellHeight,
                                                                                clipMinY,
                                                                                clipMinX,
                                                                                uiRowIndex,
                                                                                uiColumnIndex,
                                                                                floatingBlockInformation.getColumns().contains(column),
                                                                                gridWidget.getViewport().getTransform(),
                                                                                renderer);

        doEdit(context);

        return true;
    }

    private double getRowOffset(final int uiRowIndex,
                                final int uiColumnIndex,
                                final BaseGridRendererHelper rendererHelper) {
        final GridCell<?> cell = gridModel.getCell(uiRowIndex,
                                                   uiColumnIndex);
        if (cell == null) {
            return rendererHelper.getRowOffset(uiRowIndex);
        }
        if (cell.getMergedCellCount() == 1) {
            return rendererHelper.getRowOffset(uiRowIndex);
        } else if (cell.getMergedCellCount() > 1) {
            return rendererHelper.getRowOffset(uiRowIndex);
        } else {
            int _uiRowIndex = uiRowIndex;
            GridCell<?> _cell = cell;
            while (_cell.getMergedCellCount() == 0) {
                _uiRowIndex--;
                _cell = gridModel.getCell(_uiRowIndex,
                                          uiColumnIndex);
            }
            return rendererHelper.getRowOffset(_uiRowIndex);
        }
    }

    private double getCellHeight(final int uiRowIndex,
                                 final int uiColumnIndex) {
        final GridCell<?> cell = gridModel.getCell(uiRowIndex,
                                                   uiColumnIndex);
        if (cell == null) {
            return gridModel.getRow(uiRowIndex).getHeight();
        }
        if (cell.getMergedCellCount() == 1) {
            return gridModel.getRow(uiRowIndex).getHeight();
        } else if (cell.getMergedCellCount() > 1) {
            return getMergedCellHeight(uiRowIndex,
                                       uiColumnIndex);
        } else {
            return getClippedMergedCellHeight(uiRowIndex,
                                              uiColumnIndex);
        }
    }

    private double getMergedCellHeight(final int uiRowIndex,
                                       final int uiColumnIndex) {
        double height = 0;
        final GridCell<?> cell = gridModel.getCell(uiRowIndex,
                                                   uiColumnIndex);
        for (int i = uiRowIndex; i < uiRowIndex + cell.getMergedCellCount(); i++) {
            height = height + gridModel.getRow(i).getHeight();
        }
        return height;
    }

    private double getClippedMergedCellHeight(final int uiRowIndex,
                                              final int uiColumnIndex) {
        final GridCell<?> cell = gridModel.getCell(uiRowIndex,
                                                   uiColumnIndex);
        GridCell<?> _cell = cell;
        int _uiRowIndex = uiRowIndex;
        while (_cell.getMergedCellCount() == 0) {
            _uiRowIndex--;
            _cell = gridModel.getCell(_uiRowIndex,
                                      uiColumnIndex);
        }
        double height = 0;
        for (int i = _uiRowIndex; i < _uiRowIndex + _cell.getMergedCellCount(); i++) {
            height = height + gridModel.getRow(i).getHeight();
        }
        return height;
    }

    @SuppressWarnings("unchecked")
    protected void doEdit(final GridBodyCellRenderContext context) {
        final int uiRowIndex = context.getRowIndex();
        final int uiColumnIndex = context.getColumnIndex();

        final GridData gridModel = gridWidget.getModel();
        final GridColumn column = gridModel.getColumns().get(uiColumnIndex);
        final GridCell<?> cell = gridModel.getCell(uiRowIndex,
                                                   uiColumnIndex);

        column.edit(cell,
                    context,
                    new Callback<GridCellValue<?>>() {

                        @Override
                        public void callback(final GridCellValue<?> value) {
                            gridModel.setCell(uiRowIndex,
                                              uiColumnIndex,
                                              value);
                            gridWidget.getLayer().batch();
                        }
                    });
    }
}
