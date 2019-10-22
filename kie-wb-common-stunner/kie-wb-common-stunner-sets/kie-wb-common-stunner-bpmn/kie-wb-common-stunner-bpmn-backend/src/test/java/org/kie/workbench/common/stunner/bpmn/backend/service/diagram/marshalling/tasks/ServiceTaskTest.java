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

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Unmarshalling;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBaseTest;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.junit.Assert.assertEquals;

public class ServiceTaskTest extends BPMNDiagramMarshallerBaseTest {

    private static final String BPMN_SERVICE_TASK_PROPERTIES_FILE_PATH =
            "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/serviceTask.bpmn";

    private static final String SERVICE_TASK_ID = "A3C25100-DFAB-4867-9282-08381BD69C6B";

    {
        super.init();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBasicUnmarshall() throws Exception {
        Diagram<Graph, Metadata> d = unmarshall(marshaller, BPMN_SERVICE_TASK_PROPERTIES_FILE_PATH);
        Node<View<ServiceTask>, ?> node = d.getGraph().getNode(SERVICE_TASK_ID);
        ServiceTask definition = node.getContent().getDefinition();
        assertEquals("Custom Email", definition.getGeneral().getName().getValue());
        assertEquals("This is an email task", definition.getGeneral().getDocumentation().getValue());
    }

    @Test
    public void testBasicMarshall() throws Exception {
        Diagram<Graph, Metadata> d = unmarshall(marshaller, BPMN_SERVICE_TASK_PROPERTIES_FILE_PATH);

        Definitions definitions = convertToDefinitions(d);

        org.eclipse.bpmn2.Task serviceTask =
                ((Process) definitions.getRootElements().get(0))
                        .getFlowElements()
                        .stream()
                        .filter(org.eclipse.bpmn2.Task.class::isInstance)
                        .map(org.eclipse.bpmn2.Task.class::cast)
                        .findFirst().get();

        assertEquals("Custom Email", serviceTask.getName());
        assertEquals("<![CDATA[This is an email task]]>", serviceTask.getDocumentation().get(0).getText());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBasicBidi() throws Exception {
        Diagram<Graph, Metadata> d = unmarshall(marshaller, BPMN_SERVICE_TASK_PROPERTIES_FILE_PATH);
        String marshall = marshaller.marshall(d);
        Diagram<Graph, Metadata> d2 = Unmarshalling.unmarshall(marshaller, new ByteArrayInputStream(marshall.getBytes(Charset.forName("UTF-8"))));

        Node<View<ServiceTask>, ?> node = d2.getGraph().getNode(SERVICE_TASK_ID);

        ServiceTask definition = node.getContent().getDefinition();
        assertEquals("Custom Email", definition.getGeneral().getName().getValue());
        assertEquals("This is an email task", definition.getGeneral().getDocumentation().getValue());
    }
}
