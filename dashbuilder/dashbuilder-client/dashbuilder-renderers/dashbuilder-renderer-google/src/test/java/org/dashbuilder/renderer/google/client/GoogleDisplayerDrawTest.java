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
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.Position;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.ExpenseReportsData.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GoogleDisplayerDrawTest extends GoogleDisplayerTest {

    @Test
    public void testGeneralSettings() {

        DisplayerSettings byYear = DisplayerSettingsFactory.newBarChartSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DATE)
                .column(COLUMN_DATE)
                .column(COLUMN_AMOUNT, SUM)
                .subType_Bar()
                .title("Title").titleVisible(true)
                .width(500).height(300)
                .margins(1, 2, 3, 4)
                .backgroundColor("white")
                .legendOn(Position.LEFT)
                .refreshOn(5, false)
                .filterOn(false, true, true)
                .xAxisTitle("Date")
                .yAxisTitle("Total")
                .buildSettings();

        // Google renderer draw calls are processed asynchronously
        // A ready() call needs to be executed in order to ignite the real chart display
        GoogleBarChartDisplayer barChart = createBarChartDisplayer(byYear);
        GoogleBarChartDisplayer.View barChartView = barChart.getView();
        barChart.ready();

        verify(barChartView).showTitle("Title");
        verify(barChartView).setFilterEnabled(true);
        verify(barChartView).setSubType(DisplayerSubType.BAR);
        verify(barChartView).setWidth(500);
        verify(barChartView).setHeight(300);
        verify(barChartView).setBgColor("white");
        verify(barChartView).setMarginTop(1);
        verify(barChartView).setMarginBottom(2);
        verify(barChartView).setMarginLeft(3);
        verify(barChartView).setMarginRight(4);
        verify(barChartView).setLegendPosition(Position.LEFT);
        verify(barChartView).enableRefreshTimer(5);
        verify(barChartView).setShowXLabels(true);
        verify(barChartView).setShowYLabels(true);
        verify(barChartView).setXAxisTitle("Date");
        verify(barChartView).setYAxisTitle("Total");
        verify(barChartView).setIsStacked(false);
        verify(barChartView).setIsBar(true);
        verify(barChartView).drawChart();
    }

    @Test
    public void testBarChartDraw() {

        DisplayerSettings byYear = DisplayerSettingsFactory.newBarChartSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DATE)
                .column(COLUMN_DATE)
                .column(COLUMN_AMOUNT, SUM)
                .subType_Bar()
                .buildSettings();

        // Bar
        GoogleBarChartDisplayer barChart = createBarChartDisplayer(byYear);
        GoogleBarChartDisplayer.View barChartView = barChart.getView();
        barChart.ready();
        verify(barChartView).setIsStacked(false);
        verify(barChartView).setIsBar(true);

        // Bar stacked
        byYear.setSubtype(DisplayerSubType.BAR_STACKED);
        barChart = createBarChartDisplayer(byYear);
        barChart.ready();
        barChartView = barChart.getView();
        verify(barChartView).setIsStacked(true);
        verify(barChartView).setIsBar(true);

        // Column
        byYear.setSubtype(DisplayerSubType.COLUMN);
        barChart = createBarChartDisplayer(byYear);
        barChart.ready();
        barChartView = barChart.getView();
        verify(barChartView).setIsStacked(false);
        verify(barChartView).setIsBar(false);

        // Bar stacked
        byYear.setSubtype(DisplayerSubType.COLUMN_STACKED);
        barChart = createBarChartDisplayer(byYear);
        barChart.ready();
        barChartView = barChart.getView();
        verify(barChartView).setIsStacked(true);
        verify(barChartView).setIsBar(false);
    }
}