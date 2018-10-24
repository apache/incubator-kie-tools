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

import java.util.List;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseGridDataTest {

    @Mock
    private GridColumnRenderer<String> columnRenderer;

    @Mock
    private BaseHeaderMetaData header;

    private BaseGridData baseGridData;

    @Before
    public void setUp() {
        baseGridData = new BaseGridData();
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

    static class CustomGridCell<T> extends BaseGridCell<T> {

        public CustomGridCell(final GridCellValue<T> value) {
            super(value);
        }
    }
}
