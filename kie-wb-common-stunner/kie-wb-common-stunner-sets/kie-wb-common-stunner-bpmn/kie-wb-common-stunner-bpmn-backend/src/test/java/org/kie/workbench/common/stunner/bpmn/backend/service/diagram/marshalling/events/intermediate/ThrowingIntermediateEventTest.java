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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.events.intermediate;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBaseTest;
import org.kie.workbench.common.stunner.bpmn.definition.BaseThrowingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class ThrowingIntermediateEventTest<T extends BaseThrowingIntermediateEvent> extends BPMNDiagramMarshallerBaseTest {

    static final String EMPTY_VALUE = "";
    static final boolean HAS_INCOME_EDGE = true;
    static final boolean HAS_NO_INCOME_EDGE = false;
    static final int ZERO_OUTGOING_EDGES = 0;
    static final int TWO_OUTGOING_EDGES = 2;

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

    ThrowingIntermediateEventTest() throws Exception {
        super.init();
        marshallDiagramWithNewMarshaller();
    }

    private void marshallDiagramWithNewMarshaller() throws Exception {
        setDiagram(unmarshall(marshaller, getBpmnThrowingIntermediateEventFilePath()));
        setRoundTripDiagram(unmarshall(marshaller, getStream(marshaller.marshall(getDiagram()))));
    }

    @Test
    public void testMarshallTopLevelEventFilledProperties() {
        for (String eventId : getFilledTopLevelEventIds()) {
            checkEventMarshalling(eventId, HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        }
    }

    @Test
    public void testMarshallTopLevelEventEmptyProperties() {
        checkEventMarshalling(getEmptyTopLevelEventId(), HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
    }

    @Test
    public void testMarshallSubprocessLevelEventFilledProperties() {
        for (String eventId : getFilledSubprocessLevelEventIds()) {
            checkEventMarshalling(eventId, HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        }
    }

    @Test
    public void testMarshallSubprocessLevelEventEmptyProperties() {
        checkEventMarshalling(getEmptySubprocessLevelEventId(), HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
    }

    @Test
    public void testMarshallTopLevelEventWithEdgesFilledProperties() {
        for (String eventId : getFilledTopLevelEventWithEdgesIds()) {
            checkEventMarshalling(eventId, HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        }
    }

    @Test
    public void testMarshallTopLevelEventWithEdgesEmptyProperties() {
        checkEventMarshalling(getEmptyTopLevelEventWithEdgesId(), HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
    }

    @Test
    public void testMarshallSubprocessLevelEventWithEdgesFilledProperties() {
        for (String eventId : getFilledSubprocessLevelEventWithEdgesIds()) {
            checkEventMarshalling(eventId, HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        }
    }

    @Test
    public void testMarshallSubprocessLevelEventWithEdgesEmptyProperties() {
        checkEventMarshalling(getEmptySubprocessLevelEventWithEdgesId(), HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
    }

    public abstract void testUnmarshallTopLevelEventFilledProperties() throws Exception;

    public abstract void testUnmarshallTopLevelEmptyEventProperties() throws Exception;

    public abstract void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception;

    public abstract void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception;

    public abstract void testUnmarshallTopLevelEventWithEdgesFilledProperties() throws Exception;

    public abstract void testUnmarshallTopLevelEventWithEdgesEmptyProperties() throws Exception;

    public abstract void testUnmarshallSubprocessLevelEventWithEdgesEmptyProperties() throws Exception;

    public abstract void testUnmarshallSubprocessLevelEventWithEdgesFilledProperties() throws Exception;

    abstract String getBpmnThrowingIntermediateEventFilePath();

    abstract Class<T> getThrowingIntermediateEventType();

    abstract String[] getFilledTopLevelEventIds();

    abstract String getEmptyTopLevelEventId();

    abstract String[] getFilledSubprocessLevelEventIds();

    abstract String getEmptySubprocessLevelEventId();

    abstract String[] getFilledTopLevelEventWithEdgesIds();

    abstract String getEmptyTopLevelEventWithEdgesId();

    abstract String[] getFilledSubprocessLevelEventWithEdgesIds();

    abstract String getEmptySubprocessLevelEventWithEdgesId();

    @SuppressWarnings("unchecked")
    T getThrowingIntermediateNodeById(Diagram<Graph, Metadata> diagram, String id, boolean hasIncomeEdge, int outgoingEdges) {
        Node<? extends Definition, ?> node = diagram.getGraph().getNode(id);
        assertThat(node).isNotNull();

        int incomeEdges = hasIncomeEdge ? 2 : 1;
        assertThat(node.getInEdges().size()).isEqualTo(incomeEdges);

        assertThat(node.getOutEdges().size()).isEqualTo(outgoingEdges);
        return getThrowingIntermediateEventType().cast(node.getContent().getDefinition());
    }

    @SuppressWarnings("unchecked")
    void checkEventMarshalling(String nodeID, boolean hasIncomeEdge, int outgoingEdges) {
        Diagram<Graph, Metadata> initialDiagram = getDiagram();
        final int amountOfNodesInDiagram = getNodes(initialDiagram).size();

        Diagram<Graph, Metadata> marshalledDiagram = getRoundTripDiagram();
        assertDiagram(marshalledDiagram, amountOfNodesInDiagram);

        assertNodesEqualsAfterMarshalling(initialDiagram, marshalledDiagram, nodeID, hasIncomeEdge, outgoingEdges);
    }

    private void assertNodesEqualsAfterMarshalling(Diagram<Graph, Metadata> before, Diagram<Graph, Metadata> after, String nodeId, boolean hasIncomeEdge, int outgoingEdges) {
        T nodeBeforeMarshalling = getThrowingIntermediateNodeById(before, nodeId, hasIncomeEdge, outgoingEdges);
        T nodeAfterMarshalling = getThrowingIntermediateNodeById(after, nodeId, hasIncomeEdge, outgoingEdges);
        assertThat(nodeAfterMarshalling).isEqualTo(nodeBeforeMarshalling);
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
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertThat(assignmentsInfo).isNotNull();
        assertThat(assignmentsInfo.getValue()).isEqualTo(value);
    }
}

