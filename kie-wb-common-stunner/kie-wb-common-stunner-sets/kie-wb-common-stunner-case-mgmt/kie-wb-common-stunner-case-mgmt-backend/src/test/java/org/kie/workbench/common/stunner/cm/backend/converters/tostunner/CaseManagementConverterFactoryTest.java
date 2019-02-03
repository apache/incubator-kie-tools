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

package org.kie.workbench.common.stunner.cm.backend.converters.tostunner;

import java.util.Collections;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.cm.backend.converters.tostunner.activities.CaseManagementCallActivityConverter;
import org.kie.workbench.common.stunner.cm.backend.converters.tostunner.processes.CaseManagementRootProcessConverter;
import org.kie.workbench.common.stunner.cm.backend.converters.tostunner.processes.CaseManagementSubProcessConverter;
import org.kie.workbench.common.stunner.cm.backend.converters.tostunner.tasks.CaseManagementTaskConverter;
import org.kie.workbench.common.stunner.core.api.FactoryManager;

import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;
import static org.mockito.Mockito.mock;

public class CaseManagementConverterFactoryTest {

    private DefinitionResolver definitionResolver;

    private FactoryManager factoryManager;

    private CaseManagementConverterFactory tested;

    @Before
    public void setUp() throws Exception {
        Definitions definitions = bpmn2.createDefinitions();
        definitions.getRootElements().add(bpmn2.createProcess());
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setPlane(di.createBPMNPlane());
        definitions.getDiagrams().add(bpmnDiagram);

        definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());

        factoryManager = mock(FactoryManager.class);

        tested = new CaseManagementConverterFactory(definitionResolver, new TypedFactoryManager(factoryManager));
    }

    @Test
    public void testRootProcessConverter() throws Exception {
        assertTrue(CaseManagementRootProcessConverter.class.isInstance(tested.rootProcessConverter()));
    }

    @Test
    public void testSubProcessConverter() throws Exception {
        assertTrue(CaseManagementSubProcessConverter.class.isInstance(tested.subProcessConverter()));
    }

    @Test
    public void testCreateCallActivityConverter() throws Exception {
        assertTrue(CaseManagementCallActivityConverter.class.isInstance(tested.callActivityConverter()));
    }

    @Test
    public void testTaskConverter() throws Exception {
        assertTrue(CaseManagementTaskConverter.class.isInstance(tested.taskConverter()));
    }
}