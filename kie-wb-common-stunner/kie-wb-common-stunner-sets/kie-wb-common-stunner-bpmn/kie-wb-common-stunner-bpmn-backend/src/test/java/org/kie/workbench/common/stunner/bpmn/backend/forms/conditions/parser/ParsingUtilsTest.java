/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.forms.conditions.parser;

import java.text.ParseException;
import java.util.Arrays;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParsingUtilsTest {

    @Test
    public void testParseJavaNameSuccessfulWithStopCharacters() throws Exception {
        char[] stopCharacters = {' ', '.', '('};
        String[] expectedValues = {"x", "$", "_name", "_näme", "näme", "näme1"};
        String[] inputs = {"x    blabla", "$. more things", "_name(", "_näme(other stuff", "näme.ABCD", "näme1"};
        testParseJavaNameSuccessful(expectedValues, inputs, stopCharacters);
    }

    @Test
    public void testParseJavaNameOnlyUnderscore() throws Exception {
        Assume.assumeTrue(isJavaVersionOlderThan9());
        char[] stopCharacters = {' ', '.', '('};
        String[] expectedValues = {"_", "_", "_", "_"};
        String[] inputs = {"_    blabla", "_", "_.text", "_(l"};
        testParseJavaNameSuccessful(expectedValues, inputs, stopCharacters);
    }

    private boolean isJavaVersionOlderThan9() {
        return (SystemUtils.IS_JAVA_1_8 || SystemUtils.IS_JAVA_1_7 || SystemUtils.IS_JAVA_1_6 ||
                SystemUtils.IS_JAVA_1_5 || SystemUtils.IS_JAVA_1_4 || SystemUtils.IS_JAVA_1_3 ||
                SystemUtils.IS_JAVA_1_2 || SystemUtils.IS_JAVA_1_1);
    }

    @Test
    public void testParseJavaNameSuccessfulWithoutStopCharacters() throws Exception {
        char[] stopCharacters = {};
        String[] inputs = {"x", "$", "_name", "_näme", "näme", "näme1"};
        String[] expectedValues = inputs;
        testParseJavaNameSuccessful(expectedValues, inputs, stopCharacters);
    }

    private void testParseJavaNameSuccessful(String[] expectedValues, String[] inputs, char[] stopCharacters) throws Exception {
        for (int i = 0; i < inputs.length; i++) {
            assertEquals(expectedValues[i], ParsingUtils.parseJavaName(inputs[i], 0, stopCharacters));
        }
    }

    @Test
    public void testParseJavaNameFailureWithStopCharacters() throws Exception {
        char[] stopCharacters = {' ', '.', '('};
        String[] inputs = {"1    blabla", "1. more things", "int(", "float(other stuff", "static.ABCD", "char ", "boolean.", "&and so on..."};
        testParseJavaNameFailure(inputs, stopCharacters);
    }

    @Test
    public void testParseJavaNameFailureWithoutStopCharacters() throws Exception {
        char[] stopCharacters = {};
        String[] inputs = {"1", "int", "float", "static", "char ", "boolean", "&and so on..."};
        testParseJavaNameFailure(inputs, stopCharacters);
    }

    private void testParseJavaNameFailure(String[] inputs, char[] stopCharacters) {
        Arrays.stream(inputs).forEach(input -> {
            try {
                ParsingUtils.parseJavaName(input, 0, stopCharacters);
                Assert.fail("An exception was expected for input value: " + input);
            } catch (ParseException e) {
            }
        });
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testParseJavaNameWithLowerOutOfBounds() throws ParseException {
        String someString = "a1234";
        char[] someStopCharacters = {};
        ParsingUtils.parseJavaName(someString, -1, someStopCharacters);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testParseJavaNameWithHigherOutOfBounds() throws ParseException {
        String someString = "a1234";
        char[] someStopCharacters = {};
        ParsingUtils.parseJavaName(someString, someString.length(), someStopCharacters);
    }
}
