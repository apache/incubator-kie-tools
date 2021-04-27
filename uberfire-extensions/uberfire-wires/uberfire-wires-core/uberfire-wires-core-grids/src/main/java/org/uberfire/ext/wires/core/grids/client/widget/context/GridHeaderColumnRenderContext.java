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
package org.uberfire.ext.wires.core.grids.client.widget.context;

import java.util.List;

import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;

/**
 * The context of a Grid's cell header during the rendering phase.
 */
public class GridHeaderColumnRenderContext {

    private final double x;
    private final List<GridColumn<?>> allColumns;
    private final List<GridColumn<?>> blockColumns;
    private final int columnIndex;
    private final GridData model;
    private final GridRenderer renderer;

    public GridHeaderColumnRenderContext(final double x,
                                         final List<GridColumn<?>> allColumns,
                                         final List<GridColumn<?>> blockColumns,
                                         final int columnIndex,
                                         final GridData model,
                                         final GridRenderer renderer) {
        this.x = x;
        this.allColumns = allColumns;
        this.blockColumns = blockColumns;
        this.columnIndex = columnIndex;
        this.model = model;
        this.renderer = renderer;
    }

    /**
     * Returns the columns x-coordinate relative to the grids origin.
     * @return
     */
    public double getX() {
        return this.x;
    }

    /**
     * Returns all columns in the block, some of which may not be visible or rendered within the Viewport.
     * @return
     */
    public List<GridColumn<?>> getAllColumns() {
        return allColumns;
    }

    /**
     * Returns the columns to render for the block.
     * @return
     */
    public List<GridColumn<?>> getBlockColumns() {
        return blockColumns;
    }

    /**
     * Returns the index of the column, in BlockColumns, being rendered.
     * @return
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    /**
     * Returns the data model for the Grid Widget to which the Column relates.
     * @return
     */
    public GridData getModel() {
        return model;
    }

    /**
     * Returns the Renderer for the Grid Widget.
     * @return
     */
    public GridRenderer getRenderer() {
        return renderer;
    }
}
