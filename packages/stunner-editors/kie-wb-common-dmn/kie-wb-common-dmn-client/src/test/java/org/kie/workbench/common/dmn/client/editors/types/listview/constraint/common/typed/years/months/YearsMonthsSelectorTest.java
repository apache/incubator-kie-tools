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

import java.util.function.Consumer;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class YearsMonthsSelectorTest {

    @Mock
    private YearsMonthsSelector.View view;

    @Mock
    private YearsMonthsValueConverter converter;

    @Mock
    private Consumer<Event> onValueChanged;

    @Mock
    private Consumer<BlurEvent> onValueInputBlur;

    private YearsMonthsSelector selector;

    @Before
    public void setup() {
        selector = new YearsMonthsSelector(view, converter);
    }

    @Test
    public void testGetValue() {

        final String expected = "duration(\"P2Y1M\")";
        final YearsMonthsValue value = mock(YearsMonthsValue.class);
        when(value.getYears()).thenReturn("2");
        when(value.getMonths()).thenReturn("1");

        when(converter.toDMNString("2", "1")).thenReturn(expected);
        when(view.getValue()).thenReturn(value);

        final String actual = selector.getValue();

        verify(view).getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetValue() {

        final String dmnString = "duration(\"P2Y1M\")";
        final YearsMonthsValue value = mock(YearsMonthsValue.class);
        when(value.getYears()).thenReturn("2");
        when(value.getMonths()).thenReturn("1");
        when(converter.fromDMNString(dmnString)).thenReturn(value);

        selector.setValue(dmnString);

        verify(view).setValue(value);
    }

    @Test
    public void testSetPlaceholder() {

        final String placeholder = "placeholder";
        selector.setPlaceholder(placeholder);
        verify(view).setPlaceHolder(placeholder);
    }

    @Test
    public void testGetElement() {

        final HTMLElement expected = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(expected);

        final Element actual = selector.getElement();

        assertEquals(expected, actual);
    }

    @Test
    public void testSetOnInputChangeCallback() {
        selector.setOnInputChangeCallback(onValueChanged);
        verify(view).onValueChanged(onValueChanged);
    }

    @Test
    public void testSetOnInputBlurCallback() {
        selector.setOnInputBlurCallback(onValueInputBlur);
        verify(view).onValueInputBlur(onValueInputBlur);
    }

    @Test
    public void testSelect() {
        selector.select();
        verify(view).select();
    }
}
