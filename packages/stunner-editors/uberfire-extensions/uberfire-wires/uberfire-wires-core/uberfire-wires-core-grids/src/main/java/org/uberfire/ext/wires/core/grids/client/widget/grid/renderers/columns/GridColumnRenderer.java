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
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns;

import java.util.List;
import java.util.function.BiFunction;

import com.ait.lienzo.client.core.shape.Group;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

public interface GridColumnRenderer<T> {

    /**
     * Renders the column's Header..
     * @param headerMetaData MetaData for the header
     * @param context Contextual information to support rendering
     * @param renderingInformation Calculated rendering information supporting rendering.
     * @param columnRenderingConstraint Function to determine whether column should be rendered to the SelectionLayer
     * @return
     */
    List<GridRenderer.RendererCommand> renderHeader(final List<GridColumn.HeaderMetaData> headerMetaData,
                                                    final GridHeaderColumnRenderContext context,
                                                    final BaseGridRendererHelper.RenderingInformation renderingInformation,
                                                    final BiFunction<Boolean, GridColumn<?>, Boolean> columnRenderingConstraint);

    /**
     * Renders the column's Header content.
     * @param headerMetaData MetaData for the header
     * @param context Contextual information to support rendering
     * @param headerRowIndex Index of the header row being rendered. Zero based.
     * @param blockWidth Width of the block of 'equal' header meta data objects.
     * @param rowHeight Height of the header row.
     * @return
     */
    Group renderHeaderContent(final List<GridColumn.HeaderMetaData> headerMetaData,
                              final GridHeaderColumnRenderContext context,
                              final int headerRowIndex,
                              final double blockWidth,
                              final double rowHeight);

    /**
     * Renders the column.textual information to support rendering
     * @param column The column to render
     * @param context Contextual information to support rendering
     * @param rendererHelper Helper for rendering.
     * @param renderingInformation Calculated rendering information supporting rendering.
     * @param columnRenderingConstraint Function to determine whether column should be rendered to the SelectionLayer
     * @return
     */
    List<GridRenderer.RendererCommand> renderColumn(final GridColumn<?> column,
                                                    final GridBodyColumnRenderContext context,
                                                    final BaseGridRendererHelper rendererHelper,
                                                    final BaseGridRendererHelper.RenderingInformation renderingInformation,
                                                    final BiFunction<Boolean, GridColumn<?>, Boolean> columnRenderingConstraint);

    /**
     * Renders a cell for the column for a row. Normally a column would use its logical index
     * to retrieve the corresponding element from the row to be rendered.
     * @param cell The cell to render
     * @param context Contextual information to support rendering
     * @return
     */
    Group renderCell(final GridCell<T> cell,
                     final GridBodyCellRenderContext context);
}
