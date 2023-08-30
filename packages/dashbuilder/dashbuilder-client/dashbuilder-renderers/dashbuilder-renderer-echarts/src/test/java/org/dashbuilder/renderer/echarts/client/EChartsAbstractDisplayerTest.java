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

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.renderer.echarts.client.js.ECharts;
import org.dashbuilder.renderer.echarts.client.js.EChartsTypeFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class EChartsAbstractDisplayerTest {

    @Mock
    EChartsTypeFactory echartsTypeFactory;

    @Mock
    @SuppressWarnings("rawtypes")
    EChartsAbstractDisplayer.View view;

    @Mock
    ECharts.Dataset echartsDataSet;

    @Mock
    DisplayerSettings displayerSettings;

    @Mock
    ColumnSettings nameColumnSettings;

    @Mock
    ColumnSettings ageColumnSettings;

    MockEChartsDisplayer displayer;

    DataSet dataSet = DataSetFactory.newDataSetBuilder()
            .label("name")
            .number("age")
            .row("John", 32)
            .row("Mark", 41)
            .row("Mary", 28)
            .buildDataSet();

    class MockEChartsDisplayer extends EChartsAbstractDisplayer<EChartsAbstractDisplayer.View> {

        public MockEChartsDisplayer(View view, EChartsTypeFactory echartsFactory) {
            super(view, echartsFactory);
        }

        @Override
        DataSetLookupConstraints getDataSetLookupConstraints() {
            return new DataSetLookupConstraints()
                    .setMaxColumns(2)
                    .setMinColumns(2)
                    .setExtraColumnsAllowed(false)
                    .setColumnTypes(new ColumnType[]{
                                                     ColumnType.LABEL,
                                                     ColumnType.NUMBER});
        }

        @Override
        void chartSetup() {
            // empty

        }

        void setDataSet(DataSet dataSet) {
            this.dataSet = dataSet;
        }

    }

    @Before
    public void prepare() {
        var nameDataColumn = dataSet.getColumnByIndex(0);
        var ageDataColumn = dataSet.getColumnByIndex(1);
        when(displayerSettings.getColumnSettings(nameDataColumn)).thenReturn(nameColumnSettings);
        when(displayerSettings.getColumnSettings(ageDataColumn)).thenReturn(ageColumnSettings);

        when(nameColumnSettings.getColumnName()).thenReturn("Name");
        when(ageColumnSettings.getColumnName()).thenReturn("Age");
        when(echartsTypeFactory.newDataset()).thenReturn(echartsDataSet);
        displayer = new MockEChartsDisplayer(view, echartsTypeFactory);
        displayer.setDisplayerSettings(displayerSettings);
        displayer.setDataSet(dataSet);

    }

    @Test
    public void testBuildDataSet() {
        displayer.buildDataSet();
        verify(echartsDataSet).setDimensions(eq(new String[]{"Name", "Age"}));
        verify(echartsDataSet).setSource(eq(new Object[][]{
                                                           {"John", "32.0"},
                                                           {"Mark", "41.0"},
                                                           {"Mary", "28.0"}
        }));
    }
    
    @Test
    public void testNoData() {
        displayer.setDataSet(DataSetFactory.newEmptyDataSet());
        displayer.updateVisualization();
        verify(view).noData();
    }

}
