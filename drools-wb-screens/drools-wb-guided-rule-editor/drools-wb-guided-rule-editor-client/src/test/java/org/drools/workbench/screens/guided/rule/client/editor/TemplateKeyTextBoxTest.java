/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(GwtMockitoTestRunner.class)
public class TemplateKeyTextBoxTest {

    private TemplateKeyTextBox textBox;

    @Before
    public void setup() {
        textBox = new TemplateKeyTextBox();
    }

    @Test
    public void checkValidationForLostFocus() {
        assertions(Assertion.trueForOnLostFocus("$1"),
                   Assertion.trueForOnLostFocus("$a"),
                   Assertion.trueForOnLostFocus("a"),
                   Assertion.falseForOnLostFocus("$"),
                   Assertion.falseForOnLostFocus("1"));
    }

    @Test
    public void checkValidationForKeyboardEntry() {
        assertions(Assertion.trueForKeyboardEntry("$"),
                   Assertion.trueForKeyboardEntry("$1"),
                   Assertion.trueForKeyboardEntry("$a"),
                   Assertion.trueForKeyboardEntry("a"),
                   Assertion.falseForKeyboardEntry("1"));
    }

    private void assertions(final Assertion... assertions) {
        final List<Assertion> tests = Arrays.asList(assertions);
        tests.stream().filter(a -> a.assertion).forEach(a -> assertTrue(textBox.isValidValue(a.value,
                                                                                             a.isOnFocusLost)));
        tests.stream().filter(a -> !a.assertion).forEach(a -> assertFalse(textBox.isValidValue(a.value,
                                                                                               a.isOnFocusLost)));
    }

    private static class Assertion {

        String value;
        boolean isOnFocusLost;
        boolean assertion;

        static Assertion trueForOnLostFocus(final String value) {
            final Assertion assertion = new Assertion();
            assertion.value = value;
            assertion.isOnFocusLost = true;
            assertion.assertion = true;
            return assertion;
        }

        static Assertion falseForOnLostFocus(final String value) {
            final Assertion assertion = new Assertion();
            assertion.value = value;
            assertion.isOnFocusLost = true;
            assertion.assertion = false;
            return assertion;
        }

        static Assertion trueForKeyboardEntry(final String value) {
            final Assertion assertion = new Assertion();
            assertion.value = value;
            assertion.isOnFocusLost = false;
            assertion.assertion = true;
            return assertion;
        }

        static Assertion falseForKeyboardEntry(final String value) {
            final Assertion assertion = new Assertion();
            assertion.value = value;
            assertion.isOnFocusLost = false;
            assertion.assertion = false;
            return assertion;
        }
    }
}
