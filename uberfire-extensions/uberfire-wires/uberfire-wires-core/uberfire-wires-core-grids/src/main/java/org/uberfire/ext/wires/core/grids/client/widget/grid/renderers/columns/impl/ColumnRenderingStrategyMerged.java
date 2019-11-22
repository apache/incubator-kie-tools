/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPathClipper;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.core.client.GWT;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.GroupingToggle;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

public class ColumnRenderingStrategyMerged {

    private static final int LOOP_THRESHOLD = 1000;

    public static List<GridRenderer.RendererCommand> render(final GridColumn<?> column,
                                                            final GridBodyColumnRenderContext context,
                                                            final BaseGridRendererHelper rendererHelper,
                                                            final BaseGridRendererHelper.RenderingInformation renderingInformation,
                                                            final BiFunction<Boolean, GridColumn<?>, Boolean> columnRenderingConstraint) {
        final double x = context.getX();
        final double absoluteGridY = context.getAbsoluteGridY();
        final double absoluteColumnX = context.getAbsoluteColumnX();
        final double clipMinY = context.getClipMinY();
        final double clipMinX = context.getClipMinX();
        final int minVisibleRowIndex = context.getMinVisibleRowIndex();
        final int maxVisibleRowIndex = context.getMaxVisibleRowIndex();
        final List<Double> allRowHeights = renderingInformation.getAllRowHeights();
        final List<Double> visibleRowOffsets = renderingInformation.getVisibleRowOffsets();
        final boolean isFloating = context.isFloating();
        final GridData model = context.getModel();
        final Transform transform = context.getTransform();
        final GridRenderer renderer = context.getRenderer();
        final GridRendererTheme theme = renderer.getTheme();
        final double columnWidth = column.getWidth();
        final double columnHeight = visibleRowOffsets.get(maxVisibleRowIndex - minVisibleRowIndex) - visibleRowOffsets.get(0) + allRowHeights.get(maxVisibleRowIndex);
        final int columnIndex = model.getColumns().indexOf(column);

        final List<GridRenderer.RendererCommand> commands = new ArrayList<>();

        //Grid lines
        commands.add((GridRenderer.RenderBodyGridLinesCommand) (rc) -> {
            if (!rc.isSelectionLayer()) {
                //- horizontal
                final MultiPath bodyGrid = theme.getBodyGridLine();
                for (int rowIndex = minVisibleRowIndex; rowIndex <= maxVisibleRowIndex; rowIndex++) {
                    final double y = visibleRowOffsets.get(rowIndex - minVisibleRowIndex) - visibleRowOffsets.get(0);
                    final GridRow row = model.getRow(rowIndex);

                    if (!row.isMerged()) {
                        //If row doesn't contain merged cells just draw a line across the visible body
                        bodyGrid.M(x, y + 0.5)
                                .L(x + columnWidth, y + 0.5);
                    } else if (!row.isCollapsed()) {
                        //If row isn't collapsed just draw a line across the visible body at the top of the merged block
                        final GridCell<?> cell = model.getCell(rowIndex,
                                                               columnIndex);

                        if (cell == null || cell.getMergedCellCount() > 0) {
                            //Draw a line-segment for empty cells and cells that are to have content rendered
                            bodyGrid.M(x, y + 0.5)
                                    .L(x + columnWidth, y + 0.5);
                        } else if (isCollapsedRowMultiValue(model,
                                                            column,
                                                            cell,
                                                            rowIndex)) {
                            //Special case for when a cell follows collapsed row(s) with multiple values
                            bodyGrid.M(x, y + 0.5)
                                    .L(x + columnWidth, y + 0.5);
                        }
                    }
                }

                //- vertical
                if (columnIndex < model.getColumnCount() - 1) {
                    bodyGrid.M(x + columnWidth + 0.5, 0)
                            .L(x + columnWidth + 0.5, columnHeight);
                }

                rc.getGroup().add(bodyGrid);
            }
        });

        //Column content
        commands.add((GridRenderer.RenderBodyGridContentCommand) (rc) -> {
            if (columnRenderingConstraint.apply(rc.isSelectionLayer(), column)) {
                final Group columnGroup = GWT.create(Group.class);
                columnGroup.setX(x);
                int iterations = 0;
                for (int rowIndex = minVisibleRowIndex; rowIndex <= maxVisibleRowIndex; rowIndex++) {

                    // This is a defensive programming to prevent this loop from never ending.
                    // The check should never be satisfied however, especially in early development, this loop sometimes became
                    // infinite. All known issue are resolved however the check remains as a safety precaution. Without the check
                    // the Workbench could appear to "lock up" - if the infinite loop scenario reoccurred. With the check,
                    // at worst, the grid will be incorrectly rendered.
                    iterations++;
                    if (iterations > LOOP_THRESHOLD) {
                        break;
                    }

                    final double y = visibleRowOffsets.get(rowIndex - minVisibleRowIndex) - visibleRowOffsets.get(0);
                    final GridRow row = model.getRow(rowIndex);
                    final GridCell<?> cell = model.getCell(rowIndex,
                                                           columnIndex);

                    //Only show content for rows that are not collapsed
                    if (row.isCollapsed()) {
                        continue;
                    }

                    //Add highlight for merged cells with different values
                    final boolean isCollapsedCellMixedValue = isCollapsedCellMixedValue(model,
                                                                                        rowIndex,
                                                                                        columnIndex);

                    if (isCollapsedCellMixedValue) {
                        final Group mixedValueGroup = renderMergedCellMixedValueHighlight(columnWidth,
                                                                                          allRowHeights.get(rowIndex));
                        mixedValueGroup.setX(0).setY(y).setListening(true);
                        columnGroup.add(mixedValueGroup);
                    }

                    //Only show content if there's a Cell behind it!
                    if (cell == null) {
                        continue;
                    }

                    //Add Group Toggle for first row in a Merged block
                    if (cell.getMergedCellCount() > 1) {
                        final GridCell<?> nextRowCell = model.getCell(rowIndex + 1,
                                                                      columnIndex);
                        if (nextRowCell != null) {
                            final Group gt = renderGroupedCellToggle(columnWidth,
                                                                     allRowHeights.get(rowIndex),
                                                                     nextRowCell.isCollapsed());
                            gt.setX(0).setY(y);
                            columnGroup.add(gt);
                        }
                    }

                    if (cell.getMergedCellCount() > 0) {
                        //If cell is "lead" i.e. top of a merged block centralize content in cell
                        final double cellHeight = getCellHeight(rowIndex,
                                                                allRowHeights,
                                                                cell);
                        final GridBodyCellRenderContext cellContext = new GridBodyCellRenderContext(absoluteColumnX,
                                                                                                    absoluteGridY + renderer.getHeaderHeight() + visibleRowOffsets.get(rowIndex - minVisibleRowIndex),
                                                                                                    columnWidth,
                                                                                                    cellHeight,
                                                                                                    clipMinY,
                                                                                                    clipMinX,
                                                                                                    rowIndex,
                                                                                                    columnIndex,
                                                                                                    isFloating,
                                                                                                    transform,
                                                                                                    renderer);

                        //Render cell's content
                        final Group cc = column.getColumnRenderer().renderCell((GridCell) cell,
                                                                               cellContext);
                        cc.setX(0).setY(y).setListening(true);
                        columnGroup.add(cc);

                        //Skip remainder of merged block
                        rowIndex = rowIndex + cell.getMergedCellCount() - 1;
                    } else {
                        //Otherwise the cell has been clipped and we need to back-track to the "lead" cell to centralize content
                        double _y = y;
                        int _rowIndex = rowIndex;
                        GridCell<?> _cell = cell;
                        while (_cell.getMergedCellCount() == 0) {
                            _rowIndex--;
                            _y = _y - allRowHeights.get(_rowIndex);
                            _cell = model.getCell(_rowIndex,
                                                  columnIndex);
                        }

                        final double cellHeight = getCellHeight(_rowIndex,
                                                                allRowHeights,
                                                                cell);
                        final GridBodyCellRenderContext cellContext = new GridBodyCellRenderContext(absoluteColumnX,
                                                                                                    absoluteGridY + renderer.getHeaderHeight() + rendererHelper.getRowOffset(_rowIndex),
                                                                                                    columnWidth,
                                                                                                    cellHeight,
                                                                                                    clipMinY,
                                                                                                    clipMinX,
                                                                                                    rowIndex,
                                                                                                    columnIndex,
                                                                                                    isFloating,
                                                                                                    transform,
                                                                                                    renderer);

                        //Render cell's content
                        final Group cc = column.getColumnRenderer().renderCell((GridCell) _cell,
                                                                               cellContext);
                        cc.setX(0).setY(_y).setListening(true);
                        columnGroup.add(cc);

                        //Skip remainder of merged block
                        rowIndex = _rowIndex + _cell.getMergedCellCount() - 1;
                    }
                }

                //Clip Column Group
                final double gridLinesStrokeWidth = theme.getBodyGridLine().getStrokeWidth();
                final BoundingBoxPathClipperFactory boundingBoxPathClipperFactory = GWT.create(BoundingBoxPathClipperFactory.class);
                final IPathClipper clipper = boundingBoxPathClipperFactory.newClipper(gridLinesStrokeWidth,
                                                                                      0,
                                                                                      columnWidth - gridLinesStrokeWidth,
                                                                                      columnHeight);
                columnGroup.setPathClipper(clipper);
                clipper.setActive(true);

                rc.getGroup().add(columnGroup);
            }
        });

        return commands;
    }

    protected static boolean isCollapsedRowMultiValue(final GridData model,
                                                      final GridColumn<?> column,
                                                      final GridCell<?> cell,
                                                      final int rowIndex) {
        GridRow row;
        int rowOffset = 1;
        final int columnIndex = column.getIndex();

        //Iterate collapsed rows checking if the values differ
        while ((row = model.getRow(rowIndex - rowOffset)).isCollapsed()) {
            final GridCell<?> nc = row.getCells().get(columnIndex);
            if (!Objects.equals(nc, cell)) {
                return true;
            }
            rowOffset++;
        }

        //Check "lead" row as well - since this is not marked as collapsed
        final GridCell<?> nc = row.getCells().get(columnIndex);
        if (!Objects.equals(nc, cell)) {
            return true;
        }
        return false;
    }

    protected static boolean isCollapsedCellMixedValue(final GridData model,
                                                       final int rowIndex,
                                                       final int columnIndex) {
        int _rowIndex = rowIndex;
        GridCell<?> currentCell = model.getCell(_rowIndex,
                                                columnIndex);
        if (currentCell != null) {
            while (_rowIndex > 0 && currentCell.getMergedCellCount() == 0) {
                _rowIndex--;
                currentCell = model.getCell(_rowIndex,
                                            columnIndex);
            }
        }

        _rowIndex++;
        if (_rowIndex > model.getRowCount() - 1) {
            return false;
        }
        while (_rowIndex < model.getRowCount() && model.getRow(_rowIndex).isCollapsed()) {
            final GridCell<?> nextCell = model.getCell(_rowIndex,
                                                       columnIndex);
            if (!Objects.equals(currentCell, nextCell)) {
                return true;
            }
            _rowIndex++;
        }

        return false;
    }

    protected static double getCellHeight(final int rowIndex,
                                          final List<Double> allRowHeights,
                                          final GridCell<?> cell) {
        double height = 0.0;
        for (int iRowIndex = rowIndex; iRowIndex < rowIndex + cell.getMergedCellCount(); iRowIndex++) {
            height = height + allRowHeights.get(iRowIndex);
        }
        return height;
    }

    private static Group renderGroupedCellToggle(final double cellWidth,
                                                 final double cellHeight,
                                                 final boolean isCollapsed) {
        return new GroupingToggle(cellWidth,
                                  cellHeight,
                                  isCollapsed);
    }

    private static Group renderMergedCellMixedValueHighlight(final double cellWidth,
                                                             final double cellHeight) {
        final Group g = new Group();
        final Rectangle multiValueHighlight = new Rectangle(cellWidth,
                                                            cellHeight);
        multiValueHighlight.setFillColor(ColorName.GOLDENROD);
        g.add(multiValueHighlight);
        return g;
    }
}
