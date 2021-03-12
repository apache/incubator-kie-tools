/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.Comparator;
import java.util.Optional;

import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

public class GridRowComparator
        implements Comparator<GridRow> {

    private final GridColumn gridColumn;

    GridRowComparator(final GridColumn gridColumn) {
        this.gridColumn = gridColumn;
    }

    @Override
    public int compare(final GridRow gridRow,
                       final GridRow otherGridRow) {

        final Optional<Object> cellValue = getValue(gridRow);
        final Optional<Object> otherCellValue = getValue(otherGridRow);

        if (!cellValue.isPresent() && !otherCellValue.isPresent()) {
            return 0;
        } else if (!cellValue.isPresent()) {
            return -1;
        } else if (!otherCellValue.isPresent()) {
            return 1;
        }

        return compareValues(cellValue.get(),
                             otherCellValue.get());
    }

    private int compareValues(final Object cellValue,
                              final Object otherCellValue) {
        if (cellValue instanceof Comparable
                && otherCellValue instanceof Comparable) {

            return ((Comparable) cellValue).compareTo(otherCellValue);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private Optional<Object> getValue(final GridRow gridRow) {
        final GridCell<?> gridCell = gridRow.getCells().get(gridColumn.getIndex());
        if (gridCell == null) {
            return Optional.empty();
        } else if (gridCell.getValue() == null) {
            return Optional.empty();
        } else {
            return Optional.of(gridCell.getValue().getValue());
        }
    }
}
