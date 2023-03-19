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

import java.util.ArrayList;
import java.util.function.Consumer;

import org.junit.Test;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridTest.Expected.build;

public class GridCellSelectionsTest extends BaseGridTest {

    @Test
    public void testSelectCell() {
        constructGridData(2, 3);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{false, false, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
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
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 2)")}
                          });

        gridData.selectCell(0,
                            0);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                   1)));
        assertEquals(1,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellMergedData() {
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

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
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

        gridData.selectCell(0,
                            0);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                   1)));
        assertEquals(2,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellGroupedDataSelectGroupedCell() {
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
        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false},
                          new boolean[]{false, true, false},
                          new Expected[][]{
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

        gridData.selectCell(0,
                            0);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                   1)));
        assertEquals(2,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellGroupedDataSelectMergedCell() {
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
        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false},
                          new boolean[]{false, true, false},
                          new Expected[][]{
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

        gridData.selectCell(0,
                            1);

        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                   1)));
        assertEquals(2,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellMultipleTimes() {
        constructGridData(2, 3);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{false, false, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
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
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 2)")}
                          });

        //Select once
        gridData.selectCell(0,
                            0);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                   1)));
        assertEquals(1,
                     gridData.getSelectedCells().size());

        //Select again
        gridData.selectCell(0,
                            0);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                   1)));
        assertEquals(1,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testClearSelections() {
        constructGridData(2, 3);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{false, false, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
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
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 2)")}
                          });

        gridData.selectCell(0,
                            0);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                   1)));
        assertEquals(1,
                     gridData.getSelectedCells().size());

        gridData.clearSelections();

        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                   1)));
        assertEquals(0,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellMoveColumn() {
        constructGridData(2, 2);
        final GridColumn<String> gc1 = gridColumns[0];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new Expected[][]{
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

        //Select cell
        gridData.selectCell(0,
                            0);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertEquals(1,
                     gridData.getSelectedCells().size());

        assertEquals("(0, 0)",
                     gridData.getCell(0,
                                      0).getValue().getValue());
        assertEquals("(1, 0)",
                     gridData.getCell(0,
                                      1).getValue().getValue());
        assertEquals("(0, 1)",
                     gridData.getCell(1,
                                      0).getValue().getValue());
        assertEquals("(1, 1)",
                     gridData.getCell(1,
                                      1).getValue().getValue());

        //Move column
        gridData.moveColumnTo(1,
                              gc1);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertEquals(1,
                     gridData.getSelectedCells().size());

        assertEquals("(1, 0)",
                     gridData.getCell(0,
                                      0).getValue().getValue());
        assertEquals("(0, 0)",
                     gridData.getCell(0,
                                      1).getValue().getValue());
        assertEquals("(1, 1)",
                     gridData.getCell(1,
                                      0).getValue().getValue());
        assertEquals("(0, 1)",
                     gridData.getCell(1,
                                      1).getValue().getValue());
    }

    @Test
    public void testMoveColumnSelectCell() {
        constructGridData(2, 2);
        final GridColumn<String> gc1 = gridColumns[0];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new Expected[][]{
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

        //Move column
        gridData.moveColumnTo(1,
                              gc1);

        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertEquals(0,
                     gridData.getSelectedCells().size());

        assertEquals("(1, 0)",
                     gridData.getCell(0,
                                      0).getValue().getValue());
        assertEquals("(0, 0)",
                     gridData.getCell(0,
                                      1).getValue().getValue());
        assertEquals("(1, 1)",
                     gridData.getCell(1,
                                      0).getValue().getValue());
        assertEquals("(0, 1)",
                     gridData.getCell(1,
                                      1).getValue().getValue());

        //Select cell
        gridData.selectCell(0,
                            0);

        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertEquals(1,
                     gridData.getSelectedCells().size());

        assertEquals("(1, 0)",
                     gridData.getCell(0,
                                      0).getValue().getValue());
        assertEquals("(0, 0)",
                     gridData.getCell(0,
                                      1).getValue().getValue());
        assertEquals("(1, 1)",
                     gridData.getCell(1,
                                      0).getValue().getValue());
        assertEquals("(0, 1)",
                     gridData.getCell(1,
                                      1).getValue().getValue());
    }

    @Test
    public void testSelectCellsMoveColumn() {
        constructGridData(3, 2);
        final GridColumn<String> gc3 = gridColumns[2];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new Expected[][]{
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

        //Select cell
        gridData.selectCells(0,
                             0,
                             2,
                             1);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   2)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   2)));
        assertEquals(2,
                     gridData.getSelectedCells().size());

        assertEquals("(0, 0)",
                     gridData.getCell(0,
                                      0).getValue().getValue());
        assertEquals("(1, 0)",
                     gridData.getCell(0,
                                      1).getValue().getValue());
        assertEquals("(2, 0)",
                     gridData.getCell(0,
                                      2).getValue().getValue());
        assertEquals("(0, 1)",
                     gridData.getCell(1,
                                      0).getValue().getValue());
        assertEquals("(1, 1)",
                     gridData.getCell(1,
                                      1).getValue().getValue());
        assertEquals("(2, 1)",
                     gridData.getCell(1,
                                      2).getValue().getValue());

        //Move column
        gridData.moveColumnTo(1,
                              gc3);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   2)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   2)));
        assertEquals(2,
                     gridData.getSelectedCells().size());

        assertEquals("(0, 0)",
                     gridData.getCell(0,
                                      0).getValue().getValue());
        assertEquals("(2, 0)",
                     gridData.getCell(0,
                                      1).getValue().getValue());
        assertEquals("(1, 0)",
                     gridData.getCell(0,
                                      2).getValue().getValue());
        assertEquals("(0, 1)",
                     gridData.getCell(1,
                                      0).getValue().getValue());
        assertEquals("(2, 1)",
                     gridData.getCell(1,
                                      1).getValue().getValue());
        assertEquals("(1, 1)",
                     gridData.getCell(1,
                                      2).getValue().getValue());
    }

    @Test
    public void testMoveColumnSelectCells() {
        constructGridData(3, 2);
        final GridColumn<String> gc3 = gridColumns[2];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new Expected[][]{
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

        //Move column
        gridData.moveColumnTo(1,
                              gc3);

        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   2)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   2)));
        assertEquals(0,
                     gridData.getSelectedCells().size());

        assertEquals("(0, 0)",
                     gridData.getCell(0,
                                      0).getValue().getValue());
        assertEquals("(2, 0)",
                     gridData.getCell(0,
                                      1).getValue().getValue());
        assertEquals("(1, 0)",
                     gridData.getCell(0,
                                      2).getValue().getValue());
        assertEquals("(0, 1)",
                     gridData.getCell(1,
                                      0).getValue().getValue());
        assertEquals("(2, 1)",
                     gridData.getCell(1,
                                      1).getValue().getValue());
        assertEquals("(1, 1)",
                     gridData.getCell(1,
                                      2).getValue().getValue());

        //Select cell
        gridData.selectCells(0,
                             0,
                             2,
                             1);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  2)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   2)));
        assertEquals(2,
                     gridData.getSelectedCells().size());

        assertEquals("(0, 0)",
                     gridData.getCell(0,
                                      0).getValue().getValue());
        assertEquals("(2, 0)",
                     gridData.getCell(0,
                                      1).getValue().getValue());
        assertEquals("(1, 0)",
                     gridData.getCell(0,
                                      2).getValue().getValue());
        assertEquals("(0, 1)",
                     gridData.getCell(1,
                                      0).getValue().getValue());
        assertEquals("(2, 1)",
                     gridData.getCell(1,
                                      1).getValue().getValue());
        assertEquals("(1, 1)",
                     gridData.getCell(1,
                                      2).getValue().getValue());
    }

    @Test
    public void testSelectCellMergedDataInsertRow() {
        constructGridData(1, 2);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            gridData.setCellValue(rowIndex,
                                  0,
                                  new BaseGridCellValue<String>("(0, 0)"));
        }

        assertGridIndexes(gridData,
                          new boolean[]{true, true},
                          new boolean[]{false, false},
                          new Expected[][]{
                                  {build(true,
                                         2,
                                         "(0, 0)")},
                                  {build(true,
                                         0,
                                         "(0, 0)")}
                          });

        gridData.selectCell(0,
                            0);
        assertEquals(2,
                     gridData.getSelectedCells().size());

        gridData.insertRow(1,
                           new BaseGridRow());

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertEquals(2,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellUnmergedDataInsertRow() {
        constructGridData(false, 1, 2);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            gridData.setCellValue(rowIndex,
                                  0,
                                  new BaseGridCellValue<String>("(0, 0)"));
        }

        assertGridIndexes(gridData,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 0)")}
                          });

        gridData.selectCell(0,
                            0);
        gridData.selectCell(1,
                            0);
        assertEquals(2,
                     gridData.getSelectedCells().size());

        gridData.insertRow(1,
                           new BaseGridRow());

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertEquals(2,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellMergedDataDeleteRow() {
        constructGridData(1, 2);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            gridData.setCellValue(rowIndex,
                                  0,
                                  new BaseGridCellValue<String>("(0, 0)"));
        }

        assertGridIndexes(gridData,
                          new boolean[]{true, true},
                          new boolean[]{false, false},
                          new Expected[][]{
                                  {build(true,
                                         2,
                                         "(0, 0)")},
                                  {build(true,
                                         0,
                                         "(0, 0)")}
                          });

        gridData.selectCell(0,
                            0);
        assertEquals(2,
                     gridData.getSelectedCells().size());

        gridData.deleteRow(1);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertEquals(1,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellMergedDataDeleteRowWithAdditionalSelections() {
        constructGridData(1, 3);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            gridData.setCellValue(rowIndex,
                                  0,
                                  new BaseGridCellValue<String>(rowIndex < 2 ? "a" : "b"));
        }

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {build(true,
                                         2,
                                         "a")},
                                  {build(true,
                                         0,
                                         "a")},
                                  {build(false,
                                         1,
                                         "b")}
                          });

        gridData.selectCell(0,
                            0);
        gridData.selectCell(2,
                            0);
        assertEquals(3,
                     gridData.getSelectedCells().size());

        gridData.deleteRow(1);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertEquals(2,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellGroupedDataDeleteRow() {
        constructGridData(1, 2);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            gridData.setCellValue(rowIndex,
                                  0,
                                  new BaseGridCellValue<String>("(0, 0)"));
        }

        assertGridIndexes(gridData,
                          new boolean[]{true, true},
                          new boolean[]{false, false},
                          new Expected[][]{
                                  {build(true,
                                         2,
                                         "(0, 0)")},
                                  {build(true,
                                         0,
                                         "(0, 0)")}
                          });

        gridData.selectCell(0,
                            0);
        gridData.collapseCell(0,
                              0);
        assertEquals(2,
                     gridData.getSelectedCells().size());

        gridData.deleteRow(0);

        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertEquals(0,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellGroupedDataDeleteRowWithAdditionalSelections() {
        constructGridData(1, 3);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            gridData.setCellValue(rowIndex,
                                  0,
                                  new BaseGridCellValue<String>(rowIndex < 2 ? "a" : "b"));
        }

        assertGridIndexes(gridData,
                          new boolean[]{true, true, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {build(true,
                                         2,
                                         "a")},
                                  {build(true,
                                         0,
                                         "a")},
                                  {build(false,
                                         1,
                                         "b")}
                          });

        gridData.selectCell(0,
                            0);
        gridData.selectCell(2,
                            0);
        gridData.collapseCell(0,
                              0);
        assertEquals(3,
                     gridData.getSelectedCells().size());

        gridData.deleteRow(0);

        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertEquals(1,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellUnmergedDataDeleteRow() {
        constructGridData(false, 1, 2);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            gridData.setCellValue(rowIndex,
                                  0,
                                  new BaseGridCellValue<String>("(0, 0)"));
        }

        assertGridIndexes(gridData,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)")},
                                  {build(false,
                                         1,
                                         "(0, 0)")}
                          });

        gridData.selectCell(0,
                            0);
        gridData.selectCell(1,
                            0);
        assertEquals(2,
                     gridData.getSelectedCells().size());

        gridData.deleteRow(1);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertEquals(1,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellThenAppendColumn() {
        constructGridData(1, 2);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            gridData.setCellValue(rowIndex,
                                  0,
                                  new BaseGridCellValue<String>("(0, 0)"));
        }

        assertGridIndexes(gridData,
                          new boolean[]{true, true},
                          new boolean[]{false, false},
                          new Expected[][]{
                                  {build(true,
                                         2,
                                         "(0, 0)")},
                                  {build(true,
                                         0,
                                         "(0, 0)")}
                          });

        gridData.selectCell(0,
                            0);
        assertEquals(2,
                     gridData.getSelectedCells().size());

        gridData.appendColumn(new MockMergableGridColumn<String>("col1",
                                                                 100));

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertEquals(2,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellInsertColumn() {
        constructGridData(1, 2);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            gridData.setCellValue(rowIndex,
                                  0,
                                  new BaseGridCellValue<String>("(0, 0)"));
        }

        assertGridIndexes(gridData,
                          new boolean[]{true, true},
                          new boolean[]{false, false},
                          new Expected[][]{
                                  {build(true,
                                         2,
                                         "(0, 0)")},
                                  {build(true,
                                         0,
                                         "(0, 0)")}
                          });

        gridData.selectCell(0,
                            0);
        assertEquals(2,
                     gridData.getSelectedCells().size());

        gridData.insertColumn(0,
                              new MockMergableGridColumn<String>("col1",
                                                                 100));

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertEquals(2,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellAppendColumnWithRowSelected() {
        doTestSelectCellWithRowSelected((data) -> data.appendColumn(new MockMergableGridColumn<String>("col1",
                                                                                                       100)));
    }

    @Test
    public void testSelectCellInsertColumnWithRowSelected() {
        doTestSelectCellWithRowSelected((data) -> data.insertColumn(0,
                                                                    new MockMergableGridColumn<String>("col1",
                                                                                                       100)));
    }

    private void doTestSelectCellWithRowSelected(final Consumer<GridData> mutation) {
        constructGridData(1, 2);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            gridData.setCellValue(rowIndex,
                                  0,
                                  new BaseGridCellValue<>(rowIndex));
            gridData.getCell(rowIndex,
                             0).setSelectionStrategy(RowSelectionStrategy.INSTANCE);
        }

        assertGridIndexes(gridData,
                          new boolean[]{false, false},
                          new boolean[]{false, false},
                          new Expected[][]{
                                  {build(false,
                                         1,
                                         0)},
                                  {build(false,
                                         1,
                                         1)}
                          });

        gridData.selectCell(0,
                            0);
        assertEquals(1,
                     gridData.getSelectedCells().size());

        mutation.accept(gridData);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                   1)));
        assertEquals(2,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellDeleteColumn() {
        constructGridData(2, 1);
        final GridColumn<String> gc1 = gridColumns[0];

        for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
            gridData.setCellValue(0,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(0, " + columnIndex + ")"));
        }

        assertGridIndexes(gridData,
                          new boolean[]{false},
                          new boolean[]{false},
                          new Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(0, 1)")}
                          });

        gridData.selectCell(0,
                            0);
        assertEquals(1,
                     gridData.getSelectedCells().size());

        gridData.deleteColumn(gc1);

        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   1)));
        assertEquals(0,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectHeaderCellDeleteColumn() {
        constructGridData(2, 0);
        final GridColumn<String> gc0 = gridColumns[0];
        final GridColumn<String> gc1 = gridColumns[1];

        gc0.getHeaderMetaData().add(new BaseHeaderMetaData("col0"));
        gc1.getHeaderMetaData().add(new BaseHeaderMetaData("col1"));

        gridData.selectHeaderCell(0, 0);
        gridData.selectHeaderCell(0, 1);
        assertEquals(2,
                     gridData.getSelectedHeaderCells().size());

        gridData.deleteColumn(gc0);

        assertTrue(gridData.getSelectedHeaderCells().contains(new GridData.SelectedCell(0,
                                                                                        0)));
        assertFalse(gridData.getSelectedHeaderCells().contains(new GridData.SelectedCell(0,
                                                                                         1)));
        assertEquals(1,
                     gridData.getSelectedHeaderCells().size());
    }

    @Test
    public void testSelectCellDeleteColumnWithAdditionalSelections() {
        constructGridData(2, 1);
        final GridColumn<String> gc1 = gridColumns[0];

        for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
            gridData.setCellValue(0,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(0, " + columnIndex + ")"));
        }

        assertGridIndexes(gridData,
                          new boolean[]{false},
                          new boolean[]{false},
                          new Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(0, 1)")}
                          });

        gridData.selectCell(0,
                            0);
        gridData.selectCell(0,
                            1);
        assertEquals(2,
                     gridData.getSelectedCells().size());

        gridData.deleteColumn(gc1);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertEquals(1,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellMoveColumnDeleteColumn() {
        constructGridData(2, 1);
        final GridColumn<String> gc1 = gridColumns[0];

        for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
            gridData.setCellValue(0,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(0, " + columnIndex + ")"));
        }

        assertGridIndexes(gridData,
                          new boolean[]{false},
                          new boolean[]{false},
                          new Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(0, 1)")}
                          });

        gridData.selectCell(0,
                            0);
        assertEquals(1,
                     gridData.getSelectedCells().size());
        gridData.moveColumnTo(1,
                              gc1);

        gridData.deleteColumn(gc1);

        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   0)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   1)));
        assertEquals(0,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testSelectCellMoveColumnToSplitSelectionsDeleteColumn() {
        constructGridData(4, 1);
        final GridColumn<String> gc2 = gridColumns[1];
        final GridColumn<String> gc4 = gridColumns[3];

        for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
            gridData.setCellValue(0,
                                  columnIndex,
                                  new BaseGridCellValue<String>("(0, " + columnIndex + ")"));
        }

        assertGridIndexes(gridData,
                          new boolean[]{false},
                          new boolean[]{false},
                          new Expected[][]{
                                  {build(false,
                                         1,
                                         "(0, 0)"), build(false,
                                                          1,
                                                          "(0, 1)"), build(false,
                                                                           1,
                                                                           "(0, 2)"), build(false,
                                                                                            1,
                                                                                            "(0, 3)")}
                          });

        gridData.selectCell(0,
                            0);
        gridData.selectCell(0,
                            1);
        gridData.selectCell(0,
                            2);
        assertEquals(3,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  2)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   3)));

        gridData.moveColumnTo(1,
                              gc4);

        gridData.deleteColumn(gc2);

        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));
        assertFalse(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                   2)));
        assertEquals(2,
                     gridData.getSelectedCells().size());
    }

    @Test
    public void testUnmergedMoveRowUpWithSelections() {
        constructGridData(false, 2, 3);
        final GridRow row1 = gridRows[1];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 1 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a[ ], 0[ ]
        // row1 = b[X], 1[X]
        // row2 = a[ ], 2[ ]

        assertGridIndexes(gridData,
                          new boolean[]{false, false, false},
                          new boolean[]{false, false, false},
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
                                                                       "2")}
                          });

        gridData.selectCell(1,
                            0);
        gridData.selectCell(1,
                            1);
        assertEquals(2,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  1)));

        //Move row
        gridData.moveRowTo(0,
                           row1);

        // row0 = b[X], 1[X]
        // row1 = a[ ], 0[ ]
        // row2 = a[ ], 2[ ]

        assertGridIndexes(gridData,
                          new boolean[]{false, false, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")}
                          });

        assertEquals(2,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));
    }

    @Test
    public void testUnmergedMoveRowDownWithSelections() {
        constructGridData(false, 2, 3);
        final GridRow row0 = gridRows[0];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 0 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = b[X], 0[X]
        // row1 = a[ ], 1[ ]
        // row2 = a[ ], 2[ ]

        assertGridIndexes(gridData,
                          new boolean[]{false, false, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")}
                          });

        gridData.selectCell(0,
                            0);
        gridData.selectCell(0,
                            1);
        assertEquals(2,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));

        //Move row
        gridData.moveRowTo(1,
                           row0);

        // row0 = a[ ], 1[ ]
        // row1 = b[X], 0[X]
        // row2 = a[ ], 2[ ]

        assertGridIndexes(gridData,
                          new boolean[]{false, false, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "1")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(false,
                                                  1,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")}
                          });

        assertEquals(2,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  1)));
    }

    @Test
    public void testMergedMoveRowUpWithSelections1() {
        constructGridData(2, 5);
        final GridRow row4 = gridRows[4];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 4 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a[ ], 0[X]
        // row1 = a[ ], 1[ ]
        // row2 = a[ ], 2[ ]
        // row3 = a[ ], 3[ ]
        // row4 = b[X], 4[ ]

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

        gridData.selectCell(0,
                            1);
        gridData.selectCell(4,
                            0);
        assertEquals(2,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));

        //Move row
        gridData.moveRowTo(3,
                           row4);

        // row0 = a[ ], 0[X]
        // row1 = a[ ], 1[ ]
        // row2 = a[ ], 2[ ]
        // row3 = b[X], 4[ ]
        // row4 = a[ ], 3[ ]

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

        assertEquals(2,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
    }

    @Test
    public void testMergedMoveRowUpWithSelections2() {
        constructGridData(2, 5);
        final GridRow row4 = gridRows[4];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 4 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a[X], 0[ ]
        // row1 = a[X], 1[ ]
        // row2 = a[X], 2[ ]
        // row3 = a[X], 3[ ]
        // row4 = b[X], 4[ ]

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

        gridData.selectCell(0,
                            0);
        gridData.selectCell(4,
                            0);
        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));

        //Move row
        gridData.moveRowTo(3,
                           row4);

        // row0 = a[X], 0[ ]
        // row1 = a[X], 1[ ]
        // row2 = a[X], 2[ ]
        // row3 = b[X], 4[ ]
        // row4 = a[X], 3[ ]

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

        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));
    }

    @Test
    public void testMergedMoveRowUpWithSelections3() {
        constructGridData(2, 5);
        final GridRow row4 = gridRows[4];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? "a" : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a[X], 0[ ]
        // row1 = a[X], 1[ ]
        // row2 = a[X], 2[ ]
        // row3 = a[X], 3[ ]
        // row4 = a[X], 4[ ]

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  5,
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
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        gridData.selectCell(0,
                            0);
        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));

        //Move row
        gridData.moveRowTo(3,
                           row4);

        // row0 = a[X], 0[ ]
        // row1 = a[X], 1[ ]
        // row2 = a[X], 2[ ]
        // row3 = a[X], 4[ ]
        // row4 = a[X], 3[ ]

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  5,
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
                                                                       "4")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")}
                          });

        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));
    }

    @Test
    public void testMergedMoveRowUpWithSelections4() {
        constructGridData(2, 5);
        final GridRow row3 = gridRows[3];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? "a" : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a[X], 0[ ]
        // row1 = a[X], 1[ ]
        // row2 = a[X], 2[ ]
        // row3 = a[X], 3[ ]
        // row4 = a[X], 4[ ]

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  5,
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
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        gridData.selectCell(0,
                            0);
        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));

        //Move row
        gridData.moveRowTo(2,
                           row3);

        // row0 = a[X], 0[ ]
        // row1 = a[X], 1[ ]
        // row2 = a[X], 2[ ]
        // row3 = a[X], 4[ ]
        // row4 = a[X], 3[ ]

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  5,
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
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "2")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));
    }

    @Test
    public void testMergedMoveRowDownWithSelections1() {
        constructGridData(2, 5);
        final GridRow row3 = gridRows[3];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 4 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a[ ], 0[X]
        // row1 = a[ ], 1[ ]
        // row2 = a[ ], 2[ ]
        // row3 = a[ ], 3[ ]
        // row4 = b[X], 4[ ]

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

        gridData.selectCell(0,
                            1);
        gridData.selectCell(4,
                            0);
        assertEquals(2,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));

        //Move row
        gridData.moveRowTo(4,
                           row3);

        // row0 = a[ ], 0[X]
        // row1 = a[ ], 1[ ]
        // row2 = a[ ], 2[ ]
        // row3 = b[X], 4[ ]
        // row4 = a[ ], 3[ ]

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

        assertEquals(2,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
    }

    @Test
    public void testMergedMoveRowDownWithSelections2() {
        constructGridData(2, 5);
        final GridRow row3 = gridRows[3];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 4 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a[X], 0[ ]
        // row1 = a[X], 1[ ]
        // row2 = a[X], 2[ ]
        // row3 = a[X], 3[ ]
        // row4 = b[X], 4[ ]

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

        gridData.selectCell(0,
                            0);
        gridData.selectCell(4,
                            0);
        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));

        //Move row
        gridData.moveRowTo(4,
                           row3);

        // row0 = a[X], 0[ ]
        // row1 = a[X], 1[ ]
        // row2 = a[X], 2[ ]
        // row3 = b[X], 4[ ]
        // row4 = a[X], 3[ ]

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

        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));
    }

    @Test
    public void testMergedMoveRowDownWithSelections3() {
        constructGridData(2, 5);
        final GridRow row3 = gridRows[3];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? "a" : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a[X], 0[ ]
        // row1 = a[X], 1[ ]
        // row2 = a[X], 2[ ]
        // row3 = a[X], 3[ ]
        // row4 = a[X], 4[ ]

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  5,
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
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        gridData.selectCell(0,
                            0);
        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));

        //Move row
        gridData.moveRowTo(4,
                           row3);

        // row0 = a[X], 0[ ]
        // row1 = a[X], 1[ ]
        // row2 = a[X], 2[ ]
        // row3 = a[X], 4[ ]
        // row4 = a[X], 3[ ]

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  5,
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
                                                                       "4")},
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "3")}
                          });

        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));
    }

    @Test
    public void testMergedMoveRowDownWithSelections4() {
        constructGridData(2, 5);
        final GridRow row0 = gridRows[0];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? "a" : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a[X], 0[ ]
        // row1 = a[X], 1[ ]
        // row2 = a[X], 2[ ]
        // row3 = a[X], 3[ ]
        // row4 = a[X], 4[ ]

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  5,
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
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        gridData.selectCell(0,
                            0);
        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));

        //Move row
        gridData.moveRowTo(1,
                           row0);

        // row0 = a[X], 0[ ]
        // row1 = a[X], 1[ ]
        // row2 = a[X], 2[ ]
        // row3 = a[X], 4[ ]
        // row4 = a[X], 3[ ]

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(true,
                                                  5,
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
                                  {Expected.build(true,
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));
    }

    @Test
    public void testGroupedMoveRowUpWithSelections1() {
        constructGridData(2, 5);
        final GridRow row4 = gridRows[4];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 4 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a[ ], 0[X] } Collapse (Lead)
        // row1 = a[ ], 1[X] } Collapse (Child)
        // row2 = a[ ], 2[X] } Collapse (Child)
        // row3 = a[ ], 3[X] } Collapse (Child)
        // row4 = b[X], 4[ ]

        //Collapse cell
        gridData.collapseCell(0,
                              0);

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, true, true, true, false},
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

        gridData.selectCell(0,
                            1);
        gridData.selectCell(4,
                            0);
        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));

        //Move row
        gridData.moveRowsTo(0,
                            new ArrayList<GridRow>() {{
                                add(row4);
                            }});

        // row0 = b[X], 4[ ]
        // row1 = a[ ], 0[X] } Collapse (Lead)
        // row2 = a[ ], 1[X] } Collapse (Child)
        // row3 = a[ ], 2[X] } Collapse (Child)
        // row4 = a[ ], 3[X] } Collapse (Child)

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, true, true, true},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "4")},
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
                                                                       "3")}
                          });

        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  1)));
    }

    @Test
    public void testGroupedMoveRowUpWithSelections2() {
        constructGridData(2, 5);
        final GridRow row4 = gridRows[4];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 4 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a[X], 0[ ] } Collapse (Lead)
        // row1 = a[X], 1[ ] } Collapse (Child)
        // row2 = a[X], 2[ ] } Collapse (Child)
        // row3 = a[X], 3[ ] } Collapse (Child)
        // row4 = b[X], 4[ ]

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

        //Collapse cell
        gridData.collapseCell(0,
                              0);

        gridData.selectCell(0,
                            0);
        gridData.selectCell(4,
                            0);
        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));

        //Move row
        gridData.moveRowsTo(0,
                            new ArrayList<GridRow>() {{
                                add(row4);
                            }});

        // row0 = b[X], 4[ ]
        // row1 = a[X], 0[ ] } Collapse (Lead)
        // row2 = a[X], 1[ ] } Collapse (Child)
        // row3 = a[X], 2[ ] } Collapse (Child)
        // row4 = a[X], 3[ ] } Collapse (Child)

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, true, true, true},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "4")},
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
                                                                       "3")}
                          });

        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));
    }

    @Test
    public void testGroupedMoveRowUpWithSelections3() {
        constructGridData(2, 5);
        final GridRow row1 = gridRows[1];
        final GridRow row2 = gridRows[2];
        final GridRow row3 = gridRows[3];
        final GridRow row4 = gridRows[4];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 0 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = b[X], 0[ ]
        // row1 = a[X], 1[ ] } Collapse (Lead)
        // row2 = a[X], 2[ ] } Collapse (Child)
        // row3 = a[X], 3[ ] } Collapse (Child)
        // row4 = a[X], 4[ ] } Collapse (Child)

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  4,
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
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        //Collapse cell
        gridData.collapseCell(1,
                              0);

        gridData.selectCell(0,
                            0);
        gridData.selectCell(1,
                            0);
        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));

        //Move row
        gridData.moveRowsTo(0,
                            new ArrayList<GridRow>() {{
                                add(row1);
                                add(row2);
                                add(row3);
                                add(row4);
                            }});

        // row0 = a[X], 1[ ] } Collapse (Lead)
        // row1 = a[X], 2[ ] } Collapse (Child)
        // row2 = a[X], 3[ ] } Collapse (Child)
        // row3 = a[X], 4[ ] } Collapse (Child)
        // row4 = b[X], 0[ ]

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, true, true, true, false},
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
                                                                       "4")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "0")}
                          });

        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));
    }

    @Test
    public void testGroupedMoveRowDownWithSelections1() {
        constructGridData(2, 5);
        final GridRow row0 = gridRows[0];
        final GridRow row1 = gridRows[1];
        final GridRow row2 = gridRows[2];
        final GridRow row3 = gridRows[3];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 4 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a[ ], 0[X] } Collapse (Lead)
        // row1 = a[ ], 1[X] } Collapse (Child)
        // row2 = a[ ], 2[X] } Collapse (Child)
        // row3 = a[ ], 3[X] } Collapse (Child)
        // row4 = b[X], 4[ ]

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

        //Collapse cell
        gridData.collapseCell(0,
                              0);

        gridData.selectCell(0,
                            1);
        gridData.selectCell(4,
                            0);
        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));

        //Move rows
        gridData.moveRowsTo(4,
                            new ArrayList<GridRow>() {{
                                add(row0);
                                add(row1);
                                add(row2);
                                add(row3);
                            }});

        // row0 = b[X], 4[ ]
        // row1 = a[ ], 0[X] } Collapse (Lead)
        // row2 = a[ ], 1[X] } Collapse (Child)
        // row3 = a[ ], 2[X] } Collapse (Child)
        // row4 = a[ ], 3[X] } Collapse (Child)

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, true, true, true},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "4")},
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
                                                                       "3")}
                          });

        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  1)));
    }

    @Test
    public void testGroupedMoveRowDownWithSelections2() {
        constructGridData(2, 5);
        final GridRow row0 = gridRows[0];
        final GridRow row1 = gridRows[1];
        final GridRow row2 = gridRows[2];
        final GridRow row3 = gridRows[3];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 4 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = a[X], 0[ ] } Collapse (Lead)
        // row1 = a[X], 1[ ] } Collapse (Child)
        // row2 = a[X], 2[ ] } Collapse (Child)
        // row3 = a[X], 3[ ] } Collapse (Child)
        // row4 = b[X], 4[ ]

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

        //Collapse cell
        gridData.collapseCell(0,
                              0);

        gridData.selectCell(0,
                            0);
        gridData.selectCell(4,
                            0);
        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));

        //Move rows
        gridData.moveRowsTo(4,
                            new ArrayList<GridRow>() {{
                                add(row0);
                                add(row1);
                                add(row2);
                                add(row3);
                            }});

        // row0 = b[X], 4[ ]
        // row1 = a[X], 0[ ] } Collapse (Lead)
        // row2 = a[X], 1[ ] } Collapse (Child)
        // row3 = a[X], 2[ ] } Collapse (Child)
        // row4 = a[X], 3[ ] } Collapse (Child)

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, true, true, true},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "4")},
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
                                                                       "3")}
                          });

        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));
    }

    @Test
    public void testGroupedMoveRowDownWithSelections3() {
        constructGridData(2, 5);
        final GridRow row0 = gridRows[0];

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                final String value = columnIndex == 0 ? (rowIndex == 0 ? "b" : "a") : Integer.toString(rowIndex);
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>(value));
            }
        }

        // row0 = b[X], 0[ ]
        // row1 = a[X], 1[ ] } Collapse (Lead)
        // row2 = a[X], 2[ ] } Collapse (Child)
        // row3 = a[X], 3[ ] } Collapse (Child)
        // row4 = a[X], 4[ ] } Collapse (Child)

        assertGridIndexes(gridData,
                          new boolean[]{false, true, true, true, true},
                          new boolean[]{false, false, false, false, false},
                          new Expected[][]{
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "0")},
                                  {Expected.build(true,
                                                  4,
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
                                                  0,
                                                  "a"), Expected.build(false,
                                                                       1,
                                                                       "4")}
                          });

        //Collapse cell
        gridData.collapseCell(1,
                              0);

        gridData.selectCell(0,
                            0);
        gridData.selectCell(1,
                            0);
        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));

        //Move rows
        gridData.moveRowsTo(4,
                            new ArrayList<GridRow>() {{
                                add(row0);
                            }});

        // row0 = a[X], 1[ ] } Collapse (Lead)
        // row1 = a[X], 2[ ] } Collapse (Child)
        // row2 = a[X], 3[ ] } Collapse (Child)
        // row3 = a[X], 4[ ] } Collapse (Child)
        // row4 = b[X], 0[ ]

        assertGridIndexes(gridData,
                          new boolean[]{true, true, true, true, false},
                          new boolean[]{false, true, true, true, false},
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
                                                                       "4")},
                                  {Expected.build(false,
                                                  1,
                                                  "b"), Expected.build(false,
                                                                       1,
                                                                       "0")}
                          });

        assertEquals(5,
                     gridData.getSelectedCells().size());
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(3,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(4,
                                                                                  0)));
    }

    @Test
    public void testSelectCellSelectedRangeChangeTopLeft() {
        constructGridData(3, 3);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{false, false, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
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
                                                                           "(2, 1)")},
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 2)"), build(false,
                                                                           1,
                                                                           "(2, 2)")}
                          });

        gridData.selectCell(1,
                            1);

        assertEquals(1,
                     gridData.getSelectedCells().size());
        assertEquals(gridData.getSelectedCellsOrigin(),
                     new GridData.SelectedCell(1,
                                               1));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  1)));

        gridData.selectCells(0,
                             0,
                             2,
                             2);

        assertEquals(4,
                     gridData.getSelectedCells().size());
        assertEquals(gridData.getSelectedCellsOrigin(),
                     new GridData.SelectedCell(1,
                                               1));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  1)));
    }

    @Test
    public void testSelectCellSelectedRangeChangeTopRight() {
        constructGridData(3, 3);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{false, false, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
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
                                                                           "(2, 1)")},
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 2)"), build(false,
                                                                           1,
                                                                           "(2, 2)")}
                          });

        gridData.selectCell(1,
                            1);

        assertEquals(1,
                     gridData.getSelectedCells().size());
        assertEquals(gridData.getSelectedCellsOrigin(),
                     new GridData.SelectedCell(1,
                                               1));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  1)));

        gridData.selectCells(0,
                             1,
                             2,
                             2);

        assertEquals(4,
                     gridData.getSelectedCells().size());
        assertEquals(gridData.getSelectedCellsOrigin(),
                     new GridData.SelectedCell(1,
                                               1));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(0,
                                                                                  2)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  2)));
    }

    @Test
    public void testSelectCellSelectedRangeChangeBottomLeft() {
        constructGridData(3, 3);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{false, false, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
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
                                                                           "(2, 1)")},
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 2)"), build(false,
                                                                           1,
                                                                           "(2, 2)")}
                          });

        gridData.selectCell(1,
                            1);

        assertEquals(1,
                     gridData.getSelectedCells().size());
        assertEquals(gridData.getSelectedCellsOrigin(),
                     new GridData.SelectedCell(1,
                                               1));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  1)));

        gridData.selectCells(1,
                             0,
                             2,
                             2);

        assertEquals(4,
                     gridData.getSelectedCells().size());
        assertEquals(gridData.getSelectedCellsOrigin(),
                     new GridData.SelectedCell(1,
                                               1));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  0)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  1)));
    }

    @Test
    public void testSelectCellSelectedRangeChangeBottomRight() {
        constructGridData(3, 3);

        for (int rowIndex = 0; rowIndex < gridData.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gridData.getColumnCount(); columnIndex++) {
                gridData.setCellValue(rowIndex,
                                      columnIndex,
                                      new BaseGridCellValue<String>("(" + columnIndex + ", " + rowIndex + ")"));
            }
        }

        assertGridIndexes(gridData,
                          new boolean[]{false, false, false},
                          new boolean[]{false, false, false},
                          new Expected[][]{
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
                                                                           "(2, 1)")},
                                  {build(false,
                                         1,
                                         "(0, 2)"), build(false,
                                                          1,
                                                          "(1, 2)"), build(false,
                                                                           1,
                                                                           "(2, 2)")}
                          });

        gridData.selectCell(1,
                            1);

        assertEquals(1,
                     gridData.getSelectedCells().size());
        assertEquals(gridData.getSelectedCellsOrigin(),
                     new GridData.SelectedCell(1,
                                               1));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  1)));

        gridData.selectCells(1,
                             1,
                             2,
                             2);

        assertEquals(4,
                     gridData.getSelectedCells().size());
        assertEquals(gridData.getSelectedCellsOrigin(),
                     new GridData.SelectedCell(1,
                                               1));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(1,
                                                                                  2)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  1)));
        assertTrue(gridData.getSelectedCells().contains(new GridData.SelectedCell(2,
                                                                                  2)));
    }
}
