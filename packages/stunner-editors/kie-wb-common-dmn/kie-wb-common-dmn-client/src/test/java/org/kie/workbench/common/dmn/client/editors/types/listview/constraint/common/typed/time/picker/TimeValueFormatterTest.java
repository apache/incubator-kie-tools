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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TimeValueFormatterTest {

    @Mock
    private TimeZoneProvider provider;

    private TimeValueFormatter formatter;

    @Before
    public void setup() {

        formatter = new TimeValueFormatter(provider);
    }

    @Test
    public void testToRawSimpleTime() {

        final String expected = "time(\"22:30:10\")";
        final String input = "22:30:10";
        final String actual = formatter.toRaw(input);

        assertEquals(expected, actual);
    }

    @Test
    public void testToRawTimeWithNegativeTimeOffSet() {

        final String expected = "time(\"22:30:10-03:00\")";
        final String input = "22:30:10 UTC -03:00";
        final String actual = formatter.toRaw(input);

        assertEquals(expected, actual);
    }

    @Test
    public void testToRawTimeWithPositiveTimeOffSet() {

        final String expected = "time(\"22:30:10+03:00\")";
        final String input = "22:30:10 UTC +03:00";
        final String actual = formatter.toRaw(input);

        assertEquals(expected, actual);
    }

    @Test
    public void testToRawTimeWithUTCTimeZone() {

        when(provider.isTimeZone("UTC")).thenReturn(false);
        final String expected = "time(\"22:30:10Z\")";
        final String input = "22:30:10 UTC";
        final String actual = formatter.toRaw(input);

        assertEquals(expected, actual);
    }

    @Test
    public void testToRawTimeWithTimeZone() {

        when(provider.isTimeZone("America/Sao_Paulo")).thenReturn(true);
        final String expected = "time(\"22:30:10@America/Sao_Paulo\")";
        final String input = "22:30:10 America/Sao_Paulo";
        final String actual = formatter.toRaw(input);

        assertEquals(expected, actual);
    }

    @Test
    public void testToRawTimeWithTimeZoneWithDash() {

        when(provider.isTimeZone(any())).thenReturn(true);

        final String expected = "time(\"22:30:10@US/Pacific-New\")";
        final String input = "22:30:10 US/Pacific-New";
        final String actual = formatter.toRaw(input);

        assertEquals(expected, actual);
    }

    @Test
    public void testToDisplaySimpleTime() {

        final String input = "time(\"22:30:10\")";
        final String expected = "22:30:10";
        final String actual = formatter.toDisplay(input);

        assertEquals(expected, actual);
    }

    @Test
    public void testToDisplayTimeWithNegativeTimeOffSet() {

        final String input = "time(\"22:30:10-03:00\")";
        final String expected = "22:30:10 UTC -03:00";
        final String actual = formatter.toDisplay(input);

        assertEquals(expected, actual);
    }

    @Test
    public void testToDisplayTimeWithPositiveTimeOffSet() {

        final String input = "time(\"22:30:10+03:00\")";
        final String expected = "22:30:10 UTC +03:00";
        final String actual = formatter.toDisplay(input);

        assertEquals(expected, actual);
    }

    @Test
    public void testToDisplayTimeWithUTCTimeOffSet() {

        final String input = "time(\"22:30:10Z\")";
        final String expected = "22:30:10 UTC";
        final String actual = formatter.toDisplay(input);

        assertEquals(expected, actual);
    }

    @Test
    public void testToDisplayTimeWithTimeZone1() {

        final String input = "time(\"22:30:10@America/Sao_Paulo\")";
        final String expected = "22:30:10 America/Sao_Paulo";
        final String actual = formatter.toDisplay(input);

        assertEquals(expected, actual);
    }

    @Test
    public void testToDisplayTimeWithTimeZoneWithDash() {

        final String input = "time(\"22:30:10@Etc/GMT-0\")";
        final String expected = "22:30:10 Etc/GMT-0";
        final String actual = formatter.toDisplay(input);

        assertEquals(expected, actual);
    }
}