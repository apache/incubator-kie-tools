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
package org.dashbuilder.renderer.c3.client.charts.area;

import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_AMOUNT;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_DATE;
import static org.dashbuilder.dataset.group.AggregateFunctionType.SUM;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.renderer.c3.client.C3BaseTest;
import org.dashbuilder.renderer.c3.client.charts.area.C3AreaChartDisplayer.View;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class C3AreaChartDisplayerTest extends C3BaseTest {
    
    private static final boolean RECEIVE_NOTIFICATION = true;
    private static final String LEGEND_POSITION = "right";
    private static final int SIZE = 300;
    private static final String TITLE = "Title";
    private static final String BLACK = "black";
    
    DisplayerSettings simpleSettings = DisplayerSettingsFactory.newBarChartSettings()
            .dataset(EXPENSES)
            .group(COLUMN_DATE)
            .column(COLUMN_DATE)
            .column(COLUMN_AMOUNT, SUM)
            .width(SIZE)
            .height(SIZE)
            .title(TITLE)
            .backgroundColor(BLACK)
            .titleVisible(true)
            .legendOn(LEGEND_POSITION)
            .filterOn(RECEIVE_NOTIFICATION, 
                      RECEIVE_NOTIFICATION, 
                      RECEIVE_NOTIFICATION)
            .buildSettings();
    
    private C3AreaChartDisplayer displayer;
    
    @Before 
    public void conf() {
        displayer = c3AreaChartDisplayer(simpleSettings);
        displayer.draw();
    }
    
    @Test
    public void fixAreaCallbackRegisteredTest() {
        verify(c3Conf).setOnrendered(displayer.fixAreaOpacityCallback);
    }
    
}