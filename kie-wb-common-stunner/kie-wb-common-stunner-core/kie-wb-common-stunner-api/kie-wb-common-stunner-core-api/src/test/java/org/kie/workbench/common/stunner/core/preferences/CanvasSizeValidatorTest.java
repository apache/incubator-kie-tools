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

package org.kie.workbench.common.stunner.core.preferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.preferences.shared.impl.validation.ValidationResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CanvasSizeValidatorTest {

    private static int MIN = 10;
    private static int MAX = 20;

    private CanvasSizeValidator validator;

    @Before
    public void setUp() {
        validator = new CanvasSizeValidator(MIN,
                                            MAX) {
        };
    }

    @Test
    public void testValidate() {
        assertIsInvalid(validator.validate(null));
        assertIsInvalid(validator.validate("not a number"));
        assertIsInvalid(validator.validate(Integer.toString(MIN - 1)));
        for (int i = MIN; i <= MAX; i++) {
            assertIsValid(validator.validate(Integer.toString(i)));
        }
        assertIsInvalid(validator.validate(Integer.toString(MAX + 1)));
    }

    private void assertIsValid(ValidationResult result) {
        assertTrue(result.isValid());
    }

    private void assertIsInvalid(ValidationResult result) {
        assertFalse(result.isValid());
        assertEquals(1,
                     result.getMessagesBundleKeys().size());
        assertEquals("PropertyValidator.CanvasSizeValidator.InvalidOutOfRange",
                     result.getMessagesBundleKeys().get(0));
    }
}
