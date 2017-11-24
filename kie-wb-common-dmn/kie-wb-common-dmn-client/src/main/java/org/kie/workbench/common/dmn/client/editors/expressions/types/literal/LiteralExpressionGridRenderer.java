/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.List;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridRenderer;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

public class LiteralExpressionGridRenderer extends BaseExpressionGridRenderer {

    public LiteralExpressionGridRenderer(final boolean hideHeader) {
        super(hideHeader);
    }

    @Override
    public Group renderHeader(final GridData model,
                              final GridHeaderRenderContext context,
                              final BaseGridRendererHelper rendererHelper,
                              final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        if (hideHeader) {
            return new Group();
        }

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

        //Column title and grid lines
        x = 0;
        for (final GridColumn<?> column : visibleBlockColumns) {
            if (column.isVisible()) {
                final double columnWidth = column.getWidth();

                //Don't render the Body's detail if we're rendering the SelectionLayer
                if (columnRenderingConstraint.apply(isSelectionLayer,
                                                    column)) {

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
                }
                x = x + columnWidth;
            }
        }

        //Divider between header and body
        final Group divider = renderHeaderBodyDivider(x);
        g.add(divider);

        return g;
    }
}