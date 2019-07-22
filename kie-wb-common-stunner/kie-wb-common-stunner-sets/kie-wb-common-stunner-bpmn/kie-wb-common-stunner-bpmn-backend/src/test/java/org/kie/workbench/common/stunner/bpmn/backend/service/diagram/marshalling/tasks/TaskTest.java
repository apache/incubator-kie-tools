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

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBaseTest;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class TaskTest<T extends BaseTask> extends BPMNDiagramMarshallerBaseTest {

    static final String EMPTY_VALUE = "";
    static final int ZERO_INCOME_EDGES = 0;
    static final int ONE_INCOME_EDGE = 1;
    static final int TWO_INCOME_EDGES = 2;
    static final boolean HAS_OUTCOME_EDGE = true;
    static final boolean HAS_NO_OUTCOME_EDGE = false;

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

    TaskTest() throws Exception {
        super.init();
        marshallDiagramWithNewMarshaller();
    }

    private void marshallDiagramWithNewMarshaller() throws Exception {
        setDiagram(unmarshall(marshaller, getBpmnTaskFilePath()));
        setRoundTripDiagram(unmarshall(marshaller, getStream(marshaller.marshall(getDiagram()))));
    }

    @Test
    public void testMarshallTopLevelTaskFilledProperties() {
        checkTaskMarshalling(getFilledTopLevelTaskId(), ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallTopLevelTaskEmptyProperties() {
        checkTaskMarshalling(getEmptyTopLevelTaskId(), ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallSubprocessLevelTaskFilledProperties() {
        checkTaskMarshalling(getFilledSubprocessLevelTaskId(), ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallSubprocessLevelTaskEmptyProperties() {
        checkTaskMarshalling(getEmptySubprocessLevelTaskId(), ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallTopLevelTaskOneIncomeFilledProperties() {
        checkTaskMarshalling(getFilledTopLevelTaskOneIncomeId(), ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallTopLevelTaskOneIncomeEmptyProperties() {
        checkTaskMarshalling(getEmptyTopLevelTaskOneIncomeId(), ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallSubprocessLevelTaskOneIncomeFilledProperties() {
        checkTaskMarshalling(getFilledSubprocessLevelTaskOneIncomeId(), ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallSubprocessLevelTaskOneIncomeEmptyProperties() {
        checkTaskMarshalling(getEmptySubprocessLevelTaskOneIncomeId(), ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallTopLevelTaskTwoIncomesFilledProperties() {
        checkTaskMarshalling(getFilledTopLevelTaskTwoIncomesId(), TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallTopLevelTaskTwoIncomesEmptyProperties() {
        checkTaskMarshalling(getEmptyTopLevelTaskTwoIncomesId(), TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallSubprocessLevelTaskTwoIncomesFilledProperties() {
        checkTaskMarshalling(getFilledSubprocessLevelTaskTwoIncomesId(), TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallSubprocessLevelTaskTwoIncomesEmptyProperties() {
        checkTaskMarshalling(getEmptySubprocessLevelTaskTwoIncomesId(), TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
    }

    public abstract void testUnmarshallTopLevelTaskFilledProperties() throws Exception;

    public abstract void testUnmarshallTopLevelEmptyTaskProperties() throws Exception;

    public abstract void testUnmarshallSubprocessLevelTaskFilledProperties() throws Exception;

    public abstract void testUnmarshallSubprocessLevelTaskEmptyProperties() throws Exception;

    public abstract void testUnmarshallTopLevelTaskOneIncomeFilledProperties() throws Exception;

    public abstract void testUnmarshallTopLevelTaskOneIncomeEmptyProperties() throws Exception;

    public abstract void testUnmarshallSubprocessLevelTaskOneIncomeEmptyProperties() throws Exception;

    public abstract void testUnmarshallSubprocessLevelTaskOneIncomeFilledProperties() throws Exception;

    public abstract void testUnmarshallTopLevelTaskTwoIncomesFilledProperties() throws Exception;

    public abstract void testUnmarshallTopLevelTaskTwoIncomesEmptyProperties() throws Exception;

    public abstract void testUnmarshallSubprocessLevelTaskTwoIncomesEmptyProperties() throws Exception;

    public abstract void testUnmarshallSubprocessLevelTaskTwoIncomesFilledProperties() throws Exception;

    abstract String getBpmnTaskFilePath();

    public int getInitialAmountOfNodes() {
        return getNodes(getDiagram()).size();
    }

    abstract Class<T> getTaskType();

    abstract String getFilledTopLevelTaskId();

    abstract String getEmptyTopLevelTaskId();

    abstract String getFilledSubprocessLevelTaskId();

    abstract String getEmptySubprocessLevelTaskId();

    abstract String getFilledTopLevelTaskOneIncomeId();

    abstract String getEmptyTopLevelTaskOneIncomeId();

    abstract String getFilledSubprocessLevelTaskOneIncomeId();

    abstract String getEmptySubprocessLevelTaskOneIncomeId();

    abstract String getFilledTopLevelTaskTwoIncomesId();

    abstract String getEmptyTopLevelTaskTwoIncomesId();

    abstract String getFilledSubprocessLevelTaskTwoIncomesId();

    abstract String getEmptySubprocessLevelTaskTwoIncomesId();

    private void assertNodesEqualsAfterMarshalling(Diagram<Graph, Metadata> before, Diagram<Graph, Metadata> after,
                                                   String nodeId, int amountOfIncomeEdges, boolean hasOutcomeEdge) {
        T nodeBeforeMarshalling = getTaskNodeById(before, nodeId, amountOfIncomeEdges, hasOutcomeEdge);
        T nodeAfterMarshalling = getTaskNodeById(after, nodeId, amountOfIncomeEdges, hasOutcomeEdge);
        assertEquals(nodeBeforeMarshalling, nodeAfterMarshalling);
    }

    @SuppressWarnings("unchecked")
    T getTaskNodeById(Diagram<Graph, Metadata> diagram, String id, int amountOfIncomeEdges, boolean hasOutcomeEdge) {
        Node<? extends Definition, ?> node = diagram.getGraph().getNode(id);
        assertNotNull(node);

        assertEquals(amountOfIncomeEdges + 1, node.getInEdges().size());

        int outcomeEdges = hasOutcomeEdge ? 1 : 0;
        assertEquals(outcomeEdges, node.getOutEdges().size());
        return getTaskType().cast(node.getContent().getDefinition());
    }

    void checkTaskMarshalling(String nodeID, int amountOfIncomeEdges, boolean hasOutcomeEdge) {
        Diagram<Graph, Metadata> initialDiagram = getDiagram();

        Diagram<Graph, Metadata> marshalledDiagram = getRoundTripDiagram();
        assertDiagram(marshalledDiagram, getInitialAmountOfNodes());

        assertNodesEqualsAfterMarshalling(initialDiagram, marshalledDiagram, nodeID, amountOfIncomeEdges, hasOutcomeEdge);
    }

    void assertGeneralSet(TaskGeneralSet generalSet, String nodeName, String documentation) {
        assertNotNull(generalSet);
        assertNotNull(generalSet.getName());
        assertNotNull(generalSet.getDocumentation());
        assertEquals(nodeName, generalSet.getName().getValue());
        assertEquals(documentation, generalSet.getDocumentation().getValue());
    }
}
