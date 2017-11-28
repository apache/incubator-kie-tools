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
package org.dashbuilder.renderer.client.selector;

import java.util.Date;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.client.AbstractDisplayer;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.dashbuilder.displayer.client.widgets.filter.DateParameterEditor;
import org.dashbuilder.displayer.client.widgets.filter.NumberParameterEditor;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
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
public class SelectorCoordinatorTest extends AbstractDisplayerTest {

    DisplayerSettings yearLabels = DisplayerSettingsFactory.newSelectorSettings()
            .dataset(EXPENSES)
            .group(COLUMN_DATE)
            .column(COLUMN_DATE)
            .column(COLUMN_AMOUNT, SUM)
            .filterOn(false, true, false)
            .subtype(DisplayerSubType.SELECTOR_LABELS)
            .buildSettings();

    DisplayerSettings dateSlider = DisplayerSettingsFactory.newSelectorSettings()
            .dataset(EXPENSES)
            .column(COLUMN_DATE)
            .filterOn(false, true, true)
            .subtype(DisplayerSubType.SELECTOR_SLIDER)
            .buildSettings();

    DisplayerSettings numberSlider = DisplayerSettingsFactory.newSelectorSettings()
            .dataset(EXPENSES)
            .column(COLUMN_AMOUNT)
            .filterOn(false, true, false)
            .subtype(DisplayerSubType.SELECTOR_SLIDER)
            .buildSettings();

    DisplayerSettings dropDownDept = DisplayerSettingsFactory.newSelectorSettings()
            .dataset(EXPENSES)
            .group(COLUMN_DEPARTMENT)
            .column(COLUMN_DEPARTMENT)
            .filterOn(false, true, true)
            .subtype(DisplayerSubType.SELECTOR_DROPDOWN)
            .multiple(true)
            .buildSettings();

    DisplayerSettings dropDownCity = DisplayerSettingsFactory.newSelectorSettings()
            .dataset(EXPENSES)
            .group(COLUMN_CITY)
            .column(COLUMN_CITY)
            .filterOn(false, true, true)
            .subtype(DisplayerSubType.SELECTOR_DROPDOWN)
            .multiple(true)
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
    AbstractDisplayer allRowsDisplayer;
    SelectorLabelSetDisplayer yearLabelDisplayer;
    SelectorSliderDisplayer dateSliderDisplayer;
    SelectorSliderDisplayer numberSliderDisplayer;
    SelectorDropDownDisplayer deptDropDownDisplayer;
    SelectorDropDownDisplayer cityDropDownDisplayer;

    @Mock
    SelectorLabelSetDisplayer.View labelSetView;

    @Mock
    SelectorSliderDisplayer.View sliderView;

    @Mock
    SyncBeanManager beanManager;

    @Mock
    DateParameterEditor dateEditor;

    @Mock
    NumberParameterEditor numberEditor;

    @Mock
    DisplayerListener listener;

    @Mock
    SelectorLabelItem labelItem;

    @Mock
    SyncBeanDef<SelectorLabelItem> labelItemBean;

    @Mock
    SyncBeanDef<SelectorDropDownItem> dropDownItemBean;

    @Mock
    SelectorDropDownItem dropDownItem;

    public SelectorLabelSetDisplayer createLabelSetDisplayer(DisplayerSettings settings) {
        return initDisplayer(new SelectorLabelSetDisplayer(labelSetView, beanManager), settings);
    }

    public SelectorSliderDisplayer createSliderDisplayer(DisplayerSettings settings) {
        return initDisplayer(new SelectorSliderDisplayer(sliderView, dateEditor, dateEditor, numberEditor, numberEditor), settings);
    }

    public SelectorDropDownDisplayer createDropDownDisplayer(DisplayerSettings settings) {
        return initDisplayer(new SelectorDropDownDisplayer(mock(SelectorDropDownDisplayerView.class), beanManager), settings);
    }

    @Before
    public void init() throws Exception {
        super.init();

        when(beanManager.lookupBean(SelectorLabelItem.class)).thenReturn(labelItemBean);
        when(labelItemBean.newInstance()).thenReturn(labelItem);

        when(beanManager.lookupBean(SelectorDropDownItem.class)).thenReturn(dropDownItemBean);
        when(dropDownItemBean.newInstance()).thenReturn(dropDownItem);

        allRowsDisplayer = createNewDisplayer(allRows);
        dateSliderDisplayer = createSliderDisplayer(dateSlider);
        numberSliderDisplayer = createSliderDisplayer(numberSlider);
        yearLabelDisplayer = createLabelSetDisplayer(yearLabels);
        cityDropDownDisplayer = createDropDownDisplayer(dropDownCity);
        deptDropDownDisplayer = createDropDownDisplayer(dropDownDept);

        displayerCoordinator = new DisplayerCoordinator(rendererManager);
        displayerCoordinator.addDisplayers(allRowsDisplayer, dateSliderDisplayer, numberSliderDisplayer, yearLabelDisplayer, cityDropDownDisplayer, deptDropDownDisplayer);
        displayerCoordinator.addListener(listener);
    }

    @Test
    public void testDrawAll() {
        displayerCoordinator.drawAll();

        verify(listener, times(6)).onDraw(any(Displayer.class));
    }

    @Test
    public void testFilterPropagations() {
        displayerCoordinator.drawAll();

        // Select the first year label (2009)
        reset(listener);
        when(labelItem.getId()).thenReturn(0);
        yearLabelDisplayer.onItemSelected(labelItem);

        // Check the allRowsDisplayer receives the filter request
        DataSet dataSet = allRowsDisplayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 13);
        verify(listener).onDataLookup(allRowsDisplayer);
        verify(listener).onRedraw(allRowsDisplayer);
    }
    @Test
    public void testTwoDateFilters() {
        displayerCoordinator.drawAll();

        // Select the first year label (2012)
        when(labelItem.getId()).thenReturn(0);
        yearLabelDisplayer.onItemSelected(labelItem);
        reset(listener);

        // Select 2012 year on slider
        when(labelItem.getId()).thenReturn(0);
        Date min = new Date(112, 0, 1);
        Date max = new Date(112, 11, 31);
        dateSliderDisplayer.onSliderChange(min.getTime(), max.getTime());

        // Check the allRowsDisplayer receives all the filter requests
        DataSet dataSet = allRowsDisplayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 13);
        verify(listener).onDataLookup(allRowsDisplayer);
        verify(listener).onRedraw(allRowsDisplayer);
    }

    @Test
    public void testSliderDateKeepsSelection() {
        displayerCoordinator.drawAll();
        Date min = (Date) dateSliderDisplayer.getSelectedMin();
        Date max = (Date) dateSliderDisplayer.getSelectedMax();

        // Select the first year label (2012)
        when(labelItem.getId()).thenReturn(0);
        yearLabelDisplayer.onItemSelected(labelItem);
        Date min2012 = (Date) dateSliderDisplayer.getSelectedMin();
        Date max2012 = (Date) dateSliderDisplayer.getSelectedMax();
        verify(sliderView).showSlider(min2012.getTime(), max2012.getTime(), 1, min2012.getTime(), max2012.getTime());

        // Shorten the slider
        Date newMin = new Date(112, 3, 3);
        dateSliderDisplayer.onSliderChange(newMin.getTime(), max.getTime());
        assertEquals(dateSliderDisplayer.getSelectedMin(), newMin);
        assertEquals(dateSliderDisplayer.getSelectedMax(), max);

        // Reset the label filter and check the slider keeps its latest selection
        reset(sliderView);
        yearLabelDisplayer.filterReset();
        assertEquals(dateSliderDisplayer.getSelectedMin(), newMin);
        assertEquals(dateSliderDisplayer.getSelectedMax(), max);
        verify(sliderView).showSlider(min.getTime(), max.getTime(), 1, newMin.getTime(), max.getTime());
    }

    @Test
    public void testMinMaxNumberSliderEquals() {
        displayerCoordinator.drawAll();

        // Filter by a range
        numberSliderDisplayer.onSliderChange(11, 100);
        DataSet dataSet = allRowsDisplayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 3);

        // Select the same min/max amount
        reset(listener);
        numberSliderDisplayer.onSliderChange(100, 100);

        // Check the allRowsDisplayer receives all the filter requests and no data is found
        dataSet = allRowsDisplayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(), 1);
        verify(listener).onDataLookup(allRowsDisplayer);
        verify(listener).onRedraw(allRowsDisplayer);
    }

    @Test
    public void testSelectedDropDownItems() {
        displayerCoordinator.drawAll();

        // Select the first item on both drop down selectors
        when(dropDownItem.getId()).thenReturn(0);
        cityDropDownDisplayer.onItemSelected(dropDownItem);

        reset(dropDownItem);
        deptDropDownDisplayer.onItemSelected(dropDownItem);
        verify(dropDownItem).select();
    }
}