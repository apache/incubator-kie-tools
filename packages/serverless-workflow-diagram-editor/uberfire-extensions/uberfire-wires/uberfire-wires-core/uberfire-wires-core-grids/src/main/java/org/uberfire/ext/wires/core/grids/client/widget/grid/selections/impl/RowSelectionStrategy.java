/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;

public class RowSelectionStrategy extends BaseCellSelectionStrategy {

    public static CellSelectionStrategy INSTANCE = new RowSelectionStrategy();

    @Override
    public boolean handleSelection(final GridData model,
                                   final int uiRowIndex,
                                   final int uiColumnIndex,
                                   final boolean isShiftKeyDown,
                                   final boolean isControlKeyDown) {
        //Remember origin which is required if we're selecting a range with the shift-key pressed
        final GridData.SelectedCell selectedCellsOrigin = model.getSelectedCellsOrigin();
        final List<GridData.SelectedCell> originalSelections = new ArrayList<GridData.SelectedCell>(model.getSelectedCells());

        //If the Control Key is pressed add additional cells to the selection
        if (!isControlKeyDown) {
            model.clearSelections();
        } else {
            model.getSelectedHeaderCells().clear();
        }

        if (isShiftKeyDown) {
            if (selectedCellsOrigin == null) {
                selectRow(model,
                          uiRowIndex);
            } else {
                model.selectCell(selectedCellsOrigin.getRowIndex(),
                                 selectedCellsOrigin.getColumnIndex());
                final int uiOriginRowIndex = selectedCellsOrigin.getRowIndex();
                selectRows(model,
                           (uiRowIndex > uiOriginRowIndex ? uiOriginRowIndex : uiRowIndex),
                           Math.abs(uiRowIndex - uiOriginRowIndex) + 1);
            }
        } else {
            selectRow(model,
                      uiRowIndex);
        }

        return hasSelectionChanged(model.getSelectedCells(),
                                   originalSelections);
    }

    private void selectRow(final GridData model,
                           final int uiRowIndex) {
        model.selectCells(uiRowIndex,
                          0,
                          model.getColumnCount(),
                          1);
    }

    private void selectRows(final GridData model,
                            final int uiRowIndex,
                            final int height) {
        model.selectCells(uiRowIndex,
                          0,
                          model.getColumnCount(),
                          height);
    }
}
