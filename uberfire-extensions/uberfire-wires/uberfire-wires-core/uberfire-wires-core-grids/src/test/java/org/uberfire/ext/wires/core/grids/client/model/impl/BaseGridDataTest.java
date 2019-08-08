/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.IntStream;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseGridDataTest {

    @Mock
    private GridColumnRenderer<String> columnRenderer;

    @Mock
    private BaseHeaderMetaData header;

    @Captor
    private ArgumentCaptor<Boolean> booleanArgumentCaptor;

    @Captor
    private ArgumentCaptor<OptionalDouble> optionalDoubleArgumentCaptor;

    private BaseGridData baseGridData;

    @Before
    public void setUp() {
        baseGridData = new BaseGridData();
    }

    @Test
    public void testDefaultHeaderRowCount() {
        assertEquals(0, baseGridData.getHeaderRowCount());
    }

    @Test
    public void testGetHeaderRowCountWithColumnWithHeaderMetaData() {
        final BaseGridColumn<String> column = new BaseGridColumn<>(header, columnRenderer, 100.0);

        baseGridData.appendColumn(column);

        assertEquals(1, baseGridData.getHeaderRowCount());
    }

    @Test
    public void testGetHeaderRowCountWithColumnsWithHeaderMetaData() {
        final BaseGridColumn<String> column1 = new BaseGridColumn<>(header, columnRenderer, 100.0);
        final BaseGridColumn<String> column2 = new BaseGridColumn<>(Collections.emptyList(), columnRenderer, 100.0);

        baseGridData.appendColumn(column1);
        baseGridData.appendColumn(column2);

        assertEquals(1, baseGridData.getHeaderRowCount());
    }

    @Test
    public void testGetHeaderRowCountWithColumnWithoutHeaderMetaData() {
        final BaseGridColumn<String> column = new BaseGridColumn<>(Collections.emptyList(), columnRenderer, 100.0);

        baseGridData.appendColumn(column);

        assertEquals(0, baseGridData.getHeaderRowCount());
    }

    @Test
    public void testDeleteColumn() {

        final BaseGridColumn<String> column1 = new BaseGridColumn<>(header, columnRenderer, 100.0);
        final BaseGridColumn<String> column2 = new BaseGridColumn<>(header, columnRenderer, 100.0);

        baseGridData.insertColumn(0, column1);
        baseGridData.insertColumn(0, column2);

        baseGridData.deleteColumn(column1);

        final List<GridColumn<?>> columns = baseGridData.getColumns();

        assertSame(column2, columns.get(0));
        assertEquals(1, columns.size());
    }

    @Test
    public void testRemoveColumnWhenThereIsTheSameColumn() {

        final BaseGridColumn<String> column1 = new BaseGridColumn<>(header, columnRenderer, 25.0);
        final BaseGridColumn<String> column2 = new BaseGridColumn<>(header, columnRenderer, 50.0);

        baseGridData.insertColumn(0, column1);
        baseGridData.insertColumn(0, column2);

        baseGridData.removeColumn(column2);

        final List<GridColumn<?>> columns = baseGridData.getColumns();

        assertSame(column1, columns.get(0));
        assertEquals(1, columns.size());
    }

    @Test
    public void testRemoveColumnWhenThereIsNotTheSameColumnButThereIsAnEqualColumn() {

        final BaseGridColumn<String> column1 = new BaseGridColumn<>(header, columnRenderer, 25.0);
        final BaseGridColumn<String> column2 = new BaseGridColumn<>(header, columnRenderer, 50.0);

        baseGridData.insertColumn(0, column1);
        baseGridData.insertColumn(1, column2);

        baseGridData.removeColumn(new BaseGridColumn<String>(header, columnRenderer, 50.0) {{
            setIndex(1);
        }});

        final List<GridColumn<?>> columns = baseGridData.getColumns();

        assertSame(column1, columns.get(0));
        assertEquals(1, columns.size());
    }

    @Test
    public void testRemoveColumnWhenThereIsNoEqualColumn() {

        final BaseGridColumn<String> column1 = new BaseGridColumn<>(header, columnRenderer, 25.0);
        final BaseGridColumn<String> column2 = new BaseGridColumn<>(header, columnRenderer, 50.0);

        baseGridData.insertColumn(0, column1);
        baseGridData.insertColumn(1, column2);

        baseGridData.removeColumn(new BaseGridColumn<String>(header, columnRenderer, 75.0));

        final List<GridColumn<?>> columns = baseGridData.getColumns();

        assertEquals(2, columns.size());
    }

    @Test
    public void testSetCellValueWhenNoExistingGridCell() {
        final BaseGridColumn<String> column = new BaseGridColumn<>(header, columnRenderer, 25.0);

        baseGridData.insertColumn(0, column);
        baseGridData.appendRow(new BaseGridRow());

        baseGridData.setCellValue(0, 0, new BaseGridCellValue<>("hello"));

        assertThat(baseGridData.getCell(0, 0).getValue().getValue()).isEqualTo("hello");
        assertThat(baseGridData.getCell(0, 0)).isInstanceOf(BaseGridCell.class);
    }

    @Test
    public void testSetCellValueWhenExistingBaseGridCell() {
        final BaseGridColumn<String> column = new BaseGridColumn<>(header, columnRenderer, 25.0);

        baseGridData.insertColumn(0, column);
        baseGridData.appendRow(new BaseGridRow());

        baseGridData.setCell(0, 0, () -> new BaseGridCell<>(new BaseGridCellValue<>("")));
        baseGridData.setCellValue(0, 0, new BaseGridCellValue<>("hello"));

        assertThat(baseGridData.getCell(0, 0).getValue().getValue()).isEqualTo("hello");
        assertThat(baseGridData.getCell(0, 0)).isInstanceOf(BaseGridCell.class);
    }

    @Test
    public void testSetCellValueWhenExistingCustomGridCell() {
        final BaseGridColumn<String> column = new BaseGridColumn<>(header, columnRenderer, 25.0);

        baseGridData.insertColumn(0, column);
        baseGridData.appendRow(new BaseGridRow());

        baseGridData.setCell(0, 0, () -> new CustomGridCell<>(new BaseGridCellValue<>("hello")));
        baseGridData.setCellValue(0, 0, new BaseGridCellValue<>("hello"));

        assertThat(baseGridData.getCell(0, 0).getValue().getValue()).isEqualTo("hello");
        assertThat(baseGridData.getCell(0, 0)).isInstanceOf(CustomGridCell.class);
    }

    @Test
    public void testSelectHeaderCell() {
        final BaseGridColumn<String> column1 = new BaseGridColumn<>(header, columnRenderer, 100.0);
        final BaseGridColumn<String> column2 = new BaseGridColumn<>(header, columnRenderer, 100.0);

        baseGridData.appendColumn(column1);
        baseGridData.appendColumn(column2);

        assertThat(baseGridData.selectHeaderCell(0, 1).getMaxRowIndex()).isEqualTo(0);

        final List<GridData.SelectedCell> selectedHeaderCells = baseGridData.getSelectedHeaderCells();
        assertThat(selectedHeaderCells).isNotEmpty();
        assertThat(selectedHeaderCells.size()).isEqualTo(1);
        assertThat(selectedHeaderCells).contains(new GridData.SelectedCell(0, 1));
    }

    @Test
    public void testSelectHeaderCellOutOfHeaderRowRange() {
        final BaseGridColumn<String> column = new BaseGridColumn<>(header, columnRenderer, 100.0);

        baseGridData.appendColumn(column);

        assertThat(baseGridData.selectHeaderCell(1, 0).getMaxRowIndex()).isEqualTo(1);

        assertThat(baseGridData.getSelectedHeaderCells()).isEmpty();
    }

    @Test
    public void testSelectHeaderCellOutOfHeaderColumnRange() {
        assertThat(baseGridData.selectHeaderCell(0, 0).getMaxRowIndex()).isEqualTo(0);

        assertThat(baseGridData.getSelectedHeaderCells()).isEmpty();
    }

    @Test
    public void appendColumn() {
        BaseGridData data = spy(baseGridData);
        final BaseGridColumn<String> column = new BaseGridColumn<>(header, columnRenderer, 100.0);
        column.setColumnWidthMode(GridColumn.ColumnWidthMode.AUTO);

        double originalWidth = data.getWidth();
        data.appendColumn(column);

        verify(data, times(1))
                .internalRefreshWidth(booleanArgumentCaptor.capture(), optionalDoubleArgumentCaptor.capture());
        assertTrue(booleanArgumentCaptor.getValue());
        assertTrue(optionalDoubleArgumentCaptor.getValue().isPresent());
        assertEquals(originalWidth, optionalDoubleArgumentCaptor.getValue().getAsDouble(), 0.1);

        reset(data);
        final BaseGridColumn<String> columnFixed = new BaseGridColumn<>(header, columnRenderer, 100.0);
        data.appendColumn(columnFixed);

        verify(data, never()).internalRefreshWidth(anyBoolean(), any());
    }

    @Test
    public void insertColumn() {
        BaseGridData data = spy(baseGridData);
        final BaseGridColumn<String> column = new BaseGridColumn<>(header, columnRenderer, 100.0);
        column.setColumnWidthMode(GridColumn.ColumnWidthMode.AUTO);

        double originalWidth = data.getWidth();
        data.insertColumn(0, column);

        verify(data, times(1))
                .internalRefreshWidth(booleanArgumentCaptor.capture(), optionalDoubleArgumentCaptor.capture());
        assertTrue(booleanArgumentCaptor.getValue());
        assertTrue(optionalDoubleArgumentCaptor.getValue().isPresent());
        assertEquals(originalWidth, optionalDoubleArgumentCaptor.getValue().getAsDouble(), 0.1);

        reset(data);
        final BaseGridColumn<String> columnFixed = new BaseGridColumn<>(header, columnRenderer, 100.0);
        data.insertColumn(0, columnFixed);

        verify(data, never()).internalRefreshWidth(anyBoolean(), any());
    }

    @Test
    public void deleteColumn() {
        BaseGridData data = spy(baseGridData);
        final BaseGridColumn<String> column = new BaseGridColumn<>(header, columnRenderer, 100.0);
        data.appendColumn(column);
        reset(data);
        data.deleteColumn(column);

        verify(data, times(2))
                .internalRefreshWidth(booleanArgumentCaptor.capture(), optionalDoubleArgumentCaptor.capture());
        assertTrue(booleanArgumentCaptor.getAllValues().stream().allMatch(elem -> elem));
        assertFalse(optionalDoubleArgumentCaptor.getAllValues().stream().allMatch(OptionalDouble::isPresent));
    }

    @Test
    public void deleteColumn_checkColumnsAndCellsShifting() {
        BaseGridData data = spy(baseGridData);
        final BaseGridColumn<String> column = new BaseGridColumn<>(header, columnRenderer, 100.0);
        final BaseGridColumn<String> column2 = new BaseGridColumn<>(header, columnRenderer, 100.0);
        final BaseGridColumn<String> column3 = new BaseGridColumn<>(header, columnRenderer, 100.0);
        final BaseGridColumn<String> column4 = new BaseGridColumn<>(header, columnRenderer, 100.0);
        data.appendColumn(column);
        data.appendColumn(column2);
        data.appendColumn(column3);
        data.appendColumn(column4);
        final BaseGridRow row = new BaseGridRow();
        data.appendRow(row);
        BaseGridCell cell = new BaseGridCell(new BaseGridCellValue(""));
        BaseGridCell cell2 = new BaseGridCell(new BaseGridCellValue(""));
        BaseGridCell cell3 = new BaseGridCell(new BaseGridCellValue(""));
        BaseGridCell cell4 = new BaseGridCell(new BaseGridCellValue(""));
        data.setCell(0,0, () -> cell);
        data.setCell(0,1, () -> cell2);
        data.setCell(0,2, () -> cell3);
        data.setCell(0,3, () -> cell4);

        assertTrue(data.getColumnCount() == 4);
        assertSame(data.getColumns().get(0), column);
        assertSame(data.getColumns().get(1), column2);
        assertSame(data.getColumns().get(2), column3);
        assertSame(data.getColumns().get(3), column4);
        assertTrue(data.getRow(0).getCells().size() == 4);
        assertSame(data.getRow(0).getCells().get(0), cell);
        assertSame(data.getRow(0).getCells().get(1), cell2);
        assertSame(data.getRow(0).getCells().get(2), cell3);
        assertSame(data.getRow(0).getCells().get(3), cell4);

        data.deleteColumn(column2);

        assertTrue(data.getColumnCount() == 3);
        assertSame(data.getColumns().get(0), column);
        assertSame(data.getColumns().get(1), column3);
        assertSame(data.getColumns().get(2), column4);
        assertTrue(data.getRow(0).getCells().size() == 3);
        assertSame(data.getRow(0).getCells().get(0), cell);
        assertSame(data.getRow(0).getCells().get(1), cell3);
        assertSame(data.getRow(0).getCells().get(2), cell4);
        verify(data, times(2))
                .internalRefreshWidth(booleanArgumentCaptor.capture(), optionalDoubleArgumentCaptor.capture());
        assertTrue(booleanArgumentCaptor.getAllValues().stream().allMatch(elem -> elem));
        assertFalse(optionalDoubleArgumentCaptor.getAllValues().stream().allMatch(OptionalDouble::isPresent));
    }

    @Test
    public void removeColumn() {
        BaseGridData data = spy(baseGridData);
        final BaseGridColumn<String> column = new BaseGridColumn<>(header, columnRenderer, 100.0);
        data.appendColumn(column);
        reset(data);
        data.removeColumn(column);

        verify(data, times(1))
                .internalRefreshWidth(booleanArgumentCaptor.capture(), optionalDoubleArgumentCaptor.capture());
        assertTrue(booleanArgumentCaptor.getValue());
        assertFalse(optionalDoubleArgumentCaptor.getValue().isPresent());
    }

    @Test
    public void refreshWidth() {
        BaseGridData data = spy(baseGridData);

        data.refreshWidth();

        verify(data, times(1))
                .internalRefreshWidth(booleanArgumentCaptor.capture(), optionalDoubleArgumentCaptor.capture());
        assertFalse(booleanArgumentCaptor.getValue());
        assertFalse(optionalDoubleArgumentCaptor.getValue().isPresent());
    }

    @Test
    public void refreshWidthWithValue() {
        BaseGridData data = spy(baseGridData);

        double width = 100;
        data.refreshWidth(width);

        verify(data, times(1))
                .internalRefreshWidth(booleanArgumentCaptor.capture(), optionalDoubleArgumentCaptor.capture());
        assertFalse(booleanArgumentCaptor.getValue());
        assertEquals(width, optionalDoubleArgumentCaptor.getValue().getAsDouble(), 0.1);
    }

    @Test
    public void internalRefreshWidth() {
        int visibleWidth = 0;
        double minimumWidth = 600;
        BaseGridData data = spy(baseGridData);

        data.setVisibleSizeAndRefresh(visibleWidth, 0);
        assertFalse(data.internalRefreshWidth(true, OptionalDouble.empty()));

        visibleWidth = 1000;
        data.setVisibleSizeAndRefresh(visibleWidth, 0);
        assertFalse(data.internalRefreshWidth(false, OptionalDouble.empty()));
        data.setVisibleSizeAndRefresh(visibleWidth, 0);
        assertFalse(data.internalRefreshWidth(true, OptionalDouble.empty()));

        visibleWidth = 500;
        data.setVisibleSizeAndRefresh(visibleWidth, 0);
        assertFalse(data.internalRefreshWidth(true, OptionalDouble.empty()));

        BaseGridColumn<String> column = new BaseGridColumn<>(header, columnRenderer, 100.0);
        data.appendColumn(column);
        column.setColumnWidthMode(GridColumn.ColumnWidthMode.AUTO);

        column.setWidth(0);
        assertTrue(data.internalRefreshWidth(true, OptionalDouble.empty()));
        assertEquals(visibleWidth, column.getWidth(), 0.1);

        column.setWidth(0);
        column.setMinimumWidth(minimumWidth);
        assertTrue(data.internalRefreshWidth(true, OptionalDouble.empty()));
        assertEquals(minimumWidth, column.getWidth(), 0.1);

        BaseGridColumn<String> fixedColumn = new BaseGridColumn<>(header, columnRenderer, 100);
        data.appendColumn(fixedColumn);
        column.setWidth(100.0);
        column.setMinimumWidth(100.0);
        assertTrue(data.internalRefreshWidth(false, OptionalDouble.empty()));
        assertEquals(visibleWidth - fixedColumn.getWidth(), column.getWidth(), 0.1);
        // if already refreshed nothing should change
        assertFalse(data.internalRefreshWidth(false, OptionalDouble.empty()));
        assertEquals(visibleWidth - fixedColumn.getWidth(), column.getWidth(), 0.1);
    }

    @Test
    public void getWidth() {
        BaseGridData data = spy(baseGridData);
        double previous = data.getWidth();
        BaseGridColumn<String> column = new BaseGridColumn<>(header, columnRenderer, 100.0);
        column.setVisible(false);
        data.appendColumn(column);
        double updated = data.getWidth();

        assertEquals(previous, updated, 0.1);
    }

    @Test
    public void calculateInitWidth() {
        BaseGridData data = spy(baseGridData);
        double originalWidth = 100;
        BaseGridColumn<String> column = new BaseGridColumn<>(header, columnRenderer, originalWidth);
        column.setColumnWidthMode(GridColumn.ColumnWidthMode.FIXED);
        data.appendColumn(column);
        assertEquals(originalWidth, data.calculateInitWidth(column, OptionalDouble.empty()), 0.1);

        data.setVisibleSizeAndRefresh(500, 0);

        BaseGridColumn<String> column1 = new BaseGridColumn<>(header, columnRenderer, originalWidth);
        column1.setColumnWidthMode(GridColumn.ColumnWidthMode.AUTO);
        data.appendColumn(column1);

        assertEquals(data.getVisibleWidth() - originalWidth, data.calculateInitWidth(column1, OptionalDouble.empty()), 0.1);

        BaseGridColumn<String> column2 = new BaseGridColumn<>(header, columnRenderer, originalWidth);
        column2.setColumnWidthMode(GridColumn.ColumnWidthMode.AUTO);
        data.appendColumn(column2);

        BaseGridColumn<String> column3 = new BaseGridColumn<>(header, columnRenderer, originalWidth);
        column3.setColumnWidthMode(GridColumn.ColumnWidthMode.AUTO);
        data.appendColumn(column3);

        assertEquals(((data.getVisibleWidth() - originalWidth) / 2), data.calculateInitWidth(column2, OptionalDouble.empty()), 0.1);

        BaseGridColumn<String> column4 = new BaseGridColumn<>(header, columnRenderer, originalWidth);
        column4.setColumnWidthMode(GridColumn.ColumnWidthMode.AUTO);
        double minWidth = originalWidth * 10;
        column4.setMinimumWidth(minWidth);
        data.appendColumn(column4);

        assertEquals(minWidth, column4.getWidth(), 0.1);
    }

    static class CustomGridCell<T> extends BaseGridCell<T> {

        public CustomGridCell(final GridCellValue<T> value) {
            super(value);
        }
    }
}
