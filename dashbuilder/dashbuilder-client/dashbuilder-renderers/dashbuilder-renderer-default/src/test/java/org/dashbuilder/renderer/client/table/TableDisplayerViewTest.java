/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.renderer.client.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;
import org.uberfire.ext.widgets.table.client.DataGrid;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(DataGrid.class)
public class TableDisplayerViewTest {

    @Mock
    PagedTable<Integer> table;

    @InjectMocks
    TableDisplayerView tableDisplayerView;

    @Test
    public void testCreateTable() {
        final HasWidgets hasWidgets = mock(HasWidgets.class);
        when(table.getRightToolbar()).thenReturn(hasWidgets);

        tableDisplayerView.setupToolbar();

        verify(hasWidgets,
               times(2)).add(any());
    }

    @Test
    public void testCreateTableUsingHorizontalPanel() {
        final HorizontalPanel panel = mock(HorizontalPanel.class);
        when(table.getRightToolbar()).thenReturn(panel);

        tableDisplayerView.setupToolbar();

        verify(panel).insert(any(),
                             eq(0));
        verify(panel).insert(any(),
                             eq(1));
    }

    @Test
    public void testCurrentPageForNextPageWithoutTotalCount(){
        final HasData display = mock(HasData.class);
        when(table.getRowCount()).thenReturn(1);
        when(display.getVisibleRange()).thenReturn(new Range(10, 10));
        assertEquals(1, tableDisplayerView.tableProvider.getCurrentPageRows(display).size());
    }

    @Test
    public void testCurrentPageForNextPageWithTotalCount(){
        final HasData display = mock(HasData.class);
        when(table.getRowCount()).thenReturn(11);
        when(display.getVisibleRange()).thenReturn(new Range(10, 10));
        assertEquals(1, tableDisplayerView.tableProvider.getCurrentPageRows(display).size());
    }

    @Test
    public void testCurrentPageForNextPageWithoutRows(){
        final HasData display = mock(HasData.class);
        when(table.getRowCount()).thenReturn(0);
        when(display.getVisibleRange()).thenReturn(new Range(10, 10));
        assertEquals(0, tableDisplayerView.tableProvider.getCurrentPageRows(display).size());
    }

    @Test
    public void testCurrentPage(){
        final HasData display = mock(HasData.class);
        when(table.getRowCount()).thenReturn(5);
        when(display.getVisibleRange()).thenReturn(new Range(0, 10));
        assertEquals(5, tableDisplayerView.tableProvider.getCurrentPageRows(display).size());
    }
}
