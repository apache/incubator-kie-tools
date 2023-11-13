/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
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

    private static final String MVEL_COMPLEX_OBJECT_EXPRESSION = "#{myObj.property}";

    private static final String MVEL_INVALID_EXPRESSION = "#{.expression}";

    private static final String MVEL_PARAM_EXPRESSION = "#{System.getProperty(\"search.value\", \"default.value\")}";

    private static final String EMPTY_REF = "";

    private static final String EXP_SEPARATOR = "-";

    private static final String VALID_STATIC_REF = "validSIGNALREF0123456789_";

    private static final String VALID_MVEL_REF_1 = "#{myObj}";
    private static final String VALID_MVEL_REF_2 = "#{m}";

    private static final String VALID_MVEL_COMPLEX_REF_1 = "#{myObj.property}";
    private static final String VALID_MVEL_COMPLEX_REF_2 = "#{m.p}";

    private static final String VALID_MVEL_COMPLEX_PARAM_REF_1 = "#{myObj.property(parameter)}";
    private static final String VALID_MVEL_COMPLEX_PARAM_REF_2 = "#{m.p(p)}";

    private static final String VALID_COMBINATION_1 = VALID_STATIC_REF + EXP_SEPARATOR + VALID_STATIC_REF;
    private static final String VALID_COMBINATION_2 = VALID_STATIC_REF + EXP_SEPARATOR + VALID_MVEL_REF_1;
    private static final String VALID_COMBINATION_3 = VALID_STATIC_REF + EXP_SEPARATOR + VALID_MVEL_COMPLEX_REF_1;
    private static final String VALID_COMBINATION_4 = VALID_STATIC_REF + EXP_SEPARATOR + VALID_MVEL_COMPLEX_PARAM_REF_1;

    private static final String VALID_EXPRESSION = "Milestone 2: Order shipped";

    private static final String INVALID_STATIC_REF = "~`!@#$%^&*()_+=-{}|][:\"';?><,./";

    private static final String INVALID_MVEL_REF = "#{~`!@#$%^&*()_+=-{}|][:\"';?><,./}";

    private static final String INVALID_MVEL_COMPLEX_REF_1 = "#{m.}";
    private static final String INVALID_MVEL_COMPLEX_REF_2 = "#{.p}";
    private static final String INVALID_MVEL_COMPLEX_REF_3 = "#{m.~`!@#$%^&*()_+=-{}|][:\"';?><,./}";
    private static final String INVALID_MVEL_COMPLEX_REF_4 = "#{m.~`!@#$%^&*()_+=-{}|][:\"';?><,./}";

    private static final String INVALID_MVEL_PARAM_COMPLEX_REF_1 = "#{myObj.property()}";
    private static final String INVALID_MVEL_PARAM_COMPLEX_REF_2 = "#{m.p()}";
    private static final String INVALID_MVEL_PARAM_COMPLEX_REF_3 = "#{m.(parameter)}";
    private static final String INVALID_MVEL_PARAM_COMPLEX_REF_4 = "#{.p(parameter)}";
    private static final String INVALID_MVEL_PARAM_COMPLEX_REF_5 = "#{.(parameter)}";
    private static final String INVALID_MVEL_PARAM_COMPLEX_REF_6 = "#{myObj~.property(parameter)}";

    private static final String INVALID_COMBINATION_1 = INVALID_STATIC_REF + VALID_STATIC_REF;
    private static final String INVALID_COMBINATION_2 = VALID_STATIC_REF + VALID_MVEL_REF_1;
    private static final String INVALID_COMBINATION_3 = VALID_STATIC_REF + VALID_MVEL_COMPLEX_REF_1;
    private static final String INVALID_COMBINATION_4 = VALID_STATIC_REF + VALID_MVEL_COMPLEX_PARAM_REF_1;

    private Validator validator;

    @Before
    public void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testSignalRef() {
        assertValidExpression(EMPTY_REF);
        assertValidExpression(VALID_STATIC_REF);
        assertValidExpression(VALID_MVEL_REF_1);
        assertValidExpression(VALID_MVEL_REF_2);
        assertValidExpression(VALID_MVEL_COMPLEX_REF_1);
        assertValidExpression(VALID_MVEL_COMPLEX_REF_2);
        assertValidExpression(VALID_MVEL_COMPLEX_PARAM_REF_1);
        assertValidExpression(VALID_MVEL_COMPLEX_PARAM_REF_2);
        assertValidExpression(VALID_COMBINATION_1);
        assertValidExpression(VALID_COMBINATION_2);
        assertValidExpression(VALID_COMBINATION_3);
        assertValidExpression(VALID_COMBINATION_4);
        assertValidExpression(VALID_EXPRESSION);

        assertInvalidExpression(INVALID_STATIC_REF);
        assertInvalidExpression(INVALID_MVEL_REF);
        assertInvalidExpression(INVALID_MVEL_COMPLEX_REF_1);
        assertInvalidExpression(INVALID_MVEL_COMPLEX_REF_2);
        assertInvalidExpression(INVALID_MVEL_COMPLEX_REF_3);
        assertInvalidExpression(INVALID_MVEL_COMPLEX_REF_4);
        assertInvalidExpression(INVALID_MVEL_PARAM_COMPLEX_REF_1);
        assertInvalidExpression(INVALID_MVEL_PARAM_COMPLEX_REF_2);
        assertInvalidExpression(INVALID_MVEL_PARAM_COMPLEX_REF_3);
        assertInvalidExpression(INVALID_MVEL_PARAM_COMPLEX_REF_4);
        assertInvalidExpression(INVALID_MVEL_PARAM_COMPLEX_REF_5);
        assertInvalidExpression(INVALID_MVEL_PARAM_COMPLEX_REF_6);
        assertInvalidExpression(INVALID_COMBINATION_1);
        assertInvalidExpression(INVALID_COMBINATION_2);
        assertInvalidExpression(INVALID_COMBINATION_3);
        assertInvalidExpression(INVALID_COMBINATION_4);
    }

    private Set<ConstraintViolation<SignalRef>> validateExpression(String expression) {
        final SignalRef signalRef = new SignalRef(expression);
        return validator.validate(signalRef);
    }

    private void assertValidExpression(String expression) {
        assertTrue(validateExpression(expression).isEmpty());
    }

    private void assertInvalidExpression(String expression) {
        assertFalse(validateExpression(expression).isEmpty());
    }
}