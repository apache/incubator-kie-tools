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

import com.google.gwt.user.client.ui.RequiresResize;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

public class GridCellTuple implements RequiresResize {

    private int rowIndex;
    private int columnIndex;
    private final GridWidget gridWidget;

    public GridCellTuple(final int rowIndex,
                         final int columnIndex,
                         final GridWidget gridWidget) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.gridWidget = gridWidget;
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

    public GridWidget getGridWidget() {
        return gridWidget;
    }

    public void proposeContainingColumnWidth(final double proposedWidth) {
        final GridColumn<?> parentColumn = gridWidget.getModel().getColumns().get(columnIndex);
        final double requiredWidth = Math.max(proposedWidth, parentColumn.getWidth());
        parentColumn.setWidth(requiredWidth);
    }

    @Override
    public void onResize() {
        //This may look like it does nothing; however it forces the column to resize it's children
        final GridColumn<?> parentColumn = gridWidget.getModel().getColumns().get(columnIndex);
        parentColumn.setWidth(parentColumn.getWidth());
    }
}
