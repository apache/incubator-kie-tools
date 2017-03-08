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
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids;

import com.ait.lienzo.client.core.shape.Group;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

/**
 * Definition of a render for the pluggable rendering mechanism.
 */
public interface GridRenderer {

    /**
     * Returns the height of the header built by this renderer. The header's height may be greater than the product
     * of the maximum number of header rows (see {@link GridColumn.HeaderMetaData}) and {@link #getHeaderRowHeight()}.
     * Header rows are always positioned adjacent to the Body.
     * @return The height of the header.
     */
    double getHeaderHeight();

    /**
     * Returns the height of a single row in the header.
     * @return The height of a row.
     */
    double getHeaderRowHeight();

    /**
     * Returns the theme.
     */
    GridRendererTheme getTheme();

    /**
     * Sets the theme.
     * @param theme
     */
    void setTheme(final GridRendererTheme theme);

    /**
     * Renders a "selector" when a grid has been selected, i.e. clicked.
     * @param width The width of the GridWidget.
     * @param height The height of the GridWidget including header and body.
     * @param renderingInformation Calculated rendering information supporting rendering.
     * @return A Group containing the "selector"
     */
    Group renderSelector(final double width,
                         final double height,
                         final BaseGridRendererHelper.RenderingInformation renderingInformation);

    /**
     * Renders the selected ranges and append to the Body Group.
     * @param model The data model for the GridWidget.
     * @param context The context of the render phase.
     * @param rendererHelper Helper for rendering.
     * @return
     */
    Group renderSelectedCells(final GridData model,
                              final GridBodyRenderContext context,
                              final BaseGridRendererHelper rendererHelper);

    /**
     * Renders the header for the Grid.
     * @param model The data model for the GridWidget.
     * @param context The context of the render phase.
     * @param rendererHelper Helper for rendering.
     * @param renderingInformation Calculated rendering information supporting rendering.
     * @return A Group containing all Shapes representing the Header.
     */
    Group renderHeader(final GridData model,
                       final GridHeaderRenderContext context,
                       final BaseGridRendererHelper rendererHelper,
                       final BaseGridRendererHelper.RenderingInformation renderingInformation);

    /**
     * Renders the body for the Grid.
     * @param model The data model for the GridWidget.
     * @param context The context of the render phase.
     * @param rendererHelper Helper for rendering.
     * @param renderingInformation Calculated rendering information supporting rendering.
     * @return A Group containing all Shapes representing the Body.
     */
    Group renderBody(final GridData model,
                     final GridBodyRenderContext context,
                     final BaseGridRendererHelper rendererHelper,
                     final BaseGridRendererHelper.RenderingInformation renderingInformation);

    /**
     * Renders a divider between Grid header and body. The divider must be positioned in the Group relative to the
     * top-left of the Grid itself; i.e. horizontal lines will need have y-coordinate {@link GridRenderer#getHeaderHeight()}.
     * The returned Group itself is not positioned when added to the Grid. This is to support different types of divider
     * that may need to be positioned at a y-coordinate different to {@link GridRenderer#getHeaderHeight()}.
     * @param width The width of the divider. May not be the width of the whole grid if there are floating columns.
     * @return A Group containing the divider positioned relative to the top-left of the Grid.
     */
    Group renderHeaderBodyDivider(final double width);

    /**
     * Renders a boundary around the grid.
     * @param width The width of the GridWidget.
     * @param height The height of the GridWidget including header and body.
     * @return A Group containing the grids boundary.
     */
    Group renderGridBoundary(final double width,
                             final double height);

    /**
     * Checks whether a cell-relative coordinate is "on" the hot-spot to toggle the collapsed/expanded state.
     * @param cellX The MouseEvent relative to the cell's x-coordinate.
     * @param cellY The MouseEvent relative to the cell's y-coordinate.
     * @param cellWidth Width of the containing cell.
     * @param cellHeight Height of the containing cell.
     * @return true if the cell coordinate is on the hot-spot.
     */
    boolean onGroupingToggle(final double cellX,
                             final double cellY,
                             final double cellWidth,
                             final double cellHeight);
}
