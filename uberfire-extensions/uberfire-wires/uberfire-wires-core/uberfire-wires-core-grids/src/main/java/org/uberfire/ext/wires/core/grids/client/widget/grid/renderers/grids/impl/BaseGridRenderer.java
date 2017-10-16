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

import java.util.List;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.types.Transform;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
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

    protected GridRendererTheme theme;

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
        this.theme = PortablePreconditions.checkNotNull("theme",
                                                        theme);
    }

    @Override
    public Group renderSelector(final double width,
                                final double height,
                                final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        final Group g = new Group();
        final MultiPath selector = theme.getSelector()
                .M(0.5,
                   0.5)
                .L(0.5,
                   height)
                .L(width,
                   height)
                .L(width,
                   0.5)
                .L(0.5,
                   0.5)
                .setListening(false);
        g.add(selector);
        return g;
    }

    @Override
    public Group renderSelectedCells(final GridData model,
                                     final GridBodyRenderContext context,
                                     final BaseGridRendererHelper rendererHelper) {
        final List<GridColumn<?>> blockColumns = context.getBlockColumns();
        final SelectionsTransformer transformer = context.getTransformer();
        final int minVisibleUiColumnIndex = model.getColumns().indexOf(blockColumns.get(0));
        final int maxVisibleUiColumnIndex = model.getColumns().indexOf(blockColumns.get(blockColumns.size() - 1));
        final int minVisibleUiRowIndex = context.getMinVisibleRowIndex();
        final int maxVisibleUiRowIndex = context.getMaxVisibleRowIndex();

        //Convert SelectedCells into SelectedRanges, i.e. group them into rectangular ranges
        final List<SelectedRange> selectedRanges = transformer.transformToSelectedRanges();

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

            final Group cs = renderSelectedRange(model,
                                                 blockColumns,
                                                 minVisibleUiColumnIndex,
                                                 _selectedRange);
            if (cs != null) {
                final double csx = rendererHelper.getColumnOffset(blockColumns,
                                                                  _selectedRange.getUiColumnIndex() - minVisibleUiColumnIndex);
                final double csy = rendererHelper.getRowOffset(_selectedRange.getUiRowIndex()) - rendererHelper.getRowOffset(minVisibleUiRowIndex);
                cs.setX(csx)
                        .setY(csy)
                        .setListening(false);
                g.add(cs);
            }
        }
        return g;
    }

    protected Group renderSelectedRange(final GridData model,
                                        final List<GridColumn<?>> blockColumns,
                                        final int minVisibleUiColumnIndex,
                                        final SelectedRange selectedRange) {
        final Group cellSelector = new Group();
        final double width = getSelectedRangeWidth(blockColumns,
                                                   minVisibleUiColumnIndex,
                                                   selectedRange);
        final double height = getSelectedRangeHeight(model,
                                                     selectedRange);
        final Rectangle selector = theme.getCellSelector()
                .setWidth(width)
                .setHeight(height)
                .setStrokeWidth(1.0)
                .setListening(false);

        final Rectangle highlight = theme.getCellSelector()
                .setWidth(width)
                .setHeight(height)
                .setFillColor(selector.getStrokeColor())
                .setListening(false)
                .setAlpha(0.25);

        cellSelector.add(highlight);
        cellSelector.add(selector);

        return cellSelector;
    }

    private double getSelectedRangeWidth(final List<GridColumn<?>> blockColumns,
                                         final int minVisibleUiColumnIndex,
                                         final SelectedRange selectedRange) {
        double width = 0;
        for (int columnIndex = 0; columnIndex < selectedRange.getWidth(); columnIndex++) {
            final int relativeColumnIndex = columnIndex + selectedRange.getUiColumnIndex() - minVisibleUiColumnIndex;
            width = width + blockColumns.get(relativeColumnIndex).getWidth();
        }
        return width;
    }

    private double getSelectedRangeHeight(final GridData model,
                                          final SelectedRange selectedRange) {
        double height = 0;
        for (int rowIndex = 0; rowIndex < selectedRange.getHeight(); rowIndex++) {
            height = height + model.getRow(selectedRange.getUiRowIndex() + rowIndex).getHeight();
        }
        return height;
    }

    @Override
    public Group renderHeader(final GridData model,
                              final GridHeaderRenderContext context,
                              final BaseGridRendererHelper rendererHelper,
                              final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        final List<GridColumn<?>> allBlockColumns = context.getAllColumns();
        final List<GridColumn<?>> visibleBlockColumns = context.getBlockColumns();
        final boolean isSelectionLayer = context.isSelectionLayer();

        final double headerRowsHeight = renderingInformation.getHeaderRowsHeight();
        final double headerRowsYOffset = renderingInformation.getHeaderRowsYOffset();

        final Group g = new Group();

        //Column backgrounds
        double x = 0;
        for (final GridColumn<?> column : visibleBlockColumns) {
            if (column.isVisible()) {
                final double w = column.getWidth();
                Rectangle header;
                if (column.isLinked()) {
                    header = theme.getHeaderLinkBackground(column);
                } else {
                    header = theme.getHeaderBackground(column);
                }
                if (header != null) {
                    header.setWidth(w)
                            .setListening(true)
                            .setHeight(headerRowsHeight)
                            .setY(headerRowsYOffset)
                            .setX(x);
                    g.add(header);
                }
                x = x + w;
            }
        }

        //Don't render the Header's detail if we're rendering the SelectionLayer
        if (isSelectionLayer) {
            return g;
        }

        //Column title and grid lines
        x = 0;
        for (final GridColumn<?> column : visibleBlockColumns) {
            if (column.isVisible()) {
                final double columnWidth = column.getWidth();
                final int columnIndex = visibleBlockColumns.indexOf(column);
                final GridHeaderColumnRenderContext headerCellRenderContext = new GridHeaderColumnRenderContext(allBlockColumns,
                                                                                                                visibleBlockColumns,
                                                                                                                columnIndex,
                                                                                                                model,
                                                                                                                this);
                final Group headerGroup = column.getColumnRenderer().renderHeader(column.getHeaderMetaData(),
                                                                                  headerCellRenderContext,
                                                                                  renderingInformation);
                headerGroup.setX(x);
                g.add(headerGroup);

                x = x + columnWidth;
            }
        }

        //Linked column icons
        x = 0;
        for (final GridColumn<?> column : visibleBlockColumns) {
            if (column.isVisible()) {
                final double w = column.getWidth();
                if (column.isLinked()) {
                    final Text t = theme.getBodyText()
                            .setFontFamily(LINK_FONT_FAMILY)
                            .setFontSize(LINK_FONT_SIZE)
                            .setText(LINK_ICON)
                            .setY(headerRowsYOffset + LINK_FONT_SIZE)
                            .setX(x + w - LINK_FONT_SIZE);
                    g.add(t);
                }
                x = x + w;
            }
        }

        //Divider between header and body
        final Group divider = renderHeaderBodyDivider(x);
        g.add(divider);

        return g;
    }

    @Override
    public Group renderHeaderBodyDivider(final double width) {
        final Group g = new Group();
        final Line divider = theme.getGridHeaderBodyDivider();
        divider.setPoints(new Point2DArray(new Point2D(0,
                                                       getHeaderHeight() + 0.5),
                                           new Point2D(width,
                                                       getHeaderHeight() + 0.5)));
        g.add(divider);
        return g;
    }

    @Override
    public Group renderBody(final GridData model,
                            final GridBodyRenderContext context,
                            final BaseGridRendererHelper rendererHelper,
                            final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        final double absoluteGridX = context.getAbsoluteGridX();
        final double absoluteGridY = context.getAbsoluteGridY();
        final double absoluteColumnOffsetX = context.getAbsoluteColumnOffsetX();
        final double clipMinY = context.getClipMinY();
        final double clipMinX = context.getClipMinX();
        final int minVisibleRowIndex = context.getMinVisibleRowIndex();
        final int maxVisibleRowIndex = context.getMaxVisibleRowIndex();
        final List<GridColumn<?>> blockColumns = context.getBlockColumns();
        final boolean isSelectionLayer = context.isSelectionLayer();
        final Transform transform = context.getTransform();
        final GridRenderer renderer = context.getRenderer();

        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final List<Double> visibleRowOffsets = renderingInformation.getVisibleRowOffsets();

        final double columnHeight = visibleRowOffsets.get(maxVisibleRowIndex - minVisibleRowIndex) - visibleRowOffsets.get(0) + model.getRow(maxVisibleRowIndex).getHeight();

        final Group g = new Group();

        //Column backgrounds
        double x = 0;
        for (final GridColumn<?> column : blockColumns) {
            if (column.isVisible()) {
                final double columnWidth = column.getWidth();
                final Rectangle body = theme.getBodyBackground(column)
                        .setWidth(columnWidth)
                        .setListening(true)
                        .setHeight(columnHeight)
                        .setX(x);
                g.add(body);
                x = x + columnWidth;
            }
        }

        //Don't render the Body's detail if we're rendering the SelectionLayer
        if (isSelectionLayer) {
            return g;
        }

        x = 0;
        for (GridColumn<?> column : blockColumns) {
            if (column.isVisible()) {
                final double columnWidth = column.getWidth();
                final double columnRelativeX = rendererHelper.getColumnOffset(blockColumns,
                                                                              blockColumns.indexOf(column)) + absoluteColumnOffsetX;
                final boolean isFloating = floatingBlockInformation.getColumns().contains(column);
                final GridBodyColumnRenderContext columnContext = new GridBodyColumnRenderContext(absoluteGridX,
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
                final Group columnGroup = column.getColumnRenderer().renderColumn(column,
                                                                                  columnContext,
                                                                                  rendererHelper,
                                                                                  renderingInformation);
                columnGroup.setX(x);
                g.add(columnGroup);

                x = x + columnWidth;
            }
        }

        return g;
    }

    @Override
    public Group renderGridBoundary(final double width,
                                    final double height) {
        final Group g = new Group();
        final Rectangle boundary = theme.getGridBoundary()
                .setWidth(width)
                .setHeight(height)
                .setListening(false)
                .setX(0.5)
                .setY(0.5);
        g.add(boundary);
        return g;
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
}
