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
package org.uberfire.ext.widgets.common.client.common;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(GwtMockitoTestRunner.class)
public class BooleanTextBoxTest {

    @Test
    public void emptyValues() {
        assertTrue(new BooleanTextBox(true).isValidValue("", false));
        assertFalse(new BooleanTextBox(false).isValidValue("", false));
    }

    @Test
    public void focusLost() {
        assertFalse(new BooleanTextBox(true).isValidValue("-", true));
        assertFalse(new BooleanTextBox(false).isValidValue("-", true));
        assertTrue(new BooleanTextBox(false).isValidValue("-", false));
        assertTrue(new BooleanTextBox(true).isValidValue("-", false));
    }

    @Test
    public void values() {
        assertFalse(new BooleanTextBox(true).isValidValue("blaa", false));
        assertTrue(new BooleanTextBox(false).isValidValue("true", false));
        assertTrue(new BooleanTextBox(false).isValidValue("false", false));
        assertTrue(new BooleanTextBox(false).isValidValue("TRUE", false));
        assertTrue(new BooleanTextBox(false).isValidValue("FALSE", false));
    }
}