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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.mockito.Spy;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(GwtMockitoTestRunner.class)
public class ColumnPickerTest {

    @Spy
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
        doAnswer((Answer<Void>) invocationOnMock -> {
            columns.add((Column) invocationOnMock.getArguments()[0]);
            return null;
        }).when(dataGrid).addColumn(any(Column.class), any(Header.class));

        doAnswer((Answer<Void>) invocationOnMock -> {
            columns.add((Column) invocationOnMock.getArguments()[1]);
            return null;
        }).when(dataGrid).insertColumn(any(int.class),
                                       any(Column.class),
                                       any(Header.class));

        doAnswer((Answer<Integer>) invocationOnMock -> columns.size()).when(dataGrid).getColumnCount();

        doAnswer((Answer<Void>) invocationOnMock -> {
            columns.remove(0);
            return null;
        }).when(dataGrid).removeColumn(0);

        when(toggleButton.addClickHandler(any(ClickHandler.class))).thenAnswer((Answer) aInvocation -> {
            clickHandler = (ClickHandler) aInvocation.getArguments()[0];
            return null;
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
    public void testToggleButton() {
        doNothing().when(columnPicker).addResetButtom(anyInt(), anyInt(), any());
        columnPicker.createToggleButton();
        clickHandler.onClick(new ClickEvent() {
        });

        verify(popup).show();

        when(toggleButton.isActive()).thenReturn(true);

        clickHandler.onClick(new ClickEvent() {
        });

        verify(popup).hide(false);
    }
}