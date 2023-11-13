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


package org.kie.workbench.common.stunner.bpmn.definition.property.connectors;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SequenceFlowPriorityTest {

    private Validator validator;

    private static final String[] PRIORITY_VALID = {"1", "5", "12"};
    private static final String[] PRIORITY_INVALID = {"0", "abc", "1.7", "01"};

    @Before
    public void init() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    @Test
    public void testPriorityValid() {
        for (String test : PRIORITY_VALID) {
            Priority priority = new Priority(test);
            Set<ConstraintViolation<Priority>> violations = this.validator.validate(priority);
            assertTrue(violations.isEmpty());
        }
    }

    @Test
    public void testPriorityInvalid() {
        for (String test : PRIORITY_INVALID) {
            Priority priority = new Priority(test);
            Set<ConstraintViolation<Priority>> violations = this.validator.validate(priority);
            assertEquals(1,
                         violations.size());
        }
    }

    @Test
    public void testPriorityValidEmpty() {
        Priority priority = new Priority("");
        Set<ConstraintViolation<Priority>> violations = this.validator.validate(priority);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testPriorityValidNull() {
        Priority priority = new Priority(null);
        Set<ConstraintViolation<Priority>> violations = this.validator.validate(priority);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testSequenceFlowExecutionSetPriorityValid() {
        for (String test : PRIORITY_VALID) {
            SequenceFlowExecutionSet sequenceFlowExecutionSet = new SequenceFlowExecutionSet();
            sequenceFlowExecutionSet.setPriority(new Priority(test));
            Set<ConstraintViolation<SequenceFlowExecutionSet>> violations = this.validator.validate(sequenceFlowExecutionSet);
            assertTrue(violations.isEmpty());
        }
    }

    @Test
    public void testSequenceFlowExecutionSetPriorityEmpty() {
        SequenceFlowExecutionSet sequenceFlowExecutionSet = new SequenceFlowExecutionSet();
        sequenceFlowExecutionSet.setPriority(new Priority(""));
        Set<ConstraintViolation<SequenceFlowExecutionSet>> violations = this.validator.validate(sequenceFlowExecutionSet);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testSequenceFlowExecutionSetPriorityInvalid() {
        for (String test : PRIORITY_INVALID) {
            SequenceFlowExecutionSet sequenceFlowExecutionSet = new SequenceFlowExecutionSet();
            sequenceFlowExecutionSet.setPriority(new Priority(test));
            Set<ConstraintViolation<SequenceFlowExecutionSet>> violations = this.validator.validate(sequenceFlowExecutionSet);
            assertEquals(1,
                         violations.size());
        }
    }

    @Test
    public void testSequenceFlowPriorityValid() {
        for (String test : PRIORITY_VALID) {
            SequenceFlow sequenceFlow = new SequenceFlow();
            sequenceFlow.getExecutionSet().setPriority(new Priority(test));
            Set<ConstraintViolation<SequenceFlow>> violations = this.validator.validate(sequenceFlow);
            assertTrue(violations.isEmpty());
        }
    }

    @Test
    public void testSequenceFlowPriorityInvalid() {
        for (String test : PRIORITY_INVALID) {
            SequenceFlow sequenceFlow = new SequenceFlow();
            sequenceFlow.getExecutionSet().setPriority(new Priority(test));
            Set<ConstraintViolation<SequenceFlow>> violations = this.validator.validate(sequenceFlow);
            assertEquals(1,
                         violations.size());
        }
    }
}
