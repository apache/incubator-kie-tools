/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl;

import java.util.Collections;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetRenderingTestUtils;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(LienzoMockitoTestRunner.class)
public class ColumnRenderingStrategyMergedTest {

    private GridData gridData = mock(GridData.class);

    private GridCell<String> gridCell = mock(GridCell.class);

    private GridRow gridRow = mock(GridRow.class);

    @Before
    public void setUp() throws Exception {
        doReturn(gridRow).when(gridData).getRow(anyInt());
        doReturn(BaseGridWidgetRenderingTestUtils.HEADER_ROW_HEIGHT).when(gridRow).getHeight();
    }

    @Test
    public void testGetCellHeightCells3() throws Exception {
        doReturn(3).when(gridCell).getMergedCellCount();
        Assertions.assertThat(ColumnRenderingStrategyMerged.getCellHeight(0, gridData, gridCell)).isEqualTo(BaseGridWidgetRenderingTestUtils.HEADER_ROW_HEIGHT * 3);
    }

    @Test
    public void testGetCellHeightCells4() throws Exception {
        doReturn(4).when(gridCell).getMergedCellCount();
        Assertions.assertThat(ColumnRenderingStrategyMerged.getCellHeight(0, gridData, gridCell)).isEqualTo(BaseGridWidgetRenderingTestUtils.HEADER_ROW_HEIGHT * 4);
    }

    @Test
    public void testIsCollapsedCellMixedValueThreeDifferentValues() throws Exception {
        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("two", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("three", 0);
        doReturn(3).when(gridData).getRowCount();
        doReturn(cellOne).when(gridData).getCell(0, 0);
        doReturn(cellTwo).when(gridData).getCell(1, 0);
        doReturn(cellThree).when(gridData).getCell(2, 0);
        doReturn(true).when(gridRow).isCollapsed();

        Assertions.assertThat(ColumnRenderingStrategyMerged.isCollapsedCellMixedValue(gridData, 2, 0)).isTrue();
    }

    @Test
    public void testIsCollapsedCellMixedValueOneDifferentValue_1() throws Exception {
        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("two", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("one", 0);
        doReturn(3).when(gridData).getRowCount();
        doReturn(cellOne).when(gridData).getCell(0, 0);
        doReturn(cellTwo).when(gridData).getCell(1, 0);
        doReturn(cellThree).when(gridData).getCell(2, 0);
        doReturn(true).when(gridRow).isCollapsed();

        Assertions.assertThat(ColumnRenderingStrategyMerged.isCollapsedCellMixedValue(gridData, 2, 0)).isTrue();
    }

    @Test
    public void testIsCollapsedCellMixedValueOneDifferentValue_2() throws Exception {
        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("two", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("one", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("one", 0);
        doReturn(3).when(gridData).getRowCount();
        doReturn(cellOne).when(gridData).getCell(0, 0);
        doReturn(cellTwo).when(gridData).getCell(1, 0);
        doReturn(cellThree).when(gridData).getCell(2, 0);
        doReturn(true).when(gridRow).isCollapsed();

        Assertions.assertThat(ColumnRenderingStrategyMerged.isCollapsedCellMixedValue(gridData, 2, 0)).isTrue();
    }

    @Test
    public void testIsCollapsedCellMixedValueOneDifferentValue_3() throws Exception {
        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("one", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("two", 0);
        doReturn(3).when(gridData).getRowCount();
        doReturn(cellOne).when(gridData).getCell(0, 0);
        doReturn(cellTwo).when(gridData).getCell(1, 0);
        doReturn(cellThree).when(gridData).getCell(2, 0);
        doReturn(true).when(gridRow).isCollapsed();

        Assertions.assertThat(ColumnRenderingStrategyMerged.isCollapsedCellMixedValue(gridData, 2, 0)).isTrue();
    }

    @Test
    public void testIsCollapsedCellMixedValue() throws Exception {
        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("one", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("one", 0);
        doReturn(3).when(gridData).getRowCount();
        doReturn(cellOne).when(gridData).getCell(0, 0);
        doReturn(cellTwo).when(gridData).getCell(1, 0);
        doReturn(cellThree).when(gridData).getCell(2, 0);
        doReturn(true).when(gridRow).isCollapsed();

        Assertions.assertThat(ColumnRenderingStrategyMerged.isCollapsedCellMixedValue(gridData, 2, 0)).isFalse();
    }

    @Test
    public void testIsCollapsedRowMixedValueThreeDifferentValues() throws Exception {
        final GridColumn<String> gridColumn = mock(GridColumn.class);
        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("two", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("three", 0);
        final GridRow gridRowOne = mock(GridRow.class);
        final GridRow gridRowTwo = mock(GridRow.class);
        final GridRow gridRowThree = mock(GridRow.class);
        doReturn(gridRowOne).when(gridData).getRow(0);
        doReturn(gridRowTwo).when(gridData).getRow(1);
        doReturn(gridRowThree).when(gridData).getRow(2);
        doReturn(false).when(gridRowOne).isCollapsed();
        doReturn(true).when(gridRowTwo).isCollapsed();
        doReturn(true).when(gridRowThree).isCollapsed();
        doReturn(Collections.singletonMap(0, cellOne)).when(gridRowOne).getCells();
        doReturn(Collections.singletonMap(0, cellTwo)).when(gridRowTwo).getCells();
        doReturn(Collections.singletonMap(0, cellThree)).when(gridRowThree).getCells();

        Assertions.assertThat(ColumnRenderingStrategyMerged.isCollapsedRowMultiValue(gridData, gridColumn, cellThree, 2)).isTrue();
    }

    @Test
    public void testIsCollapsedRowMixedValueOneDifferentValue_1() throws Exception {
        final GridColumn<String> gridColumn = mock(GridColumn.class);
        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("one", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("two", 0);
        final GridRow gridRowOne = mock(GridRow.class);
        final GridRow gridRowTwo = mock(GridRow.class);
        final GridRow gridRowThree = mock(GridRow.class);
        doReturn(gridRowOne).when(gridData).getRow(0);
        doReturn(gridRowTwo).when(gridData).getRow(1);
        doReturn(gridRowThree).when(gridData).getRow(2);
        doReturn(false).when(gridRowOne).isCollapsed();
        doReturn(true).when(gridRowTwo).isCollapsed();
        doReturn(true).when(gridRowThree).isCollapsed();
        doReturn(Collections.singletonMap(0, cellOne)).when(gridRowOne).getCells();
        doReturn(Collections.singletonMap(0, cellTwo)).when(gridRowTwo).getCells();
        doReturn(Collections.singletonMap(0, cellThree)).when(gridRowThree).getCells();

        Assertions.assertThat(ColumnRenderingStrategyMerged.isCollapsedRowMultiValue(gridData, gridColumn, cellThree, 2)).isTrue();
    }

    @Test
    public void testIsCollapsedRowMixedValueOneDifferentValue_2() throws Exception {
        final GridColumn<String> gridColumn = mock(GridColumn.class);
        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("two", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("one", 0);
        final GridRow gridRowOne = mock(GridRow.class);
        final GridRow gridRowTwo = mock(GridRow.class);
        final GridRow gridRowThree = mock(GridRow.class);
        doReturn(gridRowOne).when(gridData).getRow(0);
        doReturn(gridRowTwo).when(gridData).getRow(1);
        doReturn(gridRowThree).when(gridData).getRow(2);
        doReturn(false).when(gridRowOne).isCollapsed();
        doReturn(true).when(gridRowTwo).isCollapsed();
        doReturn(true).when(gridRowThree).isCollapsed();
        doReturn(Collections.singletonMap(0, cellOne)).when(gridRowOne).getCells();
        doReturn(Collections.singletonMap(0, cellTwo)).when(gridRowTwo).getCells();
        doReturn(Collections.singletonMap(0, cellThree)).when(gridRowThree).getCells();

        Assertions.assertThat(ColumnRenderingStrategyMerged.isCollapsedRowMultiValue(gridData, gridColumn, cellThree, 2)).isTrue();
    }

    @Test
    public void testIsCollapsedRowMixedValueOneDifferentValue_3() throws Exception {
        final GridColumn<String> gridColumn = mock(GridColumn.class);
        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("one", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("two", 0);
        final GridRow gridRowOne = mock(GridRow.class);
        final GridRow gridRowTwo = mock(GridRow.class);
        final GridRow gridRowThree = mock(GridRow.class);
        doReturn(gridRowOne).when(gridData).getRow(0);
        doReturn(gridRowTwo).when(gridData).getRow(1);
        doReturn(gridRowThree).when(gridData).getRow(2);
        doReturn(false).when(gridRowOne).isCollapsed();
        doReturn(true).when(gridRowTwo).isCollapsed();
        doReturn(true).when(gridRowThree).isCollapsed();
        doReturn(Collections.singletonMap(0, cellOne)).when(gridRowOne).getCells();
        doReturn(Collections.singletonMap(0, cellTwo)).when(gridRowTwo).getCells();
        doReturn(Collections.singletonMap(0, cellThree)).when(gridRowThree).getCells();

        Assertions.assertThat(ColumnRenderingStrategyMerged.isCollapsedRowMultiValue(gridData, gridColumn, cellThree, 2)).isTrue();
    }

    @Test
    public void testIsCollapsedRowMixedValue() throws Exception {
        final GridColumn<String> gridColumn = mock(GridColumn.class);
        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("one", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("one", 0);
        final GridRow gridRowOne = mock(GridRow.class);
        final GridRow gridRowTwo = mock(GridRow.class);
        final GridRow gridRowThree = mock(GridRow.class);
        doReturn(gridRowOne).when(gridData).getRow(0);
        doReturn(gridRowTwo).when(gridData).getRow(1);
        doReturn(gridRowThree).when(gridData).getRow(2);
        doReturn(false).when(gridRowOne).isCollapsed();
        doReturn(true).when(gridRowTwo).isCollapsed();
        doReturn(true).when(gridRowThree).isCollapsed();
        doReturn(Collections.singletonMap(0, cellOne)).when(gridRowOne).getCells();
        doReturn(Collections.singletonMap(0, cellTwo)).when(gridRowTwo).getCells();
        doReturn(Collections.singletonMap(0, cellThree)).when(gridRowThree).getCells();

        Assertions.assertThat(ColumnRenderingStrategyMerged.isCollapsedRowMultiValue(gridData, gridColumn, cellThree, 2)).isFalse();
    }

    private GridCell<String> gridCellWithMockedMergedCellCount(final String value, final int mergedCellCount) {
        return new BaseGridCell<String>(new BaseGridCellValue<String>(value)) {
            @Override
            public int getMergedCellCount() {
                return mergedCellCount;
            }
        };
    }
}
