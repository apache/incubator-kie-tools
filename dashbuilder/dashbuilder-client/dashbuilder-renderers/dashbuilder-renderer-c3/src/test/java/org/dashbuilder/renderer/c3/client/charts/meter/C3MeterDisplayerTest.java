/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.renderer.c3.client.charts.meter;

import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_AMOUNT;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_DATE;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_ID;
import static org.dashbuilder.dataset.group.AggregateFunctionType.SUM;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.RawDataSet;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.renderer.c3.client.C3BaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class C3MeterDisplayerTest extends C3BaseTest {
    
    private static final String CL1 = "CL1";
    private static final String CL2 = "CL2";
    
    RawDataSet twoColumnsRawDS = new RawDataSet(
                new String [] {CL1,CL2},
                new Class[] {String.class, String.class}, 
                new String[][]{
                    {"VAL1", "1"},
                    {"VAL2", "2"}
                });
    
    RawDataSet oneColumnsRawDS = new RawDataSet(
                new String [] {CL1},
                new Class[] {String.class}, 
                new String[][]{
                    {"1"},
                    {"2"}
                });
    
    DisplayerSettings meterSettings = DisplayerSettingsFactory.newMeterChartSettings()
            .dataset(EXPENSES)
            .filter(COLUMN_ID, FilterFactory.isNull())
            .group(COLUMN_DATE)
            .column(COLUMN_DATE)
            .column(COLUMN_AMOUNT, SUM).format("","0.0000")
            .buildSettings();
    
    DisplayerSettings meterSettingsOneColumn = DisplayerSettingsFactory.newMeterChartSettings()
            .dataset(EXPENSES)
            .filter(COLUMN_ID, FilterFactory.isNull())
            .column(COLUMN_AMOUNT).format("","0.0")
            .buildSettings();

    @Test
    public void c3meterColumnsExtractorsTest() throws ParseException {
        C3MeterChartDisplayer c3MeterChartDisplayer = c3MeterChartDisplayer(meterSettings);
        c3MeterChartDisplayer.draw();
        
        DataSet twoColumnDS = twoColumnsRawDS.toDataSet();
        DataSet oneColumnDS = oneColumnsRawDS.toDataSet();
        
        DataColumn cl1 = twoColumnDS.getColumnById(CL1);
        DataColumn cl2 = twoColumnDS.getColumnById(CL2);
        
        String[][] valuesWhenUsingGrouping = c3MeterChartDisplayer.extractGroupingValues(cl1, cl2);
        
        assertEquals(2, valuesWhenUsingGrouping.length);
        
        assertArrayEquals(new String[] {"VAL1", "1"}, valuesWhenUsingGrouping[0]);
        assertArrayEquals(new String[] {"VAL2", "2"}, valuesWhenUsingGrouping[1]);
        
        cl1 = oneColumnDS.getColumnById(CL1);
        
        String[] singleColumnValues = c3MeterChartDisplayer.extractSingleColumnValues(cl1);
        
        assertEquals(3, singleColumnValues.length);
        
        assertArrayEquals(new String[] {CL1, "1", "2"}, singleColumnValues);
        
    }
    
    @Test
    public void columnFormatTest() throws ParseException {
        C3MeterChartDisplayer c3MeterChartDisplayer = c3MeterChartDisplayer(meterSettings);
        c3MeterChartDisplayer.draw();
        
        Object formattedTooltip = c3MeterChartDisplayer.formatTooltip(1l, null, null);
        assertEquals("1.0000", formattedTooltip);
        
        c3MeterChartDisplayer = c3MeterChartDisplayer(meterSettingsOneColumn);
        c3MeterChartDisplayer.draw();
        
        formattedTooltip = c3MeterChartDisplayer.formatTooltip(1l, null, null);
        assertEquals("1.0", formattedTooltip);
    }
    
    
}