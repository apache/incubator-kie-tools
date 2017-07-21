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
package org.uberfire.ext.wires.client.demo.grids;

import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;

/**
 * Utility class to fill the example grids in this Showcase demo
 */
public class GridDataFactory {

    public static double FILL_FACTOR = 0.75;

    /**
     * Populate a non-merged grid. Columns should already have been appended.
     * @param grid The grid to populate
     * @param rowCount The number of required rows
     */
    public static void populate(final GridData grid,
                                final int rowCount) {
        final int columnCount = grid.getColumnCount();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            final GridRow row = new BaseGridRow(getRowHeight());
            grid.appendRow(row);
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                final GridColumn<?> column = grid.getColumns().get(columnIndex);
                if (column instanceof RowNumberColumn) {
                    grid.setCell(rowIndex,
                                 columnIndex,
                                 new BaseGridCellValue<Integer>(rowIndex + 1));
                    grid.getCell(rowIndex,
                                 columnIndex).setSelectionManager(RowSelectionStrategy.INSTANCE);
                } else if (Math.random() < FILL_FACTOR) {
                    grid.setCell(rowIndex,
                                 columnIndex,
                                 new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
                }
            }
        }
    }

    //Pick one of three random row heights
    private static double getRowHeight() {
        final int r = (int) Math.round(Math.random() * 3);
        switch (r) {
            case 0:
                return 20.0;
            case 1:
                return 40.0;
        }
        return 60.0;
    }
}
