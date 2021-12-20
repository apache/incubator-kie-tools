/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;

public class HeaderSingleCellSelectionStrategy extends BaseCellSelectionStrategy {

    public static CellSelectionStrategy INSTANCE = new HeaderSingleCellSelectionStrategy();

    @Override
    public boolean handleSelection(final GridData model,
                                   final int uiHeaderRowIndex,
                                   final int uiHeaderColumnIndex,
                                   final boolean isShiftKeyDown,
                                   final boolean isControlKeyDown) {
        final List<GridData.SelectedCell> originalSelections = new ArrayList<>(model.getSelectedHeaderCells());

        //Get extents of block for Header cell
        final List<GridColumn<?>> gridColumns = model.getColumns();
        final List<GridColumn.HeaderMetaData> headerMetaData = gridColumns.get(uiHeaderColumnIndex).getHeaderMetaData();
        final int blockStartColumnIndex = ColumnIndexUtilities.getHeaderBlockStartColumnIndex(gridColumns,
                                                                                              headerMetaData.get(uiHeaderRowIndex),
                                                                                              uiHeaderRowIndex,
                                                                                              uiHeaderColumnIndex);
        final int blockEndColumnIndex = ColumnIndexUtilities.getHeaderBlockEndColumnIndex(gridColumns,
                                                                                          headerMetaData.get(uiHeaderRowIndex),
                                                                                          uiHeaderRowIndex,
                                                                                          uiHeaderColumnIndex);

        model.clearSelections();
        IntStream.range(blockStartColumnIndex, blockEndColumnIndex + 1)
                .forEach(blockColumnIndex -> model.selectHeaderCell(uiHeaderRowIndex,
                                                                    blockColumnIndex));

        return hasSelectionChanged(model.getSelectedHeaderCells(),
                                   originalSelections);
    }
}
