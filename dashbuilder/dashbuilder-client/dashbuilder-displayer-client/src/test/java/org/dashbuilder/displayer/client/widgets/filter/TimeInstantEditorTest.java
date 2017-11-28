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
package org.dashbuilder.displayer.client.widgets.filter;

import org.dashbuilder.dataset.date.Month;
import org.dashbuilder.dataset.date.TimeAmount;
import org.dashbuilder.dataset.date.TimeInstant;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TimeInstantEditorTest {

    @Mock
    TimeInstantEditor.View timeInstantView;

    @Mock
    TimeAmountEditor timeAmountEditor;

    @Mock
    Command changeCommand;

    @Test
    public void testViewInitialization() {
        TimeInstant beginMonth = new TimeInstant(TimeInstant.TimeMode.BEGIN, DateIntervalType.MONTH, Month.JANUARY, new TimeAmount(10, DateIntervalType.DAY));
        TimeInstantEditor timeInstantEditor = new TimeInstantEditor(timeInstantView, timeAmountEditor);
        timeInstantEditor.init(beginMonth, changeCommand);

        assertEquals(timeInstantView, timeInstantEditor.view);
        verify(timeInstantView).init(timeInstantEditor);
        verify(timeInstantView).clearTimeModeSelector();
        verify(timeInstantView, times(TimeInstant.TimeMode.values().length)).addTimeModeItem(any(TimeInstant.TimeMode.class));
        verify(timeInstantView).setSelectedTimeModeIndex(TimeInstant.TimeMode.BEGIN.getIndex());

        verify(timeInstantView).enableIntervalTypeSelector();
        verify(timeInstantView).clearIntervalTypeSelector();
        verify(timeInstantView, times(TimeInstantEditor.INTERVAL_TYPES.size())).addIntervalTypeItem(any(DateIntervalType.class));
        verify(timeInstantView).setSelectedIntervalTypeIndex(3);
    }

    @Test
    public void testNullInitialization() {
        TimeInstantEditor timeInstantEditor = new TimeInstantEditor(timeInstantView, timeAmountEditor);
        timeInstantEditor.init(null, changeCommand);

        assertEquals(timeInstantView, timeInstantEditor.view);
        verify(timeInstantView).init(timeInstantEditor);
        verify(timeInstantView).clearTimeModeSelector();
        verify(timeInstantView, times(TimeInstant.TimeMode.values().length)).addTimeModeItem(any(TimeInstant.TimeMode.class));
        verify(timeInstantView).setSelectedTimeModeIndex(TimeInstant.TimeMode.NOW.getIndex());

        verify(timeInstantView).disableIntervalTypeSelector();
        verify(timeInstantView, never()).clearIntervalTypeSelector();
        verify(timeInstantView, never()).enableIntervalTypeSelector();
        verify(timeInstantView, never()).addIntervalTypeItem(any(DateIntervalType.class));
        verify(timeInstantView, never()).setSelectedIntervalTypeIndex(any(Integer.class));
    }

    @Test
    public void testChangeTimeMode() {
        when(timeInstantView.getTimeModeSelectedIndex()).thenReturn(TimeInstant.TimeMode.BEGIN.getIndex());

        TimeInstantEditor timeInstantEditor = new TimeInstantEditor(timeInstantView, timeAmountEditor);
        timeInstantEditor.init(null, changeCommand);
        timeInstantEditor.changeTimeMode();

        TimeInstant timeInstant = timeInstantEditor.getTimeInstant();
        assertEquals(timeInstant.getTimeMode(), TimeInstant.TimeMode.BEGIN);
        verify(changeCommand).execute();
    }

    @Test
    public void testChangeIntervalType() {
        when(timeInstantView.getSelectedIntervalTypeIndex()).thenReturn(0);

        TimeInstantEditor timeInstantEditor = new TimeInstantEditor(timeInstantView, timeAmountEditor);
        timeInstantEditor.init(null, changeCommand);
        timeInstantEditor.changeIntervalType();

        TimeInstant timeInstant = timeInstantEditor.getTimeInstant();
        assertEquals(timeInstant.getIntervalType(), DateIntervalType.MINUTE);
        verify(changeCommand).execute();
    }

    @Test
    public void testSwitchToNow() {

        TimeInstantEditor timeInstantEditor = new TimeInstantEditor(timeInstantView, timeAmountEditor);
        timeInstantEditor.init(null, changeCommand);

        reset(timeInstantView);
        when(timeInstantView.getTimeModeSelectedIndex()).thenReturn(TimeInstant.TimeMode.NOW.getIndex());
        timeInstantEditor.changeTimeMode();

        TimeInstant timeInstant = timeInstantEditor.getTimeInstant();
        assertEquals(timeInstant.getTimeMode(), TimeInstant.TimeMode.NOW);
        verify(timeInstantView).disableIntervalTypeSelector();
        verify(timeInstantView, never()).enableIntervalTypeSelector();
        verify(changeCommand).execute();
    }

    @Test
    public void testSwitchFromNow() {

        TimeInstantEditor timeInstantEditor = new TimeInstantEditor(timeInstantView, timeAmountEditor);
        timeInstantEditor.init(null, changeCommand);
        reset(timeInstantView);

        when(timeInstantView.getTimeModeSelectedIndex()).thenReturn(TimeInstant.TimeMode.END.getIndex());
        timeInstantEditor.changeTimeMode();

        TimeInstant timeInstant = timeInstantEditor.getTimeInstant();
        assertEquals(timeInstant.getTimeMode(), TimeInstant.TimeMode.END);
        verify(timeInstantView).enableIntervalTypeSelector();
        verify(changeCommand).execute();
    }
}