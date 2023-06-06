/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateValueFormatter;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValueFormatter;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DateTimeValueConverterTest {

    @Mock
    private DateValueFormatter dateValueFormatter;

    @Mock
    private TimeValueFormatter timeValueFormatter;

    private DateTimeValueConverter converter;

    @Before
    public void setup() {
        converter = spy(new DateTimeValueConverter(dateValueFormatter, timeValueFormatter));
    }

    @Test
    public void testToDMNStringValuesNotSet() {

        final DateTimeValue value = new DateTimeValue();

        final String actual = converter.toDMNString(value);

        assertEquals("", actual);
    }

    @Test
    public void testAppendPrefixAndSuffix() {

        final String value = "value";
        final String expected = DateTimeValueConverter.PREFIX + value + DateTimeValueConverter.SUFFIX;

        final String actual = converter.appendPrefixAndSuffix(value);

        assertEquals(expected, actual);
    }

    @Test
    public void removePrefixAndSuffix() {

        final String expected = "value";
        final String raw = DateTimeValueConverter.PREFIX + expected + DateTimeValueConverter.SUFFIX;

        final String actual = converter.removePrefixAndSuffix(raw);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetTime() {

        final String time = "19:45:00";
        final String expected = "something" + time;
        final DateTimeValue dateTimeValue = new DateTimeValue();
        dateTimeValue.setTime(time);

        when(timeValueFormatter.getTime(time)).thenReturn(expected);

        final String actual = converter.getTime(dateTimeValue);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetDate() {

        final String date = "2019-06-02";
        final String expected = "something" + date;
        final DateTimeValue dateTimeValue = new DateTimeValue();
        dateTimeValue.setDate(date);

        when(dateValueFormatter.getDate(date)).thenReturn(expected);

        final String actual = converter.getDate(dateTimeValue);

        assertEquals(expected, actual);
    }

    @Test
    public void testExtractTime() {

        final String time = "22:45:00-03:00";
        final String input = "2019-06-02T" + time;

        final String actual = converter.extractTime(input);

        assertEquals(time, actual);
    }

    @Test
    public void testExtractDate() {

        final String date = "2019-06-02";
        final String input = date + "T22:45:00-03:00";

        final String actual = converter.extractDate(input);

        assertEquals(date, actual);
    }

    @Test
    public void testExtractDateMissingValue() {

        final String actual = converter.extractDate("");

        assertEquals("", actual);
    }

    @Test
    public void testFromDMNString() {

        final String input = "some dmn string";
        final String value = "some value without prefix and suffix";
        final String date = "date";
        final String time = "time";
        final String expectedTime = "expectedTime";
        final String expectedDate = "expectedDate";

        doReturn(value).when(converter).removePrefixAndSuffix(input);
        doReturn(date).when(converter).extractDate(value);
        doReturn(time).when(converter).extractTime(value);

        when(dateValueFormatter.addPrefixAndSuffix(date)).thenReturn(expectedDate);
        when(timeValueFormatter.appendPrefixAndSuffix(time)).thenReturn(expectedTime);

        final DateTimeValue actual = converter.fromDMNString(input);

        assertEquals(expectedDate, actual.getDate());
        assertEquals(expectedTime, actual.getTime());
    }

    @Test
    public void testToDisplay() {

        final String rawString = "raw";
        final String date = "date";
        final String time = "time";
        final String displayDate = "displayDate";
        final String displayTime = "displayTime";
        final String expected = displayDate + ", " + displayTime;
        final DateTimeValue dateTimeValue = new DateTimeValue();
        dateTimeValue.setDate(date);
        dateTimeValue.setTime(time);

        doReturn(dateTimeValue).when(converter).fromDMNString(rawString);
        when(dateValueFormatter.toDisplay(date)).thenReturn(displayDate);
        when(timeValueFormatter.toDisplay(time)).thenReturn(displayTime);

        final String actual = converter.toDisplay(rawString);

        assertEquals(expected, actual);
    }
}