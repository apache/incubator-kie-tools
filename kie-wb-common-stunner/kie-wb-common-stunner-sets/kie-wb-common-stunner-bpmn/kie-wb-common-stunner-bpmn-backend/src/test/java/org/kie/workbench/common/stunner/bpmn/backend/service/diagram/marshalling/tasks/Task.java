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

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Unmarshalling;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBase;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller.NEW;
import static org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller.OLD;

@RunWith(Parameterized.class)
public abstract class Task<T extends BaseTask> extends BPMNDiagramMarshallerBase {

    static final String EMPTY_VALUE = "";
    static final int ZERO_INCOME_EDGES = 0;
    static final int ONE_INCOME_EDGE = 1;
    static final int TWO_INCOME_EDGES = 2;
    static final boolean HAS_OUTCOME_EDGE = true;
    static final boolean HAS_NO_OUTCOME_EDGE = false;

    protected DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> marshaller = null;

    @Parameterized.Parameters
    public static List<Object[]> marshallers() {
        return Arrays.asList(new Object[][]{
                // New (un)marshaller is disabled for now due to found incompleteness
                {OLD}//, {NEW}
        });
    }

    Task(Marshaller marshallerType) {
        super.init();
        switch (marshallerType) {
            case OLD:
                marshaller = oldMarshaller;
                break;
            case NEW:
                marshaller = newMarshaller;
                break;
        }
    }

    @Ignore
    @Test
    public void testMigration() throws Exception {
        Diagram<Graph, Metadata> oldDiagram = Unmarshalling.unmarshall(oldMarshaller, getBpmnTaskFilePath());
        Diagram<Graph, Metadata> newDiagram = Unmarshalling.unmarshall(newMarshaller, getBpmnTaskFilePath());

        // Doesn't work, due to old Marshaller and new Marshaller have different BPMNDefinitionSet uuids
        // assertEquals(oldDiagram.getGraph(), newDiagram.getGraph());

        // Let's check nodes only.
        assertDiagramEquals(oldDiagram, newDiagram, getBpmnTaskFilePath());
    }

    @Test
    public void testMarshallTopLevelTaskFilledProperties() throws Exception {
        checkTaskMarshalling(getFilledTopLevelTaskId(), ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallTopLevelTaskEmptyProperties() throws Exception {
        checkTaskMarshalling(getEmptyTopLevelTaskId(), ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallSubprocessLevelTaskFilledProperties() throws Exception {
        checkTaskMarshalling(getFilledSubprocessLevelTaskId(), ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallSubprocessLevelTaskEmptyProperties() throws Exception {
        checkTaskMarshalling(getEmptySubprocessLevelTaskId(), ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallTopLevelTaskOneIncomeFilledProperties() throws Exception {
        checkTaskMarshalling(getFilledTopLevelTaskOneIncomeId(), ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallTopLevelTaskOneIncomeEmptyProperties() throws Exception {
        checkTaskMarshalling(getEmptyTopLevelTaskOneIncomeId(), ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallSubprocessLevelTaskOneIncomeFilledProperties() throws Exception {
        checkTaskMarshalling(getFilledSubprocessLevelTaskOneIncomeId(), ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallSubprocessLevelTaskOneIncomeEmptyProperties() throws Exception {
        checkTaskMarshalling(getEmptySubprocessLevelTaskOneIncomeId(), ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallTopLevelTaskTwoIncomesFilledProperties() throws Exception {
        checkTaskMarshalling(getFilledTopLevelTaskTwoIncomesId(), TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallTopLevelTaskTwoIncomesEmptyProperties() throws Exception {
        checkTaskMarshalling(getEmptyTopLevelTaskTwoIncomesId(), TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallSubprocessLevelTaskTwoIncomesFilledProperties() throws Exception {
        checkTaskMarshalling(getFilledSubprocessLevelTaskTwoIncomesId(), TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
    }

    @Test
    public void testMarshallSubprocessLevelTaskTwoIncomesEmptyProperties() throws Exception {
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

    @SuppressWarnings("unchecked")
    void checkTaskMarshalling(String nodeID, int amountOfIncomeEdges, boolean hasOutcomeEdge) throws Exception {
        Diagram<Graph, Metadata> initialDiagram = unmarshall(marshaller, getBpmnTaskFilePath());
        final int AMOUNT_OF_NODES_IN_DIAGRAM = getNodes(initialDiagram).size();
        String resultXml = marshaller.marshall(initialDiagram);

        Diagram<Graph, Metadata> marshalledDiagram = unmarshall(marshaller, getStream(resultXml));
        assertDiagram(marshalledDiagram, AMOUNT_OF_NODES_IN_DIAGRAM);

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
