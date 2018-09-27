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
package org.dashbuilder.renderer.client.table;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.common.client.widgets.FilterLabel;
import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.dashbuilder.displayer.client.formatter.ValueFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.dashbuilder.dataset.ExpenseReportsData.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.COUNT;
import static org.dashbuilder.dataset.group.AggregateFunctionType.MIN;
import static org.dashbuilder.dataset.sort.SortOrder.ASCENDING;
import static org.dashbuilder.dataset.sort.SortOrder.DESCENDING;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TableDisplayerTest extends AbstractDisplayerTest {

    public TableDisplayer createTableDisplayer(DisplayerSettings settings) {
        return initDisplayer(new TableDisplayer(mock(TableDisplayer.View.class), mock(FilterLabelSet.class)), settings);
    }

    @Mock
    DisplayerListener displayerListener;

    @Mock
    Command selectCommand;

    @Mock
    FilterLabel filterLabel;

    public void resetFilterLabelSet(FilterLabelSet filterLabelSet) {
        reset(filterLabelSet);
        doAnswer(invocationOnMock -> filterLabel).when(filterLabelSet).addLabel(anyString());
    }

    @Test
    public void testTableDraw() {

        DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .tableOrderDefault(COLUMN_DEPARTMENT, SortOrder.DESCENDING)
                .tableOrderEnabled(true)
                .tablePageSize(10)
                .tableWidth(1000)
                .filterOn(true, true, true)
                .allowCsvExport(true)
                .allowExcelExport(false)
                .buildSettings();

        TableDisplayer table = createTableDisplayer(allRows);
        TableDisplayer.View tableView = table.getView();
        FilterLabelSet filterLabelSet = table.getFilterLabelSet();
        table.draw();

        verify(tableView).setWidth(1000);
        verify(tableView).setSortEnabled(true);
        verify(tableView, times(2)).setTotalRows(50, true);
        verify(tableView).createTable(10, filterLabelSet);
        verify(tableView).addColumn(ColumnType.NUMBER, COLUMN_ID, COLUMN_ID, 0, false, true);
        verify(tableView).addColumn(ColumnType.LABEL, COLUMN_CITY, COLUMN_CITY, 1, true, true);
        verify(tableView).addColumn(ColumnType.LABEL, COLUMN_DEPARTMENT, COLUMN_DEPARTMENT, 2, true, true);
        verify(tableView).addColumn(ColumnType.LABEL, COLUMN_EMPLOYEE, COLUMN_EMPLOYEE, 3, true, true);
        verify(tableView).addColumn(ColumnType.DATE, COLUMN_DATE, COLUMN_DATE, 4, false, true);
        verify(tableView).addColumn(ColumnType.NUMBER, COLUMN_AMOUNT, COLUMN_AMOUNT, 5, false, true);
        verify(tableView).setExportToCsvEnabled(true);
        verify(tableView).setExportToXlsEnabled(false);
        verify(tableView).gotoFirstPage();
    }

    @Test
    public void testEmptyTableDraw() {

        DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .filter(COLUMN_ID, FilterFactory.isNull())
                .tablePageSize(10)
                .buildSettings();

        TableDisplayer table = createTableDisplayer(allRows);
        TableDisplayer.View tableView = table.getView();
        FilterLabelSet filterLabelSet = table.getFilterLabelSet();
        table.draw();

        verify(tableView).createTable(10, filterLabelSet);
        verify(tableView, times(2)).setTotalRows(0, true);
        verify(tableView).setPagerEnabled(false);
        verify(tableView, never()).setPagerEnabled(true);

        reset(tableView);
        table.redraw();
        verify(tableView, never()).setPagerEnabled(true);
    }

    @Test
    public void testTableSort() {

        DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .tablePageSize(10)
                .tableOrderDefault(COLUMN_ID, SortOrder.DESCENDING)
                .buildSettings();

        // Sorted by ID descending by default
        TableDisplayer table = createTableDisplayer(allRows);
        TableDisplayer.View tableView = table.getView();
        table.draw();
        assertEquals(table.getDataSetHandler().getLastDataSet().getValueAt(0, 0), 50d);

        // Sort disabled (no effect)
        allRows.setTableSortEnabled(false);
        table = createTableDisplayer(allRows);
        tableView = table.getView();
        table.draw();
        reset(tableView);
        table.sortBy(COLUMN_ID, SortOrder.DESCENDING);
        verify(tableView, never()).redrawTable();
        assertEquals(table.getDataSetHandler().getLastDataSet().getValueAt(0, 0), 50d);

        // Sort enabled
        allRows.setTableSortEnabled(true);
        table = createTableDisplayer(allRows);
        tableView = table.getView();
        table.draw();
        reset(tableView);
        table.sortBy(COLUMN_ID, SortOrder.ASCENDING);
        verify(tableView).redrawTable();
        assertEquals(table.getDataSetHandler().getLastDataSet().getValueAt(0, 0), 1d);
    }

    @Test
    public void testSelectCellDisabled() {

        DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .tablePageSize(10)
                .tableOrderDefault(COLUMN_ID, SortOrder.DESCENDING)
                .filterOff(false)
                .buildSettings();

        TableDisplayer table = createTableDisplayer(allRows);
        TableDisplayer.View view = table.getView();
        FilterLabelSet filterLabelSet = table.getFilterLabelSet();
        table.addListener(displayerListener);
        table.addOnCellSelectedCommand(selectCommand);
        table.draw();

        reset(view);
        reset(displayerListener);
        resetFilterLabelSet(filterLabelSet);
        table.selectCell(COLUMN_DEPARTMENT, 3);

        verify(selectCommand, never()).execute();
        verify(view, never()).gotoFirstPage();
        verify(filterLabelSet, never()).addLabel(anyString());
        verify(displayerListener, never()).onRedraw(table);
        assertNull(table.getSelectedCellColumn());
        assertNull(table.getSelectedCellRow());
    }

    @Test
    public void testSelectCellNoDrillDown() {

        DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .tablePageSize(10)
                .tableOrderDefault(COLUMN_ID, SortOrder.DESCENDING)
                .filterOn(false, true, true)
                .buildSettings();

        TableDisplayer table = createTableDisplayer(allRows);
        TableDisplayer.View view = table.getView();
        FilterLabelSet filterLabelSet = table.getFilterLabelSet();
        table.addListener(displayerListener);
        table.addOnCellSelectedCommand(selectCommand);
        table.draw();

        reset(view);
        reset(displayerListener);
        resetFilterLabelSet(filterLabelSet);
        table.selectCell(COLUMN_DEPARTMENT, 3);

        verify(selectCommand).execute();
        verify(view, never()).gotoFirstPage();
        verify(filterLabelSet).addLabel(anyString());
        verify(displayerListener, never()).onRedraw(table);
        assertEquals(table.getSelectedCellColumn(), COLUMN_DEPARTMENT);
        assertEquals(table.getSelectedCellRow(), new Integer(3));
    }

    @Test
    public void testSelectCellDrillDown() {

        DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .tablePageSize(10)
                .tableOrderDefault(COLUMN_ID, SortOrder.DESCENDING)
                .filterOn(true, true, true)
                .buildSettings();

        TableDisplayer table = createTableDisplayer(allRows);
        TableDisplayer.View view = table.getView();
        FilterLabelSet filterLabelSet = table.getFilterLabelSet();
        table.addListener(displayerListener);
        table.addOnCellSelectedCommand(selectCommand);
        table.draw();

        reset(view);
        reset(displayerListener);
        resetFilterLabelSet(filterLabelSet);
        table.selectCell(COLUMN_DEPARTMENT, 3);

        verify(view, atLeastOnce()).gotoFirstPage();
        verify(view).redrawTable();
        verify(filterLabelSet, atLeastOnce()).addLabel(anyString());
        verify(view, times(2)).setTotalRows(11, true);
        verify(displayerListener).onRedraw(table);
        verify(selectCommand).execute();
        assertEquals(table.getSelectedCellColumn(), COLUMN_DEPARTMENT);
        assertEquals(table.getSelectedCellRow(), new Integer(3));
    }

    @Test
    public void testSelectCellReset() {

        DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .tablePageSize(10)
                .tableOrderDefault(COLUMN_ID, SortOrder.DESCENDING)
                .filterOn(false, true, true)
                .buildSettings();

        TableDisplayer table = createTableDisplayer(allRows);
        TableDisplayer.View view = table.getView();
        FilterLabelSet filterLabelSet = table.getFilterLabelSet();
        table.addListener(displayerListener);
        table.addOnCellSelectedCommand(selectCommand);
        table.draw();
        resetFilterLabelSet(filterLabelSet);
        table.selectCell(COLUMN_DEPARTMENT, 3);

        reset(view);
        reset(selectCommand);
        reset(displayerListener);
        resetFilterLabelSet(filterLabelSet);
        table.selectCell(COLUMN_DEPARTMENT, 3);

        verify(selectCommand).execute();
        verify(view, never()).gotoFirstPage();
        verify(filterLabelSet, never()).addLabel(anyString());
        verify(displayerListener, never()).onRedraw(table);
        assertNull(table.getSelectedCellColumn());
        assertNull(table.getSelectedCellRow());
    }

    @Test
    public void testSelectCellCommands() {
        DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .tablePageSize(10)
                .tableOrderDefault(COLUMN_ID, SortOrder.DESCENDING)
                .filterOn(false, true, true)
                .buildSettings();

        TableDisplayer table = createTableDisplayer(allRows);
        TableDisplayer.View view = table.getView();
        FilterLabelSet filterLabelSet = table.getFilterLabelSet();
        table.addListener(displayerListener);
        table.addOnCellSelectedCommand(selectCommand);
        final Command selectedCommand = mock(Command.class);
        table.addOnCellSelectedCommand(selectedCommand);
        table.draw();
        resetFilterLabelSet(filterLabelSet);
        table.selectCell(COLUMN_DEPARTMENT, 3);

        verify(selectCommand).execute();
        verify(selectedCommand).execute();
    }

    @Test
    public void testFormatEmpty() {
        TableDisplayer table = createTableDisplayer(DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .buildSettings());

        table.addFormatter(COLUMN_EMPLOYEE, new ValueFormatter() {
            public String formatValue(DataSet dataSet, int row, int column) {
                return "test";
            }
            public String formatValue(Object value) {
                return "test";
            }
        });
        table.draw();
        String value = table.formatValue(100, 3);
        assertEquals(value, "test");
    }

    @Test
    public void test_DASHBUILDE_20_Fix() {
        DisplayerSettings groupedTable = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .group(COLUMN_CITY)
                .column(COLUMN_CITY, "City")
                .column(COUNT, "#Expenses").format("Number of expenses", "#,##0")
                .column(COLUMN_AMOUNT, MIN).format("Min", "$ #,###")
                .column(COLUMN_AMOUNT, MIN).format("Min", "$ #,###")
                .column(COLUMN_AMOUNT, MIN).format("Min", "$ #,###")
                .column(COLUMN_AMOUNT, MIN).format("Min", "$ #,###")
                .tablePageSize(10)
                .tableOrderEnabled(true)
                .tableOrderDefault(COLUMN_CITY, DESCENDING)
                .filterOn(false, true, true)
                .buildSettings();

        TableDisplayer table = createTableDisplayer(groupedTable);
        table.addListener(displayerListener);
        table.draw();
        table.sortBy("#Expenses", ASCENDING);
        verify(displayerListener, never()).onError(eq(table), any(ClientRuntimeError.class));
    }

    @Test
    public void testIsTotalRowsExactSQL() {
        final DataSet dataSet = mock(DataSet.class);
        final DataSetDef dataSetDef = new DataSetDef();
        dataSetDef.setProvider(DataSetProviderType.SQL);
        when(dataSet.getDefinition()).thenReturn(dataSetDef);

        TableDisplayer table = createTableDisplayer(DisplayerSettingsFactory.newTableSettings().tablePageSize(10).buildSettings());

        assertTrue(table.isTotalRowsExact(dataSet,
                                          5));
        assertTrue(table.isTotalRowsExact(dataSet,
                                          10));
        assertTrue(table.isTotalRowsExact(dataSet,
                                          15));
    }

    @Test
    public void testIsTotalRowsExactRemote() {
        final DataSet dataSet = mock(DataSet.class);
        final DataSetDef dataSetDef = new DataSetDef();
        dataSetDef.setProvider(() -> "REMOTE");
        when(dataSet.getDefinition()).thenReturn(dataSetDef);

        TableDisplayer table = createTableDisplayer(DisplayerSettingsFactory.newTableSettings().tablePageSize(10).buildSettings());

        assertTrue(table.isTotalRowsExact(dataSet,
                                          5));
        assertFalse(table.isTotalRowsExact(dataSet,
                                           10));
        assertFalse(table.isTotalRowsExact(dataSet,
                                           15));
    }

    @Test
    public void testIsPagerEnabledRemote() {
        final DataSet dataSet = mock(DataSet.class);
        final DataSetDef dataSetDef = new DataSetDef();
        dataSetDef.setProvider(() -> "REMOTE");
        when(dataSet.getDefinition()).thenReturn(dataSetDef);

        TableDisplayer table = createTableDisplayer(DisplayerSettingsFactory.newTableSettings().tablePageSize(10).buildSettings());

        assertFalse(table.isPagerEnabled(dataSet,
                                         5));
        assertTrue(table.isPagerEnabled(dataSet,
                                        10));
        verify(dataSet,
               never()).getRowCountNonTrimmed();
    }

    @Test
    public void testIsPagerEnabledSQL() {
        final DataSet dataSet = mock(DataSet.class);
        final DataSetDef dataSetDef = new DataSetDef();
        dataSetDef.setProvider(DataSetProviderType.SQL);
        when(dataSet.getDefinition()).thenReturn(dataSetDef);
        when(dataSet.getRowCountNonTrimmed()).thenReturn(5,
                                                         10,
                                                         15);

        TableDisplayer table = createTableDisplayer(DisplayerSettingsFactory.newTableSettings().tablePageSize(10).buildSettings());

        assertFalse(table.isPagerEnabled(dataSet,
                                         5));
        assertFalse(table.isPagerEnabled(dataSet,
                                         10));
        assertTrue(table.isPagerEnabled(dataSet,
                                        15));
    }

    @Test
    public void testIsRemoteProvider() {
        final DataSet dataSet = mock(DataSet.class);
        final DataSetDef dataSetDefSQL = new DataSetDef();
        dataSetDefSQL.setProvider(DataSetProviderType.SQL);
        final DataSetDef dataSetDefRemote = new DataSetDef();
        dataSetDefRemote.setProvider(() -> "REMOTE");
        when(dataSet.getDefinition()).thenReturn(new DataSetDef(),
                                                 dataSetDefSQL,
                                                 dataSetDefRemote);

        TableDisplayer table = createTableDisplayer(null);

        //No provider set
        assertFalse(table.isRemoteProvider(dataSet));
        //Provider SQL
        assertFalse(table.isRemoteProvider(dataSet));
        //Provider REMOTE
        assertTrue(table.isRemoteProvider(dataSet));
    }

    @Test
    public void testPageSize() {
        TableDisplayer table = createTableDisplayer(DisplayerSettingsFactory.newTableSettings().tablePageSize(10).buildSettings());

        assertEquals(10,
                     table.getPageSize());

        when(table.getView().getPageSize()).thenReturn(20);

        assertEquals(20,
                     table.getPageSize());
    }
}