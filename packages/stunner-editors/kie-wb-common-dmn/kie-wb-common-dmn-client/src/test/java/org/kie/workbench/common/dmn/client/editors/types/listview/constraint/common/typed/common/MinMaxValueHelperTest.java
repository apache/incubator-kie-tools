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

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Event;
import elemental2.dom.HTMLInputElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.MinMaxValueHelper.OLD_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.MinMaxValueHelper.isValidValue;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.MinMaxValueHelper.setupMinMaxHandlers;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MinMaxValueHelperTest {

    @Mock
    private HTMLInputElement input;

    @Mock
    private Event event;

    @Test
    public void testSetupMinMaxHandlersOnKeyDownWhenValueIsValid() {

        input.value = "50";
        input.max = "100";
        input.min = "-100";
        setupMinMaxHandlers(input);

        final Object result = input.onkeydown.onInvoke(event);

        assertEquals(result, true);
        verify(input).setAttribute(OLD_ATTR, "50");
    }

    @Test
    public void testSetupMinMaxHandlersOnKeyDownWhenValueIsNotValid() {

        input.value = "200";
        input.max = "100";
        input.min = "-100";
        setupMinMaxHandlers(input);

        final Object result = input.onkeydown.onInvoke(event);

        assertEquals(result, true);
        verify(input, never()).setAttribute(Mockito.<String>any(), Mockito.<String>any());
    }

    @Test
    public void testSetupMinMaxHandlersOnKeyUpWhenNewValueIsValid() {

        final String newValue = "2";
        final String oldValue = "42";

        input.value = newValue;
        input.max = "100";
        input.min = "-100";
        setupMinMaxHandlers(input);
        when(input.getAttribute(OLD_ATTR)).thenReturn(oldValue);

        final Object result = input.onkeyup.onInvoke(event);
        final String actualValue = input.value;

        assertEquals(result, true);
        assertEquals(actualValue, newValue);
    }

    @Test
    public void testSetupMinMaxHandlersOnKeyUpWhenNewValueIsNotValid() {

        final String newValue = "150";
        final String oldValue = "42";

        input.value = newValue;
        input.max = "100";
        input.min = "-100";
        setupMinMaxHandlers(input);
        when(input.getAttribute(OLD_ATTR)).thenReturn(oldValue);

        final Object result = input.onkeyup.onInvoke(event);
        final String actualValue = input.value;

        assertEquals(result, true);
        assertEquals(actualValue, oldValue);
    }

    @Test
    public void testSetupMinMaxHandlersOnFocusOutWhenNewValueIsValid() {

        final String newValue = "2";
        final String oldValue = "42";

        input.value = newValue;
        input.max = "100";
        input.min = "-100";
        setupMinMaxHandlers(input);
        when(input.getAttribute(OLD_ATTR)).thenReturn(oldValue);

        final Object result = input.onfocusout.onInvoke(event);
        final String actualValue = input.value;

        assertEquals(result, true);
        assertEquals(actualValue, newValue);
    }

    @Test
    public void testSetupMinMaxHandlersOnFocusOutWhenNewValueIsNotValid() {

        final String newValue = "150";
        final String oldValue = "42";

        input.value = newValue;
        input.max = "100";
        input.min = "-100";
        setupMinMaxHandlers(input);
        when(input.getAttribute(OLD_ATTR)).thenReturn(oldValue);

        final Object result = input.onfocusout.onInvoke(event);
        final String actualValue = input.value;

        assertEquals(result, true);
        assertEquals(actualValue, oldValue);
    }

    @Test
    public void testIsValidValueWhenWithoutMaxValue() {
        input.min = "0";
        assertTrue(isValidValue(input, 0));
    }

    @Test
    public void testIsValidValueWhenWithoutMinValue() {
        input.max = "0";
        assertTrue(isValidValue(input, 0));
    }
}
