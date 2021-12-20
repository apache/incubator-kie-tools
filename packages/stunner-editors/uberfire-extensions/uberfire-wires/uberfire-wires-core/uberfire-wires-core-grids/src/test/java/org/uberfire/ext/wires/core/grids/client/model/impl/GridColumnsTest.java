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

import org.junit.Test;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static org.junit.Assert.assertEquals;
import static org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridTest.Expected.build;

public class GridColumnsTest extends BaseGridTest {

    @Test
    public void testAppendColumn() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)")}
                          });

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(1,
                     data.getColumns().get(1).getIndex());

        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        data.appendColumn(gc3);

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)"), build(false,
                                                                           1,
                                                                           null)},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)"), build(false,
                                                                           1,
                                                                           null)}
                          });

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(1,
                     data.getColumns().get(1).getIndex());
        assertEquals(2,
                     data.getColumns().get(2).getIndex());
    }

    @Test
    public void testInsertColumn() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)")}
                          });

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(1,
                     data.getColumns().get(1).getIndex());

        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        data.insertColumn(1,
                          gc3);

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       "(1, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       "(1, 1)")}
                          });

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(2,
                     data.getColumns().get(1).getIndex());
        assertEquals(1,
                     data.getColumns().get(2).getIndex());
    }

    @Test
    public void testDeleteColumn() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);
        data.appendColumn(gc3);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)"), build(false,
                                                                           1,
                                                                           "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)"), build(false,
                                                                           1,
                                                                           "(2, 1)")}
                          });

        assertEquals(3,
                     data.getRow(0).getCells().size());
        assertEquals(3,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(1,
                     data.getColumns().get(1).getIndex());
        assertEquals(2,
                     data.getColumns().get(2).getIndex());

        data.deleteColumn(gc2);

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(2, 1)")}
                          });

        assertEquals(2,
                     data.getRow(0).getCells().size());
        assertEquals(2,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(1,
                     data.getColumns().get(1).getIndex());
    }

    @Test
    public void testDeleteColumnThenInsertColumn() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);
        data.appendColumn(gc3);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)"), build(false,
                                                                           1,
                                                                           "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)"), build(false,
                                                                           1,
                                                                           "(2, 1)")}
                          });

        assertEquals(3,
                     data.getRow(0).getCells().size());
        assertEquals(3,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(1,
                     data.getColumns().get(1).getIndex());
        assertEquals(2,
                     data.getColumns().get(2).getIndex());

        data.deleteColumn(gc2);

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(2, 1)")}
                          });

        assertEquals(2,
                     data.getRow(0).getCells().size());
        assertEquals(2,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(1,
                     data.getColumns().get(1).getIndex());

        final GridColumn<String> gc4 = new MockMergableGridColumn<String>("col4",
                                                                          100);
        data.insertColumn(1,
                          gc4);

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       "(2, 1)")}
                          });

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(2,
                     data.getColumns().get(1).getIndex());
        assertEquals(1,
                     data.getColumns().get(2).getIndex());
    }

    @Test
    public void testInsertx2ColumnThenDeletex2Column() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);
        data.appendColumn(gc3);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        //Validate initial setup
        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)"), build(false,
                                                                           1,
                                                                           "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)"), build(false,
                                                                           1,
                                                                           "(2, 1)")}
                          });
        assertEquals(3,
                     data.getRow(0).getCells().size());
        assertEquals(3,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(1,
                     data.getColumns().get(1).getIndex());
        assertEquals(2,
                     data.getColumns().get(2).getIndex());

        //Insert column#1 and validate
        final GridColumn<String> tgc1 = new MockMergableGridColumn<String>("col4",
                                                                           100);
        data.insertColumn(1,
                          tgc1);

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       "(1, 0)"), build(false,
                                                                                        1,
                                                                                        "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       "(1, 1)"), build(false,
                                                                                        1,
                                                                                        "(2, 1)")}
                          });

        assertEquals(3,
                     data.getRow(0).getCells().size());
        assertEquals(3,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(3,
                     data.getColumns().get(1).getIndex());
        assertEquals(1,
                     data.getColumns().get(2).getIndex());
        assertEquals(2,
                     data.getColumns().get(3).getIndex());

        //Insert column#2 and check validate
        final GridColumn<String> tgc2 = new MockMergableGridColumn<String>("col5",
                                                                           100);
        data.insertColumn(1,
                          tgc2);

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       null), build(false,
                                                                                    1,
                                                                                    "(1, 0)"), build(false,
                                                                                                     1,
                                                                                                     "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       null), build(false,
                                                                                    1,
                                                                                    "(1, 1)"), build(false,
                                                                                                     1,
                                                                                                     "(2, 1)")}
                          });

        assertEquals(3,
                     data.getRow(0).getCells().size());
        assertEquals(3,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(4,
                     data.getColumns().get(1).getIndex());
        assertEquals(3,
                     data.getColumns().get(2).getIndex());
        assertEquals(1,
                     data.getColumns().get(3).getIndex());
        assertEquals(2,
                     data.getColumns().get(4).getIndex());

        //Delete column#1 and validate
        data.deleteColumn(tgc1);

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       "(1, 0)"), build(false,
                                                                                        1,
                                                                                        "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       "(1, 1)"), build(false,
                                                                                        1,
                                                                                        "(2, 1)")}
                          });

        assertEquals(3,
                     data.getRow(0).getCells().size());
        assertEquals(3,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(3,
                     data.getColumns().get(1).getIndex());
        assertEquals(1,
                     data.getColumns().get(2).getIndex());
        assertEquals(2,
                     data.getColumns().get(3).getIndex());

        //Delete column#2 and validate
        data.deleteColumn(tgc2);

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)"), build(false,
                                                                           1,
                                                                           "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)"), build(false,
                                                                           1,
                                                                           "(2, 1)")}
                          });

        assertEquals(3,
                     data.getRow(0).getCells().size());
        assertEquals(3,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(1,
                     data.getColumns().get(1).getIndex());
        assertEquals(2,
                     data.getColumns().get(2).getIndex());
    }

    @Test
    public void testInsertx2ColumnThenDeletex2ColumnReverseOrder() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);
        data.appendColumn(gc3);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        //Validate initial setup
        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)"), build(false,
                                                                           1,
                                                                           "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)"), build(false,
                                                                           1,
                                                                           "(2, 1)")}
                          });

        assertEquals(3,
                     data.getRow(0).getCells().size());
        assertEquals(3,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(1,
                     data.getColumns().get(1).getIndex());
        assertEquals(2,
                     data.getColumns().get(2).getIndex());

        //Insert column#1 and validate
        final GridColumn<String> tgc1 = new MockMergableGridColumn<String>("col4",
                                                                           100);
        data.insertColumn(1,
                          tgc1);

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       "(1, 0)"), build(false,
                                                                                        1,
                                                                                        "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       "(1, 1)"), build(false,
                                                                                        1,
                                                                                        "(2, 1)")}
                          });

        assertEquals(3,
                     data.getRow(0).getCells().size());
        assertEquals(3,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(3,
                     data.getColumns().get(1).getIndex());
        assertEquals(1,
                     data.getColumns().get(2).getIndex());
        assertEquals(2,
                     data.getColumns().get(3).getIndex());

        //Insert column#2 and check validate
        final GridColumn<String> tgc2 = new MockMergableGridColumn<String>("col5",
                                                                           100);
        data.insertColumn(1,
                          tgc2);

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       null), build(false,
                                                                                    1,
                                                                                    "(1, 0)"), build(false,
                                                                                                     1,
                                                                                                     "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       null), build(false,
                                                                                    1,
                                                                                    "(1, 1)"), build(false,
                                                                                                     1,
                                                                                                     "(2, 1)")}
                          });
        assertEquals(3,
                     data.getRow(0).getCells().size());
        assertEquals(3,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(4,
                     data.getColumns().get(1).getIndex());
        assertEquals(3,
                     data.getColumns().get(2).getIndex());
        assertEquals(1,
                     data.getColumns().get(3).getIndex());
        assertEquals(2,
                     data.getColumns().get(4).getIndex());

        //Delete column#2 and validate
        data.deleteColumn(tgc2);

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       "(1, 0)"), build(false,
                                                                                        1,
                                                                                        "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       "(1, 1)"), build(false,
                                                                                        1,
                                                                                        "(2, 1)")}
                          });

        assertEquals(3,
                     data.getRow(0).getCells().size());
        assertEquals(3,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(3,
                     data.getColumns().get(1).getIndex());
        assertEquals(1,
                     data.getColumns().get(2).getIndex());
        assertEquals(2,
                     data.getColumns().get(3).getIndex());

        //Delete column#1 and validate
        data.deleteColumn(tgc1);

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)"), build(false,
                                                                           1,
                                                                           "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)"), build(false,
                                                                           1,
                                                                           "(2, 1)")}
                          });

        assertEquals(3,
                     data.getRow(0).getCells().size());
        assertEquals(3,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(1,
                     data.getColumns().get(1).getIndex());
        assertEquals(2,
                     data.getColumns().get(2).getIndex());
    }

    @Test
    public void testMoveColumnThenDelete() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);
        data.appendColumn(gc3);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        //Validate initial setup
        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)"), build(false,
                                                                           1,
                                                                           "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)"), build(false,
                                                                           1,
                                                                           "(2, 1)")}
                          });

        assertEquals(3,
                     data.getRow(0).getCells().size());
        assertEquals(3,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(1,
                     data.getColumns().get(1).getIndex());
        assertEquals(2,
                     data.getColumns().get(2).getIndex());

        //Move column and validate
        data.moveColumnTo(0,
                          gc3);

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(2, 0)"), build(false,
                                                          1,
                                                          "(0, 0)"), build(false,
                                                                           1,
                                                                           "(1, 0)")},
                                  {build(false,
                                         1,
                                         "(2, 1)"), build(false,
                                                          1,
                                                          "(0, 1)"), build(false,
                                                                           1,
                                                                           "(1, 1)")}
                          });

        assertEquals(3,
                     data.getRow(0).getCells().size());
        assertEquals(3,
                     data.getRow(1).getCells().size());

        assertEquals(2,
                     data.getColumns().get(0).getIndex());
        assertEquals(0,
                     data.getColumns().get(1).getIndex());
        assertEquals(1,
                     data.getColumns().get(2).getIndex());

        //Delete column and validate
        data.deleteColumn(gc1);

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(2, 0)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(false,
                                         1,
                                         "(2, 1)"), build(false,
                                                          1,
                                                          "(1, 1)")}
                          });

        assertEquals(2,
                     data.getRow(0).getCells().size());
        assertEquals(2,
                     data.getRow(1).getCells().size());

        assertEquals(1,
                     data.getColumns().get(0).getIndex());
        assertEquals(0,
                     data.getColumns().get(1).getIndex());
    }

    @Test
    public void testUpdateColumn() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        final GridColumn<String> gc3 = new MockMergableGridColumn<String>("col3",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);
        data.appendColumn(gc3);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)"), build(false,
                                                                           1,
                                                                           "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)"), build(false,
                                                                           1,
                                                                           "(2, 1)")}
                          });

        assertEquals(3,
                     data.getRow(0).getCells().size());
        assertEquals(3,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(1,
                     data.getColumns().get(1).getIndex());
        assertEquals(2,
                     data.getColumns().get(2).getIndex());

        final MockMergableGridColumn<String> gc4 = new MockMergableGridColumn<String>("col4",
                                                                                      100);
        data.updateColumn(1,
                          gc4);

        assertGridIndexes(data,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       "(2, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          null), build(false,
                                                                       1,
                                                                       "(2, 1)")}
                          });

        assertEquals(2,
                     data.getRow(0).getCells().size());
        assertEquals(2,
                     data.getRow(1).getCells().size());

        assertEquals(0,
                     data.getColumns().get(0).getIndex());
        assertEquals(1,
                     data.getColumns().get(1).getIndex());
        assertEquals(2,
                     data.getColumns().get(2).getIndex());

        assertEquals(gc1,
                     data.getColumns().get(0));
        assertEquals(gc4,
                     data.getColumns().get(1));
        assertEquals(gc3,
                     data.getColumns().get(2));
    }
}
