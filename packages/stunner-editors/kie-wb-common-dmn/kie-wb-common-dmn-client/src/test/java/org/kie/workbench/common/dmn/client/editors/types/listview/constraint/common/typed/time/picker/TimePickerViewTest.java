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

import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.Moment;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TimePickerViewTest {

    @Mock
    private HTMLAnchorElement increaseHours;

    @Mock
    private HTMLAnchorElement decreaseHours;

    @Mock
    private HTMLAnchorElement increaseMinutes;

    @Mock
    private HTMLAnchorElement decreaseMinutes;

    @Mock
    private HTMLAnchorElement increaseSeconds;

    @Mock
    private HTMLAnchorElement decreaseSeconds;

    @Mock
    private HTMLElement hours;

    @Mock
    private HTMLElement minutes;

    @Mock
    private HTMLElement seconds;

    @Mock
    private Moment date;

    private TimePickerView view;

    @Before
    public void setup() {
        view = spy(new TimePickerView(increaseHours,
                                      decreaseHours,
                                      increaseMinutes,
                                      decreaseMinutes,
                                      increaseSeconds,
                                      decreaseSeconds,
                                      hours,
                                      minutes,
                                      seconds));

        doReturn(date).when(view).getDate();
    }

    @Test
    public void testRefresh() {

        final int hours = 14;
        final int minutes = 25;
        final int seconds = 17;
        final Consumer<Moment> onDateChanged = mock(Consumer.class);
        view.setOnDateChanged(onDateChanged);

        when(date.hours()).thenReturn(hours);
        when(date.minutes()).thenReturn(minutes);
        when(date.seconds()).thenReturn(seconds);

        view.refresh();

        verify(view).setHours(hours);
        verify(view).setMinutes(minutes);
        verify(view).setSeconds(seconds);
        verify(onDateChanged).accept(date);
    }

    @Test
    public void testFormatSingleDigit() {

        final double input = 4.0d;
        testFormat(input, "04");
    }

    @Test
    public void testFormatTwoDigits() {

        final double input = 14.0d;
        testFormat(input, "14");
    }

    @Test
    public void testFormatZero() {

        final double input = 0.0d;
        testFormat(input, "00");
    }

    private void testFormat(final double input, final String expected) {

        final String actual = view.format(input);
        assertEquals(expected, actual);
    }

    @Test
    public void testOnIncreaseHoursClick() {

        when(date.hours()).thenReturn(1);
        doNothing().when(view).refresh();

        view.onIncreaseHoursClick(null);

        verify(date).hours(2);
    }

    @Test
    public void testOnDecreaseHoursClick() {

        when(date.hours()).thenReturn(2);
        doNothing().when(view).refresh();

        view.onDecreaseHoursClick(null);

        verify(date).hours(1);
    }

    @Test
    public void testOnIncreaseMinutesClick() {

        when(date.minutes()).thenReturn(1);
        doNothing().when(view).refresh();

        view.onIncreaseMinutesClick(null);

        verify(date).minutes(2);
    }

    @Test
    public void testOnDecreaseMinutesClick() {

        when(date.minutes()).thenReturn(2);
        doNothing().when(view).refresh();

        view.onDecreaseMinutesClick(null);

        verify(date).minutes(1);
    }

    @Test
    public void testOnIncreaseSecondsClick() {

        when(date.seconds()).thenReturn(1);
        doNothing().when(view).refresh();

        view.onIncreaseSecondsClick(null);

        verify(date).seconds(2);
    }

    @Test
    public void testOnDecreaseSecondsClick() {

        when(date.seconds()).thenReturn(2);
        doNothing().when(view).refresh();

        view.onDecreaseSecondsClick(null);

        verify(date).seconds(1);
    }

    @Test
    public void testUpdateSeconds() {

        when(date.hours()).thenReturn(23);
        when(date.minutes()).thenReturn(59);
        when(date.seconds()).thenReturn(59);
        doNothing().when(view).refresh();

        view.updateSeconds(0);

        verify(date).seconds(0);
        verify(date).minutes(59);
        verify(date).hours(23);
        verify(view).refresh();
    }
}