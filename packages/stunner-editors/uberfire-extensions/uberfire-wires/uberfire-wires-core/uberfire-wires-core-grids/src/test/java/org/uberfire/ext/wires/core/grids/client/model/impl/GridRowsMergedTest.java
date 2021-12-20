/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.model.impl;

import java.util.Arrays;

import org.junit.Test;

public class GridRowsMergedTest extends BaseGridTest {

    @Test
    public void testRemoveRow() {
        constructGridData(1, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = (rowIndex > 0 && rowIndex < 4 ? "b" : "a");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a")},
                                  {Expected.build(true,
                                                  3,
                                                  "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "a")}
                          });

        gridData.deleteRow(2);

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, false},
                          new boolean[]{false, false, false, false},
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
                                  {Expected.build(false,
                                                  1,
                                                  "a")}
                          });
    }

    @Test
    public void testAppendRow() {
        constructGridData(1, 4);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = (rowIndex < 2 ? "a" : "b");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true},
                          new boolean[]{false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")},
                                  {Expected.build(true,
                                                  2,
                                                  "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "b")}
                          });

        gridData.appendRow(new BaseGridRow());

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")},
                                  {Expected.build(true,
                                                  2,
                                                  "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "b")},
                                  {Expected.build(false,
                                                  1,
                                                  null)}
                          });
    }

    @Test
    public void testInsertRowAtZeroIndex() {
        constructGridData(1, 4);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = (rowIndex < 2 ? "a" : "b");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true},
                          new boolean[]{false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")},
                                  {Expected.build(true,
                                                  2,
                                                  "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "b")}
                          });

        gridData.insertRow(0,
                           new BaseGridRow());

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  null)},
                                  {Expected.build(true,
                                                  2,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")},
                                  {Expected.build(true,
                                                  2,
                                                  "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "b")}
                          });
    }

    @Test
    public void testInsertRowAtStartEndBlock() {
        constructGridData(1, 4);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = (rowIndex < 2 ? "a" : "b");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true},
                          new boolean[]{false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")},
                                  {Expected.build(true,
                                                  2,
                                                  "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "b")}
                          });

        gridData.insertRow(2,
                           new BaseGridRow());

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")},
                                  {Expected.build(false,
                                                  1,
                                                  null)},
                                  {Expected.build(true,
                                                  2,
                                                  "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "b")}
                          });
    }

    @Test
    public void testInsertRowAtMidBlock() {
        constructGridData(1, 4);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("a"));
            }
        }

        assertGridIndexes(gridData,
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

        gridData.insertRow(2,
                           new BaseGridRow());

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")},
                                  {Expected.build(false,
                                                  1,
                                                  null)},
                                  {Expected.build(true,
                                                  2,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")}
                          });
    }

    @Test
    public void testDeleteRowAtZeroIndex() {
        constructGridData(1, 4);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = (rowIndex < 2 ? "a" : "b");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true},
                          new boolean[]{false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")},
                                  {Expected.build(true,
                                                  2,
                                                  "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "b")}
                          });

        gridData.deleteRow(0);

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a")},
                                  {Expected.build(true,
                                                  2,
                                                  "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "b")}
                          });
    }

    @Test
    public void testDeleteRowAtStartEndBlock() {
        constructGridData(1, 4);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = (rowIndex < 2 ? "a" : "b");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true},
                          new boolean[]{false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")},
                                  {Expected.build(true,
                                                  2,
                                                  "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "b")}
                          });

        gridData.deleteRow(2);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a")},
                                  {Expected.build(false,
                                                  1,
                                                  "b")}
                          });
    }

    @Test
    public void testDeleteRowAtMidBlock() {
        constructGridData(1, 4);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("a"));
            }
        }

        assertGridIndexes(gridData,
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

        gridData.deleteRow(2);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true},
                          new boolean[]{false, false, false},
                          new Expected[][]{
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
    public void testMergedBlock_MoveRowUp_Index4to3() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 4 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, 0
        // row1 = a, 1
        // row2 = a, 2
        // row3 = a, 3
        // row4 = b, 4

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  4,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        //Move row
        gridData.moveRowTo(3,
                           gridRows[4]);

        // row0 = a, 0
        // row1 = a, 1
        // row2 = a, 2
        // row3 = b, 4
        // row4 = a, 3

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, false, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "4")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowUp_Index3to2() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 3 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, 0
        // row1 = a, 1
        // row2 = a, 2
        // row3 = b, 3
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, false, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        //Move row
        gridData.moveRowTo(2,
                           gridRows[3]);

        // row0 = a, 0
        // row1 = a, 1
        // row2 = b, 3
        // row3 = a, 2
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowUp_Index2to1() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 2 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, 0
        // row1 = a, 1
        // row2 = b, 2
        // row3 = a, 3
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        //Move row
        gridData.moveRowTo(1,
                           gridRows[2]);

        // row0 = a, 0
        // row1 = b, 2
        // row2 = a, 1
        // row3 = a, 3
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{false, false, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowUp_Index1to0() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, 0
        // row1 = b, 1
        // row2 = a, 2
        // row3 = a, 3
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{false, false, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        //Move row
        gridData.moveRowTo(0,
                           gridRows[1]);

        // row0 = b, 1
        // row1 = a, 0
        // row2 = a, 2
        // row3 = a, 3
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  4,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowUp_Index2to1_NewMergedBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 ? "b" : "a") : (rowIndex < 2 ? "a" : "b");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, a
        // row1 = b, a
        // row2 = a, b
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "a")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "a")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(true,
                                                                       3,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")}
                          });

        //Move row
        gridData.moveRowTo(1,
                           gridRows[2]);

        // row0 = a, a
        // row1 = a, b
        // row2 = b, a
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowUp_Index3to2_NewMergedBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 ? "b" : "a") : (rowIndex == 2 ? "a" : "b");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, b
        // row1 = b, b
        // row2 = a, a
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")}
                          });

        //Move row
        gridData.moveRowTo(2,
                           gridRows[3]);

        // row0 = a, b
        // row1 = b, b
        // row2 = a, b
        // row3 = a, a
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       3,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "b")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowDown_Index0to1() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 4 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, 0
        // row1 = a, 1
        // row2 = a, 2
        // row3 = a, 3
        // row4 = b, 4

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  4,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        //Move row
        gridData.moveRowTo(1,
                           gridRows[0]);

        // row0 = a, 1
        // row1 = a, 0
        // row2 = a, 2
        // row3 = a, 3
        // row4 = b, 4

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  4,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowDown_Index1to2() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 3 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, 0
        // row1 = a, 1
        // row2 = a, 2
        // row3 = b, 3
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, false, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        //Move row
        gridData.moveRowTo(2,
                           gridRows[1]);

        // row0 = a, 0
        // row1 = a, 2
        // row2 = a, 1
        // row3 = b, 3
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, false, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowDown_Index2to3() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 2 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, 0
        // row1 = a, 1
        // row2 = b, 2
        // row3 = a, 3
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        //Move row
        gridData.moveRowTo(3,
                           gridRows[2]);

        // row0 = a, 0
        // row1 = a, 1
        // row2 = a, 3
        // row3 = b, 2
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, false, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowDown_Index3to4() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, 0
        // row1 = b, 1
        // row2 = a, 2
        // row3 = a, 3
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{false, false, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        //Move row
        gridData.moveRowTo(4,
                           gridRows[3]);

        // row0 = a, 0
        // row1 = b, 1
        // row2 = a, 2
        // row3 = a, 4
        // row4 = a, 3

        assertGridIndexes(gridData,
                          new boolean[]{false, false, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowDown_Index1to2_NewMergedBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 ? "b" : "a") : (rowIndex < 2 ? "a" : "b");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, a
        // row1 = b, a
        // row2 = a, b
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "a")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "a")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(true,
                                                                       3,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")}
                          });

        //Move row
        gridData.moveRowTo(2,
                           gridRows[1]);

        // row0 = a, a
        // row1 = a, b
        // row2 = b, a
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowDown_Index2to3_NewMergedBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 ? "b" : "a") : (rowIndex == 2 ? "a" : "b");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, b
        // row1 = b, b
        // row2 = a, a
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")}
                          });

        //Move row
        gridData.moveRowTo(3,
                           gridRows[2]);

        // row0 = a, b
        // row1 = b, b
        // row2 = a, b
        // row3 = a, a
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       3,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "b")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowsDown_Indexes2and3to4() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 ? "b" : "a") : (rowIndex == 2 ? "a" : "b");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, b
        // row1 = b, b
        // row2 = a, a
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")}
                          });

        //Move row
        gridData.moveRowsTo(4,
                            Arrays.asList(gridRows[2], gridRows[3]));

        // row0 = a, b
        // row1 = b, b
        // row2 = a, b
        // row3 = a, a
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       3,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "b")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowsDown_Indexes0and1and2to3() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 ? "b" : "a") : (rowIndex == 2 ? "a" : "b");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, b
        // row1 = b, b
        // row2 = a, a
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")}
                          });

        //Move row
        gridData.moveRowsTo(3,
                            Arrays.asList(gridRows[0], gridRows[1], gridRows[2]));

        // row0 = a, b
        // row1 = a, b
        // row2 = b, b
        // row3 = a, a
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(true,
                                                                       3,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "b")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowsDown_Indexes0and1to4() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 ? "b" : "a") : (rowIndex == 2 ? "a" : "b");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, b
        // row1 = b, b
        // row2 = a, a
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")}
                          });

        //Move row
        gridData.moveRowsTo(4,
                            Arrays.asList(gridRows[0], gridRows[1]));

        // row0 = a, a
        // row1 = a, b
        // row2 = a, b
        // row3 = a, b
        // row4 = b, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  4,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       4,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "b")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowsUp_Indexes3and4to1() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 ? "b" : "a") : (rowIndex == 2 ? "a" : "b");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, b
        // row1 = b, b
        // row2 = a, a
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")}
                          });

        //Move row
        gridData.moveRowsTo(1,
                            Arrays.asList(gridRows[3], gridRows[4]));

        // row0 = a, b
        // row1 = a, b
        // row2 = a, b
        // row3 = b, b
        // row4 = a, a

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(true,
                                                                       4,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowsUp_Indexes2and3and4to1() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 ? "b" : "a") : (rowIndex == 2 ? "a" : "b");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, b
        // row1 = b, b
        // row2 = a, a
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")}
                          });

        //Move row
        gridData.moveRowsTo(1,
                            Arrays.asList(gridRows[2], gridRows[3], gridRows[4]));

        // row0 = a, b
        // row1 = a, a
        // row2 = a, b
        // row3 = a, b
        // row4 = b, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  4,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       3,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "b")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowsUp_Indexes2and3to0() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 ? "b" : "a") : (rowIndex == 2 ? "a" : "b");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, b
        // row1 = b, b
        // row2 = a, a
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")}
                          });

        //Move row
        gridData.moveRowsTo(0,
                            Arrays.asList(gridRows[2], gridRows[3]));

        // row0 = a, a
        // row1 = a, b
        // row2 = a, b
        // row3 = b, b
        // row4 = a, b

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "a")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       4,
                                                                       "b")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "b")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "b")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowsUp_notAllRowsMergedAfterMoving() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex < 3 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = b, 0
        // row1 = b, 1
        // row2 = b, 2
        // row3 = a, 3
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        //Move row
        gridData.moveRowsTo(0,
                            Arrays.asList(gridRows[2], gridRows[3]));

        // row0 = b, 2
        // row1 = a, 3
        // row2 = b, 0
        // row3 = b, 1
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{false, false, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(true,
                                                  2,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowsUp_notAllRowsMergedBeforeMoving() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex % 2 == 1 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, 0
        // row1 = b, 1
        // row2 = a, 2
        // row3 = b, 3
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{false, false, false, false, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        //Move row
        gridData.moveRowsTo(2,
                            Arrays.asList(gridRows[3], gridRows[4]));

        // row0 = a, 0
        // row1 = b, 1
        // row2 = b, 3
        // row3 = a, 4
        // row4 = a, 2

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  2,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowsDown_notAllRowsMergedAfterMoving() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex < 3 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = b, 0
        // row1 = b, 1
        // row2 = b, 2
        // row3 = a, 3
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        //Move row
        gridData.moveRowsTo(3,
                            Arrays.asList(gridRows[0], gridRows[1]));

        // row0 = b, 2
        // row1 = a, 3
        // row2 = b, 0
        // row3 = b, 1
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{false, false, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(true,
                                                  2,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });
    }

    @Test
    public void testMergedBlock_MoveRowsDown_notAllRowsMergedBeforeMoving() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex % 2 == 1 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, 0
        // row1 = b, 1
        // row2 = a, 2
        // row3 = b, 3
        // row4 = a, 4

        assertGridIndexes(gridData,
                          new boolean[]{false, false, false, false, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        //Move row
        gridData.moveRowsTo(4,
                            Arrays.asList(gridRows[0], gridRows[1], gridRows[2]));

        // row0 = b, 3
        // row1 = a, 4
        // row2 = a, 0
        // row3 = b, 1
        // row4 = a, 2

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, false, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")}
                          });
    }
}
