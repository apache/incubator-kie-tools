/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.date.DayOfWeek;
import org.dashbuilder.dataset.date.Month;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.ExpenseReportsData.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SelectorLabelSetDisplayerTest extends AbstractDisplayerTest {

    @Mock
    SyncBeanManager beanManager;

    @Mock
    SyncBeanDef<SelectorLabelItem> labelItemBean;

    @Mock
    SelectorLabelItem labelItem;

    public SelectorLabelSetDisplayer createSelectorDisplayer(DisplayerSettings settings) {
        return initDisplayer(new SelectorLabelSetDisplayer(mock(SelectorLabelSetDisplayer.View.class), beanManager), settings);
    }

    @Before
    public void setUp() {
        when(beanManager.lookupBean(SelectorLabelItem.class)).thenReturn(labelItemBean);
        when(labelItemBean.newInstance()).thenReturn(labelItem);
    }

    @Test
    public void testDraw() {
        DisplayerSettings departmentList = DisplayerSettingsFactory.newSelectorSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DEPARTMENT)
                .column(COLUMN_DEPARTMENT)
                .column(COLUMN_ID, AggregateFunctionType.COUNT)
                .filterOn(false, true, false)
                .buildSettings();

        SelectorLabelSetDisplayer presenter = createSelectorDisplayer(departmentList);
        SelectorLabelSetDisplayer.View view = presenter.getView();
        presenter.draw();

        verify(view).clearItems();
        verify(view, times(5)).addItem(any());

        // Verify the entries are sorted by default
        DataSet dataSet = presenter.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0, 0), "Engineering");
        assertEquals(dataSet.getValueAt(4, 0), "Support");
    }

    @Test
    public void testNoData() {
        DisplayerSettings departmentList = DisplayerSettingsFactory.newSelectorSettings()
                .dataset(EXPENSES)
                .filter(COLUMN_ID, FilterFactory.isNull())
                .group(COLUMN_DEPARTMENT)
                .column(COLUMN_DEPARTMENT)
                .column(COLUMN_ID, AggregateFunctionType.COUNT)
                .buildSettings();

        SelectorLabelSetDisplayer presenter = createSelectorDisplayer(departmentList);
        SelectorLabelSetDisplayer.View view = presenter.getView();
        presenter.draw();

        verify(view).clearItems();
        verify(view, never()).addItem(any());
    }

    @Test
    public void testNullNotShown() {
        DisplayerSettings departmentList = DisplayerSettingsFactory.newSelectorSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DEPARTMENT)
                .column(COLUMN_DEPARTMENT)
                .column(COLUMN_ID, AggregateFunctionType.COUNT)
                .buildSettings();

        // Insert a null entry into the dataset
        DataSet expensesDataSet = clientDataSetManager.getDataSet(EXPENSES);
        int column = expensesDataSet.getColumnIndex(expensesDataSet.getColumnById(COLUMN_DEPARTMENT));
        expensesDataSet.setValueAt(0, column, null);

        // ... and make sure it's not shown
        SelectorLabelSetDisplayer presenter = createSelectorDisplayer(departmentList);
        SelectorLabelSetDisplayer.View view = presenter.getView();
        presenter.draw();

        verify(view, times(5)).addItem(any());
        verify(view, never()).addItem(null);
    }

    @Test
    public void testSelectDisabled() {
        DisplayerSettings departmentList = DisplayerSettingsFactory.newSelectorSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DEPARTMENT)
                .column(COLUMN_DEPARTMENT)
                .column(COLUMN_ID, AggregateFunctionType.COUNT)
                .filterOff(true)
                .buildSettings();

        SelectorLabelSetDisplayer presenter = createSelectorDisplayer(departmentList);
        DisplayerListener listener = mock(DisplayerListener.class);
        SelectorLabelSetDisplayer.View view = presenter.getView();
        presenter.draw();

        reset(view);
        presenter.addListener(listener);
        presenter.onItemSelected(labelItem);

        // Check filter notifications
        verify(listener, never()).onFilterEnabled(eq(presenter), any(DataSetGroup.class));
        verify(listener, never()).onRedraw(presenter);

        // Ensure data does not change
        verify(view, never()).clearItems();
        verify(view, never()).addItem(any());
    }

    @Test
    public void testSelectItem() {
        DisplayerSettings departmentList = DisplayerSettingsFactory.newSelectorSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DEPARTMENT)
                .column(COLUMN_DEPARTMENT)
                .column(COLUMN_ID, AggregateFunctionType.COUNT)
                .filterOn(false, true, true)
                .buildSettings();

        SelectorLabelSetDisplayer presenter = createSelectorDisplayer(departmentList);
        SelectorLabelSetDisplayer.View view = presenter.getView();
        DisplayerListener listener = mock(DisplayerListener.class);
        presenter.draw();

        // Select an item
        reset(view);
        when(labelItem.getId()).thenReturn(1);
        presenter.addListener(listener);
        presenter.onItemSelected(labelItem);

        // Ensure data does not change
        verify(view, never()).clearItems();
        verify(view, never()).addItem(any());

        // Verify the item selected is correct
        ArgumentCaptor<DataSetGroup> argument = ArgumentCaptor.forClass(DataSetGroup.class);
        verify(listener).onFilterEnabled(eq(presenter), argument.capture());
        verify(listener, never()).onRedraw(presenter);
        DataSetGroup dataSetGroup = argument.getValue();
        List<Interval> selectedIntervals = dataSetGroup.getSelectedIntervalList();
        assertEquals(selectedIntervals.size(), 1);
        Interval selectedInterval = selectedIntervals.get(0);
        assertEquals(selectedInterval.getName(), "Management");
    }

    @Test
    public void testMultipleSelect() {
        DisplayerSettings departmentList = DisplayerSettingsFactory.newSelectorSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DEPARTMENT)
                .column(COLUMN_DEPARTMENT)
                .column(COLUMN_ID, AggregateFunctionType.COUNT)
                .filterOn(false, true, true)
                .multiple(true)
                .buildSettings();

        SelectorLabelSetDisplayer presenter = createSelectorDisplayer(departmentList);
        SelectorLabelSetDisplayer.View view = presenter.getView();
        DisplayerListener listener = mock(DisplayerListener.class);
        presenter.draw();

        // Select an item
        reset(view);
        presenter.addListener(listener);
        when(labelItem.getId()).thenReturn(1);
        presenter.onItemSelected(labelItem);
        when(labelItem.getId()).thenReturn(2);
        presenter.onItemSelected(labelItem);

        // Ensure data does not change
        verify(view, never()).clearItems();
        verify(view, never()).addItem(any());

        // Verify the item selected is correct
        ArgumentCaptor<DataSetGroup> argument = ArgumentCaptor.forClass(DataSetGroup.class);
        verify(listener, times(2)).onFilterEnabled(eq(presenter), argument.capture());
        verify(listener, never()).onRedraw(presenter);
        DataSetGroup dataSetGroup = argument.getValue();
        List<Interval> selectedIntervals = dataSetGroup.getSelectedIntervalList();
        assertEquals(selectedIntervals.size(), 2);
        Interval selectedInterval1 = selectedIntervals.get(0);
        Interval selectedInterval2 = selectedIntervals.get(1);
        assertEquals(selectedInterval1.getName(), "Management");
        assertEquals(selectedInterval2.getName(), "Sales");
    }

    @Test
    public void testDrillDown() {
        DisplayerSettings departmentList = DisplayerSettingsFactory.newSelectorSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DEPARTMENT)
                .column(COLUMN_DEPARTMENT)
                .column(COLUMN_ID, AggregateFunctionType.COUNT)
                .filterOn(true, true, true)
                .buildSettings();

        SelectorLabelSetDisplayer presenter = createSelectorDisplayer(departmentList);
        SelectorLabelSetDisplayer.View view = presenter.getView();
        DisplayerListener listener = mock(DisplayerListener.class);
        presenter.draw();

        reset(view);
        when(labelItem.getId()).thenReturn(1);
        presenter.addListener(listener);
        presenter.onItemSelected(labelItem);

        // Check filter notifications
        verify(listener).onFilterEnabled(eq(presenter), any(DataSetGroup.class));
        verify(listener).onRedraw(presenter);

        // Check selector refreshes
        verify(view).clearItems();
        verify(view, times(1)).addItem(any());
    }


    @Test
    public void testNullEntries() {
        // Insert a null entry into the dataset
        DataSet expensesDataSet = clientDataSetManager.getDataSet(EXPENSES);
        int column = expensesDataSet.getColumnIndex(expensesDataSet.getColumnById(COLUMN_DEPARTMENT));
        expensesDataSet.setValueAt(0, column, null);

        // Create a selector displayer
        DisplayerSettings departmentList = DisplayerSettingsFactory.newSelectorSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DEPARTMENT)
                .column(COLUMN_DEPARTMENT)
                .column(COLUMN_ID, AggregateFunctionType.COUNT)
                .filterOn(false, true, true)
                .buildSettings();

        SelectorLabelSetDisplayer presenter = createSelectorDisplayer(departmentList);
        SelectorLabelSetDisplayer.View view = presenter.getView();
        DisplayerListener listener = mock(DisplayerListener.class);
        presenter.addListener(listener);
        presenter.draw();

        // Verify that null entries are not shown
        verify(view, times(5)).addItem(any());
        verify(view, never()).addItem(null);

        // Select an item
        reset(listener);
        when(labelItem.getId()).thenReturn(1);
        presenter.onItemSelected(labelItem);

        // Verify the item selected is correct
        ArgumentCaptor<DataSetGroup> argument = ArgumentCaptor.forClass(DataSetGroup.class);
        verify(listener).onFilterEnabled(eq(presenter), argument.capture());
        DataSetGroup dataSetGroup = argument.getValue();
        Interval selectedInterval = dataSetGroup.getSelectedIntervalList().get(0);
        assertEquals(selectedInterval.getName(), "Engineering");
    }


    @Test
    public void testSortFixedMonthDefault() {
        DisplayerSettings displayerSettings = DisplayerSettingsFactory.newSelectorSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DATE).fixed(DateIntervalType.MONTH, true)
                .column(COLUMN_DATE)
                .column(COLUMN_ID, AggregateFunctionType.COUNT)
                .buildSettings();

        SelectorLabelSetDisplayer presenter = createSelectorDisplayer(displayerSettings);
        presenter.draw();
        DataSet dataSet = presenter.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0, 0), "1");
        assertEquals(dataSet.getValueAt(11, 0), "12");
    }

    @Test
    public void testSortFixedFirstMonth() {
        DisplayerSettings displayerSettings = DisplayerSettingsFactory.newSelectorSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DATE).fixed(DateIntervalType.MONTH, true).firstMonth(Month.FEBRUARY)
                .column(COLUMN_DATE)
                .column(COLUMN_ID, AggregateFunctionType.COUNT)
                .buildSettings();

        SelectorLabelSetDisplayer presenter = createSelectorDisplayer(displayerSettings);
        presenter.draw();
        DataSet dataSet = presenter.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0, 0), "2");
        assertEquals(dataSet.getValueAt(11, 0), "1");
    }

    @Test
    public void testSortFixedDayOfWeekDefault() {
        DisplayerSettings displayerSettings = DisplayerSettingsFactory.newSelectorSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DATE).fixed(DateIntervalType.DAY_OF_WEEK, true)
                .column(COLUMN_DATE)
                .column(COLUMN_ID, AggregateFunctionType.COUNT)
                .buildSettings();

        SelectorLabelSetDisplayer presenter = createSelectorDisplayer(displayerSettings);
        presenter.draw();
        DataSet dataSet = presenter.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0, 0), "2");
        assertEquals(dataSet.getValueAt(6, 0), "1");
    }

    @Test
    public void testSortFixedFirstDayOfWeek() {
        DisplayerSettings displayerSettings = DisplayerSettingsFactory.newSelectorSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DATE).fixed(DateIntervalType.DAY_OF_WEEK, true).firstDay(DayOfWeek.SUNDAY)
                .column(COLUMN_DATE)
                .column(COLUMN_ID, AggregateFunctionType.COUNT)
                .buildSettings();

        SelectorLabelSetDisplayer presenter = createSelectorDisplayer(displayerSettings);
        presenter.draw();
        DataSet dataSet = presenter.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0, 0), "1");
        assertEquals(dataSet.getValueAt(6, 0), "7");
    }

    @Test
    public void testSortFixedHour() {
        DisplayerSettings displayerSettings = DisplayerSettingsFactory.newSelectorSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DATE).fixed(DateIntervalType.HOUR, true)
                .column(COLUMN_DATE)
                .column(COLUMN_ID, AggregateFunctionType.COUNT)
                .buildSettings();

        SelectorLabelSetDisplayer presenter = createSelectorDisplayer(displayerSettings);
        presenter.draw();
        DataSet dataSet = presenter.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0, 0), "0");
        assertEquals(dataSet.getValueAt(23, 0), "23");
    }

    @Test
    public void testSortFixedMinute() {
        DisplayerSettings displayerSettings = DisplayerSettingsFactory.newSelectorSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DATE).fixed(DateIntervalType.MINUTE, true)
                .column(COLUMN_DATE)
                .column(COLUMN_ID, AggregateFunctionType.COUNT)
                .buildSettings();

        SelectorLabelSetDisplayer presenter = createSelectorDisplayer(displayerSettings);
        presenter.draw();
        DataSet dataSet = presenter.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0, 0), "0");
        assertEquals(dataSet.getValueAt(59, 0), "59");
    }

    @Test
    public void testSortFixedSecond() {
        DisplayerSettings displayerSettings = DisplayerSettingsFactory.newSelectorSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DATE).fixed(DateIntervalType.SECOND, true)
                .column(COLUMN_DATE)
                .column(COLUMN_ID, AggregateFunctionType.COUNT)
                .buildSettings();

        SelectorLabelSetDisplayer presenter = createSelectorDisplayer(displayerSettings);
        presenter.draw();
        DataSet dataSet = presenter.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0, 0), "0");
        assertEquals(dataSet.getValueAt(59, 0), "59");
    }
}