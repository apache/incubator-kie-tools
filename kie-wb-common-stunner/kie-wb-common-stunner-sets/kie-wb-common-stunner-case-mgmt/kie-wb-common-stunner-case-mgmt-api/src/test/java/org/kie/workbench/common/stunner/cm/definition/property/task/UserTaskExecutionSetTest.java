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

package org.kie.workbench.common.stunner.cm.definition.property.task;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.Priority;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserTaskExecutionSetTest {

    private static final String NAME_VALID = "My_New_Name";
    private static final String NAME_INVALID = "   ";
    private static final String PRIORITY_VALID = "1";
    private static final String PRIORITY_INVALID = "a";
    private Validator validator;
    private UserTaskExecutionSet tested;

    @Before
    public void setUp() throws Exception {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();

        tested = new UserTaskExecutionSet();
        tested.setTaskName(new TaskName(NAME_VALID));
        tested.setPriority(new Priority(PRIORITY_VALID));
    }

    @Test
    public void testAllValid() {
        Set<ConstraintViolation<UserTaskExecutionSet>> violations = this.validator.validate(tested);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testNameInvalid() {
        tested.setTaskName(new TaskName(NAME_INVALID));
        Set<ConstraintViolation<UserTaskExecutionSet>> violations = this.validator.validate(tested);
        assertEquals(1, violations.size());
    }

    @Test
    public void testPriorityInvalid() {
        tested.setPriority(new Priority(PRIORITY_INVALID));
        Set<ConstraintViolation<UserTaskExecutionSet>> violations = this.validator.validate(tested);
        assertEquals(1, violations.size());
    }
}
