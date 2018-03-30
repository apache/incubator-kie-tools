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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Assertions {

    private static int count(final String string,
                             final String substring) {
        int count = 0;
        int idx = 0;
        while ((idx = string.indexOf(substring,
                                     idx)) != -1) {
            idx++;
            count++;
        }
        return count;
    }

    public static void assertDiagram(Diagram<Graph, Metadata> diagram,
                                     int nodesSize) {
        assertEquals(nodesSize, getNodes(diagram).size());
    }

    public static void assertDiagram(String result,
                                     int diagramCount,
                                     int nodeCount,
                                     int edgeCount) {
        int d = count(result,
                      "<bpmndi:BPMNDiagram");
        int n = count(result,
                      "<bpmndi:BPMNShape");
        int e = count(result,
                      "<bpmndi:BPMNEdge");
        assertEquals("diagram count should match", diagramCount, d);
        assertEquals("node count should match", nodeCount, n);
        assertEquals("edge count should match", edgeCount, e);
    }

    public static List<Node> getNodes(Diagram<Graph, Metadata> diagram) {
        Graph graph = diagram.getGraph();
        assertNotNull(graph);
        Iterator<Node> nodesIterable = graph.nodes().iterator();
        List<Node> nodes = new ArrayList<>();
        nodesIterable.forEachRemaining(nodes::add);
        return nodes;
    }

    public static void assertDocumentation(Diagram<Graph, Metadata> diagram,
                                           String id,
                                           String value) {
        Optional<BPMNDefinition> documentation = getNodes(diagram).stream()
                .filter(node -> node.getContent() instanceof View && node.getUUID().equals(id))
                .map(node -> (View) node.getContent())
                .filter(view -> view.getDefinition() instanceof BPMNDefinition)
                .map(view -> (BPMNDefinition) view.getDefinition())
                .findFirst();
        String documentationValue = (documentation.isPresent() ? documentation.get().getGeneral().getDocumentation().getValue() : null);
        assertEquals(value,
                     documentationValue);
    }
}
