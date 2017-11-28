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
package org.dashbuilder.displayer.client.widgets.group;

import javax.enterprise.event.Event;

import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.displayer.client.events.DataSetGroupDateChanged;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.group.DateIntervalType.*;
import static org.dashbuilder.dataset.group.GroupStrategy.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetGroupDateEditorTest {

    @Mock
    DataSetGroupDateEditor.View view;

    @Mock
    Event<DataSetGroupDateChanged> changeEvent;

    DataSetGroupDateEditor presenter;

    @Before
    public void init() {
        presenter = new DataSetGroupDateEditor(view, changeEvent);
    }

    @Test
    public void testFixedModeInit() {
        presenter.init(new ColumnGroup("col", "col", FIXED, 15, HOUR.toString()));

        verify(view).setFixedModeValue(true);
        verify(view).clearIntervalTypeSelector();
        verify(view, times(FIXED_INTERVALS_SUPPORTED.size())).addIntervalTypeItem(any(DateIntervalType.class));
        verify(view).setSelectedIntervalTypeIndex(anyInt());
        verify(view, never()).setMaxIntervalsVisibility(true);
        verify(view, never()).setFirstDayVisibility(true);
        verify(view, never()).setFirstMonthVisibility(true);
    }

    @Test
    public void testDynamicModeInit() {
        ColumnGroup columnGroup = new ColumnGroup("col", "col", DYNAMIC, 15, HOUR.toString());
        presenter.init(columnGroup);

        verify(view).setFixedModeValue(false);
        verify(view).clearIntervalTypeSelector();
        verify(view, times(DateIntervalType.values().length)).addIntervalTypeItem(any(DateIntervalType.class));
        verify(view).setSelectedIntervalTypeIndex(anyInt());
        verify(view).setMaxIntervalsVisibility(true);
        verify(view).setMaxIntervalsValue("15");

        verify(view, never()).setFirstDayVisibility(true);
        verify(view, never()).setFirstMonthVisibility(true);
    }

    @Test
    public void testFirstMonthDayVisibility() {
        presenter.init(new ColumnGroup("col", "col", FIXED, 15, QUARTER.toString()));
        verify(view).setFirstMonthVisibility(false);
        verify(view).setFirstDayVisibility(false);
        verify(view, never()).setFirstDayVisibility(true);
        verify(view, never()).setFirstMonthVisibility(true);

        reset(view);
        presenter.init(new ColumnGroup("col", "col", FIXED, 15, MONTH.toString()));
        verify(view).setFirstMonthVisibility(true);
        verify(view).setFirstDayVisibility(false);
        verify(view, never()).setFirstDayVisibility(true);

        reset(view);
        presenter.init(new ColumnGroup("col", "col", FIXED, 15, DAY_OF_WEEK.toString()));
        verify(view).setFirstDayVisibility(true);
        verify(view).setFirstMonthVisibility(false);
        verify(view, never()).setFirstMonthVisibility(true);

        reset(view);
        presenter.init(new ColumnGroup("col", "col", FIXED, 15, HOUR.toString()));
        verify(view).setFirstMonthVisibility(false);
        verify(view).setFirstDayVisibility(false);
        verify(view, never()).setFirstDayVisibility(true);
        verify(view, never()).setFirstMonthVisibility(true);

        reset(view);
        presenter.init(new ColumnGroup("col", "col", FIXED, 15, MINUTE.toString()));
        verify(view).setFirstMonthVisibility(false);
        verify(view).setFirstDayVisibility(false);
        verify(view, never()).setFirstDayVisibility(true);
        verify(view, never()).setFirstMonthVisibility(true);

        reset(view);
        presenter.init(new ColumnGroup("col", "col", FIXED, 15, SECOND.toString()));
        verify(view).setFirstMonthVisibility(false);
        verify(view).setFirstDayVisibility(false);
        verify(view, never()).setFirstDayVisibility(true);
        verify(view, never()).setFirstMonthVisibility(true);

    }

    @Test
    public void testEnableFixedMode() {
        presenter.init(new ColumnGroup("col", "col", DYNAMIC, 15, CENTURY.toString()));
        reset(view);

        when(view.getFixedModeValue()).thenReturn(true);
        presenter.onFixedStrategyChanged();
        verify(changeEvent).fire(any(DataSetGroupDateChanged.class));

        ColumnGroup result = presenter.getColumnGroup();
        assertEquals(result.getStrategy(), FIXED);
        verify(view).setFirstMonthVisibility(true);
        verify(view, never()).setMaxIntervalsVisibility(true);
        verify(view, never()).setFirstDayVisibility(true);
    }

    @Test
    public void testDisableFixedMode() {
        when(view.getFixedModeValue()).thenReturn(false);

        presenter.init(new ColumnGroup("col", "col", FIXED, 15, HOUR.toString()));
        presenter.onFixedStrategyChanged();
        verify(changeEvent).fire(any(DataSetGroupDateChanged.class));

        ColumnGroup result = presenter.getColumnGroup();
        assertEquals(result.getStrategy(), DYNAMIC);
        verify(view).setMaxIntervalsVisibility(true);
        verify(view).setMaxIntervalsValue("15");
    }
}