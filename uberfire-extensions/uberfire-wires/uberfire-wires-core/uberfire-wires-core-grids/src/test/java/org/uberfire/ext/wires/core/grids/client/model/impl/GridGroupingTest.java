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

import org.junit.Test;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridTest.Expected.build;

public class GridGroupingTest extends BaseGridTest {

    @Test
    public void testInitialSetup() {
        constructGridData(2, 3);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            final GridRow row = gridData.getRow(rowIndex);
            assertFalse(row.isMerged());
            assertFalse(row.isCollapsed());
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final GridCell<?> cell = gridData.getCell(rowIndex,
                                                          columnIndex);
                assertFalse(cell.isMerged());
            }
        }

        assertEquals(3,
                     gridData.getRowCount());
    }

    @Test
    public void testGroup() {
        constructGridData(2, 3);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        gridData.setCellValue(1,
                              0,
                              new BaseGridCellValue<String>("(0, 0)"));

        //Group cells
        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false},
                          new boolean[]{false, true, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         2,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(true,
                                         0,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 1)")},
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 2)")}
                          });

        //Ungroup cells
        gridData.expandCell(0,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         2,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(true,
                                         0,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 1)")},
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 2)")}
                          });
    }

    @Test
    public void testGroupNotCombineWhenCellsValuesUpdatedAbove() {
        //Tests that cells with the same value do not combine into existing collapsed blocks
        //Test #1 - Update cells above the existing collapsed block
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        // [ (0,0) (1,0) ]
        // [ (0,1) (1,1) ]
        // [ (0,2) (1,2) ]
        // [ (0,2) (1,3) ]
        // [ (0,4) (1,4) ]

        gridData.setCellValue(3,
                              0,
                              new BaseGridCellValue<String>("(0, 2)"));

        //Group cells
        gridData.collapseCell(2,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{false, false, true, true, false},
                          new boolean[]{false, false, false, true, false},
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
                                                          "(1, 1)")},
                                  {build(true,
                                         2,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 2)")},
                                  {build(true,
                                         0,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 3)")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(false,
                                                          1,
                                                          "(1, 4)")}
                          });

        //Set cell above existing block (should not affect existing block)
        gridData.setCellValue(1,
                              0,
                              new BaseGridCellValue<String>("(0, 2)"));

        assertGridIndexes(gridData,
                          new boolean[]{false, false, true, true, false},
                          new boolean[]{false, false, false, true, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 1)")},
                                  {build(true,
                                         2,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 2)")},
                                  {build(true,
                                         0,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 3)")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(false,
                                                          1,
                                                          "(1, 4)")}
                          });

        //Set cell above existing block (should create a new block)
        gridData.setCellValue(0,
                              0,
                              new BaseGridCellValue<String>("(0, 2)"));

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, false, false, true, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         2,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(true,
                                         0,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 1)")},
                                  {build(true,
                                         2,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 2)")},
                                  {build(true,
                                         0,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 3)")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(false,
                                                          1,
                                                          "(1, 4)")}
                          });

        //Ungroup cell (should result in a single block spanning 4 rows)
        gridData.expandCell(2,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         4,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(true,
                                         0,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 1)")},
                                  {build(true,
                                         0,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 2)")},
                                  {build(true,
                                         0,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 3)")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(false,
                                                          1,
                                                          "(1, 4)")}
                          });
    }

    @Test
    public void testGroupNotCombineWhenCellsValuesUpdatedBelow() {
        //Tests that cells with the same value do not combine into existing collapsed blocks
        //Test #2 - Update cells below the existing collapsed block
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        // [ (0,0) (1,0) ]
        // [ (0,1) (1,1) ]
        // [ (0,1) (1,2) ]
        // [ (0,3) (1,3) ]
        // [ (0,4) (1,4) ]

        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("(0, 1)"));

        //Group cells
        gridData.collapseCell(1,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, false, false},
                          new boolean[]{false, false, true, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(true,
                                         2,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)")},
                                  {build(true,
                                         0,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 2)")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(false,
                                                          1,
                                                          "(1, 3)")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(false,
                                                          1,
                                                          "(1, 4)")}
                          });

        //Set cell below existing block (should not affect existing block)
        gridData.setCellValue(3,
                              0,
                              new BaseGridCellValue<String>("(0, 1)"));

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, false, false},
                          new boolean[]{false, false, true, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(true,
                                         2,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)")},
                                  {build(true,
                                         0,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 2)")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 3)")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(false,
                                                          1,
                                                          "(1, 4)")}
                          });

        //Set cell below existing block (should create a new block)
        gridData.setCellValue(4,
                              0,
                              new BaseGridCellValue<String>("(0, 1)"));

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, true, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(true,
                                         2,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)")},
                                  {build(true,
                                         0,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 2)")},
                                  {build(true,
                                         2,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 3)")},
                                  {build(true,
                                         0,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 4)")}
                          });

        //Ungroup cell (should result in a single block spanning 4 rows)
        gridData.expandCell(1,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(true,
                                         4,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)")},
                                  {build(true,
                                         0,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 2)")},
                                  {build(true,
                                         0,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 3)")},
                                  {build(true,
                                         0,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 4)")}
                          });
    }

    @Test
    public void testGroupOverlap_CollapseBlockWithinParent() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        // [   g1  (1,0) ]
        // [   g1    g2  ]      [   g1  (1,0) ]
        // [   g1    g2  ] ---> [   g1    g2  ] ---> [   g1  (1,0) ]
        // [   g1    g2  ]      [   g1  (1,4) ]
        // [   g1  (1,4) ]

        gridData.setCellValue(0,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(1,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(3,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(4,
                              0,
                              new BaseGridCellValue<String>("g1"));

        gridData.setCellValue(1,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(2,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(3,
                              1,
                              new BaseGridCellValue<String>("g2"));

        //Group g2
        gridData.collapseCell(1,
                              1);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, true, true, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         5,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 0)")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      3,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 4)")}
                          });

        //Group g1
        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, true, true},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         5,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 0)")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      3,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 4)")}
                          });

        //Ungroup g1
        gridData.expandCell(0,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, true, true, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         5,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 0)")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      3,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 4)")}
                          });
    }

    @Test
    public void testGroupOverlap_CollapseRightColumn_SingleCellOverlap_SplitBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        // [ (0,0)   g2  ]
        // [ (0,1)   g2  ]      [ (0,0)   g2  ]      [ (0,0)   g2  ]      [ (0,0)   g2  ]
        // [   g1    g2  ] ---> [   g1  (1,3) ] ---> [   g1  (1,3) ] ---> [   g1  (1,3) ]
        // [   g1  (1,3) ]      [   g1  (1,4) ]                           [   g1  (1,4) ]
        // [   g1  (1,4) ]

        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(3,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(4,
                              0,
                              new BaseGridCellValue<String>("g1"));

        gridData.setCellValue(0,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(1,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(2,
                              1,
                              new BaseGridCellValue<String>("g2"));

        //Group g2 - should split g1
        gridData.collapseCell(0,
                              1);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(true,
                                                          3,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         2,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 3)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 4)")}
                          });

        //Group g1
        gridData.collapseCell(3,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, false, true},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(true,
                                                          3,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         2,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 3)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 4)")}
                          });

        //Ungroup g1
        gridData.expandCell(3,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(true,
                                                          3,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         2,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 3)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 4)")}
                          });
    }

    @Test
    public void testGroupOverlap_CollapseLeftColumn_SingleCellOverlap_SplitBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        // [   g1  (1,0) ]
        // [   g1  (1,1) ]      [   g1  (1,0) ]                           [   g1  (1,0) ]
        // [   g1    g2  ] ---> [ (0,3)   g2  ] ---> [ (0,3)   g2  ] ---> [ (0,3)   g2  ]
        // [ (0,3)   g2  ]      [ (0,4)   g2  ]                           [ (0,4)   g2  ]
        // [ (0,4)   g2  ]

        gridData.setCellValue(0,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(1,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("g1"));

        gridData.setCellValue(2,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(3,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(4,
                              1,
                              new BaseGridCellValue<String>("g2"));

        //Group g1 - should split g2
        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         3,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 0)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 1)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          2,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });

        //Group g2
        gridData.collapseCell(3,
                              1);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, false, true},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         3,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 0)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 1)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          2,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });

        //Ungroup g2
        gridData.expandCell(3,
                            1);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         3,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 0)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 1)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          2,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });
    }

    @Test
    public void testGroupOverlap_ChildBlockCoversTableExtents() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        // [  g1    g2 ]
        // [  g1    g2 ]
        // [ (0,2)  g2 ]
        // [ (0,3)  g2 ]
        // [ (0,4)  g2 ]

        gridData.setCellValue(0,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(1,
                              0,
                              new BaseGridCellValue<String>("g1"));

        gridData.setCellValue(0,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(1,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(2,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(3,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(4,
                              1,
                              new BaseGridCellValue<String>("g2"));

        //Group g1
        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         2,
                                         "g1"), build(true,
                                                      5,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });

        //Ungroup g1
        gridData.expandCell(0,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         2,
                                         "g1"), build(true,
                                                      5,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });
    }

    @Test
    public void testGroupOverlap_CollapseLeftColumn_SingleCellOverlapMidTable_SplitBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        // [ (0,0)  (1,0) ]
        // [  g1    (1,1) ]
        // [  g1      g2  ]
        // [ (0,3)    g2  ]
        // [ (0,4)    g2  ]

        gridData.setCellValue(1,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("g1"));

        gridData.setCellValue(2,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(3,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(4,
                              1,
                              new BaseGridCellValue<String>("g2"));

        //Group g1 - should split g2
        gridData.collapseCell(1,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, true, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(true,
                                         2,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 1)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          2,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });

        //Ungroup g1
        gridData.expandCell(1,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(true,
                                         2,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 1)")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      3,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });
    }

    @Test
    public void testGroupOverlap_CollapseLeftColumn_SubExtentOverlap_NoSplitBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        // [ (0,0)  (1,0) ]
        // [  g1      g2  ]
        // [  g1      g2  ]
        // [ (0,3)    g2  ]
        // [ (0,4)    g2  ]

        gridData.setCellValue(1,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("g1"));

        gridData.setCellValue(1,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(2,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(3,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(4,
                              1,
                              new BaseGridCellValue<String>("g2"));

        //Group g1 - doesn't need to split g2 since it spans all of g1
        gridData.collapseCell(1,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, true, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(true,
                                         2,
                                         "g1"), build(true,
                                                      4,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });

        //Ungroup g1
        gridData.expandCell(1,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(true,
                                         2,
                                         "g1"), build(true,
                                                      4,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });
    }

    @Test
    public void testGroupOverlap_CollapseLeftColumn_MultipleCellOverlap_SplitBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        // [   g1   (1,0) ]
        // [   g1     g2  ]      [   g1  (1,0) ]
        // [   g1     g2  ] ---> [ (0,3)   g2  ]
        // [ (0,3)    g2  ]      [ (0,4)   g2  ]
        // [ (0,4)    g2  ]

        gridData.setCellValue(0,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(1,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("g1"));

        gridData.setCellValue(1,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(2,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(3,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(4,
                              1,
                              new BaseGridCellValue<String>("g2"));

        //Group g1 - should split g2
        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         3,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 0)")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      2,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          2,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });

        //Ungroup g1
        gridData.expandCell(0,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         3,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 0)")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      4,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });
    }

    @Test
    public void testGroupOverlap_CollapseWholeTable() {
        constructGridData(2, 5);

        // [   g1    g2  ]
        // [   g1    g2  ]
        // [   g1    g2  ] ---> [   g1    g2  ]
        // [   g1    g2  ]
        // [   g1    g2  ]

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            gridData.setCellValue(rowIndex,
                                  0,
                                  new BaseGridCellValue<String>("g1"));
            gridData.setCellValue(rowIndex,
                                  1,
                                  new BaseGridCellValue<String>("g2"));
        }

        //Group g1
        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, true, true},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         5,
                                         "g1"), build(true,
                                                      5,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")}
                          });

        //Ungroup g1
        gridData.expandCell(0,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         5,
                                         "g1"), build(true,
                                                      5,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")}
                          });
    }

    @Test
    public void testGroupOverlap_CollapseWholeTableExceptLastRow() {
        constructGridData(2, 5);

        // [   g1    g2  ]
        // [   g1    g2  ]
        // [   g1    g2  ] ---> [   g1    g2  ]
        // [   g1    g2  ]      [ (0,4) (1,4) ]
        // [ (0,4) (1,4) ]

        for (int rowIndex = 0; rowIndex < gridData.getRowCount() - 1; rowIndex++) {
            gridData.setCellValue(rowIndex,
                                  0,
                                  new BaseGridCellValue<String>("g1"));
            gridData.setCellValue(rowIndex,
                                  1,
                                  new BaseGridCellValue<String>("g2"));
        }

        gridData.setCellValue(4,
                              0,
                              new BaseGridCellValue<String>("(0, 4)"));
        gridData.setCellValue(4,
                              1,
                              new BaseGridCellValue<String>("(1, 4)"));

        //Group g1
        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, true, true, true, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         4,
                                         "g1"), build(true,
                                                      4,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(false,
                                                          1,
                                                          "(1, 4)")}
                          });

        //Ungroup g1
        gridData.expandCell(0,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         4,
                                         "g1"), build(true,
                                                      4,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(false,
                                                          1,
                                                          "(1, 4)")}
                          });
    }

    @Test
    public void testGroupOverlap_CollapseLeftColumn_SingleCellOverlapBottom_SplitBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        // [   g1  (1,0) ]
        // [   g1  (1,1) ]      [   g1  (1,0) ]      [   g1  (1,0) ]      [   g1  (1,0) ]      [   g1  (1,0) ]
        // [   g1    g2  ] ---> [ (0,3)   g2  ] ---> [ (0,3)   g2  ] ---> [   g1  (1,1) ] ---> [ (0,3)   g2  ]
        // [ (0,3)   g2  ]      [ (0,4)   g2  ]                           [   g1    g2  ]
        // [ (0,4)   g2  ]                                                [ (0,3)   g2  ]

        gridData.setCellValue(0,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(1,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("g1"));

        gridData.setCellValue(2,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(3,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(4,
                              1,
                              new BaseGridCellValue<String>("g2"));

        //Group g1 - should split g2
        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         3,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 0)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 1)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          2,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });

        //Group g2
        gridData.collapseCell(3,
                              1);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, false, true},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         3,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 0)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 1)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          2,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });

        //Ungroup g1 - should not recombine g2 as it has been split and collapsed
        gridData.expandCell(0,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, true},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         3,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 0)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 1)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          2,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });

        //Group g1 - check re-applying collapse preserves indexing
        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, false, true},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         3,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 0)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 1)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          2,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });

        //Ungroup g1 - check re-applying collapse preserves indexing
        gridData.expandCell(0,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, true},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         3,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 0)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 1)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          2,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });
    }

    @Test
    public void testGroupOverlap_CollapseRightColumn_ChildSubExtent() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        // [   g1    g2  ]                           [   g1    g2  ]
        // [   g1    g2  ]                           [   g1    g2  ]
        // [   g1    g2  ] ---> [   g1    g2  ] ---> [   g1    g2  ]
        // [ (0,3)   g2  ]                           [ (0,3)   g2  ]
        // [ (0,4)   g2  ]                           [ (0,4)   g2  ]

        gridData.setCellValue(0,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(1,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("g1"));

        gridData.setCellValue(0,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(1,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(2,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(3,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(4,
                              1,
                              new BaseGridCellValue<String>("g2"));

        //Group g2
        gridData.collapseCell(0,
                              1);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, true, true},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         3,
                                         "g1"), build(true,
                                                      5,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });

        //Ungroup g1 - should result in g2 being split and collapsed
        gridData.expandCell(0,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, true},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         3,
                                         "g1"), build(true,
                                                      3,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          2,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });

        //Ungroup g2 - should restore to original configuration
        gridData.expandCell(3,
                            1);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         3,
                                         "g1"), build(true,
                                                      5,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });
    }

    @Test
    public void testGroupOverlap_CollapseLeftColumn_MultipleCellOverlapTableExtent_SplitBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        // [   g1    g2  ]                           [   g1    g2  ]
        // [   g1    g2  ]      [   g1    g2  ]      [   g1    g2  ]
        // [   g1    g2  ] ---> [ (0,3)   g2  ] ---> [   g1    g2  ]
        // [ (0,3)   g2  ]      [ (0,4)   g2  ]      [ (0,3)   g2  ]
        // [ (0,4)   g2  ]                           [ (0,4)   g2  ]

        gridData.setCellValue(0,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(1,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("g1"));

        gridData.setCellValue(0,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(1,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(2,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(3,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(4,
                              1,
                              new BaseGridCellValue<String>("g2"));

        //Group g1
        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         3,
                                         "g1"), build(true,
                                                      5,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });

        //Ungroup g2
        gridData.expandCell(0,
                            1);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         3,
                                         "g1"), build(true,
                                                      5,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });

        //Group g2
        gridData.collapseCell(0,
                              1);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, true, true},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         3,
                                         "g1"), build(true,
                                                      5,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2")}
                          });
    }

    @Test
    public void testGroupOverlap_CollapseLeftColumn_SingleCellOverlapTop_SplitBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        // [ (0,0)   g2  ]
        // [ (0,1)   g2  ]      [ (0,0)   g2  ]
        // [   g1    g2  ] ---> [ (0,1)   g2  ] ---> [ (0,0)   g2  ]
        // [   g1  (1,3) ]      [   g1    g2  ]      [   g1    g2  ]
        // [   g1  (1,4) ]

        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(3,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(4,
                              0,
                              new BaseGridCellValue<String>("g1"));

        gridData.setCellValue(0,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(1,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(2,
                              1,
                              new BaseGridCellValue<String>("g2"));

        //Group g1 - should split g2
        gridData.collapseCell(2,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, true, true},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(true,
                                                          2,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(true,
                                         3,
                                         "g1"), build(false,
                                                      1,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 3)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 4)")}
                          });

        //Group g2
        gridData.collapseCell(0,
                              1);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, false, true, true},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(true,
                                                          2,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(true,
                                         3,
                                         "g1"), build(false,
                                                      1,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 3)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 4)")}
                          });

        //Ungroup g1 - g2 should remain split
        gridData.expandCell(2,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(true,
                                                          2,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(true,
                                         3,
                                         "g1"), build(false,
                                                      1,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 3)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 4)")}
                          });

        //Ungroup g2 - g2 should not be split
        gridData.expandCell(0,
                            1);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(true,
                                                          3,
                                                          "g2")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(true,
                                                          0,
                                                          "g2")},
                                  {build(true,
                                         3,
                                         "g1"), build(true,
                                                      0,
                                                      "g2")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 3)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 4)")}
                          });
    }

    @Test
    public void testGroupOverlap_CollapseRightColumn_SingleCellOverlapBottom_NestedSplitBlocks() {
        constructGridData(3, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        // [ (0,0) (1,0)   g3  ]
        // [ (0,1) (1,1)   g3  ]      [ (0,0) (1,0)   g3  ]
        // [   g1    g2    g3  ] ---> [   g1    g2  (2,3) ] ---> [ (0,0) (1,0)   g3  ]
        // [   g1    g2  (2,3) ]      [ (0,4)   g2  (2,4) ]      [   g1    g2  (2,3) ]
        // [ (0,4)   g2  (2,4) ]

        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(3,
                              0,
                              new BaseGridCellValue<String>("g1"));

        gridData.setCellValue(2,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(3,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(4,
                              1,
                              new BaseGridCellValue<String>("g2"));

        gridData.setCellValue(0,
                              2,
                              new BaseGridCellValue<String>("g3"));
        gridData.setCellValue(1,
                              2,
                              new BaseGridCellValue<String>("g3"));
        gridData.setCellValue(2,
                              2,
                              new BaseGridCellValue<String>("g3"));

        //Check initial setup
        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)"), build(true,
                                                                           3,
                                                                           "g3")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)"), build(true,
                                                                           0,
                                                                           "g3")},
                                  {build(true,
                                         2,
                                         "g1"), build(true,
                                                      3,
                                                      "g2"), build(true,
                                                                   0,
                                                                   "g3")},
                                  {build(true,
                                         0,
                                         "g1"), build(true,
                                                      0,
                                                      "g2"), build(false,
                                                                   1,
                                                                   "(2, 3)")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2"), build(false,
                                                                       1,
                                                                       "(2, 4)")}
                          });

        //Group g3 - should split g1 and g2
        gridData.collapseCell(0,
                              2);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)"), build(true,
                                                                           3,
                                                                           "g3")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)"), build(true,
                                                                           0,
                                                                           "g3")},
                                  {build(false,
                                         1,
                                         "g1"), build(false,
                                                      1,
                                                      "g2"), build(true,
                                                                   0,
                                                                   "g3")},
                                  {build(false,
                                         1,
                                         "g1"), build(true,
                                                      2,
                                                      "g2"), build(false,
                                                                   1,
                                                                   "(2, 3)")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2"), build(false,
                                                                       1,
                                                                       "(2, 4)")}
                          });

        //Group g2
        gridData.collapseCell(3,
                              1);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, false, true},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)"), build(true,
                                                                           3,
                                                                           "g3")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)"), build(true,
                                                                           0,
                                                                           "g3")},
                                  {build(false,
                                         1,
                                         "g1"), build(false,
                                                      1,
                                                      "g2"), build(true,
                                                                   0,
                                                                   "g3")},
                                  {build(false,
                                         1,
                                         "g1"), build(true,
                                                      2,
                                                      "g2"), build(false,
                                                                   1,
                                                                   "(2, 3)")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(true,
                                                          0,
                                                          "g2"), build(false,
                                                                       1,
                                                                       "(2, 4)")}
                          });
    }

    @Test
    public void testGroupOverlap_CollapseLeftColumn_StaggeredSingleCellOverlapTop_NestedSplitBlocks() {
        constructGridData(3, 7);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        // [ (0,0) (1,0)   g3  ]
        // [ (0,1) (1,1)   g3  ]      [ (0,0) (1,0)   g3  ]      [ (0,0) (1,0)   g3  ]
        // [ (0,2)   g2    g3) ]      [ (0,1) (1,1)   g3  ]      [ (0,1) (1,1)   g3  ]      [ (0,0) (1,0)   g3  ]
        // [ (0,3)   g2  (2,3) ] ---> [ (0,2)   g2    g3  ] ---> [ (0,2)   g2    g3  ] ---> [   g1    g2  (2,4) ]
        // [   g1    g2  (2,4) ]      [ (0,3)   g2  (2,3) ]      [   g1    g2  (2,4) ]
        // [   g1  (1,5) (2,5) ]      [   g1    g2  (2,4) ]
        // [   g1  (1,6) (2,6) ]

        gridData.setCellValue(4,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(5,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(6,
                              0,
                              new BaseGridCellValue<String>("g1"));

        gridData.setCellValue(2,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(3,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(4,
                              1,
                              new BaseGridCellValue<String>("g2"));

        gridData.setCellValue(0,
                              2,
                              new BaseGridCellValue<String>("g3"));
        gridData.setCellValue(1,
                              2,
                              new BaseGridCellValue<String>("g3"));
        gridData.setCellValue(2,
                              2,
                              new BaseGridCellValue<String>("g3"));

        //Check initial setup
        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true, true, true},
                          new boolean[]{false, false, false, false, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)"), build(true,
                                                                           3,
                                                                           "g3")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)"), build(true,
                                                                           0,
                                                                           "g3")},
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(true,
                                                          3,
                                                          "g2"), build(true,
                                                                       0,
                                                                       "g3")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          0,
                                                          "g2"), build(false,
                                                                       1,
                                                                       "(2, 3)")},
                                  {build(true,
                                         3,
                                         "g1"), build(true,
                                                      0,
                                                      "g2"), build(false,
                                                                   1,
                                                                   "(2, 4)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 5)"), build(false,
                                                                       1,
                                                                       "(2, 5)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 6)"), build(false,
                                                                       1,
                                                                       "(2, 6)")}
                          });

        //Group g1 - should split g2
        gridData.collapseCell(4,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true, true, true},
                          new boolean[]{false, false, false, false, false, true, true},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)"), build(true,
                                                                           3,
                                                                           "g3")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)"), build(true,
                                                                           0,
                                                                           "g3")},
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(true,
                                                          2,
                                                          "g2"), build(true,
                                                                       0,
                                                                       "g3")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          0,
                                                          "g2"), build(false,
                                                                       1,
                                                                       "(2, 3)")},
                                  {build(true,
                                         3,
                                         "g1"), build(false,
                                                      1,
                                                      "g2"), build(false,
                                                                   1,
                                                                   "(2, 4)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 5)"), build(false,
                                                                       1,
                                                                       "(2, 5)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 6)"), build(false,
                                                                       1,
                                                                       "(2, 6)")}
                          });

        //Group g2 - should split g1
        gridData.collapseCell(2,
                              1);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true, true, true},
                          new boolean[]{false, false, false, true, false, true, true},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)"), build(true,
                                                                           2,
                                                                           "g3")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)"), build(true,
                                                                           0,
                                                                           "g3")},
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(true,
                                                          2,
                                                          "g2"), build(false,
                                                                       1,
                                                                       "g3")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          0,
                                                          "g2"), build(false,
                                                                       1,
                                                                       "(2, 3)")},
                                  {build(true,
                                         3,
                                         "g1"), build(false,
                                                      1,
                                                      "g2"), build(false,
                                                                   1,
                                                                   "(2, 4)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 5)"), build(false,
                                                                       1,
                                                                       "(2, 5)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 6)"), build(false,
                                                                       1,
                                                                       "(2, 6)")}
                          });

        //Group g3
        gridData.collapseCell(0,
                              2);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true, true, true},
                          new boolean[]{false, true, false, true, false, true, true},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)"), build(true,
                                                                           2,
                                                                           "g3")},
                                  {build(false,
                                         1,
                                         "(0, 1)"), build(false,
                                                          1,
                                                          "(1, 1)"), build(true,
                                                                           0,
                                                                           "g3")},
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(true,
                                                          2,
                                                          "g2"), build(false,
                                                                       1,
                                                                       "g3")},
                                  {build(false,
                                         1,
                                         "(0, 3)"), build(true,
                                                          0,
                                                          "g2"), build(false,
                                                                       1,
                                                                       "(2, 3)")},
                                  {build(true,
                                         3,
                                         "g1"), build(false,
                                                      1,
                                                      "g2"), build(false,
                                                                   1,
                                                                   "(2, 4)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 5)"), build(false,
                                                                       1,
                                                                       "(2, 5)")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 6)"), build(false,
                                                                       1,
                                                                       "(2, 6)")}
                          });
    }

    @Test
    public void testGroupOverlap_CollapseLeftColumn_MultipleCellOverlap_NestedSplitBlocks() {
        constructGridData(3, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        // [ (0,0)   g2    g3  ]
        // [   g1    g2    g3  ]      [ (0,0)   g2    g3  ]      [ (0,0)   g2    g3  ]
        // [   g1  (1,2)   g3  ] ---> [   g1  (1,2)   g3  ] ---> [   g1  (1,3) (2,3) ]
        // [   g1  (1,3) (2,3) ]      [   g1  (1,3) (2,3) ]      [ (0,4) (1,4) (2,4) ]
        // [ (0,4) (1,4) (2,4) ]      [ (0,4) (1,4) (2,4) ]

        gridData.setCellValue(1,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("g1"));
        gridData.setCellValue(3,
                              0,
                              new BaseGridCellValue<String>("g1"));

        gridData.setCellValue(0,
                              1,
                              new BaseGridCellValue<String>("g2"));
        gridData.setCellValue(1,
                              1,
                              new BaseGridCellValue<String>("g2"));

        gridData.setCellValue(0,
                              2,
                              new BaseGridCellValue<String>("g3"));
        gridData.setCellValue(1,
                              2,
                              new BaseGridCellValue<String>("g3"));
        gridData.setCellValue(2,
                              2,
                              new BaseGridCellValue<String>("g3"));

        //Check initial setup
        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(true,
                                                          2,
                                                          "g2"), build(true,
                                                                       3,
                                                                       "g3")},
                                  {build(true,
                                         3,
                                         "g1"), build(true,
                                                      0,
                                                      "g2"), build(true,
                                                                   0,
                                                                   "g3")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 2)"), build(true,
                                                                       0,
                                                                       "g3")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 3)"), build(false,
                                                                       1,
                                                                       "(2, 3)")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(false,
                                                          1,
                                                          "(1, 4)"), build(false,
                                                                           1,
                                                                           "(2, 4)")}
                          });

        //Group g2 - should split g1 but not g3
        gridData.collapseCell(0,
                              1);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, true, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(true,
                                                          2,
                                                          "g2"), build(true,
                                                                       3,
                                                                       "g3")},
                                  {build(false,
                                         1,
                                         "g1"), build(true,
                                                      0,
                                                      "g2"), build(true,
                                                                   0,
                                                                   "g3")},
                                  {build(true,
                                         2,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 2)"), build(true,
                                                                       0,
                                                                       "g3")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 3)"), build(false,
                                                                       1,
                                                                       "(2, 3)")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(false,
                                                          1,
                                                          "(1, 4)"), build(false,
                                                                           1,
                                                                           "(2, 4)")}
                          });

        //Group g1 - should split g3
        gridData.collapseCell(2,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, true, false, true, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(true,
                                                          2,
                                                          "g2"), build(true,
                                                                       2,
                                                                       "g3")},
                                  {build(false,
                                         1,
                                         "g1"), build(true,
                                                      0,
                                                      "g2"), build(true,
                                                                   0,
                                                                   "g3")},
                                  {build(true,
                                         2,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 2)"), build(false,
                                                                       1,
                                                                       "g3")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 3)"), build(false,
                                                                       1,
                                                                       "(2, 3)")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(false,
                                                          1,
                                                          "(1, 4)"), build(false,
                                                                           1,
                                                                           "(2, 4)")}
                          });

        //Ungroup g1 - g3 should remain split as we don't merge into collapsed cells
        gridData.expandCell(2,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, true, false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(true,
                                                          2,
                                                          "g2"), build(true,
                                                                       2,
                                                                       "g3")},
                                  {build(false,
                                         1,
                                         "g1"), build(true,
                                                      0,
                                                      "g2"), build(true,
                                                                   0,
                                                                   "g3")},
                                  {build(true,
                                         2,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 2)"), build(false,
                                                                       1,
                                                                       "g3")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 3)"), build(false,
                                                                       1,
                                                                       "(2, 3)")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(false,
                                                          1,
                                                          "(1, 4)"), build(false,
                                                                           1,
                                                                           "(2, 4)")}
                          });

        //Group g1 (again) - there should be no change in state, other than an additional collapsed row
        gridData.collapseCell(2,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, true, false, true, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(true,
                                                          2,
                                                          "g2"), build(true,
                                                                       2,
                                                                       "g3")},
                                  {build(false,
                                         1,
                                         "g1"), build(true,
                                                      0,
                                                      "g2"), build(true,
                                                                   0,
                                                                   "g3")},
                                  {build(true,
                                         2,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 2)"), build(false,
                                                                       1,
                                                                       "g3")},
                                  {build(true,
                                         0,
                                         "g1"), build(false,
                                                      1,
                                                      "(1, 3)"), build(false,
                                                                       1,
                                                                       "(2, 3)")},
                                  {build(false,
                                         1,
                                         "(0, 4)"), build(false,
                                                          1,
                                                          "(1, 4)"), build(false,
                                                                           1,
                                                                           "(2, 4)")}
                          });
    }

    @Test
    public void testGroupUpdateCellValue() {
        constructGridData(2, 3);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        gridData.setCellValue(1,
                              0,
                              new BaseGridCellValue<String>("(0, 0)"));

        //Group cells
        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false},
                          new boolean[]{false, true, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         2,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(true,
                                         0,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 1)")},
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 2)")}
                          });

        //Update cell value
        gridData.setCellValue(0,
                              0,
                              new BaseGridCellValue<String>("<changed>"));

        //Ungroup cells
        gridData.expandCell(0,
                            0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         2,
                                         "<changed>"), build(false,
                                                             1,
                                                             "(1, 0)")},
                                  {build(true,
                                         0,
                                         "<changed>"), build(false,
                                                             1,
                                                             "(1, 1)")},
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 2)")}
                          });
    }

    @Test
    public void testGroupMovedColumnUpdateCellValue() {
        constructGridData(2, 3);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        gridData.setCellValue(1,
                              0,
                              new BaseGridCellValue<String>("(0, 0)"));

        //Group cells
        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false},
                          new boolean[]{false, true, false},
                          new BaseGridTest.Expected[][]{
                                  {build(true,
                                         2,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 0)")},
                                  {build(true,
                                         0,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(1, 1)")},
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 2)")}
                          });

        //Move column
        gridData.moveColumnTo(1,
                              gridColumns[0]);

        //Update cell value
        gridData.setCellValue(0,
                              1,
                              new BaseGridCellValue<String>("<changed>"));

        //Ungroup cells
        gridData.expandCell(0,
                            1);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new BaseGridTest.Expected[][]{
                                  {build(false,
                                         1,
                                         "(1, 0)"), build(true,
                                                          2,
                                                          "<changed>")},
                                  {build(false,
                                         1,
                                         "(1, 1)"), build(true,
                                                          0,
                                                          "<changed>")},
                                  {build(false,
                                         1,
                                         "(1, 2)"), build(false,
                                                          1,
                                                          "(0, 2)")}
                          });
    }

    @Test
    public void testMergedDeleteCellValue() {
        constructGridData(2, 3);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        gridData.setCellValue(1,
                              0,
                              new BaseGridCellValue<String>("(0, 0)"));

        //Group cells
        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false},
                          new boolean[]{false, true, false},
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
        gridData.deleteCell(0,
                            0);

        assertGridIndexes(gridData,
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
    public void testRemoveRowIndex0FromGroupedBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("(0, 1)"));
        gridData.setCellValue(3,
                              0,
                              new BaseGridCellValue<String>("(0, 1)"));

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 4)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });

        gridData.collapseCell(1,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, false},
                          new boolean[]{false, false, true, true, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 4)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });

        final GridData.Range rows = gridData.deleteRow(0);
        assertEquals(0,
                     rows.getMinRowIndex());
        assertEquals(0,
                     rows.getMaxRowIndex());

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, false},
                          new boolean[]{false, true, true, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
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
    public void testRemoveRowIndex1FromGroupedBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("(0, 1)"));
        gridData.setCellValue(3,
                              0,
                              new BaseGridCellValue<String>("(0, 1)"));

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 4)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });

        gridData.collapseCell(1,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, false},
                          new boolean[]{false, false, true, true, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 4)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });

        final GridData.Range rows = gridData.deleteRow(1);
        assertEquals(1,
                     rows.getMinRowIndex());
        assertEquals(3,
                     rows.getMaxRowIndex());

        assertGridIndexes(gridData,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 4)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });
    }

    @Test
    public void testRemoveRowIndex2FromGroupedBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("(0, 1)"));
        gridData.setCellValue(3,
                              0,
                              new BaseGridCellValue<String>("(0, 1)"));

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 4)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });

        gridData.collapseCell(1,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, false},
                          new boolean[]{false, false, true, true, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 4)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });

        final GridData.Range rows = gridData.deleteRow(2);
        assertEquals(1,
                     rows.getMinRowIndex());
        assertEquals(3,
                     rows.getMaxRowIndex());

        assertGridIndexes(gridData,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 4)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });
    }

    @Test
    public void testRemoveRowIndex3FromGroupedBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("(0, 1)"));
        gridData.setCellValue(3,
                              0,
                              new BaseGridCellValue<String>("(0, 1)"));

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 4)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });

        gridData.collapseCell(1,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, false},
                          new boolean[]{false, false, true, true, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 4)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });

        final GridData.Range rows = gridData.deleteRow(3);
        assertEquals(1,
                     rows.getMinRowIndex());
        assertEquals(3,
                     rows.getMaxRowIndex());

        assertGridIndexes(gridData,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 4)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });
    }

    @Test
    public void testRemoveRowIndex4FromGroupedBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("(0, 1)"));
        gridData.setCellValue(3,
                              0,
                              new BaseGridCellValue<String>("(0, 1)"));

        // (0, 0), (1, 0)
        // (0, 1), (1, 1)
        // (0, 1), (1, 2)
        // (0, 1), (1, 3)
        // (0, 4), (1, 4)

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 4)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });

        gridData.collapseCell(1,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, false},
                          new boolean[]{false, false, true, true, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")},
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 4)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 4)")}
                          });

        final GridData.Range rows = gridData.deleteRow(4);
        assertEquals(4,
                     rows.getMinRowIndex());
        assertEquals(4,
                     rows.getMaxRowIndex());

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true},
                          new boolean[]{false, false, true, true},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")},
                                  {Expected.build(true,
                                                  3,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 1)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 2)")},
                                  {Expected.build(true,
                                                  0,
                                                  "(0, 1)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 3)")}
                          });
    }

    @Test
    public void testRemoveOnlyRow() {
        constructGridData(2, 1);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{false},
                          new boolean[]{false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "(0, 0)"), Expected.build(false,
                                                                            1,
                                                                            "(1, 0)")}
                          });

        final GridData.Range rows = gridData.deleteRow(0);
        assertEquals(0,
                     rows.getMinRowIndex());
        assertEquals(0,
                     rows.getMaxRowIndex());

        assertEquals(0,
                     gridData.getRowCount());
    }

    @Test
    public void testRemoveAllRows() {
        constructGridData(2, 3);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        gridData.setCellValue(1,
                              0,
                              new BaseGridCellValue<String>("(0, 0)"));
        gridData.setCellValue(2,
                              0,
                              new BaseGridCellValue<String>("(0, 0)"));

        assertGridIndexes(gridData,
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
                                                                            "(1, 2)")}
                          });

        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true},
                          new boolean[]{false, true, true},
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
                                                                            "(1, 2)")}
                          });

        final GridData.Range rows = gridData.deleteRow(1);
        assertEquals(0,
                     rows.getMinRowIndex());
        assertEquals(2,
                     rows.getMaxRowIndex());

        assertEquals(0,
                     gridData.getRowCount());
    }

    @Test
    public void testGrouped_MoveUp_Rowsx3ToIndex0_Blockx3Rows() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 0 || rowIndex == 4 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = b, 0
        // row1 = a, 1 } Collapse (Lead)
        // row2 = a, 2 } Collapse (Child)
        // row3 = a, 3 } Collapse (Child)
        // row4 = b, 4

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  3,
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

        //Collapse cell
        gridData.collapseCell(1,
                              0);

        //Move row
        gridData.moveRowsTo(0,
                            new ArrayList<GridRow>() {{
                                add(gridRows[1]);
                                add(gridRows[2]);
                                add(gridRows[3]);
                            }});

        // row0 = a, 1 } Collapse (Lead)
        // row1 = a, 2 } Collapse (Child)
        // row2 = a, 3 } Collapse (Child)
        // row3 = b, 0
        // row4 = b, 4

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, true, true, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  3,
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
                                  {Expected.build(true,
                                                  2,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });
    }

    @Test
    public void testGrouped_MoveUp_Rowsx2ToIndex1_Blockx2Rows() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 || rowIndex == 4 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, 0
        // row1 = b, 1
        // row2 = a, 2 } Collapse (Lead)
        // row3 = a, 3 } Collapse (Child)
        // row4 = b, 4

        assertGridIndexes(gridData,
                          new boolean[]{false, false, true, true, false},
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
                                                  2,
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

        //Collapse cell
        gridData.collapseCell(2,
                              0);

        //Move row
        gridData.moveRowsTo(1,
                            new ArrayList<GridRow>() {{
                                add(gridRows[2]);
                                add(gridRows[3]);
                            }});

        // row0 = a, 0 } Should remain unchanged
        // row1 = a, 2 } Collapse (Lead)
        // row2 = a, 3 } Collapse (Child)
        // row3 = b, 1
        // row4 = b, 4

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, true, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(true,
                                                  2,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });
    }

    @Test
    public void testGrouped_MoveUp_Rowsx2ToIndex0_Blockx2Rows() {
        constructGridData(2, 6);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 || rowIndex == 4 ? "a" : "b") : "c";
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = b, c
        // row1 = a, c
        // row2 = b, c } Collapse (Lead)
        // row3 = b, c } Collapse (Child)
        // row4 = a, c
        // row5 = b, c

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true, true},
                          new boolean[]{false, false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       6,
                                                                       "c")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "c")},
                                  {Expected.build(true,
                                                  2,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "c")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "c")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "c")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "c")}
                          });

        //Collapse cell
        gridData.collapseCell(2,
                              0);

        //Move row
        gridData.moveRowsTo(0,
                            new ArrayList<GridRow>() {{
                                add(gridRows[2]);
                                add(gridRows[3]);
                            }});

        // row0 = b, c } Collapse (Lead)
        // row1 = b, c } Collapse (Child)
        // row2 = b, c } Should remain unchanged
        // row3 = a, c
        // row4 = a, c
        // row5 = b, c

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true, true},
                          new boolean[]{false, true, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "b"), Expected.build(true,
                                                                       2,
                                                                       "c")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "c")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       4,
                                                                       "c")},
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "c")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "c")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "c")}
                          });
    }

    @Test
    public void testGrouped_MoveUp_Rowsx2ToIndex0_Blockx2Rows_MakeNewSplitBlock() {
        constructGridData(2, 6);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 || rowIndex == 4 ? "a" : "b") : (rowIndex == 0 ? "d" : "c");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = b, d
        // row1 = a, c
        // row2 = b, c } Collapse (Lead)
        // row3 = b, c } Collapse (Child)
        // row4 = a, c
        // row5 = b, c

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true, true},
                          new boolean[]{false, false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "d")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       5,
                                                                       "c")},
                                  {Expected.build(true,
                                                  2,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "c")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "c")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "c")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "c")}
                          });

        //Collapse cell
        gridData.collapseCell(2,
                              0);

        //Move row
        gridData.moveRowsTo(0,
                            new ArrayList<GridRow>() {{
                                add(gridRows[2]);
                                add(gridRows[3]);
                            }});

        // row0 = b, c } Collapse (Lead)
        // row1 = b, c } Collapse (Child)
        // row2 = b, d } Should remain unchanged
        // row3 = a, c
        // row4 = a, c
        // row5 = b, c

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false, true, true, true},
                          new boolean[]{false, true, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "b"), Expected.build(true,
                                                                       2,
                                                                       "c")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "c")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "d")},
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(true,
                                                                       3,
                                                                       "c")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "c")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "c")}
                          });
    }

    @Test
    public void testGrouped_MoveUp_Rowsx2ToIndex0_Blockx2Rows_MakeNewMergedBlock() {
        constructGridData(2, 6);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 || rowIndex == 4 ? "a" : "b") : (rowIndex == 0 || rowIndex == 3 ? "d" : "c");
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = b, d
        // row1 = a, c
        // row2 = b, c } Collapse (Lead)
        // row3 = b, d } Collapse (Child)
        // row4 = a, c
        // row5 = b, c

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true, true},
                          new boolean[]{false, false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "d")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "c")},
                                  {Expected.build(true,
                                                  2,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "c")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "d")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(true,
                                                                       2,
                                                                       "c")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "c")}
                          });

        //Collapse cell
        gridData.collapseCell(2,
                              0);

        //Move row
        gridData.moveRowsTo(0,
                            new ArrayList<GridRow>() {{
                                add(gridRows[2]);
                                add(gridRows[3]);
                            }});

        // row0 = b, c } Collapse (Lead)
        // row1 = b, d } Collapse (Child)
        // row2 = b, d } Should remain unchanged
        // row3 = a, c
        // row4 = a, c
        // row5 = b, c

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false, true, true, true},
                          new boolean[]{false, true, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "c")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "d")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "d")},
                                  {Expected.build(true,
                                                  2,
                                                  "a"), Expected.build(true,
                                                                       3,
                                                                       "c")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(true,
                                                                       0,
                                                                       "c")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(true,
                                                                       0,
                                                                       "c")}
                          });
    }

    @Test
    public void testGrouped_MoveDown_Rowsx3ToIndex4_Blockx3Rows_NewMergedBlock() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 0 || rowIndex == 4 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = b, 0
        // row1 = a, 1 } Collapse (Lead)
        // row2 = a, 2 } Collapse (Child)
        // row3 = a, 3 } Collapse (Child)
        // row4 = b, 4

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, false},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  3,
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

        //Collapse cell
        gridData.collapseCell(1,
                              0);

        //Move row
        gridData.moveRowsTo(4,
                            new ArrayList<GridRow>() {{
                                add(gridRows[1]);
                                add(gridRows[2]);
                                add(gridRows[3]);
                            }});

        // row0 = b, 0
        // row1 = b, 4
        // row2 = a, 1 } Collapse (Lead)
        // row3 = a, 2 } Collapse (Child)
        // row4 = a, 3 } Collapse (Child)

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, true, true},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  2,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "4")},
                                  {Expected.build(true,
                                                  3,
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
                                                                       "3")}
                          });
    }

    @Test
    public void testGrouped_MoveDown_Rowsx2ToIndex4_Blockx2Rows_NewMergedGroup() {
        constructGridData(2, 5);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 || rowIndex == 2 || rowIndex == 4 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a, 0
        // row1 = b, 1 } Collapse (Lead)
        // row2 = b, 2 } Collapse (Child)
        // row3 = a, 3
        // row4 = b, 4

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, false, false},
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
                                                                       "2")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        //Collapse cell
        gridData.collapseCell(1,
                              0);

        //Move row
        gridData.moveRowsTo(4,
                            new ArrayList<GridRow>() {{
                                add(gridRows[1]);
                                add(gridRows[2]);
                            }});

        // row0 = a, 0
        // row1 = a, 3
        // row2 = b, 4 } Should remain unchanged
        // row3 = b, 1 } Collapse (Lead)
        // row4 = b, 2 } Collapse (Child)

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false, true, true},
                          new boolean[]{false, false, false, false, true},
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
                                                                       "3")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "4")},
                                  {Expected.build(true,
                                                  2,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(true,
                                                  0,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "2")}
                          });
    }
}