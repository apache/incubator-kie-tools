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

import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Group;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.BaseGridColumnRenderer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public abstract class BaseGridTest {

    protected GridData gridData;

    protected GridRow[] gridRows;

    protected GridColumn<String>[] gridColumns;

    // true by default
    private boolean isMerged = true;

    protected void constructGridData(final boolean isMerged, final int columnCount, final int rowCount) {
        this.isMerged = isMerged;
        constructGridData(columnCount, rowCount);
    }

    protected void constructGridData(final int columnCount, final int rowCount) {
        gridData = new BaseGridData(isMerged);

        gridColumns = new GridColumn[columnCount];
        for (int i = 0; i < columnCount; i++) {
            gridColumns[i] = new MockMergableGridColumn<>("col" + i, 100);
            gridData.appendColumn(gridColumns[i]);
        }

        gridRows = new GridRow[rowCount];
        for (int i = 0; i < rowCount; i++) {
            gridRows[i] = new BaseGridRow();
            gridData.appendRow(gridRows[i]);
        }
    }

    public void assertGridIndexes(final GridData data,
                                  final boolean[] expectedRowMergeStates,
                                  final boolean[] expectedRowCollapseStates,
                                  final Expected[][] expectedCellStates) {
        if (data.getRowCount() != expectedRowMergeStates.length) {
            fail("Size of parameter 'expectedRowMergeStates' differs to expected row count.");
        }
        if (data.getRowCount() != expectedRowCollapseStates.length) {
            fail("Size of parameter 'expectedRowCollapseStates' differs to expected row count.");
        }
        if (data.getRowCount() != expectedCellStates.length) {
            fail("Size of parameter 'expectedCellStates' differs to expected row count.");
        }
        for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
            final GridRow row = data.getRow(rowIndex);
            assertEquals("Row[" + rowIndex + "] actual isMerged() differs to expected.",
                         expectedRowMergeStates[rowIndex],
                         row.isMerged());
            assertEquals("Row[" + rowIndex + "] actual isCollapsed() differs to expected.",
                         expectedRowCollapseStates[rowIndex],
                         row.isCollapsed());

            if (data.getColumnCount() != expectedCellStates[rowIndex].length) {
                fail("Size of parameter 'expectedCellStates[" + rowIndex + "]' differs to expected column count.");
            }

            for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
                final GridCell cell = data.getCell(rowIndex,
                                                   columnIndex);
                if (cell == null) {
                    assertNull("Cell[" + columnIndex + ", " + rowIndex + "] was expected to be null.",
                               expectedCellStates[rowIndex][columnIndex].value);
                } else {
                    assertEquals("Cell[" + columnIndex + ", " + rowIndex + "] actual isMerged() differs to expected.",
                                 expectedCellStates[rowIndex][columnIndex].isMerged,
                                 cell.isMerged());
                    assertEquals("Cell[" + columnIndex + ", " + rowIndex + "] actual getMergedCellCount() differs to expected.",
                                 expectedCellStates[rowIndex][columnIndex].mergedCellCount,
                                 cell.getMergedCellCount());
                    assertEquals("Cell[" + columnIndex + ", " + rowIndex + "] actual getValue() differs to expected.",
                                 expectedCellStates[rowIndex][columnIndex].value,
                                 cell.getValue().getValue());
                }
            }
        }
    }

    public static class Expected {

        private boolean isMerged;
        private int mergedCellCount;
        private Object value;

        private Expected(final boolean isMerged,
                         final int mergedCellCount,
                         final Object value) {
            this.isMerged = isMerged;
            this.mergedCellCount = mergedCellCount;
            this.value = value;
        }

        private Expected(final Object value) {
            this.value = value;
        }

        public static Expected build(final Object value) {
            return new Expected(value);
        }

        public static Expected build(final boolean isMerged,
                                     final int mergedCellCount,
                                     final Object value) {
            return new Expected(isMerged,
                                mergedCellCount,
                                value);
        }
    }

    public static class MockMergableGridColumn<T> extends BaseGridColumn<T> {

        public MockMergableGridColumn(final String title,
                                      final double width) {
            super(new BaseHeaderMetaData(title),
                  new MockMergableGridColumnRenderer<T>(),
                  width);
        }

        @Override
        public void edit(final GridCell<T> cell,
                         final GridBodyCellRenderContext context,
                         final Consumer<GridCellValue<T>> callback) {
            //Do nothing
        }
    }

    public static class MockMergableGridColumnRenderer<T> extends BaseGridColumnRenderer<T> {

        @Override
        public Group renderCell(final GridCell<T> cell,
                                final GridBodyCellRenderContext context) {
            return null;
        }
    }
}
