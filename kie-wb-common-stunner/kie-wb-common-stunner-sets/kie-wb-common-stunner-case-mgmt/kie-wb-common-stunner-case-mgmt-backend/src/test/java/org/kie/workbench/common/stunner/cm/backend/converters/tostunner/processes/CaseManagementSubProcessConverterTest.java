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

package org.kie.workbench.common.stunner.cm.backend.converters.tostunner.processes;

import java.util.Collections;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.cm.backend.converters.tostunner.CaseManagementConverterFactory;
import org.kie.workbench.common.stunner.cm.backend.converters.tostunner.properties.CaseManagementPropertyReaderFactory;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.core.api.FactoryManager;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;
import static org.mockito.Mockito.mock;

public class CaseManagementSubProcessConverterTest {

    private CaseManagementSubProcessConverter tested;

    @Before
    public void setUp() throws Exception {
        Definitions definitions = bpmn2.createDefinitions();
        definitions.getRootElements().add(bpmn2.createProcess());
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setPlane(di.createBPMNPlane());
        definitions.getDiagrams().add(bpmnDiagram);

        DefinitionResolver definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());

        FactoryManager factoryManager = mock(FactoryManager.class);

        TypedFactoryManager typedFactoryManager = new TypedFactoryManager(factoryManager);

        tested = new CaseManagementSubProcessConverter(typedFactoryManager,
                                                       new CaseManagementPropertyReaderFactory(definitionResolver),
                                                       definitionResolver,
                                                       new CaseManagementConverterFactory(definitionResolver, typedFactoryManager));
    }

    @Test
    public void testGetAdhocSubprocessClass() throws Exception {
        assertEquals(tested.getAdhocSubprocessClass(), AdHocSubprocess.class);
    }
}