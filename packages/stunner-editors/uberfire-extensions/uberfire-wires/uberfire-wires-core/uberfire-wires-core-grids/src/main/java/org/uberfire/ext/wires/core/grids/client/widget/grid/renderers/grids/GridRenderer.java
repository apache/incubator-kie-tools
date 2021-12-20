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

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.ait.lienzo.client.core.shape.Group;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBoundaryRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.SelectedRange;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

/**
 * Definition of a render for the pluggable rendering mechanism.
 */
public interface GridRenderer {

    interface GridRendererContext {

        Group getGroup();

        boolean isSelectionLayer();
    }

    /**
     * Generic command to render a component of the grid
     */
    interface RendererCommand {

        void execute(GridRendererContext parameter);
    }

    /**
     * Command to render the "Selector" component of the grid
     */
    interface RenderSelectorCommand extends RendererCommand {

    }

    /**
     * Command to render the "Selected cells" component of the grid
     */
    interface RenderSelectedCellsCommand extends RendererCommand {

    }

    /**
     * Command to render the "Grid boundary" components of the grid
     */
    interface RenderGridBoundaryCommand extends RendererCommand {

    }

    /**
     * Generic command for all header related rendering.
     */
    interface RendererHeaderCommand extends RendererCommand {

    }

    /**
     * Command to render the "Grid lines" components of the grid header
     */
    interface RenderHeaderGridLinesCommand extends RendererHeaderCommand {

    }

    /**
     * Command to render the "background" components of the grid header
     */
    interface RenderHeaderBackgroundCommand extends RendererHeaderCommand {

    }

    /**
     * Command to render the "content" components of the grid header
     */
    interface RenderHeaderContentCommand extends RendererHeaderCommand {

    }

    /**
     * Generic command for all body related rendering.
     */
    interface RendererBodyCommand extends RendererCommand {

    }

    /**
     * Command to render the "Grid lines" components of the grid body
     */
    interface RenderBodyGridLinesCommand extends RendererBodyCommand {

    }

    /**
     * Command to render the "background" components of the grid
     */
    interface RenderBodyGridBackgroundCommand extends RendererBodyCommand {

    }

    /**
     * Command to render the "content" components of the grid
     */
    interface RenderBodyGridContentCommand extends RendererBodyCommand {

    }

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
     * @return A command that adds the "selector".
     */
    RendererCommand renderSelector(final double width,
                                   final double height,
                                   final BaseGridRendererHelper.RenderingInformation renderingInformation);

    /**
     * Renders the selected ranges and append to the Body Group.
     * @param model The data model for the GridWidget.
     * @param context The context of the render phase.
     * @param rendererHelper Helper for rendering.
     * @param selectedCells The cells that are selected.
     * @param selectedCellsYOffsetStrategy A function that returns the YOffset of a selection.
     * @param selectedCellsHeightStrategy A function that returns the height of a selection.
     * @return A command that adds the "selected cells".
     */
    RendererCommand renderSelectedCells(final GridData model,
                                        final GridBodyRenderContext context,
                                        final BaseGridRendererHelper rendererHelper,
                                        final List<GridData.SelectedCell> selectedCells,
                                        final BiFunction<SelectedRange, Integer, Double> selectedCellsYOffsetStrategy,
                                        final Function<SelectedRange, Double> selectedCellsHeightStrategy);

    /**
     * Renders the header for the Grid.
     * @param model The data model for the GridWidget.
     * @param context The context of the render phase.
     * @param rendererHelper Helper for rendering.
     * @param renderingInformation Calculated rendering information supporting rendering.
     * @return Commands that add the "header".
     */
    List<RendererCommand> renderHeader(final GridData model,
                                       final GridHeaderRenderContext context,
                                       final BaseGridRendererHelper rendererHelper,
                                       final BaseGridRendererHelper.RenderingInformation renderingInformation);

    /**
     * Renders the body for the Grid.
     * @param model The data model for the GridWidget.
     * @param context The context of the render phase.
     * @param rendererHelper Helper for rendering.
     * @param renderingInformation Calculated rendering information supporting rendering.
     * @return Commands that add the "body".
     */
    List<RendererCommand> renderBody(final GridData model,
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
    RendererCommand renderHeaderBodyDivider(final double width);

    /**
     * Renders a boundary around the grid.
     * @param context The context of the render phase.
     * @return A command that adds the grids "boundary".
     */
    RendererCommand renderGridBoundary(final GridBoundaryRenderContext context);

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

    /**
     * Sets the constraint to control rendering of columns. The default implementation does not render to
     * the SelectionLayer as a performance optimisation and only their basic definition, e.g. size, background
     * colour, grid is rendered to support selection. Nested tables need to be rendered to the SelectionLayer
     * to support their selection and other User interactions.
     * @param columnRenderingConstraint
     */
    void setColumnRenderConstraint(final BiFunction<Boolean, GridColumn<?>, Boolean> columnRenderingConstraint);
}
