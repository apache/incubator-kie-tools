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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.assertj.core.api.Assertions.assertThat;

public class LaneTest extends BPMNDiagramMarshallerBase {

    private static final String BPMN_LANE_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/lanesProcess.bpmn";

    private static final String EMPTY_LANE_ID = "_F6EBDAC4-127E-4626-93DC-234EDCEF6353";
    private static final String FILLED_LANE_ID = "_326DD976-52C1-4782-A738-D95A43A0E395";
    private static final String LANE_WITH_NODES_ID = "_9B3559C9-83AE-48E4-98D6-8C03E9116B34";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 7;

    private static final String EMPTY_VALUE = "";

    private DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> marshaller;

    @Before
    public void setUp() {
        super.init();
        this.marshaller = newMarshaller;
    }

    @Test
    public void testMarshallLaneFilledProperties() throws Exception {
        checkLaneMarshalling(FILLED_LANE_ID);
    }

    @Test
    public void testMarshallLaneEmptyProperties() throws Exception {
        checkLaneMarshalling(EMPTY_LANE_ID);
    }

    @Test
    public void testMarshallLaneWithNodes() throws Exception {
        final String START_EVENT_INSIDE_LANE_ID = "_46E9A1B3-A54F-4BA9-A98F-E5869E49C51E";
        final String TASK_INSIDE_LANE_ID = "_58A4AAD8-EB1E-4729-B36F-2089B853E600";
        final String END_EVENT_INSIDE_LANE_ID = "_95807837-C856-4A92-85D6-9EAFCB28B416";

        checkLaneMarshalling(LANE_WITH_NODES_ID,
                             START_EVENT_INSIDE_LANE_ID,
                             TASK_INSIDE_LANE_ID,
                             END_EVENT_INSIDE_LANE_ID);
    }

    @Test
    public void testUnmarshallLaneFilledProperties() throws Exception {
        final String EVENT_NAME = "Lane name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Lane doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_LANE_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        Lane filledLane = getLaneNodeById(diagram, FILLED_LANE_ID);
        assertGeneralSet(filledLane.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);

        assertLaneSubNodes(diagram, FILLED_LANE_ID);
    }

    @Test
    public void testUnmarshallLaneEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_LANE_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        Lane emptyLane = getLaneNodeById(diagram, EMPTY_LANE_ID);
        assertGeneralSet(emptyLane.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);

        assertLaneSubNodes(diagram, EMPTY_LANE_ID);
    }

    @Test
    public void testUnmarshallLaneWithNodes() throws Exception {
        final String EVENT_NAME = "Lane";

        final String START_EVENT_INSIDE_LANE_ID = "_46E9A1B3-A54F-4BA9-A98F-E5869E49C51E";
        final String TASK_INSIDE_LANE_ID = "_58A4AAD8-EB1E-4729-B36F-2089B853E600";
        final String END_EVENT_INSIDE_LANE_ID = "_95807837-C856-4A92-85D6-9EAFCB28B416";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_LANE_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        Lane laneWithNodes = getLaneNodeById(diagram, LANE_WITH_NODES_ID);
        assertGeneralSet(laneWithNodes.getGeneral(), EVENT_NAME, EMPTY_VALUE);

        assertLaneSubNodes(diagram, LANE_WITH_NODES_ID,
                           START_EVENT_INSIDE_LANE_ID,
                           TASK_INSIDE_LANE_ID,
                           END_EVENT_INSIDE_LANE_ID);
    }

    private void assertNodesEqualsAfterMarshalling(Diagram<Graph, Metadata> before, Diagram<Graph, Metadata> after, String nodeId) {
        Lane nodeBeforeMarshalling = getLaneNodeById(before, nodeId);
        Lane nodeAfterMarshalling = getLaneNodeById(after, nodeId);
        assertThat(nodeAfterMarshalling).isEqualTo(nodeBeforeMarshalling);
    }

    private Lane getLaneNodeById(Diagram<Graph, Metadata> diagram, String id) {
        Node<? extends Definition, ?> node = getNodebyId(diagram, id);
        return (Lane) node.getContent().getDefinition();
    }

    private Node<? extends Definition, ?> getNodebyId(Diagram<Graph, Metadata> diagram, String id) {
        Node<? extends Definition, ?> node = diagram.getGraph().getNode(id);
        assertThat(node).isNotNull();
        return node;
    }

    private void checkLaneMarshalling(String nodeID, String... nodesInsideLane) throws Exception {
        Diagram<Graph, Metadata> initialDiagram = unmarshall(marshaller, BPMN_LANE_FILE_PATH);
        final int AMOUNT_OF_NODES_IN_DIAGRAM = getNodes(initialDiagram).size();
        String resultXml = marshaller.marshall(initialDiagram);

        Diagram<Graph, Metadata> marshalledDiagram = unmarshall(marshaller, getStream(resultXml));
        assertDiagram(marshalledDiagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        assertNodesEqualsAfterMarshalling(initialDiagram, marshalledDiagram, nodeID);

        assertLaneSubNodes(marshalledDiagram, nodeID, nodesInsideLane);
    }

    private void assertGeneralSet(BPMNGeneralSet generalSet, String nodeName, String documentation) {
        assertThat(generalSet).isNotNull();
        assertThat(generalSet.getName()).isNotNull();
        assertThat(generalSet.getDocumentation()).isNotNull();
        assertThat(generalSet.getName().getValue()).isEqualTo(nodeName);
        assertThat(generalSet.getDocumentation().getValue()).isEqualTo(documentation);
    }

    private void assertLaneSubNodes(Diagram<Graph, Metadata> diagram, String laneId, String... nodesInsideLane) {
        Node<? extends Definition, ?> node = getNodebyId(diagram, laneId);
        assertThat(node.getInEdges()).hasSize(1);
        assertThat(node.getOutEdges()).hasSize(nodesInsideLane.length);

        List<String> subNodesUUIDs = new ArrayList<>();
        for (Edge item : node.getOutEdges()) {
            assertThat(item).isNotNull();
            assertThat(item.getSourceNode()).isNotNull();
            assertThat(item.getTargetNode()).isNotNull();
            assertThat(item.getSourceNode().getUUID()).isEqualTo(laneId);

            subNodesUUIDs.add(item.getTargetNode().getUUID());
        }
        assertThat(subNodesUUIDs).containsExactlyInAnyOrder(nodesInsideLane);
    }
}
