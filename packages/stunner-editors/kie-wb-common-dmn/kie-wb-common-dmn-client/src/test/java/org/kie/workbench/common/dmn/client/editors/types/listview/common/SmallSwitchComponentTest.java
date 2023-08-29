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

package org.kie.workbench.common.dmn.client.editors.types.listview.common;

import com.google.gwtmockito.GwtMockitoTestRunner;
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
public class SmallSwitchComponentTest {

    @Mock
    private SmallSwitchComponent.View view;

    private SmallSwitchComponent switchComponent;

    @Before
    public void setup() {
        switchComponent = new SmallSwitchComponent(view);
    }

    @Test
    public void testGetElement() {
        final HTMLElement expectedElement = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(expectedElement);

        final HTMLElement actualElement = switchComponent.getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testSetValueWhenValueIsTrue() {
        final boolean value = true;

        switchComponent.setValue(value);

        verify(view).setValue(value);
    }

    @Test
    public void testGetValueWhenValueIsTrue() {
        final boolean expectedValue = true;
        when(view.getValue()).thenReturn(expectedValue);

        final boolean actualValue = switchComponent.getValue();

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testSetValueWhenValueIsFalse() {
        final boolean value = false;

        switchComponent.setValue(value);

        verify(view).setValue(value);
    }

    @Test
    public void testGetValueWhenValueIsFalse() {
        final boolean expectedValue = false;
        when(view.getValue()).thenReturn(expectedValue);

        final boolean actualValue = switchComponent.getValue();

        assertEquals(expectedValue, actualValue);
    }
}
