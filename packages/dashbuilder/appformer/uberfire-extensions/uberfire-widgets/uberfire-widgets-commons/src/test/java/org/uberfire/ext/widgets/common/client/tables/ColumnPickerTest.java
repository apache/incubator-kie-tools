/*
 * Copyright 2019 JBoss Inc
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

package org.uberfire.ext.widgets.common.client.tables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@RunWith(GwtMockitoTestRunner.class)
public class ColumnPickerTest {

    @InjectMocks
    ColumnPicker columnPicker;

    @GwtMock
    DataGrid dataGrid;

    @GwtMock
    Button toggleButton;

    @GwtMock
    PopupPanel popup;

    ClickHandler clickHandler;

    @Before
    public void setUp() throws Exception {
        final List<Column> columns = new ArrayList<Column>();
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                columns.add((Column) invocationOnMock.getArguments()[0]);
                return null;
            }
        }).when(dataGrid).addColumn(any(Column.class), any(Header.class));

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                columns.add((Column) invocationOnMock.getArguments()[1]);
                return null;
            }
        }).when(dataGrid).insertColumn(any(int.class),
                                       any(Column.class),
                                       any(Header.class));

        doAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                return columns.size();
            }
        }).when(dataGrid).getColumnCount();

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                columns.remove(0);
                return null;
            }
        }).when(dataGrid).removeColumn(0);

        when(toggleButton.addClickHandler(any(ClickHandler.class))).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock aInvocation) throws Throwable {
                clickHandler = (ClickHandler) aInvocation.getArguments()[0];
                return null;
            }
        });
    }

    @Test
    public void testAddRemoveColumn() {
        final Column column = mock(Column.class);
        when(column.getDataStoreName()).thenReturn("id");
        final ColumnMeta meta = new ColumnMeta(column, "caption");
        meta.setHeader(new TextHeader("header"));

        columnPicker.addColumn(meta);

        assertTrue(columnPicker.getColumnMetaList().contains(meta));
        verify(dataGrid).insertColumn(0, column, meta.getHeader());
        assertEquals(1, dataGrid.getColumnCount());

        columnPicker.removeColumn(meta);

        assertFalse(columnPicker.getColumnMetaList().contains(meta));
        verify(dataGrid).removeColumn(0);
        assertEquals(0, dataGrid.getColumnCount());
    }


    @Test
    public void testSortColumn() {
        final Column column1 = mock(Column.class);
        final ColumnMeta meta1 = new ColumnMeta(column1, "caption1", true, 1);
        meta1.setHeader(new TextHeader("header1"));
        final Column column0 = mock(Column.class);
        final ColumnMeta meta0 = new ColumnMeta(column0, "caption0", true, 0);
        meta0.setHeader(new TextHeader("header0"));

        columnPicker.addColumns(Arrays.asList(meta1, meta0));

        assertEquals(2, columnPicker.getColumnMetaList().size());
        verify(dataGrid).insertColumn(0, column0, meta0.getHeader());
        verify(dataGrid).insertColumn(1, column1, meta1.getHeader());
        assertEquals(2, dataGrid.getColumnCount());
    }

    @Test
    public void testColumnPreference() {
        final Column column = mock(Column.class);
        when(column.getDataStoreName()).thenReturn("id");
        final ColumnMeta meta = new ColumnMeta(column, "caption");
        meta.setHeader(new TextHeader("header"));

        columnPicker.addColumn(meta);
        final List<GridColumnPreference> columnsState = columnPicker.getColumnsState();
        assertEquals(1, columnsState.size());

        final GridColumnPreference preference = columnsState.get(0);
        assertEquals(preference.getName(), column.getDataStoreName());
        assertEquals(0, preference.getPosition().intValue());
    }

    @Test
    public void testToggleButton() {
        columnPicker.createToggleButton();
        clickHandler.onClick(new ClickEvent() {
        });

        verify(popup).show();

        when(toggleButton.isActive()).thenReturn(true);

        clickHandler.onClick(new ClickEvent() {
        });

        verify(popup).hide(false);
    }

    @Test
    public void testAdjustColumnWidths() {
        final Column column1 = createColumn("col1", "col1");
        final ColumnMeta meta1 = new ColumnMeta(column1, "caption1", true, 1);
        meta1.setHeader(new TextHeader("header1"));
        final Column column2 = createColumn("col2", "col2");
        final ColumnMeta meta2 = new ColumnMeta(column2, "caption2", true, 0);
        meta2.setHeader(new TextHeader("header2"));
        final Column column3 = createColumn("col3", "col3");
        final ColumnMeta meta3 = new ColumnMeta(column3, "caption3", true, 0);
        meta3.setHeader(new TextHeader("header3"));

        when(dataGrid.getColumnWidth(column1)).thenReturn("35px");
        when(dataGrid.getColumnWidth(column2)).thenReturn(null);
        when(dataGrid.getColumnWidth(column3)).thenReturn(null);

        List<ColumnMeta> columnMetasList = new ArrayList<ColumnMeta>();
        columnMetasList.add(meta1);
        columnMetasList.add(meta2);
        columnMetasList.add(meta3);
        columnPicker.addColumns(columnMetasList);

        verify(dataGrid).setColumnWidth(column1, "35px");
        verify(dataGrid).setColumnWidth(column2, 50, Style.Unit.PCT);
        verify(dataGrid).setColumnWidth(column3, 50, Style.Unit.PCT);
    }

    private Column createColumn(String value,
                                String dataStoreName) {

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

    @Test
    public void testSetColumnWidth() {
        final Column column1 = createColumn("col1", "col1");
        final ColumnMeta meta1 = new ColumnMeta(column1, "caption1", true, 1);
        final Column column2 = createColumn("col2", "col2");
        final ColumnMeta meta2 = new ColumnMeta(column2, "caption2", true, 0);

        when(dataGrid.getColumnWidth(column1)).thenReturn("38.0px");

        columnPicker.addColumns(Lists.newArrayList(meta1, meta2));

        verify(dataGrid).setColumnWidth(eq(column1), eq("38px"));
    }

    @Test
    public void testAddColumnsIncrementally() {
        final Column column1 = createColumn("col1", "col1");
        final ColumnMeta meta1 = new ColumnMeta(column1, "caption1", true, 0);
        final Column column2 = createColumn("col2", "col2");
        final ColumnMeta meta2 = new ColumnMeta(column2, "caption2", true, 1);

        when(dataGrid.getColumn(0)).thenReturn(column1);
        when(dataGrid.getColumn(1)).thenReturn(column2);
        when(dataGrid.getColumnWidth(column1)).thenReturn("100px");
        when(dataGrid.getColumnWidth(column2)).thenReturn("90%");

        columnPicker.addColumns(Collections.singletonList(meta1));

        verify(dataGrid).setColumnWidth(eq(column1), eq(100.0), eq(Style.Unit.PCT));

        columnPicker.addColumns(Collections.singletonList(meta2));

        verify(dataGrid).setColumnWidth(eq(column1), eq("100px"));
        verify(dataGrid).setColumnWidth(eq(column2), eq(100.0), eq(Style.Unit.PCT));
    }

    @Test
    public void testDataGridMinWidthCalculation() {
        int defaultColumnWidthSize = 150;
        int fixedColumn1Width = 38;
        final Column column1 = createColumn("col1", "col1");
        final ColumnMeta meta1 = new ColumnMeta(column1, "caption1", true, 1);
        final Column column2 = createColumn("col2", "col2");
        final ColumnMeta meta2 = new ColumnMeta(column2, "caption2", true, 0);
        final Column column3 = createColumn("col3", "col3");
        final ColumnMeta meta3 = new ColumnMeta(column3, "caption3", false, 2);

        when(dataGrid.getColumn(0)).thenReturn(column1);
        when(dataGrid.getColumn(1)).thenReturn(column2);
        when(dataGrid.getColumnWidth(column1)).thenReturn(fixedColumn1Width + Style.Unit.PX.getType());
        when(dataGrid.getColumnWidth(column2)).thenReturn(100 + Style.Unit.PC.getType());
        when(dataGrid.getColumnWidth(column3)).thenReturn(57 + Style.Unit.PX.getType());

        columnPicker.addColumns(Arrays.asList(meta1, meta2, meta3));
        assertEquals(fixedColumn1Width + ColumnPicker.DETAULT_COLUMN_WIDTH, columnPicker.getDataGridMinWidth());

        columnPicker.setDefaultColumnWidthSize(defaultColumnWidthSize);
        columnPicker.adjustColumnWidths();
        assertEquals(fixedColumn1Width + defaultColumnWidthSize, columnPicker.getDataGridMinWidth());
    }
}