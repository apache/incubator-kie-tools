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

import com.google.gwt.event.dom.client.ChangeEvent;
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
public class SmallSwitchComponentViewTest {

    @Mock
    private HTMLInputElement inputCheckbox;

    private SmallSwitchComponentView view;

    @Before
    public void setup() {
        view = spy(new SmallSwitchComponentView(inputCheckbox));
    }

    @Test
    public void testOnInputCheckBoxChange() {
        final ChangeEvent event = mock(ChangeEvent.class);

        view.onInputCheckBoxChange(event);

        verify(view).callOnValueChanged();
    }

    @Test
    public void testGetValueWhenCheckBoxIsChecked() {

        final boolean expectedValue = true;
        inputCheckbox.checked = expectedValue;

        final boolean actualValue = view.getValue();

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGetValueWhenCheckBoxIsNotChecked() {

        final boolean expectedValue = false;
        inputCheckbox.checked = expectedValue;

        final boolean actualValue = view.getValue();

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testSetValue() {

        final boolean value = true;
        inputCheckbox.checked = false;

        view.setValue(value);

        assertEquals(value, inputCheckbox.checked);
    }
}
