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

package org.kie.workbench.common.stunner.bpmn.definition.property.event.signal;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SignalRefTest {

    private static final String WRONG_REF = "~`!@#$%^&*()_+=-{}|][:\"';?><,./";

    private static final String VALID_REF = "validSIGNALREF0123456789_";

    private static final String MVEL_EXPRESSION = "#{validSIGNALREF0123456789_mvel}";

    private static final String MVEL_COMPLEX_OBJECT_EXPRESSION = "#{myObj.property}";

    private static final String MVEL_INVALID_EXPRESSION = "#{.expression}";

    private static final String MVEL_SYSTEM_PROPERTY = "#{System.getProperty(\"search.value\", \"default.value\")}";

    private Validator validator;

    @Before
    public void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testSignalRefWithEmptySignal() {
        SignalRef signalRef = new SignalRef();

        Set<ConstraintViolation<SignalRef>> validations = validator.validate(signalRef);

        assertTrue(validations.isEmpty());
    }

    @Test
    public void testSignalRefWithMvelExpression() {
        SignalRef signalRef = new SignalRef(MVEL_EXPRESSION);

        Set<ConstraintViolation<SignalRef>> validations = validator.validate(signalRef);

        assertTrue(validations.isEmpty());
    }

    @Test
    public void testSignalRefWithMvelComplexObjectExpression() {
        SignalRef signalRef = new SignalRef(MVEL_COMPLEX_OBJECT_EXPRESSION);

        Set<ConstraintViolation<SignalRef>> validations = validator.validate(signalRef);

        assertTrue(validations.isEmpty());
    }

    @Test
    public void testSignalRefWithMvelSystemProperty() {
        SignalRef signalRef = new SignalRef(MVEL_SYSTEM_PROPERTY);

        Set<ConstraintViolation<SignalRef>> validations = validator.validate(signalRef);

        assertTrue(validations.isEmpty());
    }

    @Test
    public void testSignalRefWithMvelInvalidExpression() {
        SignalRef signalRef = new SignalRef(MVEL_INVALID_EXPRESSION);

        Set<ConstraintViolation<SignalRef>> validations = validator.validate(signalRef);

        assertFalse(validations.isEmpty());
    }

    @Test
    public void testSignalRefWithValidSignal() {
        SignalRef signalRef = new SignalRef(VALID_REF);

        Set<ConstraintViolation<SignalRef>> validations = validator.validate(signalRef);

        assertTrue(validations.isEmpty());
    }

    @Test
    public void testSignalRefWithWrongSignal() {
        SignalRef signalRef = new SignalRef(WRONG_REF);

        Set<ConstraintViolation<SignalRef>> validations = validator.validate(signalRef);

        assertFalse(validations.isEmpty());
    }
}