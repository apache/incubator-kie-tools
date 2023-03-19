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
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntPredicate;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper.RenderingInformation;
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
        this.gridWidget = Objects.requireNonNull(gridWidget, "gridWidget");
        this.gridModel = Objects.requireNonNull(gridWidget.getModel(), "gridModel");
    }

    @Override
    public boolean selectCell(final Point2D rp,
                              final boolean isShiftKeyDown,
                              final boolean isControlKeyDown) {
        final Integer uiRowIndex = CoordinateUtilities.getUiRowIndex(gridWidget,
                                                                     rp.getY());
        final Integer uiColumnIndex = CoordinateUtilities.getUiColumnIndex(gridWidget,
                                                                           rp.getX());
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
            strategy = cell.getSelectionStrategy();
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
    public boolean selectHeaderCell(final Point2D rp,
                                    final boolean isShiftKeyDown,
                                    final boolean isControlKeyDown) {
        final Integer uiHeaderRowIndex = CoordinateUtilities.getUiHeaderRowIndex(gridWidget,
                                                                                 rp);
        final Integer uiHeaderColumnIndex = CoordinateUtilities.getUiColumnIndex(gridWidget,
                                                                                 rp.getX());
        if (uiHeaderRowIndex == null || uiHeaderColumnIndex == null) {
            return false;
        }

        return selectHeaderCell(uiHeaderRowIndex,
                                uiHeaderColumnIndex,
                                isShiftKeyDown,
                                isControlKeyDown);
    }

    @Override
    public boolean selectHeaderCell(final int uiHeaderRowIndex,
                                    final int uiHeaderColumnIndex,
                                    final boolean isShiftKeyDown,
                                    final boolean isControlKeyDown) {
        if (uiHeaderColumnIndex < 0 || uiHeaderColumnIndex > gridModel.getColumnCount() - 1) {
            return false;
        }

        final GridColumn<?> gridColumn = gridModel.getColumns().get(uiHeaderColumnIndex);
        final List<GridColumn.HeaderMetaData> gridColumnHeaderMetaData = gridColumn.getHeaderMetaData();
        if (uiHeaderRowIndex < 0 || uiHeaderRowIndex > gridColumnHeaderMetaData.size() - 1) {
            return false;
        }
        final GridColumn.HeaderMetaData headerMetaData = gridColumnHeaderMetaData.get(uiHeaderRowIndex);
        final CellSelectionStrategy strategy = headerMetaData.getSelectionStrategy();

        return strategy.handleSelection(gridModel,
                                        uiHeaderRowIndex,
                                        uiHeaderColumnIndex,
                                        isShiftKeyDown,
                                        isControlKeyDown);
    }

    @Override
    public boolean adjustSelection(final SelectionExtension direction,
                                   final boolean isShiftKeyDown) {
        final GridData.SelectedCell origin = gridModel.getSelectedCellsOrigin();
        if (origin == null) {
            if (gridModel.getSelectedHeaderCells().size() > 0) {
                final GridData.SelectedCell selectedHeaderCell = gridModel.getSelectedHeaderCells().get(0);
                if (movingHorizontally(direction)) {
                    return moveInHeaderHorizontally(direction, selectedHeaderCell);
                }
                if (movingVertically(direction)) {
                    return moveInHeaderVerticallyOrMoveToData(direction, selectedHeaderCell);
                }
            }
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

    private static boolean movingHorizontally(final SelectionExtension direction) {
        return direction == SelectionExtension.LEFT || direction == SelectionExtension.RIGHT;
    }

    private static boolean movingVertically(final SelectionExtension direction) {
        return direction == SelectionExtension.UP || direction == SelectionExtension.DOWN;
    }

    private boolean moveInHeaderHorizontally(final SelectionExtension direction,
                                             final GridData.SelectedCell selectedHeaderCell) {
        final int uiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(gridModel.getColumns(),
                                                                         selectedHeaderCell.getColumnIndex());

        final GridColumn.HeaderMetaData columnHeaderMetaData = gridModel.getColumns().get(uiColumnIndex)
                .getHeaderMetaData().get(selectedHeaderCell.getRowIndex());

        final int headerBlockStartIndex = ColumnIndexUtilities.getHeaderBlockStartColumnIndex(gridModel.getColumns(),
                                                                                              columnHeaderMetaData,
                                                                                              selectedHeaderCell.getRowIndex(),
                                                                                              uiColumnIndex);
        final int headerBlockEndIndex = ColumnIndexUtilities.getHeaderBlockEndColumnIndex(gridModel.getColumns(),
                                                                                          columnHeaderMetaData,
                                                                                          selectedHeaderCell.getRowIndex(),
                                                                                          uiColumnIndex);
        int proposedUiColumnIndex = uiColumnIndex + direction.getDeltaX();
        final int hiddenColumnXCompensation = computeHiddenColumnsCompensation(proposedUiColumnIndex, direction);
        proposedUiColumnIndex = proposedUiColumnIndex + hiddenColumnXCompensation;

        if (direction == SelectionExtension.LEFT) {
            proposedUiColumnIndex = Math.min(headerBlockStartIndex - 1,
                                             proposedUiColumnIndex);
        }

        if (direction == SelectionExtension.RIGHT) {
            proposedUiColumnIndex = Math.max(headerBlockEndIndex + 1,
                                             proposedUiColumnIndex);
        }

        if (proposedUiColumnIndex < 0 || proposedUiColumnIndex > gridModel.getColumnCount() - 1) {
            return false;
        }

        final int proposedUiRowIndex = // either keep the same or pick the closest one
                Math.min(selectedHeaderCell.getRowIndex(),
                         ColumnIndexUtilities.getMaxUiHeaderRowIndexOfColumn(gridModel, proposedUiColumnIndex));

        return selectHeaderCell(proposedUiRowIndex,
                                proposedUiColumnIndex,
                                false,
                                false);
    }

    private boolean moveInHeaderVerticallyOrMoveToData(final SelectionExtension direction,
                                                       final GridData.SelectedCell selectedHeaderCell) {
        final int uiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(gridModel.getColumns(),
                                                                         selectedHeaderCell.getColumnIndex());

        final boolean selectionChanged = selectHeaderCell(selectedHeaderCell.getRowIndex() + direction.getDeltaY(),
                                                          uiColumnIndex,
                                                          false,
                                                          false);
        if (!selectionChanged && direction == SelectionExtension.DOWN) {
            return selectCell(0,
                              uiColumnIndex,
                              false,
                              false);
        } else {
            return selectionChanged;
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

    /**
     * @return count of hidden columns that are as one hidden section starting on given index
     */
    private int computeHiddenColumnsCompensation(final int startingIndex,
                                                 final SelectionExtension direction) {
        int index = startingIndex;
        int hiddenColumnsCount = 0;
        IntPredicate outOfBound = columnIndex -> columnIndex < 0 || columnIndex >= gridModel.getColumnCount();
        if (gridModel.getColumnCount() > 0) {
            while (!outOfBound.test(index) && !gridModel.getColumns().get(index).isVisible()) {
                hiddenColumnsCount++;
                index += direction.getDeltaX();
            }
        }
        return hiddenColumnsCount * direction.getDeltaX();
    }

    private boolean moveSelection(final GridData.SelectedCell origin,
                                  final SelectionExtension direction) {
        final int dx = direction.getDeltaX();
        final int dy = direction.getDeltaY();
        final int currentUiRowIndex = origin.getRowIndex();
        final int currentUiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(gridModel.getColumns(),
                                                                                origin.getColumnIndex());
        final int proposedUiRowIndex = currentUiRowIndex + dy;
        int proposedUiColumnIndex = currentUiColumnIndex + dx;
        final int hiddenColumnXCompensation = computeHiddenColumnsCompensation(proposedUiColumnIndex, direction);
        proposedUiColumnIndex = proposedUiColumnIndex + hiddenColumnXCompensation;

        if (canMoveFromDataToHeader(direction, proposedUiRowIndex)) {
            return moveFromDataToHeader(proposedUiColumnIndex);
        }

        if (!isCoordinateWithinExtents(proposedUiRowIndex,
                                       proposedUiColumnIndex)) {
            return false;
        }

        return selectCell(proposedUiRowIndex,
                          proposedUiColumnIndex,
                          false,
                          false);
    }

    private boolean canMoveFromDataToHeader(final SelectionExtension direction,
                                            final int proposedUiRowIndex) {
        return direction == SelectionExtension.UP && proposedUiRowIndex < 0 && gridModel.getHeaderRowCount() > 0;
    }

    private boolean moveFromDataToHeader(final int uiColumnIndex) {
        final int uiHeaderRowIndex = ColumnIndexUtilities.getMaxUiHeaderRowIndexOfColumn(gridModel, uiColumnIndex);
        return selectHeaderCell(uiHeaderRowIndex,
                                uiColumnIndex,
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
    public boolean startEditingCell(final Point2D rp) {
        //Get row information
        final Integer uiRowIndex = CoordinateUtilities.getUiRowIndex(gridWidget,
                                                                     rp.getY());
        if (uiRowIndex == null) {
            return false;
        }

        //Get column information
        final Point2D gridWidgetComputedLocation = gridWidget.getComputedLocation();
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(rp.getX());
        final GridColumn<?> column = ci.getColumn();
        if (column == null) {
            return false;
        }

        return edit(uiRowIndex,
                    ci,
                    rendererHelper.getRenderingInformation(),
                    Optional.of(rp.add(gridWidgetComputedLocation)));
    }

    @Override
    public boolean startEditingCell(final int uiRowIndex,
                                    final int uiColumnIndex) {
        if (!isCoordinateWithinExtents(uiRowIndex,
                                       uiColumnIndex)) {
            return false;
        }

        final RenderingInformation renderingInformation = computeRenderingInformation();

        BaseGridRendererHelper.ColumnInformation ci = getFloatingColumnInformation(uiColumnIndex, renderingInformation);
        if (ci == null) {
            ci = getBodyColumnInformation(uiColumnIndex, renderingInformation);
        }
        if (ci == null) {
            return false;
        }

        return edit(uiRowIndex,
                    ci,
                    renderingInformation,
                    Optional.empty());
    }

    private BaseGridRendererHelper.ColumnInformation getFloatingColumnInformation(final int uiColumnIndex,
                                                                                  final RenderingInformation renderingInformation) {
        final GridColumn<?> column = gridModel.getColumns().get(uiColumnIndex);
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
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

    private BaseGridRendererHelper.ColumnInformation getBodyColumnInformation(final int uiColumnIndex,
                                                                              final RenderingInformation renderingInformation) {
        final GridColumn<?> column = gridModel.getColumns().get(uiColumnIndex);
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
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
                         final BaseGridRendererHelper.ColumnInformation ci,
                         final RenderingInformation renderingInformation,
                         final Optional<Point2D> rp) {
        final GridColumn<?> column = ci.getColumn();
        final int uiColumnIndex = ci.getUiColumnIndex();
        final double offsetX = ci.getOffsetX();

        //Get rendering information
        final GridRenderer renderer = gridWidget.getRenderer();
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        if (renderingInformation == null) {
            return false;
        }

        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final double floatingX = floatingBlockInformation.getX();
        final double floatingWidth = floatingBlockInformation.getWidth();

        //Construct context of MouseEvent
        final Point2D gridWidgetComputedLocation = gridWidget.getComputedLocation();
        final double cellX = gridWidgetComputedLocation.getX() + offsetX;
        final double cellY = gridWidgetComputedLocation.getY() + renderer.getHeaderHeight() + getRowOffset(uiRowIndex,
                                                                                                           uiColumnIndex,
                                                                                                           rendererHelper);
        final double cellHeight = getCellHeight(uiRowIndex,
                                                uiColumnIndex,
                                                renderingInformation);

        final Group header = gridWidget.getHeader();
        final double clipMinX = gridWidgetComputedLocation.getX() + floatingX + floatingWidth;
        final double clipMinY = gridWidgetComputedLocation.getY() + (header == null ? 0.0 : header.getY()) + renderer.getHeaderHeight();

        final GridBodyCellEditContext context = new GridBodyCellEditContext(cellX,
                                                                            cellY,
                                                                            column.getWidth(),
                                                                            cellHeight,
                                                                            clipMinY,
                                                                            clipMinX,
                                                                            uiRowIndex,
                                                                            uiColumnIndex,
                                                                            floatingBlockInformation.getColumns().contains(column),
                                                                            gridWidget.getViewport().getTransform(),
                                                                            renderer,
                                                                            rp);

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
                                 final int uiColumnIndex,
                                 final RenderingInformation renderingInformation) {
        final List<Double> allRowHeights = renderingInformation.getAllRowHeights();
        final GridCell<?> cell = gridModel.getCell(uiRowIndex, uiColumnIndex);
        if (cell == null) {
            return allRowHeights.get(uiRowIndex);
        }
        if (cell.getMergedCellCount() == 1) {
            return allRowHeights.get(uiRowIndex);
        } else if (cell.getMergedCellCount() > 1) {
            return getMergedCellHeight(uiRowIndex,
                                       uiColumnIndex,
                                       renderingInformation);
        } else {
            return getClippedMergedCellHeight(uiRowIndex,
                                              uiColumnIndex,
                                              renderingInformation);
        }
    }

    private double getMergedCellHeight(final int uiRowIndex,
                                       final int uiColumnIndex,
                                       final RenderingInformation renderingInformation) {
        double height = 0;
        final List<Double> allRowHeights = renderingInformation.getAllRowHeights();
        final GridCell<?> cell = gridModel.getCell(uiRowIndex, uiColumnIndex);
        for (int i = uiRowIndex; i < uiRowIndex + cell.getMergedCellCount(); i++) {
            height = height + allRowHeights.get(i);
        }
        return height;
    }

    private double getClippedMergedCellHeight(final int uiRowIndex,
                                              final int uiColumnIndex,
                                              final RenderingInformation renderingInformation) {
        final List<Double> allRowHeights = renderingInformation.getAllRowHeights();
        final GridCell<?> cell = gridModel.getCell(uiRowIndex, uiColumnIndex);
        GridCell<?> _cell = cell;
        int _uiRowIndex = uiRowIndex;
        while (_cell.getMergedCellCount() == 0) {
            _uiRowIndex--;
            _cell = gridModel.getCell(_uiRowIndex,
                                      uiColumnIndex);
        }
        double height = 0;
        for (int i = _uiRowIndex; i < _uiRowIndex + _cell.getMergedCellCount(); i++) {
            height = height + allRowHeights.get(i);
        }
        return height;
    }

    @SuppressWarnings("unchecked")
    protected void doEdit(final GridBodyCellEditContext context) {
        final int uiRowIndex = context.getRowIndex();
        final int uiColumnIndex = context.getColumnIndex();

        final GridData gridModel = gridWidget.getModel();
        final GridColumn column = gridModel.getColumns().get(uiColumnIndex);
        final GridCell<?> cell = gridModel.getCell(uiRowIndex,
                                                   uiColumnIndex);

        column.edit(cell,
                    context,
                    value -> {
                        gridModel.setCellValue(uiRowIndex,
                                               uiColumnIndex,
                                               (GridCellValue<?>) value);
                        gridWidget.getLayer().batch();
                    });
    }

    /**
     * Computing of RenderingInformation is quite complex operation
     * It is preferable to compute it just once and reuse
     * See https://issues.redhat.com/browse/DROOLS-4793
     */
    private RenderingInformation computeRenderingInformation() {
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        return rendererHelper.getRenderingInformation();
    }
}
