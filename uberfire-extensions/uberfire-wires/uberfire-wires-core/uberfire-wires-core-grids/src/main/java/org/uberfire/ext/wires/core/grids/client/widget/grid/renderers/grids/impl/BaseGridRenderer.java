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
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.types.Transform;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBoundaryRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.SelectionsTransformer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

/**
 * A renderer that only renders the visible columns and rows of merged data. This implementation
 * can render the data either in a merged state or non-merged state.
 */
public class BaseGridRenderer implements GridRenderer {

    private static final int HEADER_HEIGHT = 64;

    private static final int HEADER_ROW_HEIGHT = 32;

    private static final String LINK_FONT_FAMILY = "Glyphicons Halflings";

    private static final double LINK_FONT_SIZE = 10.0;

    private static final String LINK_ICON = "\ue144";

    private Integer highlightCellColumnIndex;

    private Integer highlightCellRowIndex;

    protected GridRendererTheme theme;

    protected BiFunction<Boolean, GridColumn<?>, Boolean> columnRenderingConstraint = (isSelectionLayer, gridColumn) -> !isSelectionLayer;

    public BaseGridRenderer(final GridRendererTheme theme) {
        setTheme(theme);
    }

    @Override
    public double getHeaderHeight() {
        return HEADER_HEIGHT;
    }

    @Override
    public double getHeaderRowHeight() {
        return HEADER_ROW_HEIGHT;
    }

    @Override
    public GridRendererTheme getTheme() {
        return theme;
    }

    @Override
    public void setTheme(final GridRendererTheme theme) {
        this.theme = Objects.requireNonNull(theme, "theme");
    }

    @Override
    public RendererCommand renderSelector(final double width,
                                          final double height,
                                          final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        return (RenderSelectorCommand) (rc) -> {
            if (!rc.isSelectionLayer()) {
                final MultiPath selector = theme.getSelector()
                        .M(0.5, 0.5)
                        .L(0.5, height)
                        .L(width, height)
                        .L(width, 0.5)
                        .L(0.5, 0.5)
                        .setListening(false);
                rc.getGroup().add(selector);
            }
        };
    }

    @Override
    public RendererCommand renderSelectedCells(final GridData model,
                                               final GridBodyRenderContext context,
                                               final BaseGridRendererHelper rendererHelper,
                                               final List<GridData.SelectedCell> selectedCells,
                                               final BiFunction<SelectedRange, Integer, Double> selectedCellsYOffsetStrategy,
                                               final Function<SelectedRange, Double> selectedCellsHeightStrategy) {
        return (RenderSelectedCellsCommand) (rc) -> {
            if (!rc.isSelectionLayer()) {

                final List<GridColumn<?>> blockColumns = context.getBlockColumns();
                final SelectionsTransformer transformer = context.getTransformer();
                final double gridLineStrokeWidth = theme.getBodyGridLine().getStrokeWidth();
                final double selectorStrokeWidth = theme.getCellSelectorBorder().getStrokeWidth();
                final int minVisibleUiColumnIndex = model.getColumns().indexOf(blockColumns.get(0));
                final int maxVisibleUiColumnIndex = model.getColumns().indexOf(blockColumns.get(blockColumns.size() - 1));
                final int minVisibleUiRowIndex = context.getMinVisibleRowIndex();
                final int maxVisibleUiRowIndex = context.getMaxVisibleRowIndex();

                //Convert SelectedCells into SelectedRanges, i.e. group them into rectangular ranges
                final List<SelectedRange> selectedRanges = transformer.transformToSelectedRanges(selectedCells);

                final Group g = new Group();
                for (SelectedRange selectedRange : selectedRanges) {
                    final int rangeOriginUiColumnIndex = selectedRange.getUiColumnIndex();
                    final int rangeOriginUiRowIndex = selectedRange.getUiRowIndex();
                    final int rangeUiWidth = selectedRange.getWidth();
                    final int rangeUiHeight = selectedRange.getHeight();

                    //Only render range highlights if they're at least partially visible
                    if (rangeOriginUiColumnIndex + rangeUiWidth - 1 < minVisibleUiColumnIndex) {
                        continue;
                    }
                    if (rangeOriginUiColumnIndex > maxVisibleUiColumnIndex) {
                        continue;
                    }
                    if (rangeOriginUiRowIndex + rangeUiHeight - 1 < minVisibleUiRowIndex) {
                        continue;
                    }
                    if (rangeOriginUiRowIndex > maxVisibleUiRowIndex) {
                        continue;
                    }

                    //Clip range to visible bounds
                    SelectedRange _selectedRange = selectedRange;
                    if (rangeOriginUiRowIndex < minVisibleUiRowIndex) {
                        final int dy = minVisibleUiRowIndex - rangeOriginUiRowIndex;
                        _selectedRange = new SelectedRange(selectedRange.getUiRowIndex() + dy,
                                                           selectedRange.getUiColumnIndex(),
                                                           selectedRange.getWidth(),
                                                           selectedRange.getHeight() - dy);
                    }

                    final Group cs = renderSelectedRange(blockColumns,
                                                         minVisibleUiColumnIndex,
                                                         _selectedRange,
                                                         selectedCellsHeightStrategy);
                    if (cs != null) {
                        final double csx = rendererHelper.getColumnOffset(blockColumns,
                                                                          _selectedRange.getUiColumnIndex() - minVisibleUiColumnIndex);
                        final double csy = selectedCellsYOffsetStrategy.apply(_selectedRange, minVisibleUiRowIndex);
                        cs.setX(csx + gridLineStrokeWidth + (selectorStrokeWidth / 2))
                                .setY(csy + gridLineStrokeWidth + (selectorStrokeWidth / 2))
                                .setListening(false);
                        g.add(cs);
                    }
                }
                rc.getGroup().add(g);
            }
        };
    }

    protected Group renderSelectedRange(final List<GridColumn<?>> blockColumns,
                                        final int minVisibleUiColumnIndex,
                                        final SelectedRange selectedRange,
                                        final Function<SelectedRange, Double> selectedCellsHeightStrategy) {
        final Group cellSelector = new Group();
        final double gridLineStrokeWidth = theme.getBodyGridLine().getStrokeWidth();
        final double selectorStrokeWidth = theme.getCellSelectorBorder().getStrokeWidth();
        final double width = getSelectedRangeWidth(blockColumns,
                                                   minVisibleUiColumnIndex,
                                                   selectedRange) - (gridLineStrokeWidth + selectorStrokeWidth);
        final double height = selectedCellsHeightStrategy.apply(selectedRange) - (gridLineStrokeWidth + selectorStrokeWidth);
        final Rectangle selector = theme.getCellSelectorBorder()
                .setWidth(width)
                .setHeight(height)
                .setListening(false);

        final Rectangle highlight = theme.getCellSelectorBackground()
                .setWidth(width)
                .setHeight(height)
                .setListening(false);

        cellSelector.add(highlight);
        cellSelector.add(selector);

        return cellSelector;
    }

    double getSelectedRangeWidth(final List<GridColumn<?>> blockColumns,
                                 final int minVisibleUiColumnIndex,
                                 final SelectedRange selectedRange) {
        double width = 0;
        for (int columnIndex = 0; columnIndex < selectedRange.getWidth(); columnIndex++) {
            final int relativeColumnIndex = columnIndex + selectedRange.getUiColumnIndex() - minVisibleUiColumnIndex;
            final GridColumn<?> uiColumn = blockColumns.get(relativeColumnIndex);
            if (uiColumn.isVisible()) {
                width = width + uiColumn.getWidth();
            }
        }
        return width;
    }

    @Override
    public List<RendererCommand> renderHeader(final GridData model,
                                              final GridHeaderRenderContext context,
                                              final BaseGridRendererHelper rendererHelper,
                                              final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        final List<RendererCommand> commands = new ArrayList<>();
        final int headerRowCount = renderingInformation.getHeaderRowCount();
        if (headerRowCount == 0) {
            return commands;
        }

        final List<GridColumn<?>> allBlockColumns = context.getAllColumns();
        final List<GridColumn<?>> visibleBlockColumns = context.getBlockColumns();
        final double headerRowsHeight = renderingInformation.getHeaderRowsHeight();
        final double headerRowsYOffset = renderingInformation.getHeaderRowsYOffset();

        //Column backgrounds
        double cx = 0;
        for (final GridColumn<?> column : visibleBlockColumns) {
            if (column.isVisible()) {
                final double x = cx;
                final double w = column.getWidth();
                Rectangle header;
                if (column.isLinked()) {
                    header = theme.getHeaderLinkBackground(column);
                } else {
                    header = theme.getHeaderBackground(column);
                }
                if (header != null) {
                    commands.add((RenderHeaderBackgroundCommand) (rc) -> {
                        if (columnRenderingConstraint.apply(rc.isSelectionLayer(), column)) {
                            header.setWidth(w)
                                    .setListening(true)
                                    .setHeight(headerRowsHeight)
                                    .setY(headerRowsYOffset)
                                    .setX(x);
                            rc.getGroup().add(header);
                        }
                    });
                }
                cx = cx + w;
            }
        }

        //Column title and grid lines
        cx = 0;
        for (final GridColumn<?> column : visibleBlockColumns) {
            if (column.isVisible()) {
                final double columnWidth = column.getWidth();

                final int columnIndex = visibleBlockColumns.indexOf(column);
                final GridHeaderColumnRenderContext headerCellRenderContext = new GridHeaderColumnRenderContext(cx,
                                                                                                                allBlockColumns,
                                                                                                                visibleBlockColumns,
                                                                                                                columnIndex,
                                                                                                                model,
                                                                                                                this);

                commands.addAll(column.getColumnRenderer().renderHeader(column.getHeaderMetaData(),
                                                                        headerCellRenderContext,
                                                                        renderingInformation,
                                                                        columnRenderingConstraint));
                cx = cx + columnWidth;
            }
        }

        //Linked column icons
        cx = 0;
        for (final GridColumn<?> column : visibleBlockColumns) {
            if (column.isVisible()) {
                final double x = cx;
                final double w = column.getWidth();

                if (column.isLinked()) {
                    commands.add((RenderHeaderContentCommand) (rc) -> {
                        if (columnRenderingConstraint.apply(rc.isSelectionLayer(), column)) {
                            final Text t = theme.getBodyText()
                                    .setFontFamily(LINK_FONT_FAMILY)
                                    .setFontSize(LINK_FONT_SIZE)
                                    .setText(LINK_ICON)
                                    .setY(headerRowsYOffset + LINK_FONT_SIZE)
                                    .setX(x + w - LINK_FONT_SIZE);
                            rc.getGroup().add(t);
                        }
                    });
                }
                cx = cx + w;
            }
        }

        //Divider between header and body
        commands.add(renderHeaderBodyDivider(rendererHelper.getWidth(visibleBlockColumns)));

        return commands;
    }

    @Override
    public RendererCommand renderHeaderBodyDivider(final double width) {
        return (RenderHeaderGridLinesCommand) (rc) -> {
            if (!rc.isSelectionLayer()) {
                final Line divider = theme.getGridHeaderBodyDivider();
                divider.setPoints(new Point2DArray(new Point2D(0,
                                                               getHeaderHeight() + 0.5),
                                                   new Point2D(width,
                                                               getHeaderHeight() + 0.5)));
                rc.getGroup().add(divider);
            }
        };
    }

    @Override
    public List<RendererCommand> renderBody(final GridData model,
                                            final GridBodyRenderContext context,
                                            final BaseGridRendererHelper rendererHelper,
                                            final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        final List<RendererCommand> commands = new ArrayList<>();

        final double absoluteGridX = context.getAbsoluteGridX();
        final double absoluteGridY = context.getAbsoluteGridY();
        final double absoluteColumnOffsetX = context.getAbsoluteColumnOffsetX();
        final double clipMinY = context.getClipMinY();
        final double clipMinX = context.getClipMinX();
        final int minVisibleRowIndex = context.getMinVisibleRowIndex();
        final int maxVisibleRowIndex = context.getMaxVisibleRowIndex();
        final List<GridColumn<?>> blockColumns = context.getBlockColumns();
        final Transform transform = context.getTransform();
        final GridRenderer renderer = context.getRenderer();

        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final List<Double> allRowHeights = renderingInformation.getAllRowHeights();
        final List<Double> visibleRowOffsets = renderingInformation.getVisibleRowOffsets();

        final double columnHeight = visibleRowOffsets.get(maxVisibleRowIndex - minVisibleRowIndex) - visibleRowOffsets.get(0) + allRowHeights.get(maxVisibleRowIndex);

        //Column backgrounds
        double cx = 0;
        for (final GridColumn<?> column : blockColumns) {
            if (column.isVisible()) {
                final double x = cx;
                final double columnWidth = column.getWidth();

                commands.add((RenderBodyGridBackgroundCommand) (rc) -> {
                    if (columnRenderingConstraint.apply(rc.isSelectionLayer(), column)) {
                        final Rectangle body = theme.getBodyBackground(column)
                                .setWidth(columnWidth)
                                .setListening(true)
                                .setHeight(columnHeight)
                                .setX(x);
                        rc.getGroup().add(body);
                    }
                });
                cx = cx + columnWidth;
            }
        }

        //Column content and grid lines
        cx = 0;
        for (GridColumn<?> column : blockColumns) {
            if (column.isVisible()) {
                final double columnWidth = column.getWidth();

                final double columnRelativeX = rendererHelper.getColumnOffset(blockColumns,
                                                                              blockColumns.indexOf(column)) + absoluteColumnOffsetX;
                final boolean isFloating = floatingBlockInformation.getColumns().contains(column);
                final GridBodyColumnRenderContext columnContext = new GridBodyColumnRenderContext(cx,
                                                                                                  absoluteGridX,
                                                                                                  absoluteGridY,
                                                                                                  absoluteGridX + columnRelativeX,
                                                                                                  clipMinY,
                                                                                                  clipMinX,
                                                                                                  minVisibleRowIndex,
                                                                                                  maxVisibleRowIndex,
                                                                                                  isFloating,
                                                                                                  model,
                                                                                                  transform,
                                                                                                  renderer);

                commands.addAll(column.getColumnRenderer().renderColumn(column,
                                                                        columnContext,
                                                                        rendererHelper,
                                                                        renderingInformation,
                                                                        columnRenderingConstraint));
                cx = cx + columnWidth;
            }
        }

        addRenderHighlightedCellsCommand(model, context, rendererHelper, renderingInformation, commands);

        return commands;
    }

    void addRenderHighlightedCellsCommand(final GridData model,
                                          final GridBodyRenderContext context,
                                          final BaseGridRendererHelper rendererHelper,
                                          final BaseGridRendererHelper.RenderingInformation renderingInformation,
                                          final List<RendererCommand> commands) {
        if (!Objects.isNull(getHighlightCellColumnIndex()) && !Objects.isNull(getHighlightCellRowIndex())) {
            commands.add(renderHighlightedCells(model,
                                                context,
                                                rendererHelper,
                                                renderingInformation));
        }
    }

    @Override
    public RendererCommand renderGridBoundary(final GridBoundaryRenderContext context) {
        return (RenderGridBoundaryCommand) (rc) -> {
            if (!rc.isSelectionLayer()) {
                final double x = context.getX();
                final double y = context.getY();
                final double width = context.getWidth();
                final double height = context.getHeight();

                final Rectangle boundary = theme.getGridBoundary()
                        .setWidth(width)
                        .setHeight(height)
                        .setListening(false)
                        .setX(x + 0.5)
                        .setY(y + 0.5);

                rc.getGroup().add(boundary);
            }
        };
    }

    @Override
    public boolean onGroupingToggle(double cellX,
                                    double cellY,
                                    double cellWidth,
                                    double cellHeight) {
        return GroupingToggle.onHotSpot(cellX,
                                        cellY,
                                        cellWidth,
                                        cellHeight);
    }

    @Override
    public void setColumnRenderConstraint(final BiFunction<Boolean, GridColumn<?>, Boolean> columnRenderingConstraint) {
        this.columnRenderingConstraint = columnRenderingConstraint;
    }

    public void clearCellHighlight() {
        highlightCellColumnIndex = null;
        highlightCellRowIndex = null;
    }

    public void highlightCell(final int column, final int row) {
        highlightCellColumnIndex = column;
        highlightCellRowIndex = row;
    }

    public Integer getHighlightCellColumnIndex() {
        return highlightCellColumnIndex;
    }

    public Integer getHighlightCellRowIndex() {
        return highlightCellRowIndex;
    }

    RendererCommand renderHighlightedCells(final GridData model,
                                           final GridBodyRenderContext context,
                                           final BaseGridRendererHelper rendererHelper,
                                           final BaseGridRendererHelper.RenderingInformation renderingInformation) {

        final GridColumn<?> column = model.getColumns().get(getHighlightCellColumnIndex());

        final int visibleRowIndex = getHighlightCellRowIndex() - renderingInformation.getMinVisibleRowIndex();

        return getRendererCommand(model,
                                  context,
                                  rendererHelper,
                                  renderingInformation,
                                  column,
                                  visibleRowIndex);
    }

    RendererCommand getRendererCommand(final GridData model,
                                       final GridBodyRenderContext context,
                                       final BaseGridRendererHelper rendererHelper,
                                       final BaseGridRendererHelper.RenderingInformation renderingInformation,
                                       final GridColumn<?> column,
                                       final int visibleRowIndex) {
        return (rc) -> {
            if (!rc.isSelectionLayer()) {
                if (model.getColumns().size() >= 1) {

                    rc.getGroup().add(makeCellHighlight(getHighlightCellRowIndex(),
                                                        visibleRowIndex,
                                                        model,
                                                        rendererHelper,
                                                        renderingInformation,
                                                        column,
                                                        context));
                }
            }
        };
    }

    Rectangle makeCellHighlight(final int rowIndex,
                                final int visibleRowIndex,
                                final GridData model,
                                final BaseGridRendererHelper rendererHelper,
                                final BaseGridRendererHelper.RenderingInformation renderingInformation,
                                final GridColumn<?> column,
                                final GridBodyRenderContext context) {

        final Rectangle r = getTheme().getHighlightedCellBackground().setListening(false);
        setCellHighlightX(r, context, rendererHelper);
        setCellHighlightY(r, rendererHelper, visibleRowIndex, model);
        setCellHighlightSize(r, model, column, renderingInformation.getAllRowHeights(), rowIndex);
        return r;
    }

    void setCellHighlightY(final Rectangle r,
                           final BaseGridRendererHelper rendererHelper,
                           final int visibleRowIndex,
                           final GridData model) {

        final GridCell<?> cell = model.getCell(getHighlightCellRowIndex(), getHighlightCellColumnIndex());

        if (!cell.isMerged()) {
            r.setY(rendererHelper.getRowOffset(visibleRowIndex));
        } else {
            int firstRowOfMergedBlock = getFirstRowIndexOfMergedBlock(model, visibleRowIndex);
            r.setY(rendererHelper.getRowOffset(firstRowOfMergedBlock));
        }
    }

    int getFirstRowIndexOfMergedBlock(final GridData model, final int rowIndex) {

        int currentIndex = rowIndex;
        GridCell<?> cell = model.getCell(rowIndex, getHighlightCellColumnIndex());
        while (cell.getMergedCellCount() == 0 && currentIndex >= 0) {
            currentIndex--;
            cell = model.getCell(currentIndex, getHighlightCellColumnIndex());
        }

        return currentIndex;
    }

    void setCellHighlightX(final Rectangle r,
                           final GridBodyRenderContext context,
                           final BaseGridRendererHelper rendererHelper) {

        double columnOffsetX = context.getAbsoluteColumnOffsetX();
        double x = rendererHelper.getColumnOffset(getHighlightCellColumnIndex()) - columnOffsetX;
        r.setX(x);
    }

    void setCellHighlightSize(final Rectangle rectangle,
                              final GridData model,
                              final GridColumn<?> column,
                              final List<Double> allRowHeights,
                              final int rowIndex) {

        final double width = column.getWidth();
        rectangle.setWidth(width);

        final int mergedCellsCount = getMergedCellsCount(model, rowIndex);
        rectangle.setHeight(allRowHeights.get(rowIndex) * mergedCellsCount);
    }

    int getMergedCellsCount(final GridData model,
                            final int rowIndex) {

        int currentIndex = rowIndex;
        GridCell<?> cell = model.getCell(rowIndex, getHighlightCellColumnIndex());
        while (cell.getMergedCellCount() == 0 && currentIndex >= 0) {
            currentIndex--;
            cell = model.getCell(currentIndex, getHighlightCellColumnIndex());
        }

        return cell.getMergedCellCount();
    }
}
