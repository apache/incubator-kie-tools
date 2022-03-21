/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserTaskTest {

    private Validator validator;

    private static final String TASK_NAME_VALID = "MyTask_123";
    private static final String TASK_NAME_INVALID = "My Task 123 ()!*@&";

    @Before
    public void init() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    @Test
    public void testTaskNameValid() {
        TaskName taskName = new TaskName(TASK_NAME_VALID);
        Set<ConstraintViolation<TaskName>> violations = this.validator.validate(taskName);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testTaskNameInvalid1() {
        TaskName taskName = new TaskName(TASK_NAME_INVALID);
        Set<ConstraintViolation<TaskName>> violations = this.validator.validate(taskName);
        assertEquals(1,
                     violations.size());
    }

    @Test
    public void testTaskNameInvalidEmpty() {
        TaskName taskName = new TaskName("");
        Set<ConstraintViolation<TaskName>> violations = this.validator.validate(taskName);
        assertEquals(2,
                     violations.size());
    }

    @Test
    public void testTaskNameInvalidNull() {
        TaskName taskName = new TaskName(null);
        Set<ConstraintViolation<TaskName>> violations = this.validator.validate(taskName);
        assertEquals(2,
                     violations.size());
    }

    @Test
    public void testUserTaskExecutionSetTaskNameValid() {
        UserTaskExecutionSet userTaskExecutionSet = new UserTaskExecutionSet();
        userTaskExecutionSet.setTaskName(new TaskName(TASK_NAME_VALID));
        Set<ConstraintViolation<UserTaskExecutionSet>> violations = this.validator.validate(userTaskExecutionSet);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testUserTaskExecutionSetTaskNameInvalid() {
        UserTaskExecutionSet userTaskExecutionSet = new UserTaskExecutionSet();
        userTaskExecutionSet.setTaskName(new TaskName(TASK_NAME_INVALID));
        Set<ConstraintViolation<UserTaskExecutionSet>> violations = this.validator.validate(userTaskExecutionSet);
        assertEquals(1,
                     violations.size());
    }

    @Test
    public void testUserTaskTaskNameValid() {
        UserTask userTask = new UserTask();
        userTask.getExecutionSet().setTaskName(new TaskName(TASK_NAME_VALID));
        Set<ConstraintViolation<UserTask>> violations = this.validator.validate(userTask);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testUserTaskTaskNameInvalid() {
        UserTask userTask = new UserTask();
        userTask.getExecutionSet().setTaskName(new TaskName(TASK_NAME_INVALID));
        Set<ConstraintViolation<UserTask>> violations = this.validator.validate(userTask);
        assertEquals(1,
                     violations.size());
    }

    @Test
    public void testUserTaskNameValid() {
        UserTask userTask = new UserTask();
        userTask.getGeneral().setName(new Name(TASK_NAME_VALID));
        Set<ConstraintViolation<UserTask>> violations = this.validator.validate(userTask);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testUserTaskNameEmpty() {
        UserTask userTask = new UserTask();
        userTask.getGeneral().setName(new Name(""));
        Set<ConstraintViolation<UserTask>> violations = this.validator.validate(userTask);
        ConstraintViolation<UserTask> violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().toString(), "general.name.value");
        assertEquals(violation.getMessage(), "may not be empty");
    }
}
