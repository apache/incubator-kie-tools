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
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceExecutionMode;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;

import static org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.SubprocessViewHandler.MULTIPLE_INSTANCE_SUB_PROCESS_ICON_PARALLEL;
import static org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.SubprocessViewHandler.MULTIPLE_INSTANCE_SUB_PROCESS_ICON_SEQUENTIAL;
import static org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.SubprocessViewHandler.REUSABLE_SUB_PROCESS_MI_BOUNDING_BOX;
import static org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.SubprocessViewHandler.REUSABLE_SUB_PROCESS_MI_MULTIPLE_INSTANCE_ICON_PARALLEL;
import static org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.SubprocessViewHandler.REUSABLE_SUB_PROCESS_MI_MULTIPLE_INSTANCE_ICON_SEQUENTIAL;
import static org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.SubprocessViewHandler.REUSABLE_SUB_PROCESS_MI_REUSABLE_ICON;
import static org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.SubprocessViewHandler.REUSABLE_SUB_PROCESS_NORMAL_BOUNDING_BOX;
import static org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.SubprocessViewHandler.REUSABLE_SUB_PROCESS_NORMAL_REUSABLE_ICON;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SubprocessViewHandlerTest extends AbstractBaseViewHandlerTest<BaseSubprocess, SubprocessViewHandler> {

    @Override
    protected SubprocessViewHandler createViewHandler() {
        return new SubprocessViewHandler();
    }

    private SVGPrimitive reusableSubProcessNormalBoundingBox;
    private SVGPrimitive reusableSubProcessNormalReusableIcon;
    private SVGPrimitive reusableSubProcessMIBoundingBox;
    private SVGPrimitive reusableSubProcessMIReusableIcon;
    private SVGPrimitive reusableSubProcessMIIconParallel;
    private SVGPrimitive reusableSubProcessMIIconSequential;
    private SVGPrimitive miSubProcessIconSequential;
    private SVGPrimitive miSubProcessIconParallel;

    @Override
    public void setUp() {
        super.setUp();
        reusableSubProcessNormalBoundingBox = mockSVGPrimitive(REUSABLE_SUB_PROCESS_NORMAL_BOUNDING_BOX);
        reusableSubProcessNormalReusableIcon = mockSVGPrimitive(REUSABLE_SUB_PROCESS_NORMAL_REUSABLE_ICON);
        reusableSubProcessMIBoundingBox = mockSVGPrimitive(REUSABLE_SUB_PROCESS_MI_BOUNDING_BOX);
        reusableSubProcessMIReusableIcon = mockSVGPrimitive(REUSABLE_SUB_PROCESS_MI_REUSABLE_ICON);
        reusableSubProcessMIIconParallel = mockSVGPrimitive(REUSABLE_SUB_PROCESS_MI_MULTIPLE_INSTANCE_ICON_PARALLEL);
        reusableSubProcessMIIconSequential = mockSVGPrimitive(REUSABLE_SUB_PROCESS_MI_MULTIPLE_INSTANCE_ICON_SEQUENTIAL);
        miSubProcessIconParallel = mockSVGPrimitive(MULTIPLE_INSTANCE_SUB_PROCESS_ICON_PARALLEL);
        miSubProcessIconSequential = mockSVGPrimitive(MULTIPLE_INSTANCE_SUB_PROCESS_ICON_SEQUENTIAL);
        shapeChildren.add(reusableSubProcessNormalBoundingBox);
        shapeChildren.add(reusableSubProcessNormalReusableIcon);
        shapeChildren.add(reusableSubProcessMIBoundingBox);
        shapeChildren.add(reusableSubProcessMIReusableIcon);
        shapeChildren.add(reusableSubProcessMIIconParallel);
        shapeChildren.add(reusableSubProcessMIIconSequential);
        shapeChildren.add(miSubProcessIconParallel);
        shapeChildren.add(miSubProcessIconSequential);
    }

    @Test
    public void testReusableSubProcessMultipleInstanceSequential() {
        prepareReusableSubprocessTest(true, true);
        verifyReusableMICommonSettings();
        verifyFillAndStroke(reusableSubProcessMIIconParallel, 1, 0, 0);
        verifyFillAndStroke(reusableSubProcessMIIconParallel, 0, 1, 1);
        verifyFillAndStroke(reusableSubProcessMIIconSequential, 1, 0, 0);
        verifyFillAndStroke(reusableSubProcessMIIconSequential, 1, 1, 1);
    }

    @Test
    public void testReusableSubProcessMultipleInstanceParallel() {
        prepareReusableSubprocessTest(true, false);
        verifyReusableMICommonSettings();
        verifyFillAndStroke(reusableSubProcessMIIconParallel, 1, 0, 0);
        verifyFillAndStroke(reusableSubProcessMIIconParallel, 1, 1, 1);
        verifyFillAndStroke(reusableSubProcessMIIconSequential, 1, 0, 0);
        verifyFillAndStroke(reusableSubProcessMIIconSequential, 0, 1, 1);
    }

    @Test
    public void testReusableSubNonMultipleInstance() {
        prepareReusableSubprocessTest(false, false);
        verifyFillAndStroke(reusableSubProcessNormalBoundingBox, 0, 0, 0);
        verifyFillAndStroke(reusableSubProcessNormalBoundingBox, 1, 1, 1);
        verifyFillAndStroke(reusableSubProcessNormalReusableIcon, 0, 0, 0);
        verifyFillAndStroke(reusableSubProcessNormalReusableIcon, 1, 1, 1);
        verifyFillAndStroke(reusableSubProcessMIBoundingBox, 1, 0, 0);
        verifyFillAndStroke(reusableSubProcessMIBoundingBox, 0, 1, 1);
        verifyFillAndStroke(reusableSubProcessMIReusableIcon, 1, 0, 0);
        verifyFillAndStroke(reusableSubProcessMIReusableIcon, 0, 1, 1);
        verifyFillAndStroke(reusableSubProcessMIIconParallel, 1, 0, 0);
        verifyFillAndStroke(reusableSubProcessMIIconParallel, 0, 1, 1);
        verifyFillAndStroke(reusableSubProcessMIIconSequential, 1, 0, 0);
        verifyFillAndStroke(reusableSubProcessMIIconSequential, 0, 1, 1);
    }

    @Test
    public void testMISubProcessMultipleInstanceSequential() {
        prepareMISubProcessTest(true, true);
        verifyFillAndStroke(miSubProcessIconParallel, 1, 0, 0);
        verifyFillAndStroke(miSubProcessIconParallel, 0, 1, 1);
        verifyFillAndStroke(miSubProcessIconSequential, 1, 1, 1);
        verifyFillAndStroke(miSubProcessIconSequential, 0, 0, 0);
    }

    @Test
    public void testMISubProcessMultipleInstanceParallel() {
        prepareMISubProcessTest(true, false);
        verifyFillAndStroke(miSubProcessIconParallel, 1, 1, 1);
        verifyFillAndStroke(miSubProcessIconParallel, 0, 0, 0);
        verifyFillAndStroke(miSubProcessIconSequential, 0, 1, 1);
        verifyFillAndStroke(miSubProcessIconSequential, 1, 0, 0);
    }

    private void verifyReusableMICommonSettings() {
        verifyFillAndStroke(reusableSubProcessNormalBoundingBox, 1, 0, 0);
        verifyFillAndStroke(reusableSubProcessNormalBoundingBox, 0, 1, 1);
        verifyFillAndStroke(reusableSubProcessNormalReusableIcon, 1, 0, 0);
        verifyFillAndStroke(reusableSubProcessNormalReusableIcon, 0, 1, 1);
        verifyFillAndStroke(reusableSubProcessMIBoundingBox, 1, 1, 1);
        verifyFillAndStroke(reusableSubProcessMIBoundingBox, 0, 0, 0);
        verifyFillAndStroke(reusableSubProcessMIReusableIcon, 1, 1, 1);
        verifyFillAndStroke(reusableSubProcessMIReusableIcon, 0, 0, 0);
    }

    private void prepareReusableSubprocessTest(boolean multipleInstance, boolean sequential) {
        ReusableSubprocess subProcess = mock(ReusableSubprocess.class);
        ReusableSubprocessTaskExecutionSet executionSet = mock(ReusableSubprocessTaskExecutionSet.class);
        IsMultipleInstance isMultipleInstance = new IsMultipleInstance(multipleInstance);
        MultipleInstanceExecutionMode executionMode = new MultipleInstanceExecutionMode(sequential);
        when(executionSet.getIsMultipleInstance()).thenReturn(isMultipleInstance);
        when(executionSet.getMultipleInstanceExecutionMode()).thenReturn(executionMode);
        when(subProcess.getExecutionSet()).thenReturn(executionSet);
        viewHandler.handle(subProcess, svgShapeView);
    }

    private void prepareMISubProcessTest(boolean multipleInstance, boolean sequential) {
        MultipleInstanceSubprocess subProcess = mock(MultipleInstanceSubprocess.class);
        MultipleInstanceSubprocessTaskExecutionSet executionSet = mock(MultipleInstanceSubprocessTaskExecutionSet.class);
        IsMultipleInstance isMultipleInstance = new IsMultipleInstance(multipleInstance);
        MultipleInstanceExecutionMode executionMode = new MultipleInstanceExecutionMode(sequential);
        when(executionSet.getIsMultipleInstance()).thenReturn(isMultipleInstance);
        when(executionSet.getMultipleInstanceExecutionMode()).thenReturn(executionMode);
        when(subProcess.getExecutionSet()).thenReturn(executionSet);
        viewHandler.handle(subProcess, svgShapeView);
    }
}
