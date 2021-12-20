/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.wires.core.grids.client.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

import static org.junit.Assert.assertEquals;

public class GridColumnIndexingTest extends BaseGridTest {

    @Test
    public void testAddInitialColumns() {
        final GridData grid = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        grid.appendColumn(gc1);
        grid.appendColumn(gc2);

        final List<GridColumn<?>> columns = grid.getColumns();

        assertEquals(2,
                     columns.size());
        assertEquals(0,
                     gc1.getIndex());
        assertEquals(1,
                     gc2.getIndex());
        assertEquals(columns.get(0),
                     gc1);
        assertEquals(columns.get(1),
                     gc2);
    }

    @Test
    public void testAddColumn() {
        final GridData grid = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        grid.appendColumn(gc1);
        grid.appendColumn(gc2);
        grid.insertColumn(1,
                          gc3);

        final List<GridColumn<?>> columns = grid.getColumns();

        assertEquals(3,
                     columns.size());
        assertEquals(0,
                     gc1.getIndex());
        assertEquals(1,
                     gc2.getIndex());
        assertEquals(2,
                     gc3.getIndex());
        assertEquals(columns.get(0),
                     gc1);
        assertEquals(columns.get(1),
                     gc3);
        assertEquals(columns.get(2),
                     gc2);
    }

    @Test
    public void testRemoveColumn() {
        final GridData grid = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        grid.appendColumn(gc1);
        grid.appendColumn(gc2);
        grid.appendColumn(gc3);

        grid.deleteColumn(gc2);

        final List<GridColumn<?>> columns = grid.getColumns();

        assertEquals(2,
                     columns.size());
        assertEquals(0,
                     gc1.getIndex());
        assertEquals(1,
                     gc3.getIndex());
        assertEquals(columns.get(0),
                     gc1);
        assertEquals(columns.get(1),
                     gc3);
    }

    @Test
    public void testMoveColumnToLeft() {
        final GridData grid = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        final GridColumn<String> gc4 = new MockMergableGridColumn<String>("col4",
                                                                          100);
        grid.appendColumn(gc1);
        grid.appendColumn(gc2);
        grid.appendColumn(gc3);
        grid.appendColumn(gc4);

        grid.moveColumnTo(1,
                          gc4);

        final List<GridColumn<?>> columns = grid.getColumns();

        assertEquals(4,
                     columns.size());
        assertEquals(0,
                     gc1.getIndex());
        assertEquals(1,
                     gc2.getIndex());
        assertEquals(2,
                     gc3.getIndex());
        assertEquals(3,
                     gc4.getIndex());
        assertEquals(columns.get(0),
                     gc1);
        assertEquals(columns.get(1),
                     gc4);
        assertEquals(columns.get(2),
                     gc2);
        assertEquals(columns.get(3),
                     gc3);
    }

    @Test
    public void testMoveColumnsToLeft_RightMostTwoColumns() {
        final GridData grid = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        final GridColumn<String> gc4 = new MockMergableGridColumn<String>("col4",
                                                                          100);
        grid.appendColumn(gc1);
        grid.appendColumn(gc2);
        grid.appendColumn(gc3);
        grid.appendColumn(gc4);

        grid.moveColumnsTo(1,
                           new ArrayList<GridColumn<?>>() {{
                               add(gc3);
                               add(gc4);
                           }});

        final List<GridColumn<?>> columns = grid.getColumns();

        assertEquals(4,
                     columns.size());
        assertEquals(0,
                     gc1.getIndex());
        assertEquals(1,
                     gc2.getIndex());
        assertEquals(2,
                     gc3.getIndex());
        assertEquals(3,
                     gc4.getIndex());
        assertEquals(columns.get(0),
                     gc1);
        assertEquals(columns.get(1),
                     gc3);
        assertEquals(columns.get(2),
                     gc4);
        assertEquals(columns.get(3),
                     gc2);
    }

    @Test
    public void testMoveColumnsToLeft_MiddleTwoColumns() {
        final GridData grid = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        final GridColumn<String> gc4 = new MockMergableGridColumn<String>("col4",
                                                                          100);
        grid.appendColumn(gc1);
        grid.appendColumn(gc2);
        grid.appendColumn(gc3);
        grid.appendColumn(gc4);

        grid.moveColumnsTo(0,
                           new ArrayList<GridColumn<?>>() {{
                               add(gc2);
                               add(gc3);
                           }});

        final List<GridColumn<?>> columns = grid.getColumns();

        assertEquals(4,
                     columns.size());
        assertEquals(0,
                     gc1.getIndex());
        assertEquals(1,
                     gc2.getIndex());
        assertEquals(2,
                     gc3.getIndex());
        assertEquals(3,
                     gc4.getIndex());
        assertEquals(columns.get(0),
                     gc2);
        assertEquals(columns.get(1),
                     gc3);
        assertEquals(columns.get(2),
                     gc1);
        assertEquals(columns.get(3),
                     gc4);
    }

    @Test
    public void testMoveColumnToRight_LeftMostColumn() {
        final GridData grid = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        final GridColumn<String> gc4 = new MockMergableGridColumn<String>("col4",
                                                                          100);
        grid.appendColumn(gc1);
        grid.appendColumn(gc2);
        grid.appendColumn(gc3);
        grid.appendColumn(gc4);

        grid.moveColumnTo(3,
                          gc1);

        final List<GridColumn<?>> columns = grid.getColumns();

        assertEquals(4,
                     columns.size());
        assertEquals(0,
                     gc1.getIndex());
        assertEquals(1,
                     gc2.getIndex());
        assertEquals(2,
                     gc3.getIndex());
        assertEquals(3,
                     gc4.getIndex());
        assertEquals(columns.get(0),
                     gc2);
        assertEquals(columns.get(1),
                     gc3);
        assertEquals(columns.get(2),
                     gc4);
        assertEquals(columns.get(3),
                     gc1);
    }

    @Test
    public void testMoveColumnsToRight_LeftMostTwoColumns() {
        final GridData grid = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        final GridColumn<String> gc4 = new MockMergableGridColumn<String>("col4",
                                                                          100);
        grid.appendColumn(gc1);
        grid.appendColumn(gc2);
        grid.appendColumn(gc3);
        grid.appendColumn(gc4);

        grid.moveColumnsTo(2,
                           new ArrayList<GridColumn<?>>() {{
                               add(gc1);
                               add(gc2);
                           }});

        final List<GridColumn<?>> columns = grid.getColumns();

        assertEquals(4,
                     columns.size());
        assertEquals(0,
                     gc1.getIndex());
        assertEquals(1,
                     gc2.getIndex());
        assertEquals(2,
                     gc3.getIndex());
        assertEquals(3,
                     gc4.getIndex());
        assertEquals(columns.get(0),
                     gc3);
        assertEquals(columns.get(1),
                     gc1);
        assertEquals(columns.get(2),
                     gc2);
        assertEquals(columns.get(3),
                     gc4);
    }

    @Test
    public void testMoveColumnsToRight_LeftMostTwoColumns_ToRightExtent() {
        final GridData grid = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        final GridColumn<String> gc4 = new MockMergableGridColumn<String>("col4",
                                                                          100);
        grid.appendColumn(gc1);
        grid.appendColumn(gc2);
        grid.appendColumn(gc3);
        grid.appendColumn(gc4);

        grid.moveColumnsTo(3,
                           new ArrayList<GridColumn<?>>() {{
                               add(gc1);
                               add(gc2);
                           }});

        final List<GridColumn<?>> columns = grid.getColumns();

        assertEquals(4,
                     columns.size());
        assertEquals(0,
                     gc1.getIndex());
        assertEquals(1,
                     gc2.getIndex());
        assertEquals(2,
                     gc3.getIndex());
        assertEquals(3,
                     gc4.getIndex());
        assertEquals(columns.get(0),
                     gc3);
        assertEquals(columns.get(1),
                     gc4);
        assertEquals(columns.get(2),
                     gc1);
        assertEquals(columns.get(3),
                     gc2);
    }

    @Test
    public void testMoveColumnsToRight_LeftMostTwoColumns_MidBlock() {
        final GridData grid = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        final GridColumn<String> gc4 = new MockMergableGridColumn<String>("col4",
                                                                          100);
        final GridColumn<String> gc5 = new MockMergableGridColumn<String>("col5",
                                                                          100);
        final GridColumn<String> gc6 = new MockMergableGridColumn<String>("col6",
                                                                          100);
        final GridColumn<String> gc7 = new MockMergableGridColumn<String>("col7",
                                                                          100);
        final GridColumn<String> gc8 = new MockMergableGridColumn<String>("col8",
                                                                          100);
        grid.appendColumn(gc1);
        grid.appendColumn(gc2);
        grid.appendColumn(gc3);
        grid.appendColumn(gc4);
        grid.appendColumn(gc5);
        grid.appendColumn(gc6);
        grid.appendColumn(gc7);
        grid.appendColumn(gc8);

        grid.moveColumnsTo(5,
                           new ArrayList<GridColumn<?>>() {{
                               add(gc1);
                               add(gc2);
                           }});

        final List<GridColumn<?>> columns = grid.getColumns();

        assertEquals(8,
                     columns.size());
        assertEquals(0,
                     gc1.getIndex());
        assertEquals(1,
                     gc2.getIndex());
        assertEquals(2,
                     gc3.getIndex());
        assertEquals(3,
                     gc4.getIndex());
        assertEquals(4,
                     gc5.getIndex());
        assertEquals(5,
                     gc6.getIndex());
        assertEquals(6,
                     gc7.getIndex());
        assertEquals(7,
                     gc8.getIndex());
        assertEquals(columns.get(0),
                     gc3);
        assertEquals(columns.get(1),
                     gc4);
        assertEquals(columns.get(2),
                     gc5);
        assertEquals(columns.get(3),
                     gc6);
        assertEquals(columns.get(4),
                     gc1);
        assertEquals(columns.get(5),
                     gc2);
        assertEquals(columns.get(6),
                     gc7);
        assertEquals(columns.get(7),
                     gc8);
    }

    @Test
    public void testRemoveRow() {
        final GridData grid = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        grid.appendColumn(gc1);
        grid.appendColumn(gc2);
        grid.appendColumn(gc3);

        final GridRow gr1 = new BaseGridRow();
        final GridRow gr2 = new BaseGridRow();
        final GridRow gr3 = new BaseGridRow();

        grid.appendRow(gr1);
        grid.appendRow(gr2);
        grid.appendRow(gr3);

        grid.deleteRow(1);

        assertEquals(2,
                     grid.getRowCount());
        assertEquals(gr1,
                     grid.getRow(0));
        assertEquals(gr3,
                     grid.getRow(1));
    }
}
