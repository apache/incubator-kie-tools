/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBase;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller.NEW;
import static org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller.OLD;

@RunWith(Parameterized.class)
public abstract class SubProcess<T extends BaseSubprocess> extends BPMNDiagramMarshallerBase {

    static final String DEFAULT_NAME = "";
    static final String DEFAULT_DOCUMENTATION = "";
    static final String EMPTY_VALUE = "";
    static final int EMPTY_INCOME_EDGES = 1;
    static final int EMPTY_OUTCOME_EDGES = 0;
    static final int ONE_INCOME_EDGE = 2;
    static final int TWO_OUTCOME_EDGES = 2;
    static final int FOUR_OUTCOME_EDGES = 4;

    static final boolean IS_ASYNC = true;
    static final boolean IS_NOT_ASYNC = false;

    private static ReentrantLock lock = new ReentrantLock();

    @Parameterized.Parameters
    public static List<Object[]> marshallers() {
        return Arrays.asList(new Object[][]{
                {OLD}, {NEW}
        });
    }

    private Marshaller currentMarshaller;

    SubProcess(Marshaller marshallerType, List<Object[]> marshallers) throws Exception {
        currentMarshaller = marshallerType;
        setUpMarshallers(marshallers);
    }

    private void setUpMarshallers(List<Object[]> marshallers) throws Exception {
        lock.lock();
        try {
            super.init();
            if (getNewDiagram() == null && getOldDiagram() == null) {
                for (Object[] o : marshallers) {
                    if (o.length > 0) {
                        if (o[0] == NEW) {
                            marshallDiagramWithNewMarshaller();
                        }
                        if (o[0] == OLD) {
                            marshallDiagramWithOldMarshaller();
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void marshallDiagramWithNewMarshaller() throws Exception {
        setNewDiagram(unmarshall(newMarshaller, getBpmnSubProcessFilePath()));
        setNewRoundTripDiagram(unmarshall(newMarshaller, getStream(newMarshaller.marshall(getNewDiagram()))));
    }

    private void marshallDiagramWithOldMarshaller() throws Exception {
        setOldDiagram(unmarshall(oldMarshaller, getBpmnSubProcessFilePath()));
        setOldRoundTripDiagram(unmarshall(oldMarshaller, getStream(oldMarshaller.marshall(getOldDiagram()))));
    }

    abstract Diagram<Graph, Metadata> getNewDiagram();

    abstract void setNewDiagram(Diagram<Graph, Metadata> diagram);

    abstract Diagram<Graph, Metadata> getNewRoundTripDiagram();

    abstract void setNewRoundTripDiagram(Diagram<Graph, Metadata> diagram);

    abstract Diagram<Graph, Metadata> getOldDiagram();

    abstract void setOldDiagram(Diagram<Graph, Metadata> diagram);

    abstract Diagram<Graph, Metadata> getOldRoundTripDiagram();

    abstract void setOldRoundTripDiagram(Diagram<Graph, Metadata> diagram);

    @Test
    public void testMarshallTopLevelEmptyPropertiesSubProcess() {
        checkSubProcessMarshalling(getTopLevelEmptyPropertiesSubProcessId(), EMPTY_INCOME_EDGES, EMPTY_OUTCOME_EDGES);
    }

    @Test
    public void testMarshallTopLevelFilledPropertiesSubProcess() {
        for (String subProcessId : getTopLevelFilledPropertiesSubProcessesIds()) {
            checkSubProcessMarshalling(subProcessId, EMPTY_INCOME_EDGES, EMPTY_OUTCOME_EDGES);
        }
    }

    @Test
    public void testMarshallTopLevelSubProcessWithEdges() throws Exception {
        checkSubProcessMarshalling(getTopLevelSubProcessWithEdgesId(), ONE_INCOME_EDGE, FOUR_OUTCOME_EDGES);
    }

    @Test
    public void testMarshallSubProcessLevelEmptyPropertiesSubProcess() {
        checkSubProcessMarshalling(getSubProcessLevelEmptyPropertiesSubProcessId(), EMPTY_INCOME_EDGES, EMPTY_OUTCOME_EDGES);
    }

    @Test
    public void testMarshallSubProcessLevelFilledPropertiesSubProcess() {
        for (String subProcessId : getSubProcessLevelFilledPropertiesSubProcessesIds()) {
            checkSubProcessMarshalling(subProcessId, EMPTY_INCOME_EDGES, EMPTY_OUTCOME_EDGES);
        }
    }

    @Test
    public void testMarshallSubProcessLevelSubProcessWithEdges() throws Exception {
        checkSubProcessMarshalling(getSubProcessLevelSubProcessWithEdgesId(), ONE_INCOME_EDGE, FOUR_OUTCOME_EDGES);
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

    abstract String[] getTopLevelFilledPropertiesSubProcessesIds();

    abstract String getTopLevelSubProcessWithEdgesId();

    abstract String getSubProcessLevelEmptyPropertiesSubProcessId();

    abstract String[] getSubProcessLevelFilledPropertiesSubProcessesIds();

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

    public Diagram<Graph, Metadata> getDiagram() {
        switch (currentMarshaller) {
            case OLD:
                return getOldDiagram();
            case NEW:
                return getNewDiagram();
            default:
                throw new IllegalArgumentException("Unexpected value, Marshaller can be only NEW or OLD.");
        }
    }

    public Diagram<Graph, Metadata> getRoundTripDiagram() {
        switch (currentMarshaller) {
            case OLD:
                return getOldRoundTripDiagram();
            case NEW:
                return getNewRoundTripDiagram();
            default:
                throw new IllegalArgumentException("Unexpected value, Marshaller can be only NEW or OLD.");
        }
    }

    /**
     * Get specific diagram for given fileName.
     * Every call of this method calls new unmarshalling on file.
     */
    protected Diagram<Graph, Metadata> getSpecificDiagram(String fileName) throws Exception {
        switch (currentMarshaller) {
            case OLD:
                return unmarshall(oldMarshaller, fileName);
            case NEW:
                return unmarshall(newMarshaller, fileName);
            default:
                throw new IllegalArgumentException("Unexpected value, Marshaller can be only NEW or OLD.");
        }
    }

    T getSubProcessNodeById(Diagram<Graph, Metadata> diagram, String id, int incomeEdges, int outcomeEdges) {
        Node<? extends Definition, ?> node = getNodebyId(diagram, id, incomeEdges, outcomeEdges);
        return getSubProcessType().cast(node.getContent().getDefinition());
    }

    @SuppressWarnings("unchecked")
    private Node<? extends Definition, ?> getNodebyId(Diagram<Graph, Metadata> diagram, String id, int incomeEdges, int outcomeEdges) {
        Node<? extends Definition, ?> node = diagram.getGraph().getNode(id);
        assertThat(node).isNotNull();
        assertThat(node.getInEdges()).hasSize(incomeEdges);
        assertThat(node.getOutEdges()).hasSize(outcomeEdges);
        return node;
    }

    void checkSubProcessMarshalling(String nodeID, int incomeEdges, int outcomeEdges) {
        Diagram<Graph, Metadata> initialDiagram = getDiagram();
        final int AMOUNT_OF_NODES_IN_DIAGRAM = getNodes(initialDiagram).size();

        Diagram<Graph, Metadata> marshalledDiagram = getRoundTripDiagram();
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

    void assertSubProcessProcessData(ProcessData processData, String variableValue) {
        assertThat(processData).isNotNull();
        assertThat(processData.getProcessVariables()).isNotNull();
        assertThat(processData.getProcessVariables().getValue()).isEqualTo(variableValue);
    }
}
