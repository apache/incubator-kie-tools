/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.uberfire.ext.widgets.common.client.tables;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.html.Text;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.ext.widgets.common.client.tables.PagedTable.DEFAULT_PAGE_SIZE;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Image.class, Label.class, Text.class})
public class PagedTableTest {

    @GwtMock
    AsyncDataProvider dataProvider;

    @GwtMock
    Select select;

    @Test
    public void testSetDataProvider() throws Exception {
        PagedTable pagedTable = new PagedTable();

        pagedTable.setDataProvider(dataProvider);
        verify(dataProvider).addDataDisplay(pagedTable);
    }

    @Test
    public void testDataGridHeight() throws Exception {
        final int PAGE_SIZE = 10;
        final int ROWS = 2;
        final int EXPECTED_HEIGHT_PX = PagedTable.HEIGHT_OFFSET_PX;
        PagedTable pagedTable = new PagedTable(PAGE_SIZE, null, false, false, false);
        pagedTable.dataGrid = spy(pagedTable.dataGrid);
        when(pagedTable.dataGrid.getRowCount()).thenReturn(ROWS);

        verify(pagedTable.dataGrid, times(0)).setHeight(anyString());
        pagedTable.loadPageSizePreferences();
        verify(pagedTable.dataGrid, times(1)).setHeight(eq(EXPECTED_HEIGHT_PX + "px"));
    }

    @Test
    public void testDataGridHeightWithMoreItemsThanPaging() throws Exception {
        final int PAGE_SIZE = 10;
        final int ROWS = 12;
        final int EXPECTED_HEIGHT_PX = PagedTable.HEIGHT_OFFSET_PX;
        var pagedTable = new PagedTable(PAGE_SIZE, null, false, false, false);
        pagedTable.dataGrid = spy(pagedTable.dataGrid);
        when(pagedTable.dataGrid.getRowCount()).thenReturn(ROWS);

        verify(pagedTable.dataGrid, times(0)).setHeight(anyString());
        pagedTable.loadPageSizePreferences();
        verify(pagedTable.dataGrid, times(1)).setHeight(eq(EXPECTED_HEIGHT_PX + "px"));
    }

    @Test
    public void testLoadPageSizePreferencesResetsPageStart() throws Exception {
        final int PAGE_SIZE = 10;

        PagedTable pagedTable = new PagedTable(PAGE_SIZE);
        pagedTable.dataGrid = spy(pagedTable.dataGrid);

        verify(pagedTable.dataGrid, times(0)).setPageStart(0);

        pagedTable.loadPageSizePreferences();
        verify(pagedTable.dataGrid, times(1)).setPageStart(0);
    }

    @Test
    public void testPageSizeSelectStartValue() throws Exception {
        final int size = 10;

        new PagedTable(size);

        verify(select).setValue(String.valueOf(size));
        verify(select).addValueChangeHandler(any());
    }

    @Test
    public void testDefaultPageSizeValue() throws Exception {
        new PagedTable();

        verify(select).setValue(String.valueOf(DEFAULT_PAGE_SIZE));
        verify(select).addValueChangeHandler(any());
    }

    @Test
    public void testDataGridMinWidthNotSetByDefault() throws Exception {
        final int PAGE_SIZE = 10;
        Element mockElement = mock(Element.class);
        final int minWidth = 800;
        PagedTable pagedTable = new PagedTable(PAGE_SIZE, null, false, false, false);
        ColumnPicker columnPickerMock = mock(ColumnPicker.class);

        when(pagedTable.dataGridContainer.getElement()).thenReturn(mockElement);
        when(columnPickerMock.getDataGridMinWidth()).thenReturn(minWidth);

        pagedTable.setColumnPicker(columnPickerMock);
        pagedTable.setTableHeight();

        verify(mockElement, never()).setAttribute(eq("style"), anyString());
    }

    @Test
    public void testEnableDataGridMinWidth() throws Exception {
        final int PAGE_SIZE = 10;
        Element mockElement = mock(Element.class);
        final int minWidth = 800;
        PagedTable pagedTable = new PagedTable(PAGE_SIZE, null, false, false, false);
        ColumnPicker columnPickerMock = mock(ColumnPicker.class);

        when(pagedTable.dataGridContainer.getElement()).thenReturn(mockElement);
        when(columnPickerMock.getDataGridMinWidth()).thenReturn(minWidth);

        pagedTable.setColumnPicker(columnPickerMock);
        pagedTable.enableDataGridMinWidth(true);
        pagedTable.setTableHeight();

        verify(columnPickerMock, never()).setDefaultColumnWidthSize(anyInt());
        verify(mockElement).setAttribute("style", "min-width:" + minWidth + Style.Unit.PX.getType());
    }

    @Test
    public void testDefaultColumnWidth() throws Exception {
        final int PAGE_SIZE = 10;
        Element mockElement = mock(Element.class);
        final int minWidth = 800;
        PagedTable pagedTable = new PagedTable(PAGE_SIZE, null, false, false, false);
        ColumnPicker columnPickerMock = mock(ColumnPicker.class);

        when(pagedTable.dataGridContainer.getElement()).thenReturn(mockElement);
        when(columnPickerMock.getDataGridMinWidth()).thenReturn(minWidth);

        pagedTable.setColumnPicker(columnPickerMock);
        pagedTable.setDefaultColumWidthSize(150);
        pagedTable.enableDataGridMinWidth(true);
        pagedTable.setTableHeight();

        verify(columnPickerMock).setDefaultColumnWidthSize(150);
        verify(mockElement).setAttribute("style", "min-width:" + minWidth + Style.Unit.PX.getType());
    }

}
