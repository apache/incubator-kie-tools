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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.events;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBaseTest;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.BaseStartEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class StartEventTest<T extends BaseStartEvent> extends BPMNDiagramMarshallerBaseTest {

    static final String EMPTY_VALUE = "";
    static final boolean NON_INTERRUPTING = false;
    static final boolean INTERRUPTING = true;
    static final int EMPTY_INCOME_EDGES = 1;
    static final int ONE_OUTCOME_EDGE = 1;

    private Diagram<Graph, Metadata> diagram;
    private Diagram<Graph, Metadata> roundTripDiagram;

    public Diagram<Graph, Metadata> getDiagram() {
        return diagram;
    }

    public void setDiagram(Diagram<Graph, Metadata> diagram) {
        this.diagram = diagram;
    }

    public Diagram<Graph, Metadata> getRoundTripDiagram() {
        return roundTripDiagram;
    }

    public void setRoundTripDiagram(Diagram<Graph, Metadata> diagram) {
        this.roundTripDiagram = diagram;
    }

    StartEventTest() throws Exception {
        super.init();
        marshallDiagramWithNewMarshaller();
    }

    private void marshallDiagramWithNewMarshaller() throws Exception {
        setDiagram(unmarshall(marshaller, getBpmnStartEventFilePath()));
        setRoundTripDiagram(unmarshall(marshaller, getStream(marshaller.marshall(getDiagram()))));
    }

    @Test
    public void testMarshallTopLevelEventFilledProperties() {
        checkEventMarshalling(getFilledTopLevelEventId());
    }

    @Test
    public void testMarshallTopLevelEmptyEventProperties() {
        checkEventMarshalling(getEmptyTopLevelEventId());
    }

    @Test
    public void testMarshallSubprocessLevelEventFilledProperties() {
        checkEventMarshalling(getFilledSubprocessLevelEventId());
    }

    @Test
    public void testMarshallSubprocessLevelEventEmptyProperties() {
        checkEventMarshalling(getEmptySubprocessLevelEventId());
    }

    public abstract void testUnmarshallTopLevelEventFilledProperties() throws Exception;

    public abstract void testUnmarshallTopLevelEmptyEventProperties() throws Exception;

    public abstract void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception;

    public abstract void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception;

    abstract Class<T> getStartEventType();

    abstract String getBpmnStartEventFilePath();

    abstract String getFilledTopLevelEventId();

    abstract String getEmptyTopLevelEventId();

    abstract String getFilledSubprocessLevelEventId();

    abstract String getEmptySubprocessLevelEventId();

    T getStartNodeById(Diagram<Graph, Metadata> diagram, String id) {
        return getStartNodeById(diagram, id, ONE_OUTCOME_EDGE);
    }

    @SuppressWarnings("unchecked")
    T getStartNodeById(Diagram<Graph, Metadata> diagram, String id, int outcomeEdges) {
        Node<? extends Definition, ?> node = getNodeById(diagram, id, outcomeEdges);
        return getStartEventType().cast(node.getContent().getDefinition());
    }

    void checkEventMarshalling(String nodeID) {
        checkEventMarshalling(nodeID, ONE_OUTCOME_EDGE);
    }

    @SuppressWarnings("unchecked")
    void checkEventMarshalling(String nodeID, int outcomeEdges) {
        Diagram<Graph, Metadata> initialDiagram = getDiagram();
        final int amountOfNodesInDiagram = getNodes(initialDiagram).size();

        Diagram<Graph, Metadata> marshalledDiagram = getRoundTripDiagram();
        assertDiagram(marshalledDiagram, amountOfNodesInDiagram);

        assertNodesEqualsAfterMarshalling(initialDiagram, marshalledDiagram, nodeID, outcomeEdges);
    }

    private void assertNodesEqualsAfterMarshalling(Diagram<Graph, Metadata> before,
                                                   Diagram<Graph, Metadata> after,
                                                   String nodeId,
                                                   int outcomeEdges) {
        T nodeBeforeMarshalling = getStartNodeById(before, nodeId, outcomeEdges);
        T nodeAfterMarshalling = getStartNodeById(after, nodeId, outcomeEdges);
        assertThat(nodeAfterMarshalling).isEqualTo(nodeBeforeMarshalling);
    }

    @SuppressWarnings("unchecked")
    private Node<? extends Definition, ?> getNodeById(Diagram<Graph, Metadata> diagram, String id, int outcomeEdges) {
        Node<? extends Definition, ?> node = diagram.getGraph().getNode(id);
        assertThat(node).isNotNull();
        assertThat(node.getInEdges()).hasSize(EMPTY_INCOME_EDGES);
        assertThat(node.getOutEdges()).hasSize(outcomeEdges);
        return node;
    }

    void assertGeneralSet(BPMNGeneralSet generalSet, String nodeName, String documentation) {
        assertNotNull(generalSet);
        assertNotNull(generalSet.getName());
        assertNotNull(generalSet.getDocumentation());
        assertEquals(nodeName, generalSet.getName().getValue());
        assertEquals(documentation, generalSet.getDocumentation().getValue());
    }

    void assertDataIOSet(DataIOSet dataIOSet, String value) {
        assertNotNull(dataIOSet);
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertNotNull(assignmentsInfo);
        assertEquals(value, assignmentsInfo.getValue());
    }

    protected void assertStartEventSlaDueDate(BaseStartEventExecutionSet executionSet, String slaDueDate) {
        assertNotNull(executionSet.getSlaDueDate());
        assertEquals(slaDueDate, executionSet.getSlaDueDate().getValue());
    }

    protected void assertStartEventIsInterrupting(BaseStartEventExecutionSet executionSet, boolean isInterrupting) {
        assertNotNull(executionSet.getIsInterrupting());
        assertEquals(isInterrupting, executionSet.getIsInterrupting().getValue());
    }
}
