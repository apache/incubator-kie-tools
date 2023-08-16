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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.tasks;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.UserTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TaskConverterPriorityTest {

    @Mock
    public TaskConverter taskConverter;

    @Mock
    public UserTaskPropertyReader userTaskPropertyReader;

    @Test
    public void testTaskConverterMvel() {
        when(userTaskPropertyReader.getPriority()).thenReturn("#{priorityVar}");
        doCallRealMethod().when(taskConverter).createUserTaskExecutionSet(any());

        final UserTaskExecutionSet userTaskExecutionSet = taskConverter.createUserTaskExecutionSet(userTaskPropertyReader);
        assertTrue(userTaskExecutionSet.getPriority().getValue().equals("#{priorityVar}"));
    }

    @Test
    public void testTaskConverterMvelSpecialChars() {
        when(userTaskPropertyReader.getPriority()).thenReturn("#{priorityVar&lt;&gt;&amp;&quot;}");
        doCallRealMethod().when(taskConverter).createUserTaskExecutionSet(any());

        final UserTaskExecutionSet userTaskExecutionSet = taskConverter.createUserTaskExecutionSet(userTaskPropertyReader);
        assertTrue(userTaskExecutionSet.getPriority().getValue().equals("#{priorityVar<>&\"}"));
    }
}