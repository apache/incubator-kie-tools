/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.widgets.common.client.common;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.datepicker.client.ui.base.constants.DatePickerLanguage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DatePickerTest {

    @Mock
    public org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker datePickerMock;
    public DatePicker datePicker;
    @Mock
    TextBox textBox;

    @Before
    public void setup() {
        when(datePickerMock.getTextBox()).thenReturn(textBox);
    }

    @Test
    public void testSetFormat() {
        datePicker = new DatePicker(datePickerMock);
        String gwtDateFormat = "dd-MMM-yyyy";
        DateTimeFormat gwtDateTimeFormat = DateTimeFormat.getFormat(gwtDateFormat);

        datePicker.setLocaleName("en");
        datePicker.setFormat(gwtDateFormat);
        verify(datePickerMock).setLanguage(DatePickerLanguage.EN);
        verify(datePickerMock).setFormat(DatePickerFormatUtilities.convertToBS3DateFormat(gwtDateFormat));

        Date now = new Date();
        now = gwtDateTimeFormat.parse(gwtDateTimeFormat.format(now));

        datePicker.setValue(now);
        verify(textBox).setValue(gwtDateTimeFormat.format(now));
        when(textBox.getValue()).thenReturn(gwtDateTimeFormat.format(now));
        assertEquals(now,
                     datePicker.getValue());
    }

    @Test
    public void testSetDatePickerLang() {
        datePicker = new DatePicker(datePickerMock);
        String gwtDateFormat = "dd-MMM-yyyy";

        datePicker.setLocaleName("es");
        datePicker.setFormat(gwtDateFormat);
        verify(datePickerMock).setLanguage(DatePickerLanguage.ES);

        datePicker.setLocaleName("fr");
        datePicker.setFormat(gwtDateFormat);
        verify(datePickerMock).setLanguage(DatePickerLanguage.FR);

        datePicker.setLocaleName("ja");
        datePicker.setFormat(gwtDateFormat);
        verify(datePickerMock).setLanguage(DatePickerLanguage.JA);

        datePicker.setLocaleName("pt_BR");
        datePicker.setFormat(gwtDateFormat);
        verify(datePickerMock).setLanguage(DatePickerLanguage.PT_BR);

        datePicker.setLocaleName("zh_CN");
        datePicker.setFormat(gwtDateFormat);
        verify(datePickerMock).setLanguage(DatePickerLanguage.ZH_CN);

        datePicker.setLocaleName("de");
        datePicker.setFormat(gwtDateFormat);
        verify(datePickerMock).setLanguage(DatePickerLanguage.DE);

        datePicker.setLocaleName("zh_TW");
        datePicker.setFormat(gwtDateFormat);
        verify(datePickerMock).setLanguage(DatePickerLanguage.ZH_TW);

        datePicker.setLocaleName("ru");
        datePicker.setFormat(gwtDateFormat);
        verify(datePickerMock).setLanguage(DatePickerLanguage.RU);

        datePicker.setLocaleName("en");
        datePicker.setFormat(gwtDateFormat);
        verify(datePickerMock).setLanguage(DatePickerLanguage.EN);
    }

    @Test
    public void testGetLocaleName() {
        datePicker = new DatePicker(datePickerMock);

        datePicker.setLocaleName("");
        assertEquals("",
                     datePicker.getLocaleName());

        datePicker.setLocaleName(null);
        assertEquals("",
                     datePicker.getLocaleName());

        datePicker.setLocaleName("default");
        assertEquals("",
                     datePicker.getLocaleName());

        String currentLocale = "testValue";
        datePicker.setLocaleName(currentLocale);
        assertEquals(currentLocale,
                     datePicker.getLocaleName());
    }

    @Test
    public void testGetDataPickerDate(){
        datePicker = new DatePicker(datePickerMock);

        assertNull(datePicker.getDataPickerDate());
    }

}

