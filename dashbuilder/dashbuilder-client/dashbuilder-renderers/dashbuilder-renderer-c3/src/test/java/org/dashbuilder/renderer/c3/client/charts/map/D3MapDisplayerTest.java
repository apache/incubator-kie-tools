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
package org.dashbuilder.renderer.c3.client.charts.map;

import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_AMOUNT;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_DATE;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_ID;
import static org.dashbuilder.dataset.group.AggregateFunctionType.SUM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.util.Map;

import org.dashbuilder.dataset.RawDataSet;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.renderer.c3.client.C3BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class D3MapDisplayerTest extends C3BaseTest {
    
    private static final String COUNTRY_1= "Country1";
    private static final String COUNTRY_2= "Country2";
    
    private static final Integer VAL_1= 1;
    private static final Integer VAL_2= 2;
    private static final String CL_TITLE = "Title in Column";
    private static final String BG_COLOR = "red";
    
    RawDataSet twoColumnsRawDS = new RawDataSet(
                new String [] {"cl", "cl2"},
                new Class[] {String.class, String.class}, 
                new String[][]{
                    {COUNTRY_1, VAL_1.toString()},
                    {COUNTRY_2, VAL_2.toString()}
                });
    
    private D3MapDisplayer displayer;
    
    
    @Before
    public void conf() {
        DisplayerSettings noData = DisplayerSettingsFactory.newLineChartSettings()
                .dataset(EXPENSES)
                .filter(COLUMN_ID, FilterFactory.isNull())
                .group(COLUMN_DATE)
                .column(COLUMN_DATE)
                .column(COLUMN_AMOUNT, SUM, CL_TITLE)
                .backgroundColor(BG_COLOR)
                .buildSettings();
        displayer = d3MapDisplayer(noData);
        displayer.draw();
    }
    
    @Test
    public void dataRetrievalTest() throws ParseException {
        Map<String, Double> data = displayer.retrieveData(twoColumnsRawDS.toDataSet());
        Double val1 = data.get(COUNTRY_1);
        assertNotNull(val1);
        assertEquals(VAL_1.intValue(), val1.intValue());
    }
    
    @Test
    public void mapConfTest() throws ParseException {
        displayer.updateVisualizationWithData();
        D3MapConf conf = displayer.getConf();
        assertEquals(CL_TITLE, conf.getTitle());
        assertEquals(BG_COLOR, conf.getBackgroundColor());
        Mockito.verify(displayer.getView()).createMap(conf);
    }

}