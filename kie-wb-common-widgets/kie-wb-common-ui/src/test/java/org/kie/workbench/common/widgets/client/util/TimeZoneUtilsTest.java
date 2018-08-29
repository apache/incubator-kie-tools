/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.client.util;

import java.util.Date;
import java.util.HashMap;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.KIE_TIMEZONE_OFFSET;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class TimeZoneUtilsTest {

    private DateTimeFormat dateTimeFormat;

    @Before
    public void setup() {

        final String offsetInMilliseconds = "10800000";

        ApplicationPreferences.setUp(new HashMap<String, String>() {{
            put(KIE_TIMEZONE_OFFSET, offsetInMilliseconds);
        }});

        dateTimeFormat = mock(DateTimeFormat.class);

        mockStatic(DateTimeFormat.class);
        when(DateTimeFormat.getFormat(anyString())).thenReturn(dateTimeFormat);
    }

    @Test
    @PrepareForTest({DateTimeFormat.class})
    public void testGetTimeZone() {

        final String expectedId = "Etc/GMT-3";
        final int expectedOffsetInMinutes = -180;
        final TimeZone timeZone = TimeZoneUtils.getTimeZone();

        assertEquals(expectedId, timeZone.getID());
        assertEquals(expectedOffsetInMinutes, timeZone.getStandardOffset());
    }

    @Test
    @PrepareForTest({TimeZoneUtils.class, DateTimeFormat.class})
    public void testFormatWithServerTimeZone() {

        final Date date = mock(Date.class);
        final TimeZone timeZone = mock(TimeZone.class);
        final String expectedFormat = "01-01-1900";

        mockStatic(TimeZoneUtils.class);
        when(TimeZoneUtils.getTimeZone()).thenReturn(timeZone);
        when(TimeZoneUtils.formatWithServerTimeZone(any(Date.class))).thenCallRealMethod();

        when(dateTimeFormat.format(eq(date), eq(timeZone))).thenReturn(expectedFormat);

        final String actualFormat = TimeZoneUtils.formatWithServerTimeZone(date);

        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    @PrepareForTest({TimeZoneUtils.class, DateTimeFormat.class})
    public void testConvertFromServerTimeZone() {

        final Date date = mock(Date.class);
        final Date expectedDate = mock(Date.class);
        final String parsedDate = "01-01-1900";

        mockStatic(TimeZoneUtils.class);
        when(TimeZoneUtils.formatWithServerTimeZone(date)).thenReturn(parsedDate);
        when(TimeZoneUtils.convertFromServerTimeZone(any(Date.class))).thenCallRealMethod();

        when(dateTimeFormat.parse(parsedDate)).thenReturn(expectedDate);

        final Date actualDate = TimeZoneUtils.convertFromServerTimeZone(date);

        assertEquals(expectedDate, actualDate);
    }

    @Test
    @PrepareForTest({TimeZoneUtils.class, DateTimeFormat.class})
    public void testConvertToServerTimeZone() {

        final Date date = mock(Date.class);
        final Date expectedDate = mock(Date.class);
        final TimeZone timeZone = mock(TimeZone.class);
        final ArgumentCaptor<TimeZone> captorTimeZone = ArgumentCaptor.forClass(TimeZone.class);
        final String convertedDate = "01-01-1900";
        final DateTimeFormat internalFormat = mock(DateTimeFormat.class);
        final int expectedClientOffset = -60;

        mockStatic(TimeZoneUtils.class);
        when(TimeZoneUtils.getTimeZone()).thenReturn(timeZone);
        when(TimeZoneUtils.internalFormatter()).thenReturn(internalFormat);
        when(TimeZoneUtils.getClientOffset(date)).thenReturn(expectedClientOffset);
        when(TimeZoneUtils.convertToServerTimeZone(any(Date.class))).thenCallRealMethod();

        when(internalFormat.format(eq(date), captorTimeZone.capture())).thenReturn(convertedDate);
        when(internalFormat.parse(convertedDate)).thenReturn(expectedDate);

        final Date actualDate = TimeZoneUtils.convertToServerTimeZone(date);

        assertEquals(expectedClientOffset, captorTimeZone.getValue().getStandardOffset());
        assertEquals(expectedDate, actualDate);
    }

    @Test
    @PrepareForTest({TimeZoneUtils.class, DateTimeFormat.class})
    public void testGetClientOffset() {

        final Date date = mock(Date.class);
        final TimeZone timeZone = mock(TimeZone.class);
        final int serverSideOffSet = -180;
        final int dateOffSet = -120;
        final int expectedOffset = -60;

        mockStatic(TimeZoneUtils.class);
        when(TimeZoneUtils.getTimeZone()).thenReturn(timeZone);
        when(TimeZoneUtils.getClientOffset(any(Date.class))).thenCallRealMethod();

        when(timeZone.getStandardOffset()).thenReturn(serverSideOffSet);
        when(date.getTimezoneOffset()).thenReturn(dateOffSet);

        final int actualOffset = TimeZoneUtils.getClientOffset(date);

        assertEquals(expectedOffset, actualOffset);
    }
}
