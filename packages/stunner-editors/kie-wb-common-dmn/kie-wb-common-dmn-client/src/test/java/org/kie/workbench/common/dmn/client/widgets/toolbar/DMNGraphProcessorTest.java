/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNElementReference;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.processing.layout.VertexPosition;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.kie.workbench.common.dmn.client.widgets.toolbar.DMNGraphProcessor.DEFAULT_TOP_VERTICAL_PADDING;
import static org.kie.workbench.common.dmn.client.widgets.toolbar.DMNGraphProcessor.INPUT_NODE_VERTICAL_PADDING;
import static org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.VertexPositioning.DEFAULT_VERTEX_HEIGHT;
import static org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.VertexPositioning.DEFAULT_VERTEX_WIDTH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNGraphProcessorTest {

    private DMNGraphProcessor processor;

    @Before
    public void setup() {
        this.processor = spy(new DMNGraphProcessor());
    }

    @Test
    public void testGetNodes() {
        final Graph graph = mock(Graph.class);
        final Decision decision1 = mock(Decision.class);
        final Decision decision2 = mock(Decision.class);
        final Node node1 = createNode(decision1, "id1");
        final Node node2 = createNode(decision2, "id2");
        final List<Node> graphNodes = createGraphNodes(node1, node2);

        when(graph.nodes()).thenReturn(graphNodes);

        final Iterable<? extends Node> nodes = processor.getNodes(graph);
        for (final Node node : nodes) {
            assertTrue(graphNodes.contains(node));
            assertFalse(processor.isReplacedByAnotherNode(node.getUUID()));
        }
    }

    @Test
    public void testGetNodesWithDecisionServices() {

        final String ds1Id = "ds1Id";
        final String ds2Id = "ds2Id";
        final Graph graph = mock(Graph.class);
        final Decision randomDecision = mock(Decision.class);
        final DecisionService ds1 = mock(DecisionService.class);
        final DecisionService ds2 = mock(DecisionService.class);
        final Decision ds1Child1 = mock(Decision.class);
        final Decision ds1Child2 = mock(Decision.class);
        final Decision ds2SingleChild = mock(Decision.class);
        final Map<String, DecisionService> decisionServices = new HashMap() {{
            put(ds1Id, ds1);
            put(ds2Id, ds2);
        }};

        final Node ds1Node = createNode(ds1, ds1Id);
        final Node ds2Node = createNode(ds2, ds2Id);
        final Node ds1Child1Node = createNode(ds1Child1, "ds1Child1");
        final Node ds1Child2Node = createNode(ds1Child2, "ds1Child2");
        final Node ds2SingleChildNode = createNode(ds2SingleChild, "ds2SingleChild");
        final Node randomDecisionNode = createNode(randomDecision, "randomDecision");

        final List<Node> graphNodes = createGraphNodes(ds1Node,
                                                       ds2Node,
                                                       ds1Child1Node,
                                                       ds1Child2Node,
                                                       ds2SingleChildNode,
                                                       randomDecisionNode);

        doReturn(graphNodes).when(processor).extractGraphNodes(graph);
        doReturn(decisionServices).when(processor).getDecisionServicesNodes(graph);

        processor.getNodes(graph);

        verify(processor).replaceDecisionServiceInnerNodes(graphNodes,
                                                           ds1Id,
                                                           ds1);

        verify(processor).replaceDecisionServiceInnerNodes(graphNodes,
                                                           ds2Id,
                                                           ds2);
    }

    @Test
    public void testExtractGraphNodes() {

        final Graph graph = mock(Graph.class);
        final Node node1 = mock(Node.class);
        final Node node2 = mock(Node.class);
        final Node node3 = mock(Node.class);
        final List<Node> graphNodes = createGraphNodes(node1,
                                                       node2,
                                                       node3);
        when(graph.nodes()).thenReturn(graphNodes);

        final List<Node> extractedNodes = processor.extractGraphNodes(graph);

        assertEquals(3, extractedNodes.size());
        assertTrue(extractedNodes.contains(node1));
        assertTrue(extractedNodes.contains(node2));
        assertTrue(extractedNodes.contains(node3));
    }

    @Test
    public void testReplaceDecisionServiceInnerNodes() {

        final String child1Id = "childId1";
        final String child2Id = "childId2";
        final String nonChildId = "nonChildId";
        final String nodeUuid = "decisionServiceNodeUuid";
        final DecisionService decisionService = new DecisionService();
        final Decision child1 = mock(Decision.class);
        final Decision child2 = mock(Decision.class);
        final Decision nonChild = mock(Decision.class);

        final Node child1Node = createNode(child1, child1Id);
        final Node child2Node = createNode(child2, child2Id);
        final Node nonChildNode = createNode(nonChild, nonChildId);
        final Node decisionServiceNode = createNode(decisionService, nodeUuid);

        final List<Node> nodes = new ArrayList(Arrays.asList(decisionServiceNode,
                                                             child1Node,
                                                             nonChildNode,
                                                             child2Node));

        final HashSet<String> innerIds = new HashSet<>(Arrays.asList(child1Id,
                                                                     child2Id));

        doReturn(innerIds).when(processor).getInnerIds(decisionService);
        doReturn(true).when(processor).hasContentDefinitionId(any());
        doReturn(child1Id).when(processor).getContentDefinitionId(child1Node);
        doReturn(child2Id).when(processor).getContentDefinitionId(child2Node);
        doReturn(nonChildId).when(processor).getContentDefinitionId(nonChildNode);

        processor.replaceDecisionServiceInnerNodes(nodes,
                                                   nodeUuid,
                                                   decisionService);

        final Map<String, String> replacedNodes = processor.getReplacedNodes();

        assertTrue(replacedNodes.containsKey(child1Id));
        assertTrue(replacedNodes.containsKey(child2Id));
        assertFalse(replacedNodes.containsKey(nonChildId));
        assertEquals(2, nodes.size());
        assertTrue(nodes.contains(decisionServiceNode));
        assertTrue(nodes.contains(nonChildNode));
    }

    @Test
    public void testGetInnerIds() {

        final DecisionService decisionService = mock(DecisionService.class);
        final List<DMNElementReference> encapsulatedDecisions = mock(List.class);
        final List<DMNElementReference> outputDecisions = mock(List.class);
        final String outputId = "outputId";
        final String encapsulatedId = "encapsulatedId";
        final List<String> outputDecisionsId = Collections.singletonList(outputId);
        final List<String> encapsulatedDecisionsId = Collections.singletonList(encapsulatedId);

        when(decisionService.getEncapsulatedDecision()).thenReturn(encapsulatedDecisions);
        when(decisionService.getOutputDecision()).thenReturn(outputDecisions);

        doReturn(outputDecisionsId).when(processor).getDecisionIds(outputDecisions);
        doReturn(encapsulatedDecisionsId).when(processor).getDecisionIds(encapsulatedDecisions);

        final Set<String> innerIds = processor.getInnerIds(decisionService);

        assertEquals(2, innerIds.size());
        assertTrue(innerIds.contains(outputId));
        assertTrue(innerIds.contains(encapsulatedId));
    }

    @Test
    public void testGetDecisionsIds() {

        final String href1 = UUID.randomUUID().toString();
        final String href2 = UUID.randomUUID().toString();
        final String href3 = UUID.randomUUID().toString();
        final DMNElementReference reference1 = new DMNElementReference();
        final DMNElementReference reference2 = new DMNElementReference();
        final DMNElementReference reference3 = new DMNElementReference();

        reference1.setHref("#" + href1);
        reference2.setHref("#" + href2);
        reference3.setHref("#" + href3);

        final List<String> decisionIds = processor.getDecisionIds(Arrays.asList(reference1,
                                                                                reference2,
                                                                                reference3));

        assertEquals(3, decisionIds.size());
        assertTrue(decisionIds.contains(href1));
        assertTrue(decisionIds.contains(href2));
        assertTrue(decisionIds.contains(href3));
    }

    @Test
    public void testGetDecisionServicesNodes() {

        final Decision decision = mock(Decision.class);
        final DecisionService decisionService = mock(DecisionService.class);
        final String id = "id";
        final Node decisionNode = createNode(decision, "someId");
        final Node decisionServiceNode = createNode(decisionService, id);
        final Graph graph = mock(Graph.class);
        final List<Node> graphNodes = createGraphNodes(decisionServiceNode, decisionNode);

        when(graph.nodes()).thenReturn(graphNodes);

        final Map<String, DecisionService> decisionServiceNodes = processor.getDecisionServicesNodes(graph);

        assertEquals(1, decisionServiceNodes.size());
        assertTrue(decisionServiceNodes.containsKey(id));
        assertTrue(decisionServiceNodes.containsValue(decisionService));
    }

    @Test
    public void testGetContentDefinitionId() {

        final HasContentDefinitionId hasContentDefinitionId = mock(HasContentDefinitionId.class);
        final String contentDefinitionId = "someContentId";
        when(hasContentDefinitionId.getContentDefinitionId()).thenReturn(contentDefinitionId);

        final Node node = createNode(hasContentDefinitionId, "thisIsTheNodeIdNotTheContent");

        final String result = processor.getContentDefinitionId(node);

        assertEquals(contentDefinitionId, result);
    }

    @Test
    public void testHasContentDefinitionId() {
        final HasContentDefinitionId hasContentDefinitionId = mock(HasContentDefinitionId.class);
        final String contentDefinitionId = "someContentId";
        when(hasContentDefinitionId.getContentDefinitionId()).thenReturn(contentDefinitionId);

        final Node node = createNode(hasContentDefinitionId, "thisIsTheNodeIdNotTheContent");

        assertTrue(processor.hasContentDefinitionId(node));
    }

    @Test
    public void testHasContentDefinitionIdWhenDoesNot() {

        final Node node = createNode(new Object(), "thisIsTheNodeIdNotTheContent");

        assertFalse(processor.hasContentDefinitionId(node));
    }

    @Test
    public void testConnect() {

        final Node parentNode = mock(Node.class);
        final Node innerNode = mock(Node.class);
        final Edge edge = mock(Edge.class);

        final List parentOutEdges = mock(List.class);
        final List parentInEdges = mock(List.class);
        final List innerOutEdges = mock(List.class);
        final List innerInEdges = new ArrayList();
        final Edge olderParentEdge = mock(Edge.class);
        final Edge someOtherNonRelatedEdge = mock(Edge.class);
        final Child child = mock(Child.class);
        when(olderParentEdge.getContent()).thenReturn(child);

        innerInEdges.add(olderParentEdge);
        innerInEdges.add(someOtherNonRelatedEdge);

        when(parentNode.getOutEdges()).thenReturn(parentOutEdges);
        when(parentNode.getInEdges()).thenReturn(parentInEdges);
        when(innerNode.getOutEdges()).thenReturn(innerOutEdges);
        when(innerNode.getInEdges()).thenReturn(innerInEdges);

        doReturn(edge).when(processor).createEdge(parentNode, innerNode);

        processor.connect(parentNode, innerNode);

        verify(parentOutEdges).add(edge);
        verify(parentInEdges, never()).add(edge);
        verify(innerOutEdges, never()).add(edge);

        assertFalse(innerInEdges.contains(olderParentEdge));
        assertTrue(innerInEdges.contains(edge));
        assertTrue(innerInEdges.contains(someOtherNonRelatedEdge));
    }

    @Test
    public void testGetChildVertexPosition() {

        final String parentId = "parentId";
        final String innerNodeId = "innerId";
        final Double horizontalPadding = 123.0;
        final Double verticalPadding = 444.0;
        final Graph<?, ?> graph = mock(Graph.class);

        final Node parentNode = mock(Node.class);
        final Optional<Node> parentNodeOpt = Optional.of(parentNode);

        doReturn(parentNodeOpt).when(processor).getNodeFromGraph(parentId, graph);
        doReturn(verticalPadding).when(processor).getVerticalPadding(parentNode, innerNodeId);

        final VertexPosition position = processor.getChildVertexPosition(parentId,
                                                                         innerNodeId,
                                                                         horizontalPadding,
                                                                         graph);

        assertEquals(innerNodeId, position.getId());
        assertEquals(horizontalPadding, position.getUpperLeft().getX());
        assertEquals(verticalPadding, position.getUpperLeft().getY());
        assertEquals(horizontalPadding + DEFAULT_VERTEX_WIDTH, position.getBottomRight().getX());
        assertEquals(verticalPadding + DEFAULT_VERTEX_HEIGHT, position.getBottomRight().getY());
    }

    @Test
    public void testGetVerticalPadding_WhenThereIsNoTargetNode() {

        final Node parentNode = mock(Node.class);
        final String innerNodeId = "innerNodeId";

        doReturn(Optional.empty()).when(processor).getTargetEdgeToId(parentNode, innerNodeId);

        final double verticalPadding = processor.getVerticalPadding(parentNode, innerNodeId);

        assertEquals(0, verticalPadding, 0.01);
    }

    @Test
    public void testGetVerticalPadding_WhenIsOutputNode() {

        final DecisionService decisionServiceOfParentNode = mock(DecisionService.class);
        final Node parentNode = createNode(decisionServiceOfParentNode, "dsId");
        final Edge edge = mock(Edge.class);
        final HasContentDefinitionId contentOfTarget = mock(HasContentDefinitionId.class);
        final String idOfTarget = "idOfTarget";
        final String innerNodeId = "innerNodeId";
        final Node targetNode = createNode(contentOfTarget, "randomId");

        when(contentOfTarget.getContentDefinitionId()).thenReturn(idOfTarget);
        when(edge.getTargetNode()).thenReturn(targetNode);

        doReturn(Optional.of(edge)).when(processor).getTargetEdgeToId(parentNode, innerNodeId);
        doReturn(true).when(processor).isOutput(decisionServiceOfParentNode, "#" + idOfTarget);

        double verticalPadding = processor.getVerticalPadding(parentNode,
                                                              innerNodeId);

        assertEquals(DEFAULT_TOP_VERTICAL_PADDING, verticalPadding, 0.01);
    }

    @Test
    public void testGetVerticalPadding_WhenIsInputNode() {

        final DecisionService decisionServiceOfParentNode = mock(DecisionService.class);
        final Node parentNode = createNode(decisionServiceOfParentNode, "dsId");
        final Edge edge = mock(Edge.class);
        final HasContentDefinitionId contentOfTarget = mock(HasContentDefinitionId.class);
        final String idOfTarget = "idOfTarget";
        final String innerNodeId = "innerNodeId";
        final Node targetNode = createNode(contentOfTarget, "randomId");

        when(contentOfTarget.getContentDefinitionId()).thenReturn(idOfTarget);
        when(edge.getTargetNode()).thenReturn(targetNode);

        doReturn(Optional.of(edge)).when(processor).getTargetEdgeToId(parentNode, innerNodeId);
        doReturn(false).when(processor).isOutput(decisionServiceOfParentNode, "#" + idOfTarget);

        double verticalPadding = processor.getVerticalPadding(parentNode,
                                                              innerNodeId);

        assertEquals(INPUT_NODE_VERTICAL_PADDING, verticalPadding, 0.01);
    }

    @Test
    public void testIsOutput_WhenItIs() {

        final DecisionService decisionService = new DecisionService();
        final DMNElementReference reference = new DMNElementReference();
        final String theId = "#theid";

        reference.setHref(theId);

        decisionService.getOutputDecision().add(reference);

        assertTrue(processor.isOutput(decisionService, theId));
    }

    @Test
    public void testIsOutput_WhenItIsNot() {

        final DecisionService decisionService = new DecisionService();
        final DMNElementReference reference = new DMNElementReference();
        final String theId = "#theid";

        reference.setHref(theId);

        decisionService.getEncapsulatedDecision().add(reference);

        assertFalse(processor.isOutput(decisionService, theId));
    }

    @Test
    public void testCreateEdge() {

        final Node parentNode = mock(Node.class);
        final Node innerNode = mock(Node.class);

        final Edge<Child, Node> edge = processor.createEdge(parentNode, innerNode);

        assertEquals(parentNode, edge.getSourceNode());
        assertEquals(innerNode, edge.getTargetNode());
    }

    private List<Node> createGraphNodes(final Node... nodes) {
        return Arrays.asList(nodes);
    }

    private Node createNode(final Object definition, final String nodeId) {
        final Node node = mock(Node.class);
        when(node.getUUID()).thenReturn(nodeId);
        final Definition content = mock(Definition.class);
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(definition);

        return node;
    }
}