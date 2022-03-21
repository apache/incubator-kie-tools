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

package org.uberfire.ext.wires.core.grids.client.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridTest;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;

import static org.junit.Assert.assertEquals;

public class ColumnIndexUtilitiesTest {

    private static final int COLUMN_COUNT = 4;

    private List<GridColumn<?>> columns;

    @Before
    public void setup() {
        this.columns = new ArrayList<>();
        for (int index = 0; index < COLUMN_COUNT; index++) {
            final GridColumn<String> column = new BaseGridTest.MockMergableGridColumn<>("col1",
                                                                                        100);
            column.setIndex(COLUMN_COUNT - index - 1);
            columns.add(column);
        }
    }

    @Test
    public void testFindUiColumnIndex() {
        assertEquals(3,
                     ColumnIndexUtilities.findUiColumnIndex(columns,
                                                            0));
        assertEquals(2,
                     ColumnIndexUtilities.findUiColumnIndex(columns,
                                                            1));
        assertEquals(1,
                     ColumnIndexUtilities.findUiColumnIndex(columns,
                                                            2));
        assertEquals(0,
                     ColumnIndexUtilities.findUiColumnIndex(columns,
                                                            3));
    }

    @Test
    public void testGetHeaderBlockStartColumnIndex() {
        //MetaDataGroups: [""][""][""][""]
        assertEquals(0,
                     ColumnIndexUtilities.getHeaderBlockStartColumnIndex(columns,
                                                                         columns.get(3).getHeaderMetaData().get(0),
                                                                         0,
                                                                         3));
    }

    @Test
    public void testGetHeaderBlockStartColumnIndexWithSplitBlock() {
        //MetaDataGroups: [""]["new-group"][""][""]
        columns.get(1).getHeaderMetaData().get(0).setColumnGroup("new-group");
        assertEquals(2,
                     ColumnIndexUtilities.getHeaderBlockStartColumnIndex(columns,
                                                                         columns.get(3).getHeaderMetaData().get(0),
                                                                         0,
                                                                         3));
    }

    @Test
    public void testGetHeaderBlockEndColumnIndex() {
        //MetaDataGroups: [""][""][""][""]
        assertEquals(3,
                     ColumnIndexUtilities.getHeaderBlockEndColumnIndex(columns,
                                                                       columns.get(0).getHeaderMetaData().get(0),
                                                                       0,
                                                                       0));
    }

    @Test
    public void testGetHeaderBlockEndColumnIndexWithSplitBlock() {
        //MetaDataGroups: [""][""]["new-group"][""]
        columns.get(2).getHeaderMetaData().get(0).setColumnGroup("new-group");
        assertEquals(1,
                     ColumnIndexUtilities.getHeaderBlockEndColumnIndex(columns,
                                                                       columns.get(0).getHeaderMetaData().get(0),
                                                                       0,
                                                                       0));
    }

    @Test
    public void testGetHeaderBlockEndColumnIndexWithIndexOutOfBounds() {
        //MetaDataGroups: [""][""][""][""]
        assertEquals(3,
                     ColumnIndexUtilities.getHeaderBlockEndColumnIndex(columns,
                                                                       columns.get(0).getHeaderMetaData().get(0),
                                                                       0,
                                                                       4));
    }

    @Test
    public void testGetHeaderBlockEndColumnIndexWithIndexOutOfBounds2() {
        //MetaDataGroups: [""][""][""][""]
        assertEquals(3,
                     ColumnIndexUtilities.getHeaderBlockEndColumnIndex(columns,
                                                                       columns.get(0).getHeaderMetaData().get(0),
                                                                       0,
                                                                       100));
    }

    @Test
    public void testGetUiHeaderRowIndex() {
        final BaseGridData model = new BaseGridData();
        columns.forEach(col -> model.appendColumn(col));

        assertEquals(0, ColumnIndexUtilities.getMaxUiHeaderRowIndexOfColumn(model, 0));

        columns.get(1).getHeaderMetaData().add(new BaseHeaderMetaData("col1", "second-row"));

        assertEquals(1, ColumnIndexUtilities.getMaxUiHeaderRowIndexOfColumn(model, 1));
    }
}
