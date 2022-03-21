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

import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RangeSelectionStrategy;

/**
 * Base implementation of a grid cell to avoid boiler-plate for more specific implementations.
 * @param <T> The Type of value
 */
public class BaseGridCell<T> implements GridCell<T> {

    protected GridCellValue<T> value;
    private int collapseLevel = 0;
    private int mergedCellCount = 1;
    private CellSelectionStrategy selectionStrategy = RangeSelectionStrategy.INSTANCE;

    public BaseGridCell(final GridCellValue<T> value) {
        this.value = value;
    }

    @Override
    public GridCellValue<T> getValue() {
        return value;
    }

    //This is not part of the GridCell interface as we don't want to expose this for general use
    protected void setValue(final GridCellValue<T> value) {
        this.value = value;
    }

    @Override
    public boolean isMerged() {
        return getMergedCellCount() != 1;
    }

    @Override
    public int getMergedCellCount() {
        return mergedCellCount;
    }

    //This is not part of the GridCell interface as we don't want to expose this for general use
    void setMergedCellCount(final int mergedCellCount) {
        this.mergedCellCount = mergedCellCount;
    }

    @Override
    public boolean isCollapsed() {
        return collapseLevel > 0;
    }

    @Override
    public void collapse() {
        collapseLevel++;
    }

    @Override
    public void expand() {
        collapseLevel--;
    }

    @Override
    public void reset() {
        mergedCellCount = 1;
        collapseLevel = 0;
    }

    @Override
    public CellSelectionStrategy getSelectionStrategy() {
        return selectionStrategy;
    }

    @Override
    public void setSelectionStrategy(final CellSelectionStrategy selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseGridCell)) {
            return false;
        }

        BaseGridCell<?> that = (BaseGridCell<?>) o;

        return !(value != null ? !value.equals(that.value) : that.value != null);
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        return result;
    }
}
