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

package org.uberfire.client.views.pfly.widgets;

import org.jboss.errai.common.client.dom.KeyboardEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SanitizedNumberInputTest {

    @InjectMocks
    SanitizedNumberInput input;

    @Before
    public void setup() {
    }

    private boolean allowNegative = false;
    private boolean allowDecimal = false;

    @Test
    public void testNumericInput() {
        testValidKeyCode("9");
        testValidKeyCode("8");
        testValidKeyCode("0");
        testValidKeyCode("Backspace");

        testInvalidKeyCode("-");
        testInvalidKeyCode("+");
        testInvalidKeyCode(" ");
        testInvalidKeyCode(".");
    }

    @Test
    public void testNumericInputNegative() {
        allowNegative = true;
        allowDecimal = false;
        testValidKeyCode("-");
        testInvalidKeyCode(".");
    }

    @Test
    public void testNumericInputDecimal() {
        allowNegative = false;
        allowDecimal = true;
        testInvalidKeyCode("-");
        testValidKeyCode(".");
    }

    @Test
    public void testNumericInputNegativeDecimal() {
        allowNegative = true;
        allowDecimal = true;
        testValidKeyCode("-");
        testValidKeyCode(".");
    }

    protected void testValidKeyCode(String key) {
        testKeyCode(key,
                    0);
    }

    protected void testInvalidKeyCode(String key) {
        testKeyCode(key,
                    1);
    }

    protected void testKeyCode(String key,
                               int wantedNumberOfInvocations) {
        final KeyboardEvent event = mock(KeyboardEvent.class);
        when(event.getKey()).thenReturn(key);
        input.getEventListener(allowNegative, allowDecimal).call(event);
        verify(event,
               times(wantedNumberOfInvocations)).preventDefault();
    }
}
