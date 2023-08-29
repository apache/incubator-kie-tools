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


package org.kie.workbench.common.forms.common.rendering.client.widgets.integerBox;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class IntegerBoxTest {

    public static final int ARROW_LEFT_KEYCODE = 37;
    public static final int ARROW_RIGHT_KEYCODE = 39;
    public static final int PERIOD_KEYCODE = 190;
    public static final Long TEST_VALUE_LONG = 27l;
    public static final String TEST_VALUE_STRING = TEST_VALUE_LONG.toString();

    protected IntegerBoxView view;

    @GwtMock
    protected GwtEvent<?> event;

    @GwtMock
    protected Widget viewWidget;

    protected IntegerBox integerBox;

    @Before
    public void setup() {
        view = mock(IntegerBoxView.class);

        when(view.asWidget()).thenReturn(viewWidget);

        integerBox = new IntegerBox(view);

        verify(view).setPresenter(integerBox);

        integerBox.asWidget();

        verify(view).asWidget();
    }

    @Test
    public void testSetValueWithoutEvents() {

        integerBox.setValue(TEST_VALUE_LONG);

        verify(view).setValue(TEST_VALUE_STRING);
    }

    @Test
    public void testSetValueWithEvents() {
        integerBox = spy(integerBox);

        integerBox.setValue(TEST_VALUE_LONG,
                            true);

        verify(view).setValue(TEST_VALUE_STRING);
        verify(integerBox).notifyValueChange(TEST_VALUE_STRING);
    }

    @Test
    public void testChangeValueToNull() {
        integerBox = spy(integerBox);

        when(view.getTextValue()).thenReturn(TEST_VALUE_STRING);
        integerBox.setValue(null,
                            true);
        verify(integerBox).notifyValueChange(null);
    }

    @Test
    public void testSetValueToNullWhileItIsAlreadyNull() {
        integerBox = spy(integerBox);

        integerBox.setValue(null,
                            true);
        verify(integerBox,
               times(0)).notifyValueChange(null);
    }

    @Test
    public void testKeyCodeLetter() {
        testKeyCode(KeyCodes.KEY_A,
                    false,
                    true);
    }

    @Test
    public void testKeyCodeSpace() {
        testKeyCode(KeyCodes.KEY_SPACE,
                    false,
                    true);
    }

    @Test
    public void testKeyCodePeriod() {
        testKeyCode(PERIOD_KEYCODE,
                    false,
                    true);
    }

    @Test
    public void testKeyCodeNumPadPeriod() {
        testKeyCode(KeyCodes.KEY_NUM_PERIOD,
                    false,
                    true);
    }

    @Test
    public void testKeyCodeDigit() {
        testKeyCode(KeyCodes.KEY_ONE,
                    false,
                    false);
    }

    @Test
    public void testKeyCodeNumPadDigit() {
        testKeyCode(KeyCodes.KEY_NUM_ONE,
                    false,
                    false);
    }

    @Test
    public void testKeyCodeBackSpace() {
        testKeyCode(KeyCodes.KEY_BACKSPACE,
                    false,
                    false);
    }

    @Test
    public void testKeyCodeLeftArrow() {
        testKeyCode(ARROW_LEFT_KEYCODE,
                    false,
                    false);
    }

    @Test
    public void testKeyCodeRightArrow() {
        testKeyCode(ARROW_RIGHT_KEYCODE,
                    false,
                    false);
    }

    @Test
    public void testKeyCodeTab() {
        testKeyCode(KeyCodes.KEY_TAB,
                    false,
                    false);
    }

    @Test
    public void testKeyCodeShiftTab() {
        testKeyCode(KeyCodes.KEY_TAB,
                    true,
                    false);
    }

    private void testKeyCode(int keyCode,
                             boolean isShiftPressed,
                             boolean expectInvalid) {
        boolean result = integerBox.isInvalidKeyCode(keyCode,
                                                     isShiftPressed);
        assertEquals(result,
                     expectInvalid);
    }

    @Test
    public void testEvents() {
        ValueChangeHandler handler = mock(ValueChangeHandler.class);
        integerBox.addValueChangeHandler(handler);
        verify(view,
               atLeast(1)).asWidget();
        verify(viewWidget).addHandler(any(),
                                      any());

        integerBox.fireEvent(event);
        verify(view,
               atLeast(2)).asWidget();
        verify(viewWidget).fireEvent(event);
    }

    @Test
    public void testEnableTrue() {
        testEnable(true);
    }

    @Test
    public void testEnableFalse() {
        testEnable(false);
    }

    private void testEnable(boolean enable) {
        integerBox.setEnabled(enable);
        verify(view).setEnabled(enable);
    }

    @Test
    public void testSetPlaceholder() {
        String placeholder = "Random placeholder";
        integerBox.setPlaceholder(placeholder);
        verify(view).setPlaceholder(eq(placeholder));
    }

    @Test
    public void testSetId() {
        String id = "field_id";
        integerBox.setId(id);
        verify(view).setId(eq(id));
    }

    @Test
    public void testSetMaxLength() {
        int maxLength = 10;
        integerBox.setMaxLength(maxLength);
        verify(view).setMaxLength(eq(maxLength));
    }
}
