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

import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.IntegerUiColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class GridRowComparatorTest {

    @Mock
    private GridColumn column;

    private GridRowComparator comparator;
    private BaseGridData baseGridData;

    @Before
    public void setUp() throws Exception {
        baseGridData = new BaseGridData();
        doReturn(0).when(column).getIndex();
        comparator = new GridRowComparator(column);
    }

    @Test
    public void testIntegerColumn() {
        baseGridData.appendColumn(mock(IntegerUiColumn.class));

        final BaseGridRow a = new BaseGridRow();
        baseGridData.appendRow(a);
        final BaseGridRow b = new BaseGridRow();
        baseGridData.appendRow(b);
        final BaseGridRow c = new BaseGridRow();
        baseGridData.appendRow(c);
        final BaseGridRow d = new BaseGridRow();
        baseGridData.appendRow(d);

        baseGridData.setCell(0, 0, () -> new BaseGridCell<>(new BaseGridCellValue<>(100)));
        baseGridData.setCell(1, 0, () -> new BaseGridCell<>(new BaseGridCellValue<>(100)));
        baseGridData.setCell(2, 0, () -> new BaseGridCell<>(new BaseGridCellValue<>(0)));
        baseGridData.setCell(3, 0, () -> new BaseGridCell<>(null));

        assertTrue(comparator.compare(a, b) == 0);
        assertTrue(comparator.compare(a, c) > 0);
        assertTrue(comparator.compare(c, a) < 0);
        assertTrue(comparator.compare(c, d) > 0);
        assertTrue(comparator.compare(d, c) < 0);
    }
}