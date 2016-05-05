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

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.datepicker.client.ui.base.constants.DatePickerDayOfWeek;
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

    @Mock
    TextBox textBox;

    public DatePicker datePicker;

    @Before
    public void setup(){
        when(datePickerMock.getTextBox()).thenReturn(textBox);
    }

    @Test
    public void testSetFormat() {
        datePicker = new DatePicker(datePickerMock);
        String gwtDateFormat = "dd-MMM-yyyy";
        DateTimeFormat gwtDateTimeFormat = DateTimeFormat.getFormat(gwtDateFormat);

        datePicker.setFormat( gwtDateFormat);
        verify(datePickerMock).setLanguage(DatePickerLanguage.EN);
        verify(datePickerMock).setFormat(DatePickerFormatUtilities.convertToBS3DateFormat(gwtDateFormat));

        Date now = new Date();
        now = gwtDateTimeFormat.parse(gwtDateTimeFormat.format(now));

        datePicker.setValue(now);
        verify(textBox).setValue(gwtDateTimeFormat.format(now));
        when(textBox.getValue()).thenReturn(gwtDateTimeFormat.format(now));
        assertEquals(now, datePicker.getValue());

    }


}

