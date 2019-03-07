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

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLInputElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class GenericSelectorViewTest {

    @Mock
    private HTMLInputElement input;

    private GenericSelectorView selectorView;

    @Before
    public void setup() {
        selectorView = spy(new GenericSelectorView(input));
    }

    @Test
    public void testGetValue() {

        final String expected = "expected";
        input.value = expected;
        final String actual = selectorView.getValue();

        assertEquals(expected, actual);
    }

    @Test
    public void testSetValue() {

        final String expected = "expected";
        selectorView.setValue(expected);
        final String actual = input.value;

        assertEquals(expected, actual);
    }

    @Test
    public void testSetPlaceholder() {

        final String attribute = "placeholder";
        final String value = "value";

        selectorView.setPlaceholder(value);

        verify(input).setAttribute(attribute, value);
    }

    @Test
    public void testSelect() {

        selectorView.select();

        verify(input).select();
    }

    @Test
    public void testOnGenericInputBlur() {

        final Consumer consumer = mock(Consumer.class);
        final BlurEvent blurEvent = mock(BlurEvent.class);
        selectorView.onValueInputBlur(consumer);
        selectorView.onGenericInputBlur(blurEvent);

        verify(consumer).accept(blurEvent);
    }
}