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

import java.util.List;

import org.junit.Test;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridTest;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HeaderSingleCellSelectionStrategyTest extends BaseCellSelectionStrategyTest {

    @Override
    protected CellSelectionStrategy getStrategy() {
        return new HeaderSingleCellSelectionStrategy();
    }

    @Test
    public void testSingleHeaderCell() {
        //MetaDataGroups: [headerInstance1][headerInstance2]
        assertTrue(strategy.handleSelection(uiModel,
                                            0,
                                            0,
                                            false,
                                            false));

        assertCellSelections(0, 0, 1);

        assertTrue(strategy.handleSelection(uiModel,
                                            0,
                                            1,
                                            false,
                                            false));

        assertCellSelections(0, 1, 1);
    }

    @Test
    public void testBlockHeaderCell() {
        //MetaDataGroups: [headerInstance1][headerInstance1][headerInstance2][headerInstance2]
        final GridColumn<?> gc3 = new BaseGridTest.MockMergableGridColumn<>("col3", 100);
        final GridColumn<?> gc4 = new BaseGridTest.MockMergableGridColumn<>("col4", 100);
        uiModel.appendColumn(gc3);
        uiModel.appendColumn(gc4);

        gc1.getHeaderMetaData().set(0, gc2.getHeaderMetaData().get(0));
        gc3.getHeaderMetaData().set(0, gc4.getHeaderMetaData().get(0));

        assertTrue(strategy.handleSelection(uiModel,
                                            0,
                                            0,
                                            false,
                                            false));

        assertCellSelections(0, 0, 2);

        assertTrue(strategy.handleSelection(uiModel,
                                            0,
                                            2,
                                            false,
                                            false));

        assertCellSelections(0, 2, 2);
    }

    private void assertCellSelections(final int headerRowIndex,
                                      final int headerColumnIndex,
                                      final int selectedCellCount) {
        final List<GridData.SelectedCell> selectedCells = uiModel.getSelectedCells();
        final List<GridData.SelectedCell> selectedHeaderCells = uiModel.getSelectedHeaderCells();
        assertEquals(0,
                     selectedCells.size());
        assertEquals(selectedCellCount,
                     selectedHeaderCells.size());
        assertTrue(selectedHeaderCells.contains(new GridData.SelectedCell(headerRowIndex,
                                                                          headerColumnIndex)));
    }
}
