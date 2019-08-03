/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.shape.view.handler;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.AbstractBaseViewHandlerTest;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.IsCase;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceExecutionMode;
import org.kie.workbench.common.stunner.cm.definition.CaseReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ProcessReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.property.task.CaseReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.cm.definition.property.task.ProcessReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;

import static org.kie.workbench.common.stunner.cm.client.shape.view.handler.SubprocessViewHandler.MULTIPLE_INSTANCE_SUBCASE_ICON_PARALLEL;
import static org.kie.workbench.common.stunner.cm.client.shape.view.handler.SubprocessViewHandler.MULTIPLE_INSTANCE_SUBCASE_ICON_SEQUENTIAL;
import static org.kie.workbench.common.stunner.cm.client.shape.view.handler.SubprocessViewHandler.MULTIPLE_INSTANCE_SUBPROCESS_ICON_PARALLEL;
import static org.kie.workbench.common.stunner.cm.client.shape.view.handler.SubprocessViewHandler.MULTIPLE_INSTANCE_SUBPROCESS_ICON_SEQUENTIAL;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SubprocessViewHandlerTest extends AbstractBaseViewHandlerTest<ReusableSubprocess, SubprocessViewHandler> {

    @Override
    protected SubprocessViewHandler createViewHandler() {
        return new SubprocessViewHandler();
    }

    private SVGPrimitive miSubprocessIconSequential;
    private SVGPrimitive miSubprocessIconParallel;

    private SVGPrimitive miSubcaseIconSequential;
    private SVGPrimitive miSubcaseIconParallel;

    @Override
    public void setUp() {
        super.setUp();
        miSubprocessIconSequential = mockSVGPrimitive(MULTIPLE_INSTANCE_SUBPROCESS_ICON_SEQUENTIAL);
        miSubprocessIconParallel = mockSVGPrimitive(MULTIPLE_INSTANCE_SUBPROCESS_ICON_PARALLEL);

        miSubcaseIconSequential = mockSVGPrimitive(MULTIPLE_INSTANCE_SUBCASE_ICON_SEQUENTIAL);
        miSubcaseIconParallel = mockSVGPrimitive(MULTIPLE_INSTANCE_SUBCASE_ICON_PARALLEL);

        shapeChildren.add(miSubprocessIconSequential);
        shapeChildren.add(miSubprocessIconParallel);

        shapeChildren.add(miSubcaseIconSequential);
        shapeChildren.add(miSubcaseIconParallel);
    }

    private void prepareCaseReusableSubprocessTest(boolean multipleInstance, boolean sequential) {
        CaseReusableSubprocess subProcess = mock(CaseReusableSubprocess.class);

        CaseReusableSubprocessTaskExecutionSet executionSet = mock(CaseReusableSubprocessTaskExecutionSet.class);
        IsMultipleInstance isMultipleInstance = new IsMultipleInstance(multipleInstance);
        MultipleInstanceExecutionMode executionMode = new MultipleInstanceExecutionMode(sequential);
        IsCase isCase = new IsCase(true);

        when(executionSet.getIsMultipleInstance()).thenReturn(isMultipleInstance);
        when(executionSet.getMultipleInstanceExecutionMode()).thenReturn(executionMode);
        when(executionSet.getIsCase()).thenReturn(isCase);
        when(subProcess.getExecutionSet()).thenReturn(executionSet);

        viewHandler.handle(subProcess, svgShapeView);
    }

    private void prepareProcessReusableSubprocessTest(boolean multipleInstance, boolean sequential) {
        ProcessReusableSubprocess subProcess = mock(ProcessReusableSubprocess.class);

        ProcessReusableSubprocessTaskExecutionSet executionSet = mock(ProcessReusableSubprocessTaskExecutionSet.class);
        IsMultipleInstance isMultipleInstance = new IsMultipleInstance(multipleInstance);
        MultipleInstanceExecutionMode executionMode = new MultipleInstanceExecutionMode(sequential);
        IsCase isCase = new IsCase(false);

        when(executionSet.getIsMultipleInstance()).thenReturn(isMultipleInstance);
        when(executionSet.getMultipleInstanceExecutionMode()).thenReturn(executionMode);
        when(executionSet.getIsCase()).thenReturn(isCase);
        when(subProcess.getExecutionSet()).thenReturn(executionSet);

        viewHandler.handle(subProcess, svgShapeView);
    }

    @Test
    public void testProcessReusableSubprocessMultipleInstanceSequential() {
        prepareProcessReusableSubprocessTest(true, true);

        verifyFillAndStroke(miSubprocessIconSequential, 1, 0, 0);
        verifyFillAndStroke(miSubprocessIconSequential, 1, 1, 1);
        verifyFillAndStroke(miSubprocessIconParallel, 1, 0, 0);
        verifyFillAndStroke(miSubprocessIconParallel, 0, 1, 1);

        verifyFillAndStroke(miSubcaseIconSequential, 0, 0, 0);
        verifyFillAndStroke(miSubcaseIconSequential, 0, 1, 1);
        verifyFillAndStroke(miSubcaseIconParallel, 0, 0, 0);
        verifyFillAndStroke(miSubcaseIconParallel, 0, 1, 1);
    }

    @Test
    public void testProcessReusableSubprocessMultipleInstanceParallel() {
        prepareProcessReusableSubprocessTest(true, false);

        verifyFillAndStroke(miSubprocessIconSequential, 1, 0, 0);
        verifyFillAndStroke(miSubprocessIconSequential, 0, 1, 1);
        verifyFillAndStroke(miSubprocessIconParallel, 1, 0, 0);
        verifyFillAndStroke(miSubprocessIconParallel, 1, 1, 1);

        verifyFillAndStroke(miSubcaseIconSequential, 0, 0, 0);
        verifyFillAndStroke(miSubcaseIconSequential, 0, 1, 1);
        verifyFillAndStroke(miSubcaseIconParallel, 0, 0, 0);
        verifyFillAndStroke(miSubcaseIconParallel, 0, 1, 1);
    }

    @Test
    public void testProcessReusableSubprocessNonMultipleInstance() {
        prepareProcessReusableSubprocessTest(false, false);

        verifyFillAndStroke(miSubprocessIconSequential, 1, 0, 0);
        verifyFillAndStroke(miSubprocessIconSequential, 0, 1, 1);
        verifyFillAndStroke(miSubprocessIconParallel, 1, 0, 0);
        verifyFillAndStroke(miSubprocessIconParallel, 0, 1, 1);

        verifyFillAndStroke(miSubcaseIconSequential, 0, 0, 0);
        verifyFillAndStroke(miSubcaseIconSequential, 0, 1, 1);
        verifyFillAndStroke(miSubcaseIconParallel, 0, 0, 0);
        verifyFillAndStroke(miSubcaseIconParallel, 0, 1, 1);
    }

    @Test
    public void testCaseReusableSubprocessMultipleInstanceSequential() {
        prepareCaseReusableSubprocessTest(true, true);

        verifyFillAndStroke(miSubprocessIconParallel, 0, 0, 0);
        verifyFillAndStroke(miSubprocessIconParallel, 0, 1, 1);
        verifyFillAndStroke(miSubprocessIconSequential, 0, 0, 0);
        verifyFillAndStroke(miSubprocessIconSequential, 0, 1, 1);

        verifyFillAndStroke(miSubcaseIconSequential, 1, 0, 0);
        verifyFillAndStroke(miSubcaseIconSequential, 1, 1, 1);
        verifyFillAndStroke(miSubcaseIconParallel, 1, 0, 0);
        verifyFillAndStroke(miSubcaseIconParallel, 0, 1, 1);
    }

    @Test
    public void testCaseReusableSubprocessMultipleInstanceParallel() {
        prepareCaseReusableSubprocessTest(true, false);

        verifyFillAndStroke(miSubprocessIconSequential, 0, 0, 0);
        verifyFillAndStroke(miSubprocessIconSequential, 0, 1, 1);
        verifyFillAndStroke(miSubprocessIconParallel, 0, 0, 0);
        verifyFillAndStroke(miSubprocessIconParallel, 0, 1, 1);

        verifyFillAndStroke(miSubcaseIconSequential, 1, 0, 0);
        verifyFillAndStroke(miSubcaseIconSequential, 0, 1, 1);
        verifyFillAndStroke(miSubcaseIconParallel, 1, 0, 0);
        verifyFillAndStroke(miSubcaseIconParallel, 1, 1, 1);
    }

    @Test
    public void testCaseReusableSubprocessNonMultipleInstance() {
        prepareCaseReusableSubprocessTest(false, false);

        verifyFillAndStroke(miSubprocessIconSequential, 0, 0, 0);
        verifyFillAndStroke(miSubprocessIconSequential, 0, 1, 1);
        verifyFillAndStroke(miSubprocessIconParallel, 0, 0, 0);
        verifyFillAndStroke(miSubprocessIconParallel, 0, 1, 1);

        verifyFillAndStroke(miSubcaseIconSequential, 1, 0, 0);
        verifyFillAndStroke(miSubcaseIconSequential, 0, 1, 1);
        verifyFillAndStroke(miSubcaseIconParallel, 1, 0, 0);
        verifyFillAndStroke(miSubcaseIconParallel, 0, 1, 1);
    }
}