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

package org.kie.workbench.common.dmn.client.widgets.grid.model;

import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

public class GridCellTuple implements RequiresResize {

    private int rowIndex;
    private int columnIndex;
    private final GridData uiModel;

    public static GridCellTuple make(final int rowIndex,
                                     final int columnIndex,
                                     final GridData uiModel) {
        return new GridCellTuple(rowIndex,
                                 columnIndex,
                                 uiModel);
    }

    public GridCellTuple(final int rowIndex,
                         final int columnIndex,
                         final GridData uiModel) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.uiModel = uiModel;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(final int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(final int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public GridData getGridData() {
        return uiModel;
    }

    public void assertWidth(final double width) {
        uiModel.getColumns().get(columnIndex).setWidth(width);
    }

    @Override
    public void onResize() {
        //This may look like it does nothing; however it forces the column to resize it's children
        final GridColumn<?> parentColumn = uiModel.getColumns().get(columnIndex);
        parentColumn.setWidth(parentColumn.getWidth());
    }
}
