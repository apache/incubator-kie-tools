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

package org.dashbuilder.displayer.client;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_AMOUNT;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_DEPARTMENT;
import static org.dashbuilder.dataset.group.AggregateFunctionType.SUM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DisplayerFilterTest extends AbstractDisplayerTest {

    DisplayerSettings byDepartment = DisplayerSettingsFactory.newPieChartSettings()
            .dataset(EXPENSES)
            .group(COLUMN_DEPARTMENT)
            .column(COLUMN_DEPARTMENT)
            .column(COLUMN_AMOUNT, SUM)
            .filterOn(false, true, true)
            .buildSettings();

    DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
            .dataset(EXPENSES)
            .filterOn(true, false, true)
            .sort(COLUMN_DEPARTMENT, SortOrder.ASCENDING)
            .buildSettings();

    @Test
    public void testDrawRequired() {
        AbstractDisplayer allRowsTable = (AbstractDisplayer) displayerLocator.lookupDisplayer(allRows);
        allRowsTable.filterUpdate(COLUMN_DEPARTMENT, 0);
        DataSet dataSet = allRowsTable.getDataSetHandler().getLastDataSet();
        assertNull(dataSet);
    }

    @Test
    public void testNotifications() {
        AbstractDisplayer deptPieChart = (AbstractDisplayer) displayerLocator.lookupDisplayer(byDepartment);
        DisplayerListener listener = mock(DisplayerListener.class);
        deptPieChart.addListener(listener);
        deptPieChart.draw();

        deptPieChart.filterUpdate(COLUMN_DEPARTMENT, 0);
        verify(listener).onFilterEnabled(eq(deptPieChart), any(DataSetGroup.class));
        verify(listener, never()).onRedraw(deptPieChart);

        deptPieChart.filterReset();
        verify(listener).onFilterReset(eq(deptPieChart), anyList());
        verify(listener, never()).onRedraw(deptPieChart);
    }

    @Test
    public void testDrillDown() {
        AbstractDisplayer allRowsTable = (AbstractDisplayer) displayerLocator.lookupDisplayer(allRows);
        DisplayerListener listener = mock(DisplayerListener.class);
        allRowsTable.addListener(listener);
        allRowsTable.draw();

        // Filter by "Engineering"
        reset(listener);
        allRowsTable.filterUpdate(COLUMN_DEPARTMENT, 0);
        DataSet dataSet = allRowsTable.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 19);

        verify(listener).onDataLookup(allRowsTable);
        verify(listener).onRedraw(allRowsTable);
    }

    @Test
    public void testFilterNotAllowed() {
        AbstractDisplayer allRowsTable = (AbstractDisplayer) displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .filterOff(true)
                .buildSettings());

        DisplayerListener listener = mock(DisplayerListener.class);
        allRowsTable.addListener(listener);
        allRowsTable.draw();

        reset(listener);
        allRowsTable.filterUpdate(COLUMN_DEPARTMENT, 0);
        DataSet dataSet = allRowsTable.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 50);
        verify(listener, never()).onDataLookup(allRowsTable);
        verify(listener, never()).onRedraw(allRowsTable);
    }
}