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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker;

import java.util.Objects;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLInputElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.Moment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TimePickerTest {

    @Mock
    private HTMLInputElement input;

    @Mock
    private TimePicker.View view;

    @Captor
    private ArgumentCaptor<Moment> momentArgumentCaptor;

    private TimePicker picker;

    @Before
    public void setup() {
        picker = spy(new TimePicker(view));
        doReturn(input).when(picker).getInputBind();
    }

    @Test
    public void testRefreshDateInPopup() {

        final Moment moment = mock(Moment.class);
        final String inputValue = "22:30:51";
        int expectedHours = 22;
        int expectedMinutes = 30;
        int expectedSeconds = 51;

        when(moment.isValid()).thenReturn(true);
        when(moment.hours()).thenReturn(expectedHours);
        when(moment.minutes()).thenReturn(expectedMinutes);
        when(moment.seconds()).thenReturn(expectedSeconds);

        doReturn(moment).when(picker).getDateInInput();

        input.value = inputValue;

        picker.refreshDateInPopup();

        verify(view).setDate(momentArgumentCaptor.capture());

        final Moment actual = momentArgumentCaptor.getValue();

        assertEquals(expectedHours, actual.hours());
        assertEquals(expectedMinutes, actual.minutes());
        assertEquals(expectedSeconds, actual.seconds());
    }

    @Test
    public void testIsDateSetInInput() {

        input.value = "01:25";
        final boolean actual = picker.isDateSetInInput();
        assertTrue(actual);
    }

    @Test
    public void testIsDateSetInInputDateNotSet() {

        input.value = "";
        final boolean actual = picker.isDateSetInInput();
        assertFalse(actual);
    }

    @Test
    public void testIsDateSetInInputNotATime() {

        input.value = "1234";
        final boolean actual = picker.isDateSetInInput();
        assertFalse(actual);
    }

    @Test
    public void testOnDateChanged() {

        final Consumer<Moment> consumer = mock(Consumer.class);
        picker.setOnDateChanged(consumer);
        final String expected = "14:55:01";
        final Moment moment = mock(Moment.class);

        when(moment.format("HH:mm:ss")).thenReturn(expected);

        picker.onDateChanged(moment);

        assertEquals(expected, input.value);
        verify(consumer).accept(argThat(argument -> Objects.equals(argument, moment)));
    }
}