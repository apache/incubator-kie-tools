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


package org.kie.workbench.common.stunner.bpmn.forms.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import com.google.gwt.junit.client.GWTTestCase;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentValue;
import org.kie.workbench.common.stunner.bpmn.forms.validation.reassignment.ReassignmentValueValidator;

public class ReassignmentValueValidatorTest extends GWTTestCase {

    private ReassignmentValueValidator validator;

    private ConstraintValidatorContext context;

    private List<String> errorMessages = new ArrayList<>();

    @Override
    public String getModuleName() {
        return "org.kie.workbench.common.stunner.bpmn.forms.validation.ReassignmentValueValidatorTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        validator = new ReassignmentValueValidator();
        context = new ConstraintValidatorContext() {
            @Override
            public void disableDefaultConstraintViolation() {
            }

            @Override
            public String getDefaultConstraintMessageTemplate() {
                return null;
            }

            @Override
            public ConstraintViolationBuilder buildConstraintViolationWithTemplate(String message) {
                errorMessages.add(message);
                return new ConstraintViolationBuilder() {
                    @Override
                    public NodeBuilderDefinedContext addNode(String name) {
                        return null;
                    }

                    @Override
                    public ConstraintValidatorContext addConstraintViolation() {
                        return context;
                    }
                };
            }
        };
    }

    @Test
    public void testEmptyReassignmentValue() {
        boolean result = validator.isValid(new ReassignmentValue(), context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testNegativeDurationReassignmentValue() {
        ReassignmentValue value = new ReassignmentValue();
        value.setDuration("-1d");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertFalse(errorMessages.isEmpty());
    }

    @Test
    public void testIsTooBigDurationReassignmentValue() {
        ReassignmentValue value = new ReassignmentValue();
        value.setDuration("1111111111111111111111111111111111111111111d");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertFalse(errorMessages.isEmpty());
    }

    @Test
    public void test1DigExpiresAtReassignmentValue() {
        ReassignmentValue value = new ReassignmentValue();
        value.setDuration("1d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test2DigExpiresAtReassignmentValue() {
        ReassignmentValue value = new ReassignmentValue();
        value.setDuration("11d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test3DigExpiresAtReassignmentValue() {
        ReassignmentValue value = new ReassignmentValue();
        value.setDuration("111d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test4DigExpiresAtReassignmentValue() {
        ReassignmentValue value = new ReassignmentValue();
        value.setDuration("1111d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
    }

    @Test
    public void test5DigExpiresAtReassignmentValue() {
        ReassignmentValue value = new ReassignmentValue();
        value.setDuration("11111d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test10DigExpiresAtReassignmentValue() {
        ReassignmentValue value = new ReassignmentValue();
        value.setDuration("1111111111d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
    }

    @Test
    public void testIntMaxExpiresAtReassignmentValue() {
        ReassignmentValue value = new ReassignmentValue();
        value.setDuration("2147483647d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testIntOverflowExpiresAtReassignmentValue() {
        ReassignmentValue value = new ReassignmentValue();
        value.setDuration("2147483648d");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertFalse(errorMessages.isEmpty());
    }
}

