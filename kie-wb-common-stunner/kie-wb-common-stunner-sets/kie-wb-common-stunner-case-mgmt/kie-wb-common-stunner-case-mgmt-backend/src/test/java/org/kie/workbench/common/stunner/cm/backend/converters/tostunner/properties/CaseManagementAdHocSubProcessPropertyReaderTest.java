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

package org.kie.workbench.common.stunner.cm.backend.converters.tostunner.properties;

import java.util.Collections;
import java.util.UUID;

import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;

public class CaseManagementAdHocSubProcessPropertyReaderTest {

    private DefinitionResolver definitionResolver;

    private CaseManagementAdHocSubProcessPropertyReader tested;

    @Before
    public void setUp() throws Exception {
        Definitions definitions = bpmn2.createDefinitions();
        definitions.getRootElements().add(bpmn2.createProcess());
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setPlane(di.createBPMNPlane());
        definitions.getDiagrams().add(bpmnDiagram);

        definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());
    }

    @Test
    public void testIsAdHocAutostart_true() throws Exception {
        String id = UUID.randomUUID().toString();

        AdHocSubProcess adHocSubProcess = bpmn2.createAdHocSubProcess();
        adHocSubProcess.setId(id);
        CustomElement.autoStart.of(adHocSubProcess).set(Boolean.TRUE);

        tested = new CaseManagementAdHocSubProcessPropertyReader(adHocSubProcess,
                                                                 definitionResolver.getDiagram(),
                                                                 definitionResolver);

        assertTrue(tested.isAdHocAutostart());
    }

    @Test
    public void testIsAdHocAutostart_false() throws Exception {
        String id = UUID.randomUUID().toString();

        AdHocSubProcess adHocSubProcess = bpmn2.createAdHocSubProcess();
        adHocSubProcess.setId(id);
        CustomElement.autoStart.of(adHocSubProcess).set(Boolean.FALSE);

        tested = new CaseManagementAdHocSubProcessPropertyReader(adHocSubProcess,
                                                                 definitionResolver.getDiagram(),
                                                                 definitionResolver);

        assertFalse(tested.isAdHocAutostart());
    }
}