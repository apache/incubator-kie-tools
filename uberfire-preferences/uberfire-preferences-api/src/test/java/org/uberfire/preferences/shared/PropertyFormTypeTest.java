/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.preferences.shared;

import org.junit.Test;

import static org.jgroups.util.Util.assertEquals;

public class PropertyFormTypeTest {

    @Test
    public void textTypeTest() {
        String someText = "someText";
        String stringValue = PropertyFormType.TEXT.toString(someText);
        Object realValue = PropertyFormType.TEXT.fromString(stringValue);

        assertEquals("someText",
                     stringValue);
        assertEquals(someText,
                     realValue);
    }

    @Test
    public void booleanTypeTest() {
        boolean someBoolean = true;
        String stringValue = PropertyFormType.BOOLEAN.toString(someBoolean);
        Object realValue = PropertyFormType.BOOLEAN.fromString(stringValue);

        assertEquals("true",
                     stringValue);
        assertEquals(someBoolean,
                     realValue);
    }

    @Test
    public void naturalNumberTypeTest() {
        int someNaturalNumber = 3;
        String stringValue = PropertyFormType.NATURAL_NUMBER.toString(someNaturalNumber);
        Object realValue = PropertyFormType.NATURAL_NUMBER.fromString(stringValue);

        assertEquals("3",
                     stringValue);
        assertEquals(someNaturalNumber,
                     realValue);
    }

    @Test
    public void secretTextTypeTest() {
        String someSecretText = "someSecretText";
        String stringValue = PropertyFormType.SECRET_TEXT.toString(someSecretText);
        Object realValue = PropertyFormType.SECRET_TEXT.fromString(stringValue);

        assertEquals("someSecretText",
                     stringValue);
        assertEquals(someSecretText,
                     realValue);
    }

    @Test
    public void colorTypeTest() {
        String someColor = "11FF55";
        String stringValue = PropertyFormType.COLOR.toString(someColor);
        Object realValue = PropertyFormType.COLOR.fromString(stringValue);

        assertEquals("11FF55",
                     stringValue);
        assertEquals(someColor,
                     realValue);
    }
}
