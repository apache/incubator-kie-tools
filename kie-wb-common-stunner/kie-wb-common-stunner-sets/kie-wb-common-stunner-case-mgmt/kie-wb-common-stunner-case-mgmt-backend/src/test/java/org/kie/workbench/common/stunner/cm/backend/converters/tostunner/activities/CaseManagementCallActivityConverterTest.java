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

package org.kie.workbench.common.stunner.cm.backend.converters.tostunner.activities;

import java.util.Collections;
import java.util.UUID;

import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.CallActivityPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.cm.definition.CaseReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ProcessReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.property.task.CaseReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.cm.definition.property.task.ProcessReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.core.api.FactoryManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CaseManagementCallActivityConverterTest {

    private DefinitionResolver definitionResolver;

    private FactoryManager factoryManager;

    private CaseManagementCallActivityConverter tested;

    @Before
    public void setUp() throws Exception {
        Definitions definitions = bpmn2.createDefinitions();
        definitions.getRootElements().add(bpmn2.createProcess());
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setPlane(di.createBPMNPlane());
        definitions.getDiagrams().add(bpmnDiagram);

        definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());

        factoryManager = mock(FactoryManager.class);

        tested = new CaseManagementCallActivityConverter(new TypedFactoryManager(factoryManager),
                                                         new PropertyReaderFactory(definitionResolver));
    }

    @Test
    public void testCreateNode_case() throws Exception {
        String uuid = UUID.randomUUID().toString();

        CallActivity callActivity = bpmn2.createCallActivity();
        callActivity.setId(uuid);
        CustomElement.isCase.of(callActivity).set(Boolean.TRUE);

        CallActivityPropertyReader propertyReader = new CallActivityPropertyReader(callActivity,
                                                                                   definitionResolver.getDiagram(),
                                                                                   definitionResolver);

        tested.createNode(callActivity, propertyReader);

        verify(factoryManager).newElement(eq(uuid),
                                          eq(getDefinitionId(CaseReusableSubprocess.class)));
    }

    @Test
    public void testCreateNode_process() throws Exception {
        String uuid = UUID.randomUUID().toString();

        CallActivity callActivity = bpmn2.createCallActivity();
        callActivity.setId(uuid);
        CustomElement.isCase.of(callActivity).set(Boolean.FALSE);

        CallActivityPropertyReader propertyReader = new CallActivityPropertyReader(callActivity,
                                                                                   definitionResolver.getDiagram(),
                                                                                   definitionResolver);

        tested.createNode(callActivity, propertyReader);

        verify(factoryManager).newElement(eq(uuid), eq(getDefinitionId(ProcessReusableSubprocess.class)));
    }

    @Test
    public void testCreateReusableSubprocessTaskExecutionSet_case() throws Exception {
        String uuid = UUID.randomUUID().toString();

        CallActivity callActivity = bpmn2.createCallActivity();
        callActivity.setId(uuid);
        CustomElement.isCase.of(callActivity).set(Boolean.TRUE);

        CallActivityPropertyReader propertyReader = new CallActivityPropertyReader(callActivity,
                                                                                   definitionResolver.getDiagram(),
                                                                                   definitionResolver);

        BaseReusableSubprocessTaskExecutionSet result =
                tested.createReusableSubprocessTaskExecutionSet(callActivity, propertyReader);

        assertTrue(CaseReusableSubprocessTaskExecutionSet.class.isInstance(result));
        assertTrue(result.getIsCase().getValue());
    }

    @Test
    public void testCreateReusableSubprocessTaskExecutionSet_process() throws Exception {
        String uuid = UUID.randomUUID().toString();

        CallActivity callActivity = bpmn2.createCallActivity();
        callActivity.setId(uuid);
        CustomElement.isCase.of(callActivity).set(Boolean.FALSE);

        CallActivityPropertyReader propertyReader = new CallActivityPropertyReader(callActivity,
                                                                                   definitionResolver.getDiagram(),
                                                                                   definitionResolver);

        BaseReusableSubprocessTaskExecutionSet result =
                tested.createReusableSubprocessTaskExecutionSet(callActivity, propertyReader);

        assertTrue(ProcessReusableSubprocessTaskExecutionSet.class.isInstance(result));
        assertFalse(result.getIsCase().getValue());
    }

    @Test
    public void testCreateReusableSubprocessTaskExecutionSetWhenAbortParentTrue_case() {
        testAbortParent(true, true);
    }

    @Test
    public void testCreateReusableSubprocessTaskExecutionSetWhenAbortParentFalse_case() {
        testAbortParent(false, true);
    }

    @Test
    public void testCreateReusableSubprocessTaskExecutionSetWhenAbortParentTrue_process() {
        testAbortParent(true, false);
    }

    @Test
    public void testCreateReusableSubprocessTaskExecutionSetWhenAbortParentFalse_process() {
        testAbortParent(false, false);
    }

    private void testAbortParent(boolean abortParent, boolean isCase) {
        String uuid = UUID.randomUUID().toString();

        CallActivity callActivity = bpmn2.createCallActivity();
        callActivity.setId(uuid);
        CustomElement.isCase.of(callActivity).set(isCase);
        CustomElement.abortParent.of(callActivity).set(abortParent);

        CallActivityPropertyReader propertyReader = new CallActivityPropertyReader(callActivity,
                                                                                   definitionResolver.getDiagram(),
                                                                                   definitionResolver);

        BaseReusableSubprocessTaskExecutionSet result =
                tested.createReusableSubprocessTaskExecutionSet(callActivity, propertyReader);

        assertEquals(isCase, result.getIsCase().getValue());
        assertEquals(abortParent, result.getAbortParent().getValue());
        if (isCase) {
            assertTrue(CaseReusableSubprocessTaskExecutionSet.class.isInstance(result));
        } else {
            assertTrue(ProcessReusableSubprocessTaskExecutionSet.class.isInstance(result));
        }
    }
}