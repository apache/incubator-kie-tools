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

package org.kie.workbench.common.dmn.backend.editors.types;

import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.editors.types.DMNSimpleTimeZone;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class TimeZoneServiceImplTest {

    private TimeZoneServiceImpl service;

    @Before
    public void setup() {
        service = spy(new TimeZoneServiceImpl());
    }

    @Test
    public void testGetTimeZones() {

        final TimeZone timeZone0 = mock(TimeZone.class);
        final TimeZone timeZone1 = mock(TimeZone.class);
        final String[] ids = new String[]{"0", "1"};

        when(timeZone0.getRawOffset()).thenReturn(0);
        when(timeZone1.getRawOffset()).thenReturn(3600000);

        doReturn(ids).when(service).getAvailableIds();
        doReturn(timeZone0).when(service).getTimeZone("0");
        doReturn(timeZone1).when(service).getTimeZone("1");

        final List<DMNSimpleTimeZone> simpleTimeZones = service.getTimeZones();

        assertEquals(2, simpleTimeZones.size());
        assertEquals(0, simpleTimeZones.get(0).getOffset(), 0.01d);
        assertEquals(1, simpleTimeZones.get(1).getOffset(), 0.01d);
        assertEquals("+00:00", simpleTimeZones.get(0).getOffsetString());
        assertEquals("+01:00", simpleTimeZones.get(1).getOffsetString());
    }

    @Test
    public void testToHours() {

        final long input = 3600000;
        final double expected = 1.0d;

        final double actual = service.toHours(input);

        assertEquals(expected, actual, 0.01d);
    }

    @Test
    public void testToHoursOneAndAHalfHour() {

        final long input = 5400000;
        final double expected = 1.5d;

        final double actual = service.toHours(input);

        assertEquals(expected, actual, 0.01d);
    }

    @Test
    public void testFormatOffsetWithPositiveIntegerHour() {

        final double input = 2;
        final String expected = "+02:00";

        testFormatOffset(input, expected);
    }

    @Test
    public void testFormatOffsetWithNegativeIntegerHour() {

        final double input = -2;
        final String expected = "-02:00";

        testFormatOffset(input, expected);
    }

    @Test
    public void testFormatOffsetWithNegativeFractionalHour() {

        final double input = -2.5;
        final String expected = "-02:30";

        testFormatOffset(input, expected);
    }

    @Test
    public void testFormatOffsetWithPositiveFractionalHour() {

        final double input = 2.5;
        final String expected = "+02:30";

        testFormatOffset(input, expected);
    }

    @Test
    public void testFormatOffsetWithZeroHour() {

        final double input = 0;
        final String expected = "+00:00";

        testFormatOffset(input, expected);
    }

    @Test
    public void testFormatOffsetWithTwoDigitsHour() {

        final double input = 10;
        final String expected = "+10:00";

        testFormatOffset(input, expected);
    }

    private void testFormatOffset(final double input,
                                  final String expected) {

        final String actual = service.formatOffset(input);
        assertEquals(expected, actual);
    }
}