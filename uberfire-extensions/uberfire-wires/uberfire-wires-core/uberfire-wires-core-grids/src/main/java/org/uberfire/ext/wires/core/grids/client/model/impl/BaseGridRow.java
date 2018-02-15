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
package org.uberfire.ext.wires.core.grids.client.model.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

/**
 * Base implementation of a grid row to avoid boiler-plate for more specific implementations.
 */
public class BaseGridRow implements GridRow {

    protected double height = 20.0;
    protected Map<Integer, GridCell<?>> cells = new HashMap<Integer, GridCell<?>>();

    private boolean hasMergedCells = false;
    private Stack<Double> heights = new Stack<Double>();
    private int collapseLevel = 0;

    public BaseGridRow() {
        this(20);
    }

    public BaseGridRow(final double height) {
        this.height = height;
        this.heights.push(height);
    }

    @Override
    public Map<Integer, GridCell<?>> getCells() {
        return Collections.unmodifiableMap(cells);
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public void setHeight(final double height) {
        this.height = height;
    }

    @Override
    public double peekHeight() {
        return heights.peek();
    }

    @Override
    public boolean isMerged() {
        return hasMergedCells;
    }

    @Override
    public boolean isCollapsed() {
        return collapseLevel > 0;
    }

    @Override
    public void collapse() {
        collapseLevel++;
        heights.push(height);
        for (GridCell<?> cell : cells.values()) {
            cell.collapse();
        }
    }

    @Override
    public void expand() {
        if (collapseLevel == 0) {
            return;
        }
        collapseLevel--;
        height = heights.pop();
        for (GridCell<?> cell : cells.values()) {
            cell.expand();
        }
    }

    @Override
    public void reset() {
        collapseLevel = 0;
        hasMergedCells = false;
        height = heights.firstElement();
        heights.clear();
        heights.push(height);
        for (GridCell<?> cell : cells.values()) {
            cell.reset();
        }
    }

    //This is not part of the GridCell interface as we don't want to expose this for general use
    @SuppressWarnings("unchecked")
    void setCell(final int columnIndex,
                 final GridCell cell) {
        cells.put(columnIndex,
                  cell);
    }

    //This is not part of the GridCell interface as we don't want to expose this for general use
    void deleteCell(final int columnIndex) {
        cells.remove(columnIndex);
    }

    //This is not part of the GridCell interface as we don't want to expose this for general use
    void setHasMergedCells(final boolean hasMergedCells) {
        this.hasMergedCells = hasMergedCells;
    }
}
