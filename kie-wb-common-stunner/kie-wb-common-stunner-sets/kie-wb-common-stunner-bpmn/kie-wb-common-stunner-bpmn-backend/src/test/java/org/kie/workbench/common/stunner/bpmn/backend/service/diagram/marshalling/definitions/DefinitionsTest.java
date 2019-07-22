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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.definitions;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBaseTest;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static junit.framework.TestCase.assertTrue;

public class DefinitionsTest extends BPMNDiagramMarshallerBaseTest {

    private static final String BPMN_BASIC_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/basic.bpmn";

    public DefinitionsTest() {
        super.init();
    }

    @Test
    public void definitionsShouldContainTargetNamespace() throws Exception {
        Diagram<Graph, Metadata> d = unmarshall(marshaller, BPMN_BASIC_FILE_PATH);
        String marshall = marshaller.marshall(d);
        assertTrue(marshall.contains("targetNamespace"));
    }

    @Test
    public void definitionsShouldContainSchemaLocation() throws Exception {
        Diagram<Graph, Metadata> d = unmarshall(marshaller, BPMN_BASIC_FILE_PATH);
        String marshall = marshaller.marshall(d);
        assertTrue(marshall.contains("schemaLocation"));
    }
}
