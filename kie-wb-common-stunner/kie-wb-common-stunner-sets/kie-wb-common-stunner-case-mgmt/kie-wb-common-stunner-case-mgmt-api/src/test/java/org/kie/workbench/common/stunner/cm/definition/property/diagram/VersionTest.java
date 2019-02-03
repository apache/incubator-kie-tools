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

package org.kie.workbench.common.stunner.cm.definition.property.diagram;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VersionTest {

    private static final String VERSION_VALID = "5.0";
    private static final String VERSION_INVALID = "";
    private Validator validator;
    private Version tested;

    @Before
    public void setUp() throws Exception {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();

        tested = new Version();
        tested.setValue(VERSION_VALID);
    }

    @Test
    public void testValueValid() {
        Set<ConstraintViolation<Version>> violations = this.validator.validate(tested);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testValueInvalid() {
        tested.setValue(VERSION_INVALID);
        Set<ConstraintViolation<Version>> violations = this.validator.validate(tested);
        assertEquals(1, violations.size());
    }
}
