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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.tasks;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.impl.ServiceTaskImpl;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsConverter;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBaseTest;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static junit.framework.TestCase.assertEquals;

public class GenericServiceTaskTest extends BPMNDiagramMarshallerBaseTest {

    private static final String BPMN_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/genericServiceTasks.bpmn";
    private static final String TASK_ID = "_414AEBA5-ED00-492E-B6E9-62331732B7B3";

    public GenericServiceTaskTest() {
        init();
    }

    @Test
    public void marshallServiceTask() throws Exception {
        Diagram<Graph, Metadata> d = unmarshall(marshaller, BPMN_FILE_PATH);

        DefinitionsConverter definitionsConverter =
                new DefinitionsConverter(d.getGraph());

        Definitions definitions =
                definitionsConverter.toDefinitions();
        Process p = (Process) definitions.getRootElements().get(0);
        assertEquals(ServiceTaskImpl.class, p.getFlowElements().stream().filter(e -> e.getId().equals(TASK_ID)).findFirst().get().getClass());
        org.eclipse.bpmn2.ServiceTask flowElement = (org.eclipse.bpmn2.ServiceTask)
                p.getFlowElements().stream().filter(e -> e.getId().equals(TASK_ID)).findFirst().get();

        assertEquals("Service", flowElement.getName());
        assertEquals("op", flowElement.getOperationRef().getName());
        assertEquals("Java", CustomAttribute.serviceImplementation.of(flowElement).get());
        assertEquals("op", CustomAttribute.serviceOperation.of(flowElement).get());
        assertEquals("hgfhfgh", CustomAttribute.serviceInterface.of(flowElement).get());
    }
}
