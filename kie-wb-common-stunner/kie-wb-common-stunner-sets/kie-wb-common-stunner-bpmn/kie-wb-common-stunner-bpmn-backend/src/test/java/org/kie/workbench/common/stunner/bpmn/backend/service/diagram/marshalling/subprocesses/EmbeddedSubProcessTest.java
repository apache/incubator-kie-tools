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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.subprocesses;

import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.di.DiagramElement;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsConverter;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Unmarshalling;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBase;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.assertj.core.api.Assertions.assertThat;

public class EmbeddedSubProcessTest extends BPMNDiagramMarshallerBase {

    private static final String BPMN_EMBEDDED_SUBPROCESS =
            "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/embeddedSubprocess.bpmn";

    @Before
    public void setUp() {
        super.init();
    }

    @Test
    public void testMigration() throws Exception {
        Diagram<Graph, Metadata> oldDiagram = Unmarshalling.unmarshall(oldMarshaller, BPMN_EMBEDDED_SUBPROCESS);
        Diagram<Graph, Metadata> newDiagram = Unmarshalling.unmarshall(newMarshaller, BPMN_EMBEDDED_SUBPROCESS);

        assertDiagramEquals(oldDiagram, newDiagram, BPMN_EMBEDDED_SUBPROCESS);
    }

    @Test
    public void testMarshallEmbeddedCoords() throws Exception {
        String END_EVENT = "shape__FF050977-4D13-47F1-8B9B-D68FDE208666";
        Diagram<Graph, Metadata> diagram = unmarshall(newMarshaller, BPMN_EMBEDDED_SUBPROCESS);

        // we start converting from the root, then pull out the result
        DefinitionsConverter definitionsConverter =
                new DefinitionsConverter(diagram.getGraph());

        Definitions definitions =
                definitionsConverter.toDefinitions();

        List<DiagramElement> planeElement = definitions.getDiagrams().get(0).getPlane().getPlaneElement();
        DiagramElement diagramElement = planeElement.stream()
                .filter(BPMNShape.class::isInstance)
                .map(s -> (BPMNShape)s)
                .filter(el -> el.getId().equals(END_EVENT)).findFirst().get();
        BPMNShape shape = (BPMNShape) diagramElement;
        assertThat(shape.getBounds().getX()).isEqualTo(885f);
        assertThat(shape.getBounds().getY()).isEqualTo(143f);
    }
}
