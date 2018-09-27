/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.renderer.google.client;

import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.ExpenseReportsData.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GoogleTableDisplayerTest extends GoogleDisplayerTest {

    @Test
    public void testTableDraw() {

        DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .tableOrderDefault(COLUMN_DEPARTMENT, SortOrder.DESCENDING)
                .tableOrderEnabled(true)
                .tablePageSize(10)
                .tableWidth(1000)
                .buildSettings();

        GoogleTableDisplayer table = createTableDisplayer(allRows);
        GoogleTableDisplayer.View tableView = table.getView();
        table.ready();

        verify(tableView).setWidth(1000);
        verify(tableView).setSortEnabled(true);
        verify(tableView).setPageSize(10);
        verify(tableView).setTotalRows(50);
        verify(tableView).setTotalPages(5);
        verify(tableView).setPagerEnabled(true);
        verify(tableView).drawTable();
    }

    @Test
    public void testEmptyTableDraw() {

        DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .filter(COLUMN_ID, FilterFactory.isNull())
                .tablePageSize(10)
                .buildSettings();

        GoogleTableDisplayer table = createTableDisplayer(allRows);
        GoogleTableDisplayer.View tableView = table.getView();
        table.ready();

        verify(tableView).setPageSize(10);
        verify(tableView).setTotalRows(0);
        verify(tableView).setTotalPages(1);
        verify(tableView).setPagerEnabled(false);
        verify(tableView).nodata();
    }

    @Test
    public void testTablePager() {

        DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .tablePageSize(10)
                .buildSettings();

        GoogleTableDisplayer table = createTableDisplayer(allRows);
        GoogleTableDisplayer.View tableView = table.getView();
        table.ready();

        // Initialization
        verify(tableView).setPageSize(10);
        verify(tableView).setTotalRows(50);
        verify(tableView).setTotalPages(5);
        verify(tableView).setCurrentPage(1);
        verify(tableView).setPagerEnabled(true);
        verify(tableView).drawTable();

        // Non existent page (no effect)
        reset(tableView);
        table.gotoPage(-1);
        assertEquals(table.getCurrentPage(), 1);
        verifyZeroInteractions(tableView);

        // Non existent page (no effect)
        reset(tableView);
        table.gotoPage(11);
        assertEquals(table.getCurrentPage(), 1);
        verifyZeroInteractions(tableView);

        // Goto page
        reset(tableView);
        table.gotoPage(5);
        verify(tableView).setCurrentPage(5);
        assertEquals(table.getCurrentPage(), 5);
        verify(tableView).drawTable();
    }

    @Test
    public void testTableSort() {

        DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .tablePageSize(10)
                .tableOrderDefault(COLUMN_ID, SortOrder.DESCENDING)
                .buildSettings();

        // Sorted by ID descending by default
        GoogleTableDisplayer table = createTableDisplayer(allRows);
        GoogleTableDisplayer.View tableView = table.getView();
        table.ready();
        assertEquals(table.getDataSetHandler().getLastDataSet().getValueAt(0,0), 50d);

        // Sort disabled (no effect)
        allRows.setTableSortEnabled(false);
        table = createTableDisplayer(allRows);
        tableView = table.getView();
        table.ready();
        reset(tableView);
        table.sortBy(COLUMN_ID);
        verify(tableView, never()).drawTable();
        assertEquals(table.getDataSetHandler().getLastDataSet().getValueAt(0, 0), 50d);

        // Sort enabled
        allRows.setTableSortEnabled(true);
        table = createTableDisplayer(allRows);
        tableView = table.getView();
        table.ready();
        reset(tableView);
        table.sortBy(COLUMN_ID);
        verify(tableView).setCurrentPage(1);
        verify(tableView).drawTable();
        assertEquals(table.getDataSetHandler().getLastDataSet().getValueAt(0, 0), 1d);
    }
}