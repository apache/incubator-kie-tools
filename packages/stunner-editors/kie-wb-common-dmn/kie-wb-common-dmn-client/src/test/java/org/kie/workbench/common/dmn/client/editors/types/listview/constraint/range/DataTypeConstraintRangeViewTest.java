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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModal;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueComponentSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueSelector;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintRangeViewTest {

    @Mock
    private DataTypeConstraintModal modal;

    @Mock
    private Event event;

    @Mock
    private HTMLDivElement startValueContainer;

    @Mock
    private HTMLDivElement endValueContainer;

    @Mock
    private HTMLInputElement includeStartValue;

    @Mock
    private HTMLInputElement includeEndValue;

    @Mock
    private DataTypeConstraintRange presenter;

    @Mock
    private TypedValueComponentSelector startValueComponentSelector;

    @Mock
    private TypedValueComponentSelector endValueComponentSelector;

    @Mock
    private TypedValueSelector startValueComponent;

    @Mock
    private TypedValueSelector endValueComponent;

    @Mock
    private Element startValueElement;

    @Mock
    private Element endValueElement;

    private DataTypeConstraintRangeView view;

    @Before
    public void setup() {
        view = spy(new DataTypeConstraintRangeView(startValueContainer,
                                                   endValueContainer,
                                                   includeStartValue,
                                                   includeEndValue,
                                                   startValueComponentSelector,
                                                   endValueComponentSelector));

        when(startValueComponentSelector.makeSelectorForType(any())).thenReturn(startValueComponent);
        when(endValueComponentSelector.makeSelectorForType(any())).thenReturn(endValueComponent);
        when(startValueComponent.getElement()).thenReturn(startValueElement);
        when(endValueComponent.getElement()).thenReturn(endValueElement);

        view.init(presenter);
        view.setComponentSelector("someType");
        presenter.setModal(modal);
    }

    @Test
    public void testInit() {
        verify(view).setupInputFields();
    }

    @Test
    public void testGetStartValue() {
        final String expected = "someString";
        when(startValueComponent.getValue()).thenReturn(expected);
        final String actual = view.getStartValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetEndValue() {
        final String expected = "someString";
        when(endValueComponent.getValue()).thenReturn(expected);
        final String actual = view.getEndValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetStartValue() {
        final String expected = "someString";
        view.setStartValue(expected);
        verify(startValueComponent).setValue(expected);
    }

    @Test
    public void testSetEndValue() {
        final String expected = "someString";
        view.setEndValue(expected);
        verify(endValueComponent).setValue(expected);
    }

    @Test
    public void testGetIncludeStartValue() {
        final boolean expected = true;
        includeStartValue.checked = expected;
        final boolean actual = view.getIncludeStartValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetIncludeStartValue() {
        final boolean expected = true;
        view.setIncludeStartValue(expected);
        final boolean actual = includeStartValue.checked;
        assertEquals(expected, actual);
    }

    @Test
    public void testGetIncludeEndValue() {
        final boolean expected = true;
        includeEndValue.checked = expected;
        final boolean actual = view.getIncludeEndValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetIncludeEndValue() {
        final boolean expected = true;
        view.setIncludeEndValue(expected);
        final boolean actual = includeEndValue.checked;
        assertEquals(expected, actual);
    }

    @Test
    public void testOnKeyUpEmptyValues() {
        when(startValueComponent.getValue()).thenReturn("");
        when(endValueComponent.getValue()).thenReturn("");
        view.onValueChanged(event);
        verify(presenter).disableOkButton();
        verify(presenter, never()).enableOkButton();
    }

    @Test
    public void testOnKeyUpNonEmptyValues() {
        when(startValueComponent.getValue()).thenReturn("1");
        when(endValueComponent.getValue()).thenReturn("2");
        view.onValueChanged(event);
        verify(presenter).enableOkButton();
        verify(presenter, never()).disableOkButton();
    }

    @Test
    public void testOnKeyUpNonEmptyStartValue() {
        when(startValueComponent.getValue()).thenReturn("123456");
        when(endValueComponent.getValue()).thenReturn("");
        view.onValueChanged(event);
        verify(presenter).disableOkButton();
        verify(presenter, never()).enableOkButton();
    }

    @Test
    public void testOnKeyUpNonEmptyEndValue() {
        when(startValueComponent.getValue()).thenReturn("");
        when(endValueComponent.getValue()).thenReturn("123456");
        view.onValueChanged(event);
        verify(presenter).disableOkButton();
        verify(presenter, never()).enableOkButton();
    }

    @Test
    public void testSetPlaceholders() {

        final String value = "value";

        view.setPlaceholders(value);

        verify(startValueComponent).setPlaceholder(value);
        verify(endValueComponent).setPlaceholder(value);
    }

    @Test
    public void testSetComponentSelector() {

        final String type = "type";

        view.setComponentSelector(type);

        verify(startValueComponentSelector).makeSelectorForType(type);
        verify(startValueContainer, times(2)).appendChild(startValueElement); // One time is in setup()

        verify(endValueComponentSelector).makeSelectorForType(type);
        verify(endValueContainer, times(2)).appendChild(endValueElement); // One time is in setup()

        verify(view, times(2)).setupInputFields(); // One time is in setup()
    }
}
