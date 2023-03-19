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
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GridMergingTest extends BaseGridTest {

    @Test
    public void testInitialSetup_NoMerging() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            assertFalse(data.getRow(rowIndex).isMerged());
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                final GridCell<?> cell = data.getCell(rowIndex,
                                                      columnIndex);
                assertFalse(cell.isMerged());
                assertEquals(1,
                             cell.getMergedCellCount());
            }
        }
    }

    @Test
    public void testInitialSetup_Column1Merged() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + (columnIndex == 0 ? "X" : rowIndex) + ")"));
            }
        }

        assertGridIndexes(data,
                          new boolean[]{true, true, true},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "(0, X)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, X)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, X)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });
    }

    @Test
    public void testMergeDownwards_SplitBlock_Rowsx2Rowx1() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        data.setCellValue(1,
                          0,
                          new BaseGridCellValue<String>("(0, 0)"));

        assertGridIndexes(data,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });
    }

    @Test
    public void testMergeDownwards_SplitBlock_Rowsx1Rowx2() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        data.setCellValue(2,
                          0,
                          new BaseGridCellValue<String>("(0, 1)"));

        assertGridIndexes(data,
                          new boolean[]{false, true, true},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  2,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });
    }

    @Test
    public void testMergeDownwards_TableExtents() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        data.setCellValue(1,
                          0,
                          new BaseGridCellValue<String>("(0, 0)"));
        data.setCellValue(2,
                          0,
                          new BaseGridCellValue<String>("(0, 0)"));

        assertGridIndexes(data,
                          new boolean[]{true, true, true},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });
    }

    @Test
    public void testMergeUpwards_SplitBlock_Rowsx2Rowsx1() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        data.setCellValue(0,
                          0,
                          new BaseGridCellValue<String>("(0, 1)"));

        assertGridIndexes(data,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });
    }

    @Test
    public void testMergeUpwards_SplitBlock_Rowsx1Rowsx2() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        data.setCellValue(1,
                          0,
                          new BaseGridCellValue<String>("(0, 2)"));

        assertGridIndexes(data,
                          new boolean[]{false, true, true},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  2,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });
    }

    @Test
    public void testMergeUpwards_TableExtents() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        data.setCellValue(1,
                          0,
                          new BaseGridCellValue<String>("(0, 2)"));

        data.setCellValue(0,
                          0,
                          new BaseGridCellValue<String>("(0, 2)"));

        assertGridIndexes(data,
                          new boolean[]{true, true, true},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });
    }

    @Test
    public void testMergeNonSequential() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        data.setCellValue(0,
                          0,
                          new BaseGridCellValue<String>("(a, b)"));

        data.setCellValue(2,
                          0,
                          new BaseGridCellValue<String>("(a, b)"));

        data.setCellValue(1,
                          0,
                          new BaseGridCellValue<String>("(a, b)"));

        assertGridIndexes(data,
                          new boolean[]{true, true, true},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "(a, b)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(a, b)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(a, b)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });
    }

    @Test
    public void testMergedUpdateCellValue() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        data.setCellValue(1,
                          0,
                          new BaseGridCellValue<String>("(0, 0)"));

        assertGridIndexes(data,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });

        //Update cell value
        data.setCellValue(0,
                          0,
                          new BaseGridCellValue<String>("<changed>"));

        assertGridIndexes(data,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "<changed>"), Expected.build(false,
                                                                               1,
                                                                               "(1, 0)")},
                                  {Expected.build(true,
                                                  0,
                                                  "<changed>"), Expected.build(false,
                                                                               1,
                                                                               "(1, 1)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });
    }

    @Test
    public void testMerged_MovedColumnRight_UpdateCellInMergedCell() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        data.setCellValue(1,
                          0,
                          new BaseGridCellValue<String>("(0, 0)"));

        assertGridIndexes(data,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });

        //Move column
        data.moveColumnTo(1,
                          gc1);

        //Update cell value
        data.setCellValue(0,
                          1,
                          new BaseGridCellValue<String>("<changed>"));

        assertGridIndexes(data,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(1, 0)"), Expected.build(true,
                                                                            2,
                                                                            "<changed>")},
                                  {Expected.build(false,
                                                  1,
                                                  "(1, 1)"), Expected.build(true,
                                                                            0,
                                                                            "<changed>")},
                                  {Expected.build(false,
                                                  1,
                                                  "(1, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(0, 2)")},
                          });
    }

    @Test
    public void testMerged_MovedColumnRight_UpdateCellInMergedCell_CheckMergeToggle() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        data.setCellValue(1,
                          0,
                          new BaseGridCellValue<String>("(0, 0)"));

        assertGridIndexes(data,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });

        //Move column
        data.moveColumnTo(1,
                          gc1);

        //Update cell value
        data.setCellValue(0,
                          1,
                          new BaseGridCellValue<String>("<changed>"));

        assertGridIndexes(data,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(1, 0)"), Expected.build(true,
                                                                            2,
                                                                            "<changed>")},
                                  {Expected.build(false,
                                                  1,
                                                  "(1, 1)"), Expected.build(true,
                                                                            0,
                                                                            "<changed>")},
                                  {Expected.build(false,
                                                  1,
                                                  "(1, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(0, 2)")},
                          });

        data.setMerged(false);

        assertGridIndexes(data,
                          new boolean[]{false, false, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(1, 0)"), Expected.build(false,
                                                                            1,
                                                                            "<changed>")},
                                  {Expected.build(false,
                                                  1,
                                                  "(1, 1)"), Expected.build(false,
                                                                            1,
                                                                            "<changed>")},
                                  {Expected.build(false,
                                                  1,
                                                  "(1, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(0, 2)")},
                          });

        data.setMerged(true);

        assertGridIndexes(data,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(1, 0)"), Expected.build(true,
                                                                            2,
                                                                            "<changed>")},
                                  {Expected.build(false,
                                                  1,
                                                  "(1, 1)"), Expected.build(true,
                                                                            0,
                                                                            "<changed>")},
                                  {Expected.build(false,
                                                  1,
                                                  "(1, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(0, 2)")},
                          });
    }

    @Test
    public void testFullIndexing_TableExtents() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        data.setCellValue(1,
                          0,
                          new BaseGridCellValue<String>("(0, 0)"));
        data.setCellValue(2,
                          0,
                          new BaseGridCellValue<String>("(0, 0)"));

        //Check initial indexing
        assertGridIndexes(data,
                          new boolean[]{true, true, true},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });

        //Clear merging
        data.setMerged(false);

        assertGridIndexes(data,
                          new boolean[]{false, false, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });

        //Set merging
        data.setMerged(true);

        assertGridIndexes(data,
                          new boolean[]{true, true, true},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });
    }

    @Test
    public void testFullIndexing_TableTopExtent() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        data.setCellValue(1,
                          0,
                          new BaseGridCellValue<String>("(0, 0)"));
        data.setCellValue(2,
                          0,
                          new BaseGridCellValue<String>("(0, 0)"));

        //Check initial indexing
        assertGridIndexes(data,
                          new boolean[]{true, true, true, false, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 3)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 4)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });

        //Clear merging
        data.setMerged(false);

        assertGridIndexes(data,
                          new boolean[]{false, false, false, false, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 3)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 4)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });

        //Set merging
        data.setMerged(true);

        assertGridIndexes(data,
                          new boolean[]{true, true, true, false, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 3)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 4)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });
    }

    @Test
    public void testFullIndexing_TableBottomExtent() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        data.setCellValue(3,
                          0,
                          new BaseGridCellValue<String>("(0, 2)"));
        data.setCellValue(4,
                          0,
                          new BaseGridCellValue<String>("(0, 2)"));

        //Check initial indexing
        assertGridIndexes(data,
                          new boolean[]{false, false, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });

        //Clear merging
        data.setMerged(false);

        assertGridIndexes(data,
                          new boolean[]{false, false, false, false, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });

        //Set merging
        data.setMerged(true);

        assertGridIndexes(data,
                          new boolean[]{false, false, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });
    }

    @Test
    public void testMergeString_TableExtents() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        data.appendColumn(gc1);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("a"));
            }
        }

        assertGridIndexes(data,
                          new boolean[]{true, true, true, true},
                          new boolean[]{false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  4,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")}
                          });
    }

    @Test
    public void testMergeString_SplitBlockx2() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        data.appendColumn(gc1);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>(rowIndex == 0 ? "b" : "a"));
            }
        }

        assertGridIndexes(data,
                          new boolean[]{false, true, true, true},
                          new boolean[]{false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b")},
                                  {Expected.build(true,
                                                  3,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")}
                          });
    }

    @Test
    public void testMergeString_SplitBlockx3() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        data.appendColumn(gc1);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                final String value = (rowIndex == 0 || rowIndex == 3 || rowIndex == 4 ? "a" : "b");
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>(value));
            }
        }

        assertGridIndexes(data,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a")},
                                  {Expected.build(true,
                                                  2,
                                                  "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "b")},
                                  {Expected.build(true,
                                                  2,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")}
                          });

        data.setMerged(false);

        assertGridIndexes(data,
                          new boolean[]{false, false, false, false, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a")},
                                  {Expected.build(false,
                                                  1,
                                                  "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "a")},
                                  {Expected.build(false,
                                                  1,
                                                  "a")}
                          });

        data.setMerged(true);

        assertGridIndexes(data,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a")},
                                  {Expected.build(true,
                                                  2,
                                                  "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "b")},
                                  {Expected.build(true,
                                                  2,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")}
                          });
    }

    @Test
    public void testMergeBoolean_TableExtents() {
        final GridData data = new BaseGridData();
        final GridColumn<Boolean> gc1 = new MockMergableGridColumn<Boolean>("col1",
                                                                            100);
        data.appendColumn(gc1);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<Boolean>(false));
            }
        }

        assertGridIndexes(data,
                          new boolean[]{true, true, true, true},
                          new boolean[]{false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  4,
                                                  false)},
                                  {Expected.build(true,
                                                  0,
                                                  false)},
                                  {Expected.build(true,
                                                  0,
                                                  false)},
                                  {Expected.build(true,
                                                  0,
                                                  false)}
                          });
    }

    @Test
    public void testMergeBoolean_SplitBlockx2() {
        final GridData data = new BaseGridData();
        final GridColumn<Boolean> gc1 = new MockMergableGridColumn<Boolean>("col1",
                                                                            100);
        data.appendColumn(gc1);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<Boolean>(rowIndex == 0));
            }
        }

        assertGridIndexes(data,
                          new boolean[]{false, true, true, true},
                          new boolean[]{false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  true)},
                                  {Expected.build(true,
                                                  3,
                                                  false)},
                                  {Expected.build(true,
                                                  0,
                                                  false)},
                                  {Expected.build(true,
                                                  0,
                                                  false)}
                          });
    }

    @Test
    public void testMergeBoolean_SplitBlockx3() {
        final GridData data = new BaseGridData();
        final GridColumn<Boolean> gc1 = new MockMergableGridColumn<Boolean>("col1",
                                                                            100);
        data.appendColumn(gc1);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                final boolean value = rowIndex == 0 || rowIndex == 3 || rowIndex == 4;
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<Boolean>(value));
            }
        }

        assertGridIndexes(data,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  true)},
                                  {Expected.build(true,
                                                  2,
                                                  false)},
                                  {Expected.build(true,
                                                  0,
                                                  false)},
                                  {Expected.build(true,
                                                  2,
                                                  true)},
                                  {Expected.build(true,
                                                  0,
                                                  true)}
                          });

        data.setMerged(false);

        assertGridIndexes(data,
                          new boolean[]{false, false, false, false, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  true)},
                                  {Expected.build(false,
                                                  1,
                                                  false)},
                                  {Expected.build(false,
                                                  1,
                                                  false)},
                                  {Expected.build(false,
                                                  1,
                                                  true)},
                                  {Expected.build(false,
                                                  1,
                                                  true)}
                          });

        data.setMerged(true);

        assertGridIndexes(data,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  true)},
                                  {Expected.build(true,
                                                  2,
                                                  false)},
                                  {Expected.build(true,
                                                  0,
                                                  false)},
                                  {Expected.build(true,
                                                  2,
                                                  true)},
                                  {Expected.build(true,
                                                  0,
                                                  true)}
                          });
    }

    @Test
    public void testMerged_DeleteCell_MergedCell() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());
        data.appendRow(new BaseGridRow());

        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                data.setCellValue(rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        data.setCellValue(1,
                          0,
                          new BaseGridCellValue<String>("(0, 0)"));

        assertGridIndexes(data,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });

        //Update cell value
        data.deleteCell(0,
                        0);

        assertGridIndexes(data,
                          new boolean[]{false, false, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(null), Expected.build(false,
                                                                        1,
                                                                        "(1, 0)")},
                                  {Expected.build(null), Expected.build(false,
                                                                        1,
                                                                        "(1, 1)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });
    }

    @Test
    public void testMerged_DeleteCell_UnmergedCell() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>("col1",
                                                                          100);
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>("col2",
                                                                          100);
        data.appendColumn(gc1);
        data.appendColumn(gc2);

        data.appendRow(new BaseGridRow());
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
                          new boolean[]{false, false, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });

        //Update cell value
        data.deleteCell(0,
                        0);

        assertGridIndexes(data,
                          new boolean[]{false, false, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(null), Expected.build(false,
                                                                        1,
                                                                        "(1, 0)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 2)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                          });
    }
}
