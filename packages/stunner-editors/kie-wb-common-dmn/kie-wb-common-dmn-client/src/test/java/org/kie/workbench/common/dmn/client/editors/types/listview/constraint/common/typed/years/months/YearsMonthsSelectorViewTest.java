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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months;

import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLInputElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class YearsMonthsSelectorViewTest {

    @Mock
    private HTMLInputElement yearInput;

    @Mock
    private HTMLInputElement monthInput;

    private YearsMonthsSelectorView view;

    @Before
    public void testSetup() {

        view = Mockito.spy(new YearsMonthsSelectorView(yearInput, monthInput));
    }

    @Test
    public void testGetValue() {

        final String inputYear = "inputYear";
        final String inputMonth = "inputMonth";
        yearInput.value = inputYear;
        monthInput.value = inputMonth;

        final YearsMonthsValue value = view.getValue();

        assertEquals(inputYear, value.getYears());
        assertEquals(inputMonth, value.getMonths());
    }

    @Test
    public void testSetValue() {

        final String months = "months";
        final String years = "years";
        final YearsMonthsValue yearsMonthsValue = mock(YearsMonthsValue.class);

        when(yearsMonthsValue.getMonths()).thenReturn(months);
        when(yearsMonthsValue.getYears()).thenReturn(years);

        view.setValue(yearsMonthsValue);

        assertEquals(years, yearInput.value);
        assertEquals(months, monthInput.value);
    }

    @Test
    public void testSetPlaceHolder() {

        final String value = "placeholderValue";
        view.setPlaceHolder(value);

        verify(yearInput).setAttribute("placeholder", value);
        verify(monthInput).setAttribute("placeholder", value);
    }

    @Test
    public void testOnYearsInputBlur() {

        final BlurEvent blurEvent = mock(BlurEvent.class);
        final NativeEvent nativeEvent = mock(NativeEvent.class);
        final EventTarget eventTarget = mock(EventTarget.class);

        when(blurEvent.getNativeEvent()).thenReturn(nativeEvent);
        when(nativeEvent.getRelatedEventTarget()).thenReturn(eventTarget);

        view.onYearsInputBlur(blurEvent);

        verify(view).handle(blurEvent);
    }

    @Test
    public void testOnMonthsInputBlur() {

        final BlurEvent blurEvent = mock(BlurEvent.class);
        final NativeEvent nativeEvent = mock(NativeEvent.class);
        final EventTarget eventTarget = mock(EventTarget.class);

        when(blurEvent.getNativeEvent()).thenReturn(nativeEvent);
        when(nativeEvent.getRelatedEventTarget()).thenReturn(eventTarget);

        view.onMonthsInputBlur(blurEvent);

        verify(view).handle(blurEvent);
    }

    @Test
    public void testIsNotYearsOrMonthsInput() {

        final Object object = mock(Object.class);

        final boolean actual = view.isYearsOrMonthsInput(object);

        assertFalse(actual);
    }

    @Test
    public void testIsYearsOrMonthsInput() {

        final boolean isMonthInput = view.isYearsOrMonthsInput(monthInput);

        assertTrue(isMonthInput);

        final boolean isYearInput = view.isYearsOrMonthsInput(yearInput);

        assertTrue(isYearInput);
    }

    @Test
    public void select() {

        view.select();

        verify(yearInput).select();
    }
}