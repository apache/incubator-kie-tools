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

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.ExpenseReportsData.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SelectorDisplayerTest extends AbstractDisplayerTest {

    public SelectorDisplayer createSelectorDisplayer(DisplayerSettings settings) {
        return initDisplayer(new SelectorDisplayer(mock(SelectorDisplayer.View.class)), settings);
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

        SelectorDisplayer presenter = createSelectorDisplayer(departmentList);
        SelectorDisplayer.View view = presenter.getView();
        presenter.draw();

        verify(view).setFilterEnabled(true);
        verify(view).clearItems();
        verify(view).showSelectHint(COLUMN_DEPARTMENT);
        verify(view, times(5)).addItem(anyString(), anyString(), eq(false));
        verify(view, never()).showResetHint(anyString());
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

        SelectorDisplayer presenter = createSelectorDisplayer(departmentList);
        SelectorDisplayer.View view = presenter.getView();
        presenter.draw();

        verify(view).clearItems();
        verify(view).showSelectHint(COLUMN_DEPARTMENT);
        verify(view, never()).addItem(anyString(), anyString(), anyBoolean());
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
        SelectorDisplayer presenter = createSelectorDisplayer(departmentList);
        SelectorDisplayer.View view = presenter.getView();
        presenter.draw();

        verify(view, never()).addItem(anyString(), eq((String) null), anyBoolean());
        verify(view, times(5)).addItem(anyString(), anyString(), eq(false));
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

        SelectorDisplayer presenter = createSelectorDisplayer(departmentList);
        DisplayerListener listener = mock(DisplayerListener.class);
        SelectorDisplayer.View view = presenter.getView();
        presenter.draw();

        reset(view);
        when(view.getSelectedId()).thenReturn("1");
        presenter.addListener(listener);
        presenter.onItemSelected();

        // Check filter notifications
        verify(listener, never()).onFilterEnabled(eq(presenter), any(DataSetGroup.class));
        verify(listener, never()).onRedraw(presenter);

        // Ensure data does not change
        verify(view).showResetHint(COLUMN_DEPARTMENT);
        verify(view, never()).clearItems();
        verify(view, never()).showSelectHint(COLUMN_DEPARTMENT);
        verify(view, never()).addItem(anyString(), anyString(), anyBoolean());
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

        SelectorDisplayer presenter = createSelectorDisplayer(departmentList);
        SelectorDisplayer.View view = presenter.getView();
        DisplayerListener listener = mock(DisplayerListener.class);
        presenter.draw();

        // Select an item
        reset(view);
        when(view.getSelectedId()).thenReturn("1");
        presenter.addListener(listener);
        presenter.onItemSelected();

        // Ensure data does not change
        verify(view, never()).clearItems();
        verify(view, never()).addItem(anyString(), anyString(), anyBoolean());

        // Verify the item selected is correct
        ArgumentCaptor<DataSetGroup> argument = ArgumentCaptor.forClass(DataSetGroup.class);
        verify(view).showResetHint(COLUMN_DEPARTMENT);
        verify(view, never()).showSelectHint(COLUMN_DEPARTMENT);
        verify(listener).onFilterEnabled(eq(presenter), argument.capture());
        verify(listener, never()).onRedraw(presenter);
        DataSetGroup dataSetGroup = argument.getValue();
        Interval selectedInterval = dataSetGroup.getSelectedIntervalList().get(0);
        assertEquals(selectedInterval.getName(), "Services");
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

        SelectorDisplayer presenter = createSelectorDisplayer(departmentList);
        SelectorDisplayer.View view = presenter.getView();
        DisplayerListener listener = mock(DisplayerListener.class);
        presenter.draw();

        reset(view);
        when(view.getSelectedId()).thenReturn("1");
        presenter.addListener(listener);
        presenter.onItemSelected();

        // Check filter notifications
        verify(listener).onFilterEnabled(eq(presenter), any(DataSetGroup.class));
        verify(listener).onRedraw(presenter);

        // Check selector refreshes
        verify(view).clearItems();
        verify(view, atLeastOnce()).showResetHint(COLUMN_DEPARTMENT);
        verify(view, never()).showSelectHint(COLUMN_DEPARTMENT);
        verify(view, times(1)).addItem(anyString(), anyString(), eq(false));
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

        SelectorDisplayer presenter = createSelectorDisplayer(departmentList);
        SelectorDisplayer.View view = presenter.getView();
        DisplayerListener listener = mock(DisplayerListener.class);
        presenter.addListener(listener);
        presenter.draw();

        // Verify that null entries are not shown
        verify(view, times(5)).addItem(anyString(), anyString(), eq(false));
        verify(view, never()).addItem(anyString(), eq((String) null), anyBoolean());

        // Select an item
        reset(listener);
        when(view.getSelectedId()).thenReturn("1");
        presenter.onItemSelected();

        // Verify the item selected is correct
        ArgumentCaptor<DataSetGroup> argument = ArgumentCaptor.forClass(DataSetGroup.class);
        verify(listener).onFilterEnabled(eq(presenter), argument.capture());
        DataSetGroup dataSetGroup = argument.getValue();
        Interval selectedInterval = dataSetGroup.getSelectedIntervalList().get(0);
        assertEquals(selectedInterval.getName(), "Engineering");
    }
}