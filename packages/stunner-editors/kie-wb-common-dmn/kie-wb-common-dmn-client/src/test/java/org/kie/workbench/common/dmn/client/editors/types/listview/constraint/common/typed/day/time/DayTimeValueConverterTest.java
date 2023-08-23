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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONValue;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.MomentDuration;
import org.uberfire.client.views.pfly.widgets.MomentDurationObject;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DayTimeValueConverter_Day;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DayTimeValueConverter_Days;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DayTimeValueConverter_Hour;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DayTimeValueConverter_Hours;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DayTimeValueConverter_Minute;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DayTimeValueConverter_Minutes;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DayTimeValueConverter_Second;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DayTimeValueConverter_Seconds;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.uberfire.client.views.pfly.widgets.MomentDuration.moment;

@RunWith(GwtMockitoTestRunner.class)
public class DayTimeValueConverterTest {

    @Mock
    private TranslationService translationService;

    @Mock
    private MomentDuration momentDuration;

    @Captor
    private ArgumentCaptor<JavaScriptObject> javaScriptObject;

    private DayTimeValueConverter converter;

    @Before
    public void setup() {
        converter = spy(new DayTimeValueConverter(translationService));
        moment = momentDuration;
    }

    @Test
    public void testToDMNString() {

        final DayTimeValue value = new DayTimeValue(2, 4, 8, 16);
        final String momentISOString = "P2DT4H8M16S";
        final String expected = "duration(\"P2DT4H8M16S\")";

        when(momentDuration.duration(javaScriptObject.capture())).thenReturn(momentDuration);
        when(momentDuration.toISOString()).thenReturn(momentISOString);

        final String actual = converter.toDMNString(value);

        assertEquals(expected, actual);
    }

    @Test
    public void testFromDMNString() {

        final String value = "P2DT4H8M16S";
        final MomentDurationObject duration = mock(MomentDurationObject.class);
        final Integer expectedDays = 2;
        final Integer expectedHours = 4;
        final Integer expectedMinutes = 8;
        final Integer expectedSeconds = 16;

        when(moment.duration(value)).thenReturn(duration);
        when(duration.days()).thenReturn(expectedDays);
        when(duration.hours()).thenReturn(expectedHours);
        when(duration.minutes()).thenReturn(expectedMinutes);
        when(duration.seconds()).thenReturn(expectedSeconds);

        final DayTimeValue actual = converter.fromDMNString(value);

        assertEquals(expectedDays, actual.getDays());
        assertEquals(expectedHours, actual.getHours());
        assertEquals(expectedMinutes, actual.getMinutes());
        assertEquals(expectedSeconds, actual.getSeconds());
    }

    @Test
    public void testToDisplayValueWithSingularStrings() {

        final String rawValue = "duration(\"P2DT4H8M16S\")";
        final DayTimeValue value = new DayTimeValue(1, 1, 1, 1);
        doReturn(value).when(converter).fromDMNString(rawValue);
        when(translationService.format(DayTimeValueConverter_Day)).thenReturn("day");
        when(translationService.format(DayTimeValueConverter_Hour)).thenReturn("hour");
        when(translationService.format(DayTimeValueConverter_Minute)).thenReturn("minute");
        when(translationService.format(DayTimeValueConverter_Second)).thenReturn("second");

        final String actual = converter.toDisplayValue(rawValue);
        final String expected = "1 day, 1 hour, 1 minute, 1 second";

        assertEquals(expected, actual);
    }

    @Test
    public void testToDisplayValueWithPluralStrings() {

        final String rawValue = "duration(\"P2DT4H8M16S\")";
        final DayTimeValue value = new DayTimeValue(2, 4, 8, 16);
        doReturn(value).when(converter).fromDMNString(rawValue);
        when(translationService.format(DayTimeValueConverter_Days)).thenReturn("days");
        when(translationService.format(DayTimeValueConverter_Hours)).thenReturn("hours");
        when(translationService.format(DayTimeValueConverter_Minutes)).thenReturn("minutes");
        when(translationService.format(DayTimeValueConverter_Seconds)).thenReturn("seconds");

        final String actual = converter.toDisplayValue(rawValue);
        final String expected = "2 days, 4 hours, 8 minutes, 16 seconds";

        assertEquals(expected, actual);
    }

    @Test
    public void testToDisplayValueWithZeroHour() {

        final String rawValue = "duration(\"P2DT0H8M16S\")";
        final DayTimeValue value = new DayTimeValue(2, 0, 8, 16);
        doReturn(value).when(converter).fromDMNString(rawValue);
        when(translationService.format(DayTimeValueConverter_Days)).thenReturn("days");
        when(translationService.format(DayTimeValueConverter_Hours)).thenReturn("hours");
        when(translationService.format(DayTimeValueConverter_Minutes)).thenReturn("minutes");
        when(translationService.format(DayTimeValueConverter_Seconds)).thenReturn("seconds");

        final String actual = converter.toDisplayValue(rawValue);
        final String expected = "2 days, 8 minutes, 16 seconds";

        assertEquals(expected, actual);
    }

    @Test
    public void testToDisplayValueWithZeroDays() {

        final String rawValue = "duration(\"P0DT4H8M16S\")";
        final DayTimeValue value = new DayTimeValue(0, 4, 8, 16);
        doReturn(value).when(converter).fromDMNString(rawValue);
        when(translationService.format(DayTimeValueConverter_Days)).thenReturn("days");
        when(translationService.format(DayTimeValueConverter_Hours)).thenReturn("hours");
        when(translationService.format(DayTimeValueConverter_Minutes)).thenReturn("minutes");
        when(translationService.format(DayTimeValueConverter_Seconds)).thenReturn("seconds");

        final String actual = converter.toDisplayValue(rawValue);
        final String expected = "4 hours, 8 minutes, 16 seconds";

        assertEquals(expected, actual);
    }

    @Test
    public void testToDisplayValueWithZeroMinutes() {

        final String rawValue = "duration(\"P2DT4H0M16S\")";
        final DayTimeValue value = new DayTimeValue(2, 4, 0, 16);
        doReturn(value).when(converter).fromDMNString(rawValue);
        when(translationService.format(DayTimeValueConverter_Days)).thenReturn("days");
        when(translationService.format(DayTimeValueConverter_Hours)).thenReturn("hours");
        when(translationService.format(DayTimeValueConverter_Minutes)).thenReturn("minutes");
        when(translationService.format(DayTimeValueConverter_Seconds)).thenReturn("seconds");

        final String actual = converter.toDisplayValue(rawValue);
        final String expected = "2 days, 4 hours, 16 seconds";

        assertEquals(expected, actual);
    }

    @Test
    public void testToDisplayValueWithZeroSeconds() {

        final String rawValue = "duration(\"P2DT4H8M0S\")";
        final DayTimeValue value = new DayTimeValue(2, 4, 8, 0);
        doReturn(value).when(converter).fromDMNString(rawValue);
        when(translationService.format(DayTimeValueConverter_Days)).thenReturn("days");
        when(translationService.format(DayTimeValueConverter_Hours)).thenReturn("hours");
        when(translationService.format(DayTimeValueConverter_Minutes)).thenReturn("minutes");
        when(translationService.format(DayTimeValueConverter_Seconds)).thenReturn("seconds");

        final String actual = converter.toDisplayValue(rawValue);
        final String expected = "2 days, 4 hours, 8 minutes";

        assertEquals(expected, actual);
    }

    @Test
    public void testCombineSingularsPluralsEmpty() {

        final String rawValue = "duration(\"P2DT0H1M0S\")";
        final DayTimeValue value = new DayTimeValue(2, 0, 1, 0);
        doReturn(value).when(converter).fromDMNString(rawValue);
        when(translationService.format(DayTimeValueConverter_Days)).thenReturn("days");
        when(translationService.format(DayTimeValueConverter_Minute)).thenReturn("minute");

        final String actual = converter.toDisplayValue(rawValue);
        final String expected = "2 days, 1 minute";

        assertEquals(expected, actual);
    }

    @Test
    public void testNumber() {

        final JSONValue actual = converter.number(1);
        final JSONValue expected = new JSONNumber(1);

        assertEquals(expected, actual);
    }

    @Test
    public void testNumberWithNullValues() {

        final JSONValue actual = converter.number(null);
        final JSONValue expected = JSONNull.getInstance();

        assertEquals(expected, actual);
    }
}
