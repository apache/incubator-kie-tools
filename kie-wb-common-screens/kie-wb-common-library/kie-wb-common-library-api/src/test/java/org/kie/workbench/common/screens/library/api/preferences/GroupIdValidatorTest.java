/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.api.preferences;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.preferences.shared.impl.validation.ValidationResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GroupIdValidatorTest {

    private GroupIdValidator groupIdValidator;

    @Before
    public void setUp() throws Exception {
        groupIdValidator = new GroupIdValidator();
    }

    @Test
    public void validGroupIdTest() {
        ValidationResult validationResult = groupIdValidator.validate("test");
        assertTrue(validationResult.isValid());
        assertEquals(0,
                     validationResult.getMessagesBundleKeys().size());
    }

    @Test
    public void invalidGroupIdTest() {
        ValidationResult validationResult = groupIdValidator.validate("test##");
        assertFalse(validationResult.isValid());
        assertEquals(1,
                     validationResult.getMessagesBundleKeys().size());
        assertEquals("PropertyValidator.ConstrainedValuesValidator.NotAllowed",
                     validationResult.getMessagesBundleKeys().get(0));
    }
}