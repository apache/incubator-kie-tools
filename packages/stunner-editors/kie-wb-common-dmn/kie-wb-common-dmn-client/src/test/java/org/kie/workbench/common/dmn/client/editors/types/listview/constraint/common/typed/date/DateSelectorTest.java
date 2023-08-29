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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date;

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
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DateSelectorTest {

    @Mock
    private DateSelector.View view;

    @Mock
    private DateValueFormatter valueFormatter;

    @Mock
    private Consumer<Event> onValueChanged;

    @Mock
    private Consumer<BlurEvent> onValueInputBlur;

    private DateSelector dateSelector;

    @Before
    public void setup() {
        dateSelector = spy(new DateSelector(view, valueFormatter));
    }

    @Test
    public void testGetValue() {

        final String expectedValue = "value";
        when(view.getValue()).thenReturn(expectedValue);

        final String actualValue = dateSelector.getValue();

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testSetValue() {

        final String value = "value";
        dateSelector.setValue(value);
        verify(view).setValue(value);
    }

    @Test
    public void testSetPlaceholder() {

        final String placeholder = "placeholder";
        dateSelector.setPlaceholder(placeholder);
        verify(view).setPlaceholder(placeholder);
    }

    @Test
    public void testGetElement() {

        final HTMLElement expected = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(expected);

        final Element actual = dateSelector.getElement();

        assertEquals(expected, actual);
    }

    @Test
    public void testSetOnInputChangeCallback() {
        dateSelector.setOnInputChangeCallback(onValueChanged);
        verify(view).onValueChanged(onValueChanged);
    }

    @Test
    public void testSetOnInputBlurCallback() {
        dateSelector.setOnInputBlurCallback(onValueInputBlur);
        verify(view).onValueInputBlur(onValueInputBlur);
    }

    @Test
    public void testSelect() {

        dateSelector.select();
        verify(view).select();
    }

    @Test
    public void testIsChildren() {

        final Object object = mock(Object.class);
        dateSelector.isChild(object);
        verify(view).isChildOfView(object);
    }
}
