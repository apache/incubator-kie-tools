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

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBase;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class SubProcess<T extends BaseSubprocess> extends BPMNDiagramMarshallerBase {

    static final String DEFAULT_NAME = "";
    static final String DEFAULT_DOCUMENTATION = "";
    static final int EMPTY_INCOME_EDGES = 1;
    static final int EMPTY_OUTCOME_EDGES = 0;
    static final int ONE_INCOME_EDGE = 2;
    static final int TWO_OUTCOME_EDGES = 2;

    protected DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> marshaller = null;

    @Before
    public void setUp() {
        super.init();
        this.marshaller = newMarshaller;
    }

    @Test
    public void testMarshallTopLevelEmptyPropertiesSubProcess() throws Exception {
        checkSubProcessMarshalling(getTopLevelEmptyPropertiesSubProcessId(), EMPTY_INCOME_EDGES, EMPTY_OUTCOME_EDGES);
    }

    @Test
    public void testMarshallTopLevelFilledPropertiesSubProcess() throws Exception {
        checkSubProcessMarshalling(getTopLevelFilledPropertiesSubProcessId(), EMPTY_INCOME_EDGES, EMPTY_OUTCOME_EDGES);
    }

    @Test
    public void testMarshallTopLevelSubProcessWithEdges() throws Exception {
        checkSubProcessMarshalling(getTopLevelSubProcessWithEdgesId(), ONE_INCOME_EDGE, TWO_OUTCOME_EDGES);
    }

    @Test
    public void testMarshallSubProcessLevelEmptyPropertiesSubProcess() throws Exception {
        checkSubProcessMarshalling(getSubProcessLevelEmptyPropertiesSubProcessId(), EMPTY_INCOME_EDGES, EMPTY_OUTCOME_EDGES);
    }

    @Test
    public void testMarshallSubProcessLevelFilledPropertiesSubProcess() throws Exception {
        checkSubProcessMarshalling(getSubProcessLevelFilledPropertiesSubProcessId(), EMPTY_INCOME_EDGES, EMPTY_OUTCOME_EDGES);
    }

    @Test
    public void testMarshallSubProcessLevelSubProcessWithEdges() throws Exception {
        checkSubProcessMarshalling(getSubProcessLevelSubProcessWithEdgesId(), ONE_INCOME_EDGE, TWO_OUTCOME_EDGES);
    }

    public abstract void testUnmarshallTopLevelEmptyPropertiesSubProcess() throws Exception;

    public abstract void testUnmarshallTopLevelFilledPropertiesSubProcess() throws Exception;

    public abstract void testUnmarshallTopLevelSubProcessWithEdges() throws Exception;

    public abstract void testUnmarshallSubProcessLevelEmptyPropertiesSubProcess() throws Exception;

    public abstract void testUnmarshallSubProcessLevelFilledPropertiesSubProcess() throws Exception;

    public abstract void testUnmarshallSubProcessLevelSubProcessWithEdges() throws Exception;

    abstract Class<T> getSubProcessType();

    abstract String getBpmnSubProcessFilePath();

    abstract String getTopLevelEmptyPropertiesSubProcessId();

    abstract String getTopLevelFilledPropertiesSubProcessId();

    abstract String getTopLevelSubProcessWithEdgesId();

    abstract String getSubProcessLevelEmptyPropertiesSubProcessId();

    abstract String getSubProcessLevelFilledPropertiesSubProcessId();

    abstract String getSubProcessLevelSubProcessWithEdgesId();

    private void assertNodesEqualsAfterMarshalling(Diagram<Graph, Metadata> before,
                                                   Diagram<Graph, Metadata> after,
                                                   String nodeId,
                                                   int incomeEdges,
                                                   int outcomeEdges) {
        T nodeBeforeMarshalling = getSubProcessNodeById(before, nodeId, incomeEdges, outcomeEdges);
        T nodeAfterMarshalling = getSubProcessNodeById(after, nodeId, incomeEdges, outcomeEdges);
        assertThat(nodeAfterMarshalling).isEqualTo(nodeBeforeMarshalling);
    }

    @SuppressWarnings("unchecked")
    T getSubProcessNodeById(Diagram<Graph, Metadata> diagram, String id, int incomeEdges, int outcomeEdges) {
        Node<? extends Definition, ?> node = getNodebyId(diagram, id, incomeEdges, outcomeEdges);
        return getSubProcessType().cast(node.getContent().getDefinition());
    }

    Node<? extends Definition, ?> getNodebyId(Diagram<Graph, Metadata> diagram, String id, int incomeEdges, int outcomeEdges) {
        Node<? extends Definition, ?> node = diagram.getGraph().getNode(id);
        assertThat(node).isNotNull();
        assertThat(node.getInEdges()).hasSize(incomeEdges);
        assertThat(node.getOutEdges()).hasSize(outcomeEdges);
        return node;
    }

    @SuppressWarnings("unchecked")
    void checkSubProcessMarshalling(String nodeID, int incomeEdges, int outcomeEdges) throws Exception {
        Diagram<Graph, Metadata> initialDiagram = unmarshall(marshaller, getBpmnSubProcessFilePath());
        final int AMOUNT_OF_NODES_IN_DIAGRAM = getNodes(initialDiagram).size();
        String resultXml = marshaller.marshall(initialDiagram);

        Diagram<Graph, Metadata> marshalledDiagram = unmarshall(marshaller, getStream(resultXml));
        assertDiagram(marshalledDiagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        assertNodesEqualsAfterMarshalling(initialDiagram, marshalledDiagram, nodeID, incomeEdges, outcomeEdges);
    }

    void assertGeneralSet(BPMNGeneralSet generalSet, String nodeName, String documentation) {
        assertThat(generalSet).isNotNull();
        assertThat(generalSet.getName()).isNotNull();
        assertThat(generalSet.getDocumentation()).isNotNull();
        assertThat(generalSet.getName().getValue()).isEqualTo(nodeName);
        assertThat(generalSet.getDocumentation().getValue()).isEqualTo(documentation);
    }

    void assertDataIOSet(DataIOSet dataIOSet, String value) {
        assertThat(dataIOSet).isNotNull();
        assertThat(dataIOSet.getAssignmentsinfo()).isNotNull();
        assertThat(dataIOSet.getAssignmentsinfo().getValue()).isEqualTo(value);
    }
}
