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

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.ExpenseReportsData.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GoogleDisplayerDataTest extends GoogleDisplayerTest {

    DisplayerSettings byYear = DisplayerSettingsFactory.newBarChartSettings()
            .dataset(EXPENSES)
            .group(COLUMN_DATE)
            .column(COLUMN_DATE)
            .column(COLUMN_AMOUNT, SUM)
            .filterOn(false, true, true)
            .sort(COLUMN_DATE, SortOrder.ASCENDING)
            .buildSettings();

    DisplayerSettings noData = DisplayerSettingsFactory.newBarChartSettings()
            .dataset(EXPENSES)
            .filter(COLUMN_ID, FilterFactory.isNull())
            .group(COLUMN_DATE)
            .column(COLUMN_DATE)
            .column(COLUMN_AMOUNT, SUM)
            .buildSettings();

    @Test
    public void testDataPush() {

        // Google renderer draw calls are processed asynchronously
        // A ready() call needs to be executed in order to ignite the real chart display
        GoogleBarChartDisplayer barChart = createBarChartDisplayer(byYear);
        barChart.ready();

        GoogleBarChartDisplayer.View view = barChart.getView();
        verify(view).dataClear();
        verify(view).dataAddColumn(ColumnType.LABEL, COLUMN_DATE, COLUMN_DATE);
        verify(view).dataAddColumn(eq(ColumnType.NUMBER), anyString(), anyString());
        verify(view).dataRowCount(4);
        verify(view, times(4)).dataSetValue(anyInt(), anyInt(), anyString());
        verify(view, times(4)).dataSetValue(anyInt(), anyInt(), any(Double.class));
        verify(view).dataFormatNumberColumn(anyString(), eq(1));
    }

    @Test
    public void testNoData() {

        // Google renderer draw calls are processed asynchronously
        // A ready() call needs to be executed in order to ignite the real chart display
        GoogleBarChartDisplayer barChart = createBarChartDisplayer(noData);
        barChart.ready();

        GoogleBarChartDisplayer.View view = barChart.getView();
        verify(view).nodata();
        verify(view, never()).dataClear();
        verify(view, never()).dataAddColumn(any(ColumnType.class), anyString(), anyString());
        verify(view, never()).dataRowCount(anyInt());
        verify(view, never()).dataSetValue(anyInt(), anyInt(), anyString());
    }
}