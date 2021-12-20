/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.List;

import com.google.gwt.event.dom.client.KeyCodes;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

public class KeyboardOperationInvokeContextMenuForSelectedCell extends BaseKeyboardOperation {

    public KeyboardOperationInvokeContextMenuForSelectedCell(final GridLayer gridLayer) {
        super(gridLayer);
    }

    @Override
    public TriStateBoolean isControlKeyDown() {
        return TriStateBoolean.TRUE;
    }

    @Override
    public int getKeyCode() {
        return KeyCodes.KEY_SPACE;
    }

    @Override
    public boolean isExecutable(final GridWidget gridWidget) {
        final GridData model = gridWidget.getModel();
        return model.getSelectedHeaderCells().size() > 0 || model.getSelectedCells().size() > 0;
    }

    @Override
    @SuppressWarnings("unused")
    public boolean perform(final GridWidget gridWidget,
                           final boolean isShiftKeyDown,
                           final boolean isControlKeyDown) {
        final GridData model = gridWidget.getModel();
        if (model.getSelectedHeaderCells().size() > 0) {
            return performHeaderOperation(gridWidget);
        } else if (model.getSelectedCells().size() > 0) {
            return performBodyOperation(gridWidget);
        }
        return false;
    }

    protected boolean performHeaderOperation(final GridWidget gridWidget) {
        final GridData model = gridWidget.getModel();
        final List<GridData.SelectedCell> selectedHeaderCells = model.getSelectedHeaderCells();
        if (selectedHeaderCells.isEmpty()) {
            return false;
        }
        final GridData.SelectedCell firstSelectedHeaderCell = selectedHeaderCells.get(0);
        final int headerRowIndex = firstSelectedHeaderCell.getRowIndex();
        final int headerColumnIndex = ColumnIndexUtilities.findUiColumnIndex(model.getColumns(),
                                                                             firstSelectedHeaderCell.getColumnIndex());
        return gridWidget.showContextMenuForHeader(headerRowIndex, headerColumnIndex);
    }

    protected boolean performBodyOperation(final GridWidget gridWidget) {
        final GridData model = gridWidget.getModel();
        final GridData.SelectedCell origin = model.getSelectedCellsOrigin();
        if (origin == null) {
            return false;
        }
        final int rowIndex = origin.getRowIndex();
        final int columnIndex = ColumnIndexUtilities.findUiColumnIndex(model.getColumns(),
                                                                       origin.getColumnIndex());
        return gridWidget.showContextMenuForCell(rowIndex, columnIndex);
    }
}
