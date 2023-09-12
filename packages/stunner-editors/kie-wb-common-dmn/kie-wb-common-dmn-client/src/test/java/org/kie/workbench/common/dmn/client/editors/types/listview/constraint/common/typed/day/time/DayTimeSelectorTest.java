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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DayTimeSelectorTest {

    @Mock
    private DayTimeSelector.View view;

    @Mock
    private DayTimeValueConverter converter;

    private DayTimeSelector dayTimeSelector;

    @Before
    public void setup() {
        dayTimeSelector = spy(new DayTimeSelector(view, converter));
    }

    @Test
    public void testGetValue() {

        final DayTimeValue value = mock(DayTimeValue.class);
        final String expected = "dmnString";
        when(view.getValue()).thenReturn(value);
        when(converter.toDMNString(value)).thenReturn(expected);
        when(value.isEmpty()).thenReturn(false);

        final String actual = dayTimeSelector.getValue();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetValueWhenValueIsEmpty() {

        final DayTimeValue value = mock(DayTimeValue.class);
        final String expected = "";
        when(view.getValue()).thenReturn(value);
        when(value.isEmpty()).thenReturn(true);

        final String actual = dayTimeSelector.getValue();

        assertEquals(expected, actual);
    }

    @Test
    public void testSetValue() {

        final DayTimeValue value = mock(DayTimeValue.class);
        final String dmnString = "dmnString";
        when(converter.fromDMNString(dmnString)).thenReturn(value);

        dayTimeSelector.setValue(dmnString);

        verify(view).setValue(value);
    }

    @Test
    public void testSetPlaceholder() {
        dayTimeSelector.setPlaceholder("");
        verifyNoMoreInteractions(view, converter);
    }

    @Test
    public void testGetElement() {

        final HTMLElement expected = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(expected);

        final Element actual = dayTimeSelector.getElement();

        assertEquals(expected, actual);
    }

    @Test
    public void testSetOnInputChangeCallback() {

        final Consumer<Event> onValueChanged = (e) -> { /* Nothing. */};

        dayTimeSelector.setOnInputChangeCallback(onValueChanged);

        verify(view).setOnValueChanged(onValueChanged);
    }

    @Test
    public void testSetOnInputBlurCallback() {

        final Consumer<BlurEvent> onValueInputBlur = (e) -> { /* Nothing. */};

        dayTimeSelector.setOnInputBlurCallback(onValueInputBlur);

        verify(view).setOnValueInputBlur(onValueInputBlur);
    }

    @Test
    public void testSelect() {
        dayTimeSelector.select();
        verify(view).select();
    }

    @Test
    public void testToDisplay() {

        final String rawValue = "rawValue";
        final String expected = "display value";
        when(converter.toDisplayValue(rawValue)).thenReturn(expected);

        final String actual = dayTimeSelector.toDisplay(rawValue);

        assertEquals(expected, actual);
    }
}
