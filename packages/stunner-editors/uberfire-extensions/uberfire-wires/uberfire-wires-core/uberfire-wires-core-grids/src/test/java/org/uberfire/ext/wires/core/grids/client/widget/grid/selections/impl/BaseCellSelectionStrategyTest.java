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

import org.junit.Before;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridTest;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;

public abstract class BaseCellSelectionStrategyTest {

    protected CellSelectionStrategy strategy;

    protected GridData uiModel;

    protected GridColumn<String> gc1;
    protected GridColumn<String> gc2;

    @Before
    public void setup() {
        this.strategy = getStrategy();
        this.uiModel = new BaseGridData();

        gc1 = new BaseGridTest.MockMergableGridColumn<>("col1",
                                                        100);
        gc2 = new BaseGridTest.MockMergableGridColumn<>("col2",
                                                        100);
        uiModel.appendColumn(gc1);
        uiModel.appendColumn(gc2);

        uiModel.appendRow(new BaseGridRow());
        uiModel.appendRow(new BaseGridRow());
        uiModel.appendRow(new BaseGridRow());
        uiModel.appendRow(new BaseGridRow());

        // [0,0 : a0] [0,1 : a1]
        // [1,0 : a0] [1,1 : a1]
        // [2,0 : b0] [2,1 : b1]
        // [3,0 : b0] [3,1 : b1]

        for (int rowIndex = 0; rowIndex < uiModel.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < uiModel.getColumnCount(); columnIndex++) {
                final String value = (rowIndex < 2 ? "a" : "b") + columnIndex;
                uiModel.setCellValue(rowIndex,
                                     columnIndex,
                                     new BaseGridCellValue<>(value));
            }
        }
    }

    protected abstract CellSelectionStrategy getStrategy();
}
