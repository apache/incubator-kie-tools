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


package org.kie.workbench.common.stunner.bpmn.client.shape.view.handler;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceExecutionMode;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;

import static org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.TaskViewHandler.MULTIPLE_INSTANCE_ICON_PARALLEL;
import static org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.TaskViewHandler.MULTIPLE_INSTANCE_ICON_SEQUENTIAL;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskViewHandlerTest extends AbstractBaseViewHandlerTest<BaseTask, TaskViewHandler> {

    private SVGPrimitive iconParallel;

    private SVGPrimitive iconSequential;

    @Override
    public void setUp() {
        super.setUp();
        iconParallel = mockSVGPrimitive(MULTIPLE_INSTANCE_ICON_PARALLEL);
        iconSequential = mockSVGPrimitive(MULTIPLE_INSTANCE_ICON_SEQUENTIAL);
        shapeChildren.add(iconParallel);
        shapeChildren.add(iconSequential);
    }

    @Override
    protected TaskViewHandler createViewHandler() {
        return new TaskViewHandler();
    }

    @Test
    public void testHandleMultipleInstanceSequential() {
        prepareTest(true, true);
        verifyFillAndStroke(iconParallel, 1, 0, 0);
        verifyFillAndStroke(iconParallel, 0, 1, 1);
        verifyFillAndStroke(iconSequential, 1, 0, 0);
        verifyFillAndStroke(iconSequential, 1, 1, 1);
    }

    @Test
    public void testHandleMultipleInstanceParallel() {
        prepareTest(true, false);
        verifyFillAndStroke(iconParallel, 1, 0, 0);
        verifyFillAndStroke(iconParallel, 1, 1, 1);
        verifyFillAndStroke(iconSequential, 1, 0, 0);
        verifyFillAndStroke(iconSequential, 0, 1, 1);
    }

    @Test
    public void testHandleNonMultipleInstance() {
        prepareTest(false, false);
        verifyFillAndStroke(iconParallel, 1, 0, 0);
        verifyFillAndStroke(iconParallel, 0, 1, 1);
        verifyFillAndStroke(iconSequential, 1, 0, 0);
        verifyFillAndStroke(iconSequential, 0, 1, 1);
    }

    private void prepareTest(boolean multipleInstance, boolean sequential) {
        UserTask userTask = mock(UserTask.class);
        UserTaskExecutionSet executionSet = mock(UserTaskExecutionSet.class);
        IsMultipleInstance isMultipleInstance = new IsMultipleInstance(multipleInstance);
        MultipleInstanceExecutionMode executionMode = new MultipleInstanceExecutionMode(sequential);
        when(executionSet.getIsMultipleInstance()).thenReturn(isMultipleInstance);
        when(executionSet.getMultipleInstanceExecutionMode()).thenReturn(executionMode);
        when(userTask.getExecutionSet()).thenReturn(executionSet);

        viewHandler.handle(userTask, svgShapeView);
    }
}
