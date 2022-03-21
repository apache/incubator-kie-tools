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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.wires.core.grids.client.model.GridData.SelectedCell;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RangeSelectionStrategyUnmergedDataTest extends BaseCellSelectionStrategyTest {

    @Before
    @Override
    public void setup() {
        super.setup();
        uiModel.setMerged(false);
    }

    @Override
    protected CellSelectionStrategy getStrategy() {
        return new RangeSelectionStrategy();
    }

    @Test
    public void singleCellSelection() {
        strategy.handleSelection(uiModel,
                                 0,
                                 0,
                                 false,
                                 false);

        final List<SelectedCell> selectedCells = uiModel.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new SelectedCell(0,
                                                           0)));
    }

    @Test
    public void extendSelectionWithShiftKey() {
        strategy.handleSelection(uiModel,
                                 0,
                                 0,
                                 false,
                                 false);
        strategy.handleSelection(uiModel,
                                 2,
                                 1,
                                 true,
                                 false);

        final List<SelectedCell> selectedCells = uiModel.getSelectedCells();
        assertEquals(6,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new SelectedCell(0,
                                                           0)));
        assertTrue(selectedCells.contains(new SelectedCell(1,
                                                           0)));
        assertTrue(selectedCells.contains(new SelectedCell(2,
                                                           0)));
        assertTrue(selectedCells.contains(new SelectedCell(0,
                                                           1)));
        assertTrue(selectedCells.contains(new SelectedCell(1,
                                                           1)));
        assertTrue(selectedCells.contains(new SelectedCell(2,
                                                           1)));
    }

    @Test
    public void extendSelectionWithControlKey() {
        strategy.handleSelection(uiModel,
                                 0,
                                 0,
                                 false,
                                 false);
        strategy.handleSelection(uiModel,
                                 2,
                                 1,
                                 false,
                                 true);

        final List<SelectedCell> selectedCells = uiModel.getSelectedCells();
        assertEquals(2,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new SelectedCell(0,
                                                           0)));
        assertTrue(selectedCells.contains(new SelectedCell(2,
                                                           1)));
    }

    @Test
    public void extendSelectionWithColumnMovedWithShiftKey() {
        uiModel.moveColumnTo(0,
                             gc2);
        strategy.handleSelection(uiModel,
                                 0,
                                 0,
                                 false,
                                 false);
        strategy.handleSelection(uiModel,
                                 2,
                                 1,
                                 true,
                                 false);

        final List<SelectedCell> selectedCells = uiModel.getSelectedCells();
        assertEquals(6,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new SelectedCell(0,
                                                           0)));
        assertTrue(selectedCells.contains(new SelectedCell(1,
                                                           0)));
        assertTrue(selectedCells.contains(new SelectedCell(2,
                                                           0)));
        assertTrue(selectedCells.contains(new SelectedCell(0,
                                                           1)));
        assertTrue(selectedCells.contains(new SelectedCell(1,
                                                           1)));
        assertTrue(selectedCells.contains(new SelectedCell(2,
                                                           1)));
    }

    @Test
    public void selectCellWithShiftKeyWithHeaderSelected() {
        uiModel.selectHeaderCell(0, 0);

        final List<SelectedCell> selectedHeaderCells = uiModel.getSelectedHeaderCells();
        assertEquals(1,
                     selectedHeaderCells.size());
        assertTrue(selectedHeaderCells.contains(new SelectedCell(0,
                                                                 0)));

        strategy.handleSelection(uiModel,
                                 0,
                                 0,
                                 true,
                                 false);

        final List<SelectedCell> selectedCells = uiModel.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new SelectedCell(0,
                                                           0)));
        assertTrue(uiModel.getSelectedHeaderCells().isEmpty());
    }

    @Test
    public void selectCellWithControlKeyWithHeaderSelected() {
        uiModel.selectHeaderCell(0, 0);

        final List<SelectedCell> selectedHeaderCells = uiModel.getSelectedHeaderCells();
        assertEquals(1,
                     selectedHeaderCells.size());
        assertTrue(selectedHeaderCells.contains(new SelectedCell(0,
                                                                 0)));

        strategy.handleSelection(uiModel,
                                 0,
                                 0,
                                 false,
                                 true);

        final List<SelectedCell> selectedCells = uiModel.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new SelectedCell(0,
                                                           0)));
        assertTrue(uiModel.getSelectedHeaderCells().isEmpty());
    }
}
