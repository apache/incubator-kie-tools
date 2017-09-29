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
 *
 */

package org.uberfire.preferences.shared.impl.validation;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConstrainedValuesValidatorTest {

    private static final Collection<String> VALUES = Arrays.asList("value1", "value2", "value3");

    private static final Function<Object, String> VALUE_PARSER = Object::toString;

    private ConstrainedValuesValidator<String> tested;

    @Before
    public void setup() {
        tested = new ConstrainedValuesValidator<>(() -> VALUES,
                                                  VALUE_PARSER);
    }

    @Test
    public void testValues() {
        assertResultSuccess(tested.validate("value1"));
        assertResultSuccess(tested.validate("value2"));
        assertResultSuccess(tested.validate("value3"));
        assertResultFailed(tested.validate("value4"));
    }

    static void assertResultSuccess(final ValidationResult result) {
        assertTrue(result.isValid());
    }

    static void assertResultFailed(final ValidationResult result) {
        assertFalse(result.isValid());
        assertEquals(1, result.getMessagesBundleKeys().size());
        assertEquals(ConstrainedValuesValidator.NOT_ALLOWED_VALIDATION_KEY, result.getMessagesBundleKeys().get(0));
    }
}
