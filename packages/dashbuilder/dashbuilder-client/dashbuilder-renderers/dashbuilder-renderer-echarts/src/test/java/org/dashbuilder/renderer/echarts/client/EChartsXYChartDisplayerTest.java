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

package org.dashbuilder.renderer.echarts.client;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Encode;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Series;
import org.dashbuilder.renderer.echarts.client.js.EChartsTypeFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EChartsXYChartDisplayerTest {

    @Mock
    Series series;

    @Mock
    Encode encode;

    @Mock
    DisplayerSettings displayerSettings;

    @Mock
    ColumnSettings c0Settings;

    @Mock
    ColumnSettings c1Settings;

    @Mock
    EChartsTypeFactory echartsFactory;

    @InjectMocks
    MockEChartsXYChartDisplayer eChartsXYChartDisplayer;

    DataSet dataSet = DataSetFactory.newDataSetBuilder()
            .label("name")
            .number("age")
            .row("John", 32)
            .row("Mark", 41)
            .row("Mary", 28)
            .buildDataSet();

    private DataColumn c0;

    private DataColumn c1;

    @Before
    public void setup() {
        c0 = dataSet.getColumnByIndex(0);
        c1 = dataSet.getColumnByIndex(1);
        eChartsXYChartDisplayer.setDataSet(dataSet);
        eChartsXYChartDisplayer.setDisplayerSettings(displayerSettings);

        when(echartsFactory.newEncode()).thenReturn(encode);
        when(echartsFactory.newSeries()).thenReturn(series);

        when(displayerSettings.getColumnSettings(eq(c0))).thenReturn(c0Settings);
        when(displayerSettings.getColumnSettings(eq(c1))).thenReturn(c1Settings);

        when(c1Settings.getColumnName()).thenReturn(c1.getId());
        when(c0Settings.getColumnName()).thenReturn(c0.getId());
    }

    @Test
    public void testBuildSeries() {
        var finalSeries = eChartsXYChartDisplayer.buildSeries();
        verify(encode).setX(c0.getId());
        verify(encode).setY(c1.getId());
        verify(series).setName(c1.getId());
        verify(series).setEncode(encode);
        assertEquals(1, finalSeries.length);
    }

    @Test
    public void testBuildSeriesEmptyDataSet() {
        eChartsXYChartDisplayer.setDataSet(DataSetFactory.newEmptyDataSet());
        var series = eChartsXYChartDisplayer.buildSeries();
        assertEquals(0, series.length);
    }

    static class MockEChartsXYChartDisplayer extends EChartsXYChartDisplayer {

        public MockEChartsXYChartDisplayer(EChartsDisplayerView<?> view, EChartsTypeFactory echartsFactory) {
            super(view, echartsFactory);
        }

        void setDataSet(DataSet dataSet) {
            this.dataSet = dataSet;
        }

    }

}
