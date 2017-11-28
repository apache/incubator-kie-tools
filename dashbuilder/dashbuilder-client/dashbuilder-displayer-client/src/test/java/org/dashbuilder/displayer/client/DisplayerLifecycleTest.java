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

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.group.AggregateFunctionType.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.dashbuilder.dataset.ExpenseReportsData.*;
import static org.dashbuilder.dataset.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class DisplayerLifecycleTest extends AbstractDisplayerTest {

    DisplayerSettings settings = DisplayerSettingsFactory.newPieChartSettings()
            .uuid("expenses")
            .dataset(EXPENSES)
            .group(COLUMN_DATE)
            .column(COLUMN_DATE)
            .column(COLUMN_AMOUNT, COUNT)
            .column(COLUMN_AMOUNT, SUM)
            .buildSettings();

    @Mock
    DisplayerListener listener;

    @Test
    public void testLookup() {
        Displayer displayer = displayerLocator.lookupDisplayer(settings);
        assertNotNull(displayer);
        assertNotNull(displayer.getDataSetHandler());
        assertNotNull(displayer.getDisplayerSettings());
        assertEquals(displayer.getDisplayerSettings(), settings);
    }

    @Test
    public void testDraw() {
        AbstractDisplayer displayer = (AbstractDisplayer) displayerLocator.lookupDisplayer(settings);
        AbstractDisplayer.View view = displayer.getView();
        displayer.addListener(listener);

        assertEquals(displayer.isDrawn(), false);
        displayer.draw();
        assertEquals(displayer.isDrawn(), true);

        verify(view).showLoading();
        verify(view).setId("expenses");

        verify(listener).onDataLookup(displayer);
        verify(listener).onDataLoaded(displayer);
        verify(listener).onDraw(displayer);
        verify(listener, never()).onRedraw(displayer);

        assertDataSetValues(displayer.getDataSetHandler().getLastDataSet(), new String[][]{
                {"2012", "13.00", "6,126.13"},
                {"2013", "11.00", "5,252.96"},
                {"2014", "11.00", "4,015.48"},
                {"2015", "15.00", "7,336.69"}
        }, 0);
    }

    @Test
    public void testRedraw() {
        AbstractDisplayer displayer = (AbstractDisplayer) displayerLocator.lookupDisplayer(settings);
        AbstractDisplayer.View view = displayer.getView();
        displayer.addListener(listener);
        displayer.draw();

        reset(view);
        reset(listener);
        assertEquals(displayer.isDrawn(), true);
        displayer.redraw();
        assertEquals(displayer.isDrawn(), true);

        verify(listener).onDataLookup(displayer);
        verify(listener).onRedraw(displayer);
        verify(listener, never()).onDraw(displayer);
        verify(view, never()).showLoading();

        assertDataSetValues(displayer.getDataSetHandler().getLastDataSet(), new String[][]{
                {"2012", "13.00", "6,126.13"},
                {"2013", "11.00", "5,252.96"},
                {"2014", "11.00", "4,015.48"},
                {"2015", "15.00", "7,336.69"}
        }, 0);
    }

    @Test
    public void testClose() {
        AbstractDisplayer displayer = (AbstractDisplayer) displayerLocator.lookupDisplayer(settings);
        AbstractDisplayer.View view = displayer.getView();
        displayer.addListener(listener);
        displayer.close();

        verify(view).clear();
        verify(listener).onClose(displayer);
    }
}