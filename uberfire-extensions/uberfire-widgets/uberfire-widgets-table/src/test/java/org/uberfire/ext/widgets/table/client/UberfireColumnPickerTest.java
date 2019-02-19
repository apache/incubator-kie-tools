/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.widgets.table.client;

import java.util.Arrays;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.gwtbootstrap3.client.ui.gwt.Widget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class UberfireColumnPickerTest {

    @Mock
    protected org.gwtbootstrap3.client.ui.gwt.DataGrid dataGrid;
    @GwtMock
    VerticalPanel popupContent;
    @InjectMocks
    private UberfireColumnPicker<String> uberfireColumnPicker;
    private ColumnMeta columnMetaTextCell1;
    private ColumnMeta columnMetaTextCell2;
    private ColumnMeta columnMetaCheckBoxCell;

    @Before
    public void setup() {
        columnMetaTextCell1 = createColumnTextCell("val1", "col1");
        columnMetaTextCell2 = createColumnTextCell("val2", "col2");
        columnMetaCheckBoxCell = createColumnCheckboxCell("columnMetaCheckBoxCell");
        uberfireColumnPicker.addColumn(columnMetaCheckBoxCell);
        uberfireColumnPicker.addColumn(columnMetaTextCell1);
        uberfireColumnPicker.addColumn(columnMetaTextCell2);
    }

    @Test
    public void testAddThisColumnToPopup() {
        assertTrue(uberfireColumnPicker.addThisColumnToPopup(columnMetaTextCell1));
        assertFalse(uberfireColumnPicker.addThisColumnToPopup(columnMetaCheckBoxCell));
    }

    @Test
    public void testOnlyAddHeaderStringColumnPickerPopup() {
        uberfireColumnPicker.showColumnPickerPopup(0, 0);
        verify(popupContent, times(2)).add(any(Widget.class));
    }

    private ColumnMeta createColumnMeta(final String value,
                                        final String dataStoreName,
                                        boolean isVisible,
                                        boolean isVisibleIndex,
                                        int position) {

        final Header<String> header = new TextHeader(value);
        ColumnMeta<String> columnMeta = new ColumnMeta(createColumn(value, dataStoreName), dataStoreName);
        columnMeta.setHeader(header);
        columnMeta.setVisible(isVisible);
        columnMeta.setVisibleIndex(isVisibleIndex);
        columnMeta.setPosition(position);
        return columnMeta;
    }

    private Column createColumn(final String value, final String dataStoreName) {
        Column<String, String> testColumn = new Column<String, String>(new TextCell()) {
            @Override
            public String getValue(String object) {
                return value;
            }
        };
        testColumn.setSortable(true);
        testColumn.setDataStoreName(dataStoreName);

        return testColumn;
    }

    private ColumnMeta createColumnTextCell(final String value, String dataStoreName) {

        return createColumnMeta(value, dataStoreName, true, true, -1);
    }

    private ColumnMeta createColumnCheckboxCell(String dataStoreName) {
        CheckboxCell checkboxCell = new CheckboxCell(true, false);
        Column<String, Boolean> checkColumn = new Column<String, Boolean>(checkboxCell) {
            @Override
            public Boolean getValue(String object) {
                return true;
            }
        };

        Header<Boolean> selectPageHeader = new Header<Boolean>(checkboxCell) {
            @Override
            public Boolean getValue() {
                return true;
            }
        };

        checkColumn.setSortable(false);
        checkColumn.setDataStoreName(dataStoreName);
        ColumnMeta<String> checkColMeta = new ColumnMeta<String>(checkColumn, "");
        checkColMeta.setHeader(selectPageHeader);
        return checkColMeta;
    }

    @Test
    public void testAddColumnBeforeActionsOnAddColumnOnDataGrid() {
        UberfireColumnPicker<String> columnPicker = new UberfireColumnPicker<>(new DataGrid<>());
        ColumnMeta<String> name = createColumnMeta("Name", "name", true, true, -1);
        ColumnMeta<String> age = createColumnMeta("Age", "age", true, true, -1);
        ColumnMeta<String> description = createColumnMeta("Description", "description", false, true, -1);
        ColumnMeta<String> actions = createColumnMeta("Actions", "actions", true, false, -1);
        ColumnMeta<String> etc = createColumnMeta("Etc", "etc", false, true, -1);

        columnPicker.addColumn(name);
        columnPicker.addColumn(age);
        columnPicker.addColumn(description);
        columnPicker.addColumn(actions);
        columnPicker.addColumn(etc);

        assertEquals(3, columnPicker.getDataGrid().getColumnCount());
        assertEquals("name", columnPicker.getDataGrid().getColumn(0).getDataStoreName());
        assertEquals("age", columnPicker.getDataGrid().getColumn(1).getDataStoreName());
        assertEquals("actions", columnPicker.getDataGrid().getColumn(2).getDataStoreName());

        columnPicker.addColumnOnDataGrid(true, etc);

        assertEquals(4, columnPicker.getDataGrid().getColumnCount());
        assertEquals("etc", columnPicker.getDataGrid().getColumn(2).getDataStoreName());
        assertEquals("actions", columnPicker.getDataGrid().getColumn(3).getDataStoreName());

        columnPicker.addColumnOnDataGrid(true, description);

        assertEquals(5, columnPicker.getDataGrid().getColumnCount());
        assertEquals("description", columnPicker.getDataGrid().getColumn(2).getDataStoreName());
        assertEquals("etc", columnPicker.getDataGrid().getColumn(3).getDataStoreName());
        assertEquals("actions", columnPicker.getDataGrid().getColumn(4).getDataStoreName());
    }

    @Test
    public void testAddColumnBeforeActionsOnAddColumn() {
        UberfireColumnPicker<String> columnPicker = new UberfireColumnPicker<>(new DataGrid<>());
        ColumnMeta<String> name = createColumnMeta("Name", "name", true, true, -1);
        ColumnMeta<String> age = createColumnMeta("Age", "age", true, true, -1);
        ColumnMeta<String> description = createColumnMeta("Description", "description", false, true, -1);
        ColumnMeta<String> actions = createColumnMeta("Actions", "actions", true, false, -1);
        ColumnMeta<String> etc = createColumnMeta("Etc", "etc", true, true, -1);

        columnPicker.addColumn(name);
        columnPicker.addColumn(age);
        columnPicker.addColumn(description);
        columnPicker.addColumn(actions);
        columnPicker.addColumn(etc);

        assertEquals(4, columnPicker.getDataGrid().getColumnCount());
        assertEquals("name", columnPicker.getDataGrid().getColumn(0).getDataStoreName());
        assertEquals("age", columnPicker.getDataGrid().getColumn(1).getDataStoreName());
        assertEquals("etc", columnPicker.getDataGrid().getColumn(2).getDataStoreName());
        assertEquals("actions", columnPicker.getDataGrid().getColumn(3).getDataStoreName());

        ColumnMeta<String> comment = createColumnMeta("comment", "comment", true, true, -1);
        columnPicker.addColumn(comment);
        assertEquals(5, columnPicker.getDataGrid().getColumnCount());
        assertEquals("name", columnPicker.getDataGrid().getColumn(0).getDataStoreName());
        assertEquals("age", columnPicker.getDataGrid().getColumn(1).getDataStoreName());
        assertEquals("etc", columnPicker.getDataGrid().getColumn(2).getDataStoreName());
        assertEquals("comment", columnPicker.getDataGrid().getColumn(3).getDataStoreName());
        assertEquals("actions", columnPicker.getDataGrid().getColumn(4).getDataStoreName());
    }

    @Test
    public void testAddColumns() {
        UberfireColumnPicker<String> columnPicker = new UberfireColumnPicker<>(new DataGrid<>());
        ColumnMeta<String> name = createColumnMeta("Name", "name", true, true, 0);
        ColumnMeta<String> age = createColumnMeta("Age", "age", true, true, 3);
        ColumnMeta<String> description = createColumnMeta("Description", "description", false, true, 1);
        ColumnMeta<String> actions = createColumnMeta("Actions", "actions", true, false, 2);
        ColumnMeta<String> etc = createColumnMeta("Etc", "etc", true, true, 4);

        columnPicker.addColumns(Arrays.asList(name, age, description, actions, etc));

        assertEquals(4, columnPicker.getDataGrid().getColumnCount());
        assertEquals("name", columnPicker.getDataGrid().getColumn(0).getDataStoreName());
        assertEquals("age", columnPicker.getDataGrid().getColumn(1).getDataStoreName());
        assertEquals("etc", columnPicker.getDataGrid().getColumn(2).getDataStoreName());
        assertEquals("actions", columnPicker.getDataGrid().getColumn(3).getDataStoreName());
    }

    @Test
    public void testRemoveColumns() {
        UberfireColumnPicker<String> columnPicker = new UberfireColumnPicker<>(new DataGrid<>());
        ColumnMeta<String> name = createColumnMeta("Name", "name", true, true, 0);
        ColumnMeta<String> age = createColumnMeta("Age", "age", true, true, 3);
        ColumnMeta<String> description = createColumnMeta("Description", "description", false, true, 1);
        ColumnMeta<String> actions = createColumnMeta("Actions", "actions", true, false, 2);
        ColumnMeta<String> etc = createColumnMeta("Etc", "etc", true, true, 4);

        columnPicker.addColumns(Arrays.asList(name, age, description, actions, etc));

        columnPicker.removeColumn(name);
        columnPicker.removeColumn(description);

        assertEquals(3, columnPicker.getDataGrid().getColumnCount());
        assertEquals("age", columnPicker.getDataGrid().getColumn(0).getDataStoreName());
        assertEquals("etc", columnPicker.getDataGrid().getColumn(1).getDataStoreName());
        assertEquals("actions", columnPicker.getDataGrid().getColumn(2).getDataStoreName());
    }
}

