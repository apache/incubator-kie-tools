/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.client.widget;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

//@RunWith(GwtMockitoTestRunner.class)
public class BindingTextBoxTest {

    BindingTextBox textBox;

    @Before
    public void setUp() throws Exception {
        textBox = new BindingTextBox();
    }

    //@Test
    public void testIsValidValueWhiteSpaceStart() throws Exception {
        assertFalse(textBox.isValidValue(" abcd",
                                         false));
    }

    //@Test
    public void testIsValidValueWhiteSpaceEnd() throws Exception {
        assertFalse(textBox.isValidValue("abcd ",
                                         false));
    }

    //@Test
    public void testIsValidValueWhiteSpaceMiddle() throws Exception {
        assertFalse(textBox.isValidValue("abcd abcd",
                                         false));
    }

    //@Test
    public void testIsValidValueSpecialCharacters() throws Exception {
        assertFalse(textBox.isValidValue("abcd%!@*()&^abcd",
                                         false));
    }

    //@Test
    public void testIsValidValueStartWithNumber() throws Exception {
        assertFalse(textBox.isValidValue("1abcd ",
                                         false));
    }

    //@Test
    public void testIsValidValue() throws Exception {
        assertTrue(textBox.isValidValue("aBCd12",
                                        false));
    }

    //@Test
    public void testIsValidValueWithSpecialStart() throws Exception {
        assertTrue(textBox.isValidValue("$a33bcd",
                                        false));
    }
}
