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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.HashSet;
import java.util.List;

import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.Task;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class ActivityPropertyWriterTest {

    @Test
    public void testEmptyInputOutputSet() {
        Task task = bpmn2.createTask();
        ActivityPropertyWriter activityPropertyWriter =
                new ActivityPropertyWriter(task, new FlatVariableScope(), new HashSet<>());
        activityPropertyWriter.setAssignmentsInfo(new AssignmentsInfo(
                "||||"
        ));
        assertNull(task.getIoSpecification());
    }

    @Test
    public void testNotEmptyInputSet() {
        Task task = bpmn2.createTask();
        ActivityPropertyWriter activityPropertyWriter =
                new ActivityPropertyWriter(task, new FlatVariableScope(), new HashSet<>());
        activityPropertyWriter.setAssignmentsInfo(new AssignmentsInfo(
                "|A:String|||"
        ));
        assertNotNull(task.getIoSpecification());
        assertTrue(task.getIoSpecification().getOutputSets().isEmpty());
        assertEquals(1, task.getIoSpecification().getInputSets().size());
    }

    @Test
    public void testNotEmptyOutputSet() {
        Task task = bpmn2.createTask();
        ActivityPropertyWriter activityPropertyWriter =
                new ActivityPropertyWriter(task, new FlatVariableScope(), new HashSet<>());
        activityPropertyWriter.setAssignmentsInfo(new AssignmentsInfo(
                "||A:String||"
        ));
        assertNotNull(task.getIoSpecification());
        assertTrue(task.getIoSpecification().getInputSets().isEmpty());
        assertEquals(1, task.getIoSpecification().getOutputSets().size());
    }

    @Test
    public void testItShouldCreateOneInputSet() {
        Task task = bpmn2.createTask();
        ActivityPropertyWriter activityPropertyWriter =
                new ActivityPropertyWriter(task, new FlatVariableScope(), new HashSet<>());
        activityPropertyWriter.setAssignmentsInfo(new AssignmentsInfo(
                "|A:String|||"
        ));
        List<InputSet> inputSets = task.getIoSpecification().getInputSets();
        assertEquals(1, inputSets.size());
    }
}