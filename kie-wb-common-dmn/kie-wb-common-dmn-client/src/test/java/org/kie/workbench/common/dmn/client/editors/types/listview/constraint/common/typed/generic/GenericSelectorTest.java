/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.generic;

import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
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
public class GenericSelectorTest {

    private GenericSelector genericSelector;

    @Mock
    private GenericSelector.View view;

    @Before
    public void setup() {
        genericSelector = spy(new GenericSelector(view));
    }

    @Test
    public void getValue() {

        final String expected = "value";
        when(view.getValue()).thenReturn(expected);
        final String actual = genericSelector.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void setValue() {

        final String value = "value";
        genericSelector.setValue(value);
        verify(view).setValue(value);
    }

    @Test
    public void setPlaceholder() {

        final String value = "value";
        genericSelector.setPlaceholder(value);
        verify(view).setPlaceholder(value);
    }

    @Test
    public void getElement() {

        final HTMLElement expected = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(expected);
        final Element actual = genericSelector.getElement();
        assertEquals(expected, actual);
    }

    @Test
    public void onValueChanged() {

        final Consumer consumer = mock(Consumer.class);
        genericSelector.onValueChanged(consumer);
        verify(view).onValueChanged(consumer);
    }

    @Test
    public void onValueInputBlur() {

        final Consumer consumer = mock(Consumer.class);
        genericSelector.onValueInputBlur(consumer);
        verify(view).onValueInputBlur(consumer);
    }

    @Test
    public void select() {

        genericSelector.select();
        verify(view).select();
    }

    @Test
    public void toDisplay() {

        final String expected = "rawValue";
        final String actual = genericSelector.toDisplay(expected);

        assertEquals(expected, actual);
    }
}