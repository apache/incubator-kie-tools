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
package org.dashbuilder.displayer.client;

import java.util.Date;
import java.util.List;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.ExpenseReportsData.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DisplayerCoordinatorTest extends AbstractDisplayerTest {

    DisplayerSettings byDepartment = DisplayerSettingsFactory.newPieChartSettings()
            .dataset(EXPENSES)
            .group(COLUMN_DEPARTMENT)
            .column(COLUMN_DEPARTMENT)
            .column(COLUMN_AMOUNT, SUM)
            .sort(COLUMN_DEPARTMENT, SortOrder.ASCENDING)
            .filterOn(false, true, true)
            .buildSettings();

    DisplayerSettings byDepartmentSelector = DisplayerSettingsFactory.newSelectorSettings()
            .dataset(EXPENSES)
            .group(COLUMN_DEPARTMENT)
            .column(COLUMN_DEPARTMENT)
            .column(COLUMN_AMOUNT, SUM)
            .sort(COLUMN_DEPARTMENT, SortOrder.ASCENDING)
            .filterOn(false, true, false)
            .buildSettings();

    DisplayerSettings byYear = DisplayerSettingsFactory.newBarChartSettings()
            .dataset(EXPENSES)
            .group(COLUMN_DATE).dynamic(DateIntervalType.YEAR, true)
            .column(COLUMN_DATE)
            .column(COLUMN_AMOUNT, SUM)
            .filterOn(false, true, true)
            .sort(COLUMN_DATE, SortOrder.ASCENDING)
            .buildSettings();

    DisplayerSettings byQuarter = DisplayerSettingsFactory.newBarChartSettings()
            .dataset(EXPENSES)
            .filter(COLUMN_ID, FilterFactory.equalsTo(1))
            .group(COLUMN_DATE).fixed(DateIntervalType.QUARTER, false)
            .column(COLUMN_DATE)
            .column(COLUMN_AMOUNT, SUM)
            .filterOn(false, true, true)
            .sort(COLUMN_DATE, SortOrder.ASCENDING)
            .buildSettings();

    DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
            .dataset(EXPENSES)
            .column(COLUMN_DEPARTMENT)
            .column(COLUMN_CITY)
            .column(COLUMN_EMPLOYEE)
            .column(COLUMN_AMOUNT)
            .column(COLUMN_DATE)
            .filterOn(true, false, true)
            .buildSettings();

    DisplayerCoordinator displayerCoordinator;
    AbstractDisplayer allRowsTable;
    AbstractDisplayer deptPieChart;
    AbstractDisplayer deptSelector;
    AbstractDisplayer yearBarChart;
    AbstractDisplayer quarterPieChart;

    @Mock
    DisplayerListener listener;

    @Before
    public void init() throws Exception {
        super.init();

        allRowsTable = createNewDisplayer(allRows);
        deptPieChart = createNewDisplayer(byDepartment);
        deptSelector = createNewDisplayer(byDepartmentSelector);
        yearBarChart = createNewDisplayer(byYear);
        quarterPieChart = createNewDisplayer(byQuarter);

        displayerCoordinator = new DisplayerCoordinator(rendererManager);
        displayerCoordinator.addDisplayers(allRowsTable, deptPieChart, deptSelector, yearBarChart, quarterPieChart);
        displayerCoordinator.addListener(listener);
    }

    @Test
    public void testDrawAll() {
        displayerCoordinator.drawAll();

        verify(listener, times(5)).onDataLookup(any(Displayer.class));
        verify(listener, times(5)).onDraw(any(Displayer.class));
    }

    @Test
    public void testFilterPropagations() {
        displayerCoordinator.drawAll();

        // Click on the "Engineering" slice
        reset(listener);
        deptPieChart.filterUpdate(COLUMN_DEPARTMENT, 0);

        // Check the allRowsTable receives the filter request
        DataSet dataSet = allRowsTable.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 19);
        verify(listener).onDataLookup(allRowsTable);
        verify(listener).onRedraw(allRowsTable);
    }

    @Test
    public void testFilterReset() {
        displayerCoordinator.drawAll();

        // Click on a slice
        deptPieChart.filterUpdate(COLUMN_DEPARTMENT, 0);
        List<Interval> deptIntervalList = deptPieChart.filterIntervals(COLUMN_DEPARTMENT);
        assertEquals(deptIntervalList.size(), 1);
        Interval deptInterval = deptIntervalList.get(0);

        // Click on a selector entry different from the slice selected above
        deptSelector.filterUpdate(COLUMN_DEPARTMENT, 1);

        // Check the pie chart receives the selector filter request
        DataSet dataSet = deptPieChart.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 1);
        assertEquals(deptPieChart.filterIndexes(COLUMN_DEPARTMENT).size(), 1);

        // Reset the pie chart filter
        deptPieChart.filterUpdate(COLUMN_DEPARTMENT, deptInterval.getIndex());
        deptIntervalList = deptPieChart.filterIntervals(COLUMN_DEPARTMENT);
        assertEquals(deptIntervalList.size(), 0);
    }

    @Test
    public void testQuarterFilter() {
        displayerCoordinator.drawAll();

        // Click on the "Q4" slice
        reset(listener);
        quarterPieChart.filterUpdate(COLUMN_DATE, 0);

        // Check the allRowsTable receives the filter request
        DataSet dataSet = allRowsTable.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 10);
        for (int i = 0; i < dataSet.getRowCount(); i++) {
            Date d = (Date) dataSet.getValueAt(i, COLUMN_DATE);
            assertTrue(d.getMonth() > 8);
        }
        verify(listener).onDataLookup(allRowsTable);
        verify(listener).onRedraw(allRowsTable);
    }

    @Test
    public void testYearFilter() {
        displayerCoordinator.drawAll();

        // Click on the "2014" slice
        reset(listener);
        yearBarChart.filterUpdate(COLUMN_DATE, 2);

        // Check the allRowsTable receives the filter request
        DataSet dataSet = allRowsTable.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 11);
        for (int i = 0; i < dataSet.getRowCount(); i++) {
            Date d = (Date) dataSet.getValueAt(i, COLUMN_DATE);
            assertEquals(d.getYear(), 114);
        }
        verify(listener).onDataLookup(allRowsTable);
        verify(listener).onRedraw(allRowsTable);
    }

    @Test
    public void testMultipleFilter() {
        displayerCoordinator.drawAll();

        // Click on the "2014" slice
        yearBarChart.filterUpdate(COLUMN_DATE, 2);

        // Click on the "Sales" slice
        deptPieChart.filterUpdate(COLUMN_DEPARTMENT, 1);

        // Check the allRowsTable receives all the filter requests
        DataSet dataSet = allRowsTable.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 2);
    }

    @Test
    public void testFilterUpdates() {
        displayerCoordinator.drawAll();

        // Filter by amount
        DataSetFilter filterOp = new DataSetFilter();
        CoreFunctionFilter columnFilter = new CoreFunctionFilter(COLUMN_AMOUNT, CoreFunctionType.BETWEEN, 1d, 1.2d);
        filterOp.addFilterColumn(columnFilter);
        yearBarChart.filterUpdate(filterOp);
        DataSet dataSet = allRowsTable.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 1);

        // Filter by a different range
        filterOp = new DataSetFilter();
        columnFilter = new CoreFunctionFilter(COLUMN_AMOUNT, CoreFunctionType.BETWEEN, 1000d, 2000d);
        filterOp.addFilterColumn(columnFilter);
        DisplayerListener listener = mock(DisplayerListener.class);
        allRowsTable.addListener(listener);
        yearBarChart.filterUpdate(filterOp);
        verify(listener).onRedraw(allRowsTable);
        dataSet = allRowsTable.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 2);
    }

    @Test
    public void testFilterWithNull() {
        // Insert a null entry into the dataset
        DataSet expensesDataSet = clientDataSetManager.getDataSet(EXPENSES);
        int column = expensesDataSet.getColumnIndex(expensesDataSet.getColumnById(COLUMN_DEPARTMENT));
        expensesDataSet.setValueAt(0, column, null);

        // Draw the charts
        displayerCoordinator.drawAll();

        // Click on the "Engineering" slice
        reset(listener);
        deptPieChart.filterUpdate(COLUMN_DEPARTMENT, 1);

        // Check the allRowsTable receives the filter request
        DataSet dataSet = allRowsTable.getDataSetHandler().getLastDataSet();
        verify(listener, never()).onError(any(Displayer.class), any(ClientRuntimeError.class));
        verify(listener).onDataLookup(allRowsTable);
        verify(listener).onRedraw(allRowsTable);
        assertEquals(dataSet.getRowCount(), 18);
    }

    /**
     * Avoid IndexOutOfBoundsException caused when a filter is notified to
     * a table consuming the whole data set (no data lookup columns set).
     */
    @Test
    public void testFullTableFilterEvent() {

        AbstractDisplayer tableNoColumns = createNewDisplayer(
                DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .filterOn(true, false, true)
                .buildSettings());

        displayerCoordinator = new DisplayerCoordinator(rendererManager);
        displayerCoordinator.addDisplayers(deptPieChart, tableNoColumns);
        displayerCoordinator.addListener(listener);
        displayerCoordinator.drawAll();

        // Click on the "Engineering" slice
        reset(listener);
        deptPieChart.filterUpdate(COLUMN_DEPARTMENT, 0);

        // Check the allRowsTable receives the filter request
        DataSet dataSet = allRowsTable.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 19);
        verify(listener).onDataLookup(allRowsTable);
        verify(listener).onRedraw(tableNoColumns);
   }
}