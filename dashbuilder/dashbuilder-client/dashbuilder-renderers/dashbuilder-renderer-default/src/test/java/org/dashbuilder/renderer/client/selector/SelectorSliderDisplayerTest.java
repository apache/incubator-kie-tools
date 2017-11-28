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

import java.util.Date;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.engine.ClientDateFormatter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.dashbuilder.displayer.client.widgets.filter.DateParameterEditor;
import org.dashbuilder.displayer.client.widgets.filter.NumberParameterEditor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.ExpenseReportsData.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SelectorSliderDisplayerTest extends AbstractDisplayerTest {

    @Mock
    ClientDateFormatter clientDateFormatter;

    @Mock
    DateParameterEditor minDateInputEditor;

    @Mock
    DateParameterEditor maxDateInputEditor;

    @Mock
    NumberParameterEditor minNumberInputEditor;

    @Mock
    NumberParameterEditor maxNumberInputEditor;

    DisplayerSettings dateSelectorSettings = DisplayerSettingsFactory.newSelectorSettings()
            .dataset(EXPENSES)
            .column(COLUMN_DATE)
            .width(200)
            .filterOn(false, true, false)
            .buildSettings();

    DisplayerSettings numberSelectorSettings = DisplayerSettingsFactory.newSelectorSettings()
            .dataset(EXPENSES)
            .column(COLUMN_AMOUNT)
            .width(200)
            .filterOn(false, true, false)
            .buildSettings();


    public SelectorSliderDisplayer createSelectorDisplayer(DisplayerSettings settings) {
        return initDisplayer(new SelectorSliderDisplayer(mock(SelectorSliderDisplayer.View.class),
                minDateInputEditor, maxDateInputEditor, minNumberInputEditor, maxNumberInputEditor), settings);
    }

    @Test
    public void testDateSelector() {
        SelectorSliderDisplayer presenter = createSelectorDisplayer(dateSelectorSettings);
        SelectorSliderDisplayer.View view = presenter.getView();
        DisplayerListener listener = mock(DisplayerListener.class);
        presenter.addListener(listener);
        presenter.draw();

        verify(view).showSlider(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble());
        verify(view).margins(anyInt(), anyInt(), anyInt(), anyInt());
        verify(view).setWidth(anyInt());

        // Ensure no filter is executed if the range does not change
        presenter.onSliderChange(1, 6);
        reset(listener);
        presenter.onSliderChange(1, 6);
        verify(listener, never()).onFilterUpdate(eq(presenter), any(DataSetFilter.class), any(DataSetFilter.class));
    }

    @Test
    public void testNumberSelector() {
        SelectorSliderDisplayer presenter = createSelectorDisplayer(numberSelectorSettings);
        SelectorSliderDisplayer.View view = presenter.getView();
        DisplayerListener listener = mock(DisplayerListener.class);
        presenter.addListener(listener);
        presenter.draw();

        verify(view).showSlider(1, 1101, 1, 1, 1101);
        verify(view).margins(anyInt(), anyInt(), anyInt(), anyInt());
        verify(view).setWidth(anyInt());

        // Ensure no filter is executed if the range does not change
        presenter.onSliderChange(1, 6);
        reset(listener);
        presenter.onSliderChange(1, 6);
        verify(listener, never()).onFilterUpdate(eq(presenter), any(DataSetFilter.class), any(DataSetFilter.class));
    }

    @Test
    public void testNoData() {
        DisplayerSettings dateSelectorSettings = DisplayerSettingsFactory.newSelectorSettings()
                .dataset(EXPENSES)
                .filter(COLUMN_DATE, FilterFactory.isNull())
                .column(COLUMN_DATE)
                .buildSettings();

        SelectorSliderDisplayer presenter = createSelectorDisplayer(dateSelectorSettings);
        SelectorSliderDisplayer.View view = presenter.getView();
        presenter.draw();

        verify(view).noData();
        verify(view, never()).showSlider(anyInt(), anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    public void testSelectDisabled() {
        dateSelectorSettings.setFilterEnabled(false);
        SelectorSliderDisplayer presenter = createSelectorDisplayer(dateSelectorSettings);
        DisplayerListener listener = mock(DisplayerListener.class);
        SelectorSliderDisplayer.View view = presenter.getView();
        presenter.draw();

        reset(view);
        presenter.addListener(listener);
        presenter.onSliderChange(0, 1);

        // Check filter notifications
        verify(listener, never()).onFilterEnabled(eq(presenter), any(DataSetFilter.class));
        verify(listener, never()).onRedraw(presenter);

        // Ensure data does not change
        verify(view, never()).showSlider(anyInt(), anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    public void testDateRangeSelection() {
        SelectorSliderDisplayer presenter = createSelectorDisplayer(dateSelectorSettings);
        SelectorSliderDisplayer.View view = presenter.getView();
        DisplayerListener listener = mock(DisplayerListener.class);
        presenter.draw();

        // Select a range
        reset(view);
        presenter.addListener(listener);
        presenter.onSliderChange(1328050800000d, 1333231200000d);

        // Ensure data does not change
        verify(view, never()).showSlider(anyInt(), anyInt(), anyInt(), anyInt(), anyInt());

        // Verify the item selected is correct
        ArgumentCaptor<DataSetFilter> argument = ArgumentCaptor.forClass(DataSetFilter.class);
        verify(listener).onFilterUpdate(eq(presenter), any(), argument.capture());
        verify(listener, never()).onRedraw(presenter);
        DataSetFilter dataSetFilter = argument.getValue();
        CoreFunctionFilter columnFilter = (CoreFunctionFilter) dataSetFilter.getColumnFilterList().get(0);
        assertEquals(columnFilter.getColumnId(), COLUMN_DATE);
        assertEquals(columnFilter.getType(), CoreFunctionType.BETWEEN);
        assertEquals(columnFilter.getParameters().size(), 2);
        assertEquals(columnFilter.getParameters().get(0), new Date(1328050800000L));
        assertEquals(columnFilter.getParameters().get(1), new Date(1333231200000L));

        // Select another range
        reset(listener);
        presenter.onSliderChange(1328050800000d, 1335823200000d);
        verify(listener).onFilterUpdate(eq(presenter), any(), argument.capture());
        dataSetFilter = argument.getValue();
        columnFilter = (CoreFunctionFilter) dataSetFilter.getColumnFilterList().get(0);
        assertEquals(columnFilter.getParameters().get(0), new Date(1328050800000L));
        assertEquals(columnFilter.getParameters().get(1), new Date(1335823200000L));
    }

    @Test
    public void testNumberRangeSelection() {
        SelectorSliderDisplayer presenter = createSelectorDisplayer(numberSelectorSettings);
        SelectorSliderDisplayer.View view = presenter.getView();
        DisplayerListener listener = mock(DisplayerListener.class);
        presenter.draw();

        // Select a range
        reset(view);
        presenter.addListener(listener);
        presenter.onSliderChange(1, 101);

        // Ensure data does not change
        verify(view, never()).showSlider(anyInt(), anyInt(), anyInt(), anyInt(), anyInt());

        // Verify the item selected is correct
        ArgumentCaptor<DataSetFilter> argument = ArgumentCaptor.forClass(DataSetFilter.class);
        verify(listener).onFilterUpdate(eq(presenter), any(), argument.capture());
        verify(listener, never()).onRedraw(presenter);
        DataSetFilter dataSetFilter = argument.getValue();
        CoreFunctionFilter columnFilter = (CoreFunctionFilter) dataSetFilter.getColumnFilterList().get(0);
        assertEquals(columnFilter.getColumnId(), COLUMN_AMOUNT);
        assertEquals(columnFilter.getType(), CoreFunctionType.BETWEEN);
        assertEquals(columnFilter.getParameters().size(), 2);
        assertEquals(columnFilter.getParameters().get(0), 1d);
        assertEquals(columnFilter.getParameters().get(1), 101d);

        // Select another range
        reset(listener);
        presenter.onSliderChange(51, 101);
        verify(listener).onFilterUpdate(eq(presenter), any(), argument.capture());
        dataSetFilter = argument.getValue();
        columnFilter = (CoreFunctionFilter) dataSetFilter.getColumnFilterList().get(0);
        assertEquals(columnFilter.getParameters().get(0), 51d);
        assertEquals(columnFilter.getParameters().get(1), 101d);
    }

    @Test
    public void testIgnoreNullDates() {
        expensesDataSet.setValueAt(0, 4, null);
        expensesDataSet.setValueAt(49, 4, null);
        SelectorSliderDisplayer presenter = createSelectorDisplayer(dateSelectorSettings);
        presenter.draw();

        DataSet dataSet = presenter.getDataSetHandler().getLastDataSet();
        Object min = dataSet.getValueAt(0, 0);
        Object max = dataSet.getValueAt(0, 1);

        assertEquals(min, expensesDataSet.getValueAt(48, 4));
        assertEquals(max, expensesDataSet.getValueAt(1, 4));
    }

    @Test
    public void testIgnoreNullNumbers() {
        expensesDataSet.setValueAt(0, 5, null);
        SelectorSliderDisplayer presenter = createSelectorDisplayer(numberSelectorSettings);
        presenter.draw();

        DataSet dataSet = presenter.getDataSetHandler().getLastDataSet();
        Object min = dataSet.getValueAt(0, 0);
        Object max = dataSet.getValueAt(0, 1);

        assertEquals(min, 1.1d);
        assertEquals(max, 1100.1d);
    }

    @Test
    public void testDateAllNullNoData() {
        for (int i = 0; i < 50; i++) {
            expensesDataSet.setValueAt(i, 4, null);
        }

        SelectorSliderDisplayer presenter = createSelectorDisplayer(dateSelectorSettings);
        presenter.draw();
        verify(presenter.getView()).noData();
    }

    @Test
    public void testNumberAllNullNoData() {
        for (int i = 0; i < 50; i++) {
            expensesDataSet.setValueAt(i, 5, null);
        }

        SelectorSliderDisplayer presenter = createSelectorDisplayer(numberSelectorSettings);
        presenter.draw();
        verify(presenter.getView()).noData();
    }

    @Test
    public void testOnMinDateEditorChanged() {
        SelectorSliderDisplayer presenter = createSelectorDisplayer(dateSelectorSettings);
        SelectorSliderDisplayer.View view = presenter.getView();
        presenter.draw();

        reset(view);
        presenter.onMinDateInputFocus();
        verify(view).setSliderEnabled(false);

        reset(view);
        when(minDateInputEditor.getValue()).thenReturn(new Date());
        presenter.onMinDateInputChange();
        verify(view).showSlider(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble());
        verify(view).setSliderEnabled(true);
    }

    @Test
    public void testOnMaxDateEditorChanged() {
        SelectorSliderDisplayer presenter = createSelectorDisplayer(dateSelectorSettings);
        SelectorSliderDisplayer.View view = presenter.getView();
        presenter.draw();

        reset(view);
        presenter.onMaxDateInputFocus();
        verify(view).setSliderEnabled(false);

        reset(view);
        when(maxDateInputEditor.getValue()).thenReturn(new Date());
        presenter.onMaxDateInputChange();
        verify(view).showSlider(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble());
        verify(view).setSliderEnabled(true);
    }
}