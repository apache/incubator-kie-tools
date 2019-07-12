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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.events;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBase;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.BaseStartEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
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
public abstract class StartEvent<T extends BaseStartEvent> extends BPMNDiagramMarshallerBase {

    static final String EMPTY_VALUE = "";
    static final boolean NON_INTERRUPTING = false;
    static final boolean INTERRUPTING = true;

    private Marshaller marshallerType;

    protected DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> marshaller = null;

    StartEvent(Marshaller marshallerType) {
        super.init();
        this.marshallerType = marshallerType;
        switch (marshallerType) {
            case OLD:
                marshaller = oldMarshaller;
                break;
            case NEW:
                marshaller = newMarshaller;
                break;
        }
    }

    @Parameterized.Parameters
    public static List<Object[]> marshallers() {
        return Arrays.asList(new Object[][]{
                // New (un)marshaller is disabled for now due to found incompleteness
                {OLD}, {NEW}
        });
    }

    @Test
    public void testMarshallTopLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(getStartEventType(), getFilledTopLevelEventId());
    }

    @Test
    public void testMarshallTopLevelEmptyEventProperties() throws Exception {
        checkEventMarshalling(getStartEventType(), getEmptyTopLevelEventId());
    }

    @Test
    public void testMarshallSubprocessLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(getStartEventType(), getFilledSubprocessLevelEventId());
    }

    @Test
    public void testMarshallSubprocessLevelEventEmptyProperties() throws Exception {
        checkEventMarshalling(getStartEventType(), getEmptySubprocessLevelEventId());
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

    private void assertNodesEqualsAfterMarshalling(Diagram<Graph, Metadata> before, Diagram<Graph, Metadata> after, String nodeId, Class<T> startType) {
        T nodeBeforeMarshalling = getStartNodeById(before, nodeId, startType);
        T nodeAfterMarshalling = getStartNodeById(after, nodeId, startType);
        assertEquals(nodeBeforeMarshalling, nodeAfterMarshalling);
    }

    @SuppressWarnings("unchecked")
    T getStartNodeById(Diagram<Graph, Metadata> diagram, String id, Class<T> type) {
        Node<? extends Definition, ?> node = diagram.getGraph().getNode(id);
        assertNotNull(node);
        assertEquals(1, node.getOutEdges().size());
        return type.cast(node.getContent().getDefinition());
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

    @SuppressWarnings("unchecked")
    void checkEventMarshalling(Class startNodeType, String nodeID) throws Exception {
        Diagram<Graph, Metadata> initialDiagram = unmarshall(marshaller, getBpmnStartEventFilePath());
        final int AMOUNT_OF_NODES_IN_DIAGRAM = getNodes(initialDiagram).size();
        String resultXml = marshaller.marshall(initialDiagram);

        Diagram<Graph, Metadata> marshalledDiagram = unmarshall(marshaller, getStream(resultXml));
        assertDiagram(marshalledDiagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        assertNodesEqualsAfterMarshalling(initialDiagram, marshalledDiagram, nodeID, startNodeType);
    }

    protected Marshaller getMarshallerType() {
        return this.marshallerType;
    }

    protected void assertStartEventSlaDueDate(BaseStartEventExecutionSet executionSet, String slaDueDate) {
        if (getMarshallerType() == Marshaller.NEW) {
            assertNotNull(executionSet.getSlaDueDate());
            assertEquals(slaDueDate, executionSet.getSlaDueDate().getValue());
        }
    }

    protected void assertStartEventIsInterrupting(BaseStartEventExecutionSet executionSet, boolean isInterrupting) {
        if (getMarshallerType() == Marshaller.NEW) {
            assertNotNull(executionSet.getIsInterrupting());
            assertEquals(isInterrupting, executionSet.getIsInterrupting().getValue());
        }
    }
}
