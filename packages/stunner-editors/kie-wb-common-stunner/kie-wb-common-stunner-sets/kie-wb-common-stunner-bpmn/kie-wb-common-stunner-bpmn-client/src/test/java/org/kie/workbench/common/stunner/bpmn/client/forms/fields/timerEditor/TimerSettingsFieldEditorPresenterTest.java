/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.timerEditor;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenterBaseTest;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.timerEditor.TimerSettingsFieldEditorPresenter.TIME_CYCLE_LANGUAGE;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TimerSettingsFieldEditorPresenterTest
        extends FieldEditorPresenterBaseTest<TimerSettingsValue, TimerSettingsFieldEditorPresenter, TimerSettingsFieldEditorPresenter.View> {

    private static final String VALUE_1 = "VALUE_1";

    private static final String VALUE_2 = "VALUE_2";

    @Before
    public void setUp() {
        super.setUp();
        verify(view,
               times(1)).setTimeCycleLanguageOptions(anyList(),
                                                     eq(TIME_CYCLE_LANGUAGE.ISO.value()));
        verifyHideParams(1);
        verifyDurationTimerDisplayMode(1,
                                       true);
    }

    @Override
    public ArgumentCaptor<TimerSettingsValue> newArgumentCaptor() {
        return ArgumentCaptor.forClass(TimerSettingsValue.class);
    }

    @Override
    public TimerSettingsFieldEditorPresenter.View mockEditorView() {
        return mock(TimerSettingsFieldEditorPresenter.View.class);
    }

    @Override
    public TimerSettingsFieldEditorPresenter newEditorPresenter(TimerSettingsFieldEditorPresenter.View view) {
        return new TimerSettingsFieldEditorPresenter(view);
    }

    @SuppressWarnings("unchecked")
    @Override
    public FieldEditorPresenter.ValueChangeHandler<TimerSettingsValue> mockChangeHandler() {
        return mock(FieldEditorPresenter.ValueChangeHandler.class);
    }

    @Test
    public void testSetDurationTimerValue() {
        TimerSettingsValue value = new TimerSettingsValue();
        value.setTimeDuration(VALUE_1);
        editor.setValue(value);
        verify(view,
               times(1)).clear();
        verifyHideParams(3);
        verifyDurationTimerDisplayMode(3,
                                       true);
        verify(view,
               times(1)).setTimeDuration(VALUE_1);
    }

    @Test
    public void testSetTimeDateTimerValue() {
        TimerSettingsValue value = new TimerSettingsValue();
        value.setTimeDate(VALUE_1);
        editor.setValue(value);
        verifyHideParams(3);
        verify(view,
               times(1)).clear();
        verifyDurationTimerDisplayMode(2,
                                       true);
        verifyTimeDateTimerDisplayMode(1,
                                       true);
        verify(view,
               times(1)).setTimeDate(VALUE_1);
    }

    @Test
    public void testTimeCycleTimerValue() {
        TimerSettingsValue value = new TimerSettingsValue();
        value.setTimeCycleLanguage(TIME_CYCLE_LANGUAGE.ISO.value());
        value.setTimeCycle(VALUE_1);
        editor.setValue(value);
        verifyHideParams(3);
        verify(view,
               times(1)).clear();
        verifyDurationTimerDisplayMode(2,
                                       true);
        verifyMultipleTimerDisplayMode(1,
                                       true);
        verify(view,
               times(1)).setTimeCycleLanguage(TIME_CYCLE_LANGUAGE.ISO.value());
        verify(view,
               times(1)).setTimeCycle(VALUE_1);
    }

    @Test
    public void testOnTimerDurationChange() {
        TimerSettingsValue value = new TimerSettingsValue();
        editor.setValue(value);
        when(view.getTimeDuration()).thenReturn(VALUE_1);
        editor.onTimerDurationChange();
        verify(changeHandler,
               times(1)).onValueChange(oldValueCaptor.capture(),
                                       newValueCaptor.capture());
        assertEquals(value,
                     oldValueCaptor.getValue());
        value.setTimeDuration(VALUE_1);
        assertEquals(value,
                     newValueCaptor.getValue());
    }

    @Test
    public void testOnTimeCycleChange() {
        TimerSettingsValue value = new TimerSettingsValue();
        editor.setValue(value);
        when(view.getTimeCycle()).thenReturn(VALUE_1);
        when(view.getTimeCycleLanguage()).thenReturn(VALUE_2);
        editor.onTimeCycleChange();
        verifyMultipleTimerChange(value);
    }

    @Test
    public void testOnTimeCycleLanguage() {
        TimerSettingsValue value = new TimerSettingsValue();
        editor.setValue(value);
        when(view.getTimeCycle()).thenReturn(VALUE_1);
        when(view.getTimeCycleLanguage()).thenReturn(VALUE_2);
        editor.onTimeCycleLanguageChange();
        verifyMultipleTimerChange(value);
    }

    private void verifyMultipleTimerChange(TimerSettingsValue oldValue) {
        verify(changeHandler,
               times(1)).onValueChange(oldValueCaptor.capture(),
                                       newValueCaptor.capture());
        assertEquals(oldValue,
                     oldValueCaptor.getValue());
        oldValue.setTimeCycle(VALUE_1);
        oldValue.setTimeCycleLanguage(VALUE_2);
        assertEquals(oldValue,
                     newValueCaptor.getValue());
    }

    @Test
    public void testOnTimeDateChange() {
        TimerSettingsValue value = new TimerSettingsValue();
        editor.setValue(value);
        when(view.getTimeDate()).thenReturn(VALUE_1);
        editor.onTimeDateChange();
        verify(changeHandler,
               times(1)).onValueChange(oldValueCaptor.capture(),
                                       newValueCaptor.capture());
        assertEquals(value,
                     oldValueCaptor.getValue());
        value.setTimeDate(VALUE_1);
        assertEquals(value,
                     newValueCaptor.getValue());
    }

    @Test
    public void testOnMultipleTimerSelected() {
        editor.onMultipleTimerSelected();
        verifyHideParams(2);
        verifyMultipleTimerDisplayMode(1,
                                       false);
        verify(editor,
               times(1)).onMultipleTimerValuesChange();
    }

    @Test
    public void testOnDurationTimerSelected() {
        editor.onDurationTimerSelected();
        verifyHideParams(2);
        verifyDurationTimerDisplayMode(2,
                                       false);
        verify(editor,
               times(1)).onTimerDurationChange();
    }

    @Test
    public void testOnDateTimerSelected() {
        editor.onDateTimerSelected();
        verifyHideParams(2);
        verifyTimeDateTimerDisplayMode(1,
                                       false);
        verify(editor,
               times(1)).onTimeDateChange();
    }

    @Test
    public void testOnShowTimeDateTimePickerWithValidDate() {
        Date date = new Date();
        when(view.getTimeDate()).thenReturn(VALUE_1);
        when(view.parseFromISO(VALUE_1)).thenReturn(date);
        editor.onShowTimeDateTimePicker();
        verify(view,
               times(1)).setTimeDateTimePickerValue(date);
        verify(view,
               times(1)).showTimeDate(false);
    }

    @Test
    public void testOnShowTimeDateTimePickerWithInValidDate() {
        when(view.getTimeDate()).thenReturn(VALUE_1);
        when(view.parseFromISO(VALUE_1)).thenThrow(new IllegalArgumentException("irrelevant"));
        editor.onShowTimeDateTimePicker();
        verify(view,
               times(1)).setTimeDateTimePickerValue(VALUE_1);
        verify(view,
               times(1)).showTimeDate(false);
    }

    @Test
    public void testOnTimeDateTimePickerChange() {
        Date value = new Date();
        when(view.getTimeDateTimePickerValue()).thenReturn(value);
        when(view.formatToISO(value)).thenReturn(VALUE_1);
        editor.onTimeDateTimePickerChange();
        verify(view,
               times(1)).formatToISO(value);
        verify(view,
               times(1)).setTimeDate(VALUE_1);
    }

    @Test
    public void testOnTimeDateTimePickerHidden() {
        editor.onTimeDateTimePickerHidden();
        verify(view,
               times(1)).showTimeDate(true);
        verify(view,
               times(2)).showTimeDateTimePicker(false);
    }

    @Test
    public void testSetReadonlyTrue() {
        editor.setReadOnly(true);
        verify(view,
               times(1)).setReadOnly(true);
    }

    @Test
    public void testSetReadonlyFalse() {
        editor.setReadOnly(false);
        verify(view,
               times(1)).setReadOnly(false);
    }

    private void verifyDurationTimerDisplayMode(int times,
                                                boolean setRadioChecked) {
        verify(view,
               times(times)).showDurationTimerParams(true);
        if (setRadioChecked) {
            verify(view,
                   times(times)).setDurationTimerChecked(true);
        }
    }

    private void verifyTimeDateTimerDisplayMode(int times,
                                                boolean setRadioChecked) {
        verify(view,
               times(times)).showDateTimerParams(true);
        if (setRadioChecked) {
            verify(view,
                   times(times)).setDateTimerChecked(true);
        }
    }

    private void verifyMultipleTimerDisplayMode(int times,
                                                boolean setRadioChecked) {
        verify(view,
               times(times)).showMultipleTimerParams(true);
        if (setRadioChecked) {
            verify(view,
                   times(times)).setMultipleTimerChecked(true);
        }
    }

    private void verifyHideParams(int times) {
        verify(view,
               times(times)).showDurationTimerParams(false);
        verify(view,
               times(times)).showMultipleTimerParams(false);
        verify(view,
               times(times)).showDateTimerParams(false);
    }
}
