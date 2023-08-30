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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common;

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
public class BaseSelectorTest {

    private BaseSelector baseSelector;

    @Mock
    private BaseSelector.View view;

    @Before
    public void testSetup() {
        baseSelector = spy(new BaseSelector(view) {
        });
    }

    @Test
    public void testSetupInputType() {
        final String defaultInputType = BaseSelector.InputType.TEXT.getHtmlInputType();
        baseSelector.setupInputType();
        verify(view).setInputType(defaultInputType);
    }

    @Test
    public void testGetValue() {
        final String expected = "value";
        when(view.getValue()).thenReturn(expected);
        final String actual = baseSelector.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetValue() {
        final String value = "value";
        baseSelector.setValue(value);
        verify(view).setValue(value);
    }

    @Test
    public void testSetPlaceholder() {
        final String value = "value";
        baseSelector.setPlaceholder(value);
        verify(view).setPlaceholder(value);
    }

    @Test
    public void testGetElement() {
        final HTMLElement expected = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(expected);
        final Element actual = baseSelector.getElement();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetOnInputChangeCallback() {
        final Consumer<Event> consumer = (e) -> {/* Nothing/ */};
        baseSelector.setOnInputChangeCallback(consumer);
        verify(view).setOnInputChangeCallback(consumer);
    }

    @Test
    public void testSetOnInputBlurCallback() {
        final Consumer<BlurEvent> consumer = (e) -> {/* Nothing/ */};
        baseSelector.setOnInputBlurCallback(consumer);
        verify(view).setOnInputBlurCallback(consumer);
    }

    @Test
    public void testSelect() {
        baseSelector.select();
        verify(view).select();
    }

    @Test
    public void testToDisplay() {
        final String expected = "rawValue";
        final String actual = baseSelector.toDisplay(expected);
        assertEquals(expected, actual);
    }
}
