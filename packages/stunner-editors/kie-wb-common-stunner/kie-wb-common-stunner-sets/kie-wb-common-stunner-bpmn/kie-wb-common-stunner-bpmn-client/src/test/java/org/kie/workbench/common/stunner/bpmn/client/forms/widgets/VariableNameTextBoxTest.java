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


package org.kie.workbench.common.stunner.bpmn.client.forms.widgets;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwtmockito.GwtMockito;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class VariableNameTextBoxTest {

    private static final String ERROR_MESSAGE = "some error";
    private static final String ERROR_REMOVED = "some error reg exp";
    private static final String ERROR_TYPED = "some error reg exp2";
    private static final Set<String> INVALID_VALUES = new HashSet<String>();

    @Captor
    private ArgumentCaptor<BlurHandler> blurCaptor;

    @Captor
    ArgumentCaptor<KeyPressHandler> keyPressCaptor;

    @Mock
    private BlurEvent blurEvent;

    @Mock
    private KeyPressEvent keyPressEvent;

    private boolean caseSensitive;

    private VariableNameTextBox textBox;

    @Parameterized.Parameters
    public static Collection<Boolean[]> caseSensitivity() {
        return Arrays.asList(new Boolean[][]{{true}, {false}});
    }

    public VariableNameTextBoxTest(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    @Before
    public void init() {
        GwtMockito.initMocks(this);
        textBox = GWT.create(VariableNameTextBox.class);
        doCallRealMethod().when(textBox).setRegExp(anyString(),
                                                   anyString(),
                                                   anyString());
        doCallRealMethod().when(textBox).setInvalidValues(anySet(),
                                                          anyBoolean(),
                                                          anyString());
        doCallRealMethod().when(textBox).isValidValue(anyString(),
                                                      anyBoolean());
        doCallRealMethod().when(textBox).setText(anyString());
        doCallRealMethod().when(textBox).testForInvalidValue(anyString());
        doCallRealMethod().when(textBox).makeValidValue(anyString());
        doCallRealMethod().when(textBox).getInvalidCharsInName(anyString());
        doCallRealMethod().when(textBox).isValidChar(anyChar());
        doCallRealMethod().when(textBox).setup();
        doCallRealMethod().when(textBox).addBlurHandler(any(BlurHandler.class));
        doCallRealMethod().when(textBox).addKeyPressHandler(any(KeyPressHandler.class));
        textBox.setRegExp(StringUtils.ALPHA_NUM_REGEXP,
                          ERROR_REMOVED,
                          ERROR_TYPED);
        INVALID_VALUES.clear();
        INVALID_VALUES.add("abc");
        INVALID_VALUES.add("CdE");
        INVALID_VALUES.add("a#$%1");
        INVALID_VALUES.add("a.");
        textBox.setInvalidValues(INVALID_VALUES,
                                 caseSensitive,
                                 ERROR_MESSAGE);
    }

    @Test
    public void testSetup() {
        when(textBox.getKeyCodeFromKeyPressEvent(any(KeyPressEvent.class))).thenReturn(64);
        when(keyPressEvent.isControlKeyDown()).thenReturn(false);
        when(keyPressEvent.isShiftKeyDown()).thenReturn(true);
        when(keyPressEvent.getCharCode()).thenReturn('@');
        when(textBox.getCursorPos()).thenReturn(4);
        when(textBox.getSelectionLength()).thenReturn(0);
        when(textBox.getValue()).thenReturn("ab12");
        when(textBox.getText()).thenReturn("ab12@");
        textBox.setup();
        verify(textBox,
               times(1)).addBlurHandler(blurCaptor.capture());
        verify(textBox,
               times(1)).addKeyPressHandler(keyPressCaptor.capture());
        BlurHandler blurHandler = blurCaptor.getValue();
        blurHandler.onBlur(blurEvent);
        verify(textBox,
               times(1)).isValidValue("ab12@",
                                      true);
        verify(textBox,
               times(1)).makeValidValue("ab12@");
        verify(textBox,
               times(1)).setValue("ab12");
        KeyPressHandler keyPressHandler = keyPressCaptor.getValue();
        keyPressHandler.onKeyPress(keyPressEvent);
        verify(keyPressEvent,
               times(1)).preventDefault();
        verify(textBox,
               times(1)).isValidValue("ab12@",
                                      false);
        verify(textBox,
               times(1)).fireValidationError(ERROR_REMOVED + ": @");
        verify(textBox,
               times(1)).fireValidationError(ERROR_TYPED + ": @");
    }

    @Test
    public void testMakeValid() {
        String makeValidResult;
        makeValidResult = textBox.makeValidValue(null);
        assertEquals("",
                     makeValidResult);
        makeValidResult = textBox.makeValidValue("");
        assertEquals("",
                     makeValidResult);
        makeValidResult = textBox.makeValidValue("aBc");
        if (caseSensitive) {
            assertEquals("aBc",
                         makeValidResult);
        } else {
            assertEquals("",
                         makeValidResult);
        }
        makeValidResult = textBox.makeValidValue("CdE");
        assertEquals("",
                     makeValidResult);
        makeValidResult = textBox.makeValidValue("c");
        assertEquals("c",
                     makeValidResult);
        makeValidResult = textBox.makeValidValue("a#b$2%1");
        assertEquals("ab21",
                     makeValidResult);
        makeValidResult = textBox.makeValidValue("a#b$2%1.3-4_5");
        assertEquals("ab213-4_5",
                     makeValidResult);
    }

    @Test
    public void testIsValidValue() {
        String isValidResult;
        isValidResult = textBox.isValidValue("a",
                                             true);
        assertEquals(null,
                     isValidResult);
        isValidResult = textBox.isValidValue("a",
                                             false);
        assertEquals(null,
                     isValidResult);
        isValidResult = textBox.isValidValue("-",
                                             true);
        assertEquals(null,
                     isValidResult);
        isValidResult = textBox.isValidValue("-",
                                             false);
        assertEquals(null,
                     isValidResult);
        isValidResult = textBox.isValidValue("_",
                                             true);
        assertEquals(null,
                     isValidResult);
        isValidResult = textBox.isValidValue("_",
                                             false);
        assertEquals(null,
                     isValidResult);
        isValidResult = textBox.isValidValue("aBc",
                                             true);
        if (caseSensitive) {
            assertEquals(null,
                         isValidResult);
        } else {
            assertEquals(ERROR_MESSAGE,
                         isValidResult);
        }
        isValidResult = textBox.isValidValue("aBc",
                                             false);
        assertEquals(null,
                     isValidResult);
        isValidResult = textBox.isValidValue("CdE",
                                             true);
        assertEquals(ERROR_MESSAGE,
                     isValidResult);
        isValidResult = textBox.isValidValue("CdE",
                                             false);
        assertEquals(null,
                     isValidResult);
        isValidResult = textBox.isValidValue("a#$%1",
                                             true);
        assertEquals(ERROR_MESSAGE,
                     isValidResult);
        isValidResult = textBox.isValidValue("a#$%1",
                                             false);
        assertEquals(ERROR_TYPED + ": #$%",
                     isValidResult);

        isValidResult = textBox.isValidValue("a.",
                                             true);
        assertEquals(ERROR_MESSAGE,
                     isValidResult);

        isValidResult = textBox.isValidValue("a.",
                                             false);
        assertEquals(ERROR_TYPED + ": .",
                     isValidResult);
    }

    @Test
    public void testKeyEnter() {
        when(textBox.getKeyCodeFromKeyPressEvent(any(KeyPressEvent.class))).thenReturn(KeyCodes.KEY_ENTER);
        when(keyPressEvent.isControlKeyDown()).thenReturn(false);
        when(keyPressEvent.isShiftKeyDown()).thenReturn(false);
        when(keyPressEvent.getCharCode()).thenReturn((char) 13);
        when(textBox.getCursorPos()).thenReturn(4);
        when(textBox.getSelectionLength()).thenReturn(0);
        when(textBox.getValue()).thenReturn("ab12");
        when(textBox.getText()).thenReturn("ab12" + (char) 13);
        textBox.setup();
        verify(textBox,
               times(1)).addBlurHandler(blurCaptor.capture());
        verify(textBox,
               times(1)).addKeyPressHandler(keyPressCaptor.capture());
        BlurHandler blurHandler = blurCaptor.getValue();
        blurHandler.onBlur(blurEvent);
        verify(textBox,
               times(1)).isValidValue("ab12" + (char) 13,
                                      true);
        verify(textBox,
               times(1)).makeValidValue("ab12" + (char) 13);
        verify(textBox,
               times(1)).setValue("ab12");
        KeyPressHandler keyPressHandler = keyPressCaptor.getValue();
        keyPressHandler.onKeyPress(keyPressEvent);
    }
}
