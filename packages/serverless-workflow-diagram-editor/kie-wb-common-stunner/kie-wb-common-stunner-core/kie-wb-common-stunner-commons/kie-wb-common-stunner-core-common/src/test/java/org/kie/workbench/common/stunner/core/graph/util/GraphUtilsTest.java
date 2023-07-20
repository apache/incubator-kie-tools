/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.util;

import java.util.List;
import java.util.OptionalInt;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.TestingSimpleDomainObject;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.graph.util.GraphUtils.computeCardinalityState;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class GraphUtilsTest {

    @Mock
    private Element<? extends Definition> element;

    @Mock
    private Definition content;

    private TestingGraphMockHandler graphTestHandler;
    private TestingGraphInstanceBuilder.TestGraph4 graphInstance;
    private TestingSimpleDomainObject domainObject;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        graphTestHandler = new TestingGraphMockHandler();
        graphInstance = TestingGraphInstanceBuilder.newGraph4(graphTestHandler);
        domainObject = new TestingSimpleDomainObject(graphTestHandler);
        when(element.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(domainObject);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void hasChildrenTest() {
        boolean hasChildren = GraphUtils.hasChildren(graphInstance.parentNode);
        assertTrue(hasChildren);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void notHasChildrenTest() {
        boolean hasChildren = GraphUtils.hasChildren(graphInstance.startNode);
        assertFalse(hasChildren);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void countChildrenTest() {
        Long countChildren = GraphUtils.countChildren(graphInstance.parentNode);
        assertEquals(Long.valueOf(4),
                     countChildren);
    }

    @Test
    public void checkBoundsExceededTest() {
        Bounds parentBounds = Bounds.create(50d, 50d, 200d, 200d);

        Bounds childBounds = Bounds.create(51d, 51d, 199d, 199d);
        assertTrue(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(51d, 51d, 200d, 200d);
        assertTrue(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(50d, 50d, 199d, 199d);
        assertTrue(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(50d, 50d, 200d, 200d);
        assertTrue(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(49d, 49d, 201d, 201d);
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(49d, 49d, 200d, 200d);
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(49d, 49d, 199d, 199d);
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(49d, 49d, 201d, 201d);
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(50d, 49d, 201d, 201d);
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(50d, 49d, 201d, 201d);
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void isDockedNodeTest() {
        assertTrue(GraphUtils.isDockedNode(graphInstance.dockedNode));
        assertFalse(GraphUtils.isDockedNode(graphInstance.startNode));
        assertFalse(GraphUtils.isDockedNode(graphInstance.intermNode));
        assertFalse(GraphUtils.isDockedNode(graphInstance.endNode));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getDockedNodesTest() {
        List<Node> dockedNodes = GraphUtils.getDockedNodes(graphInstance.intermNode);
        assertEquals(dockedNodes.size(), 1);
        assertEquals(dockedNodes.get(0), graphInstance.dockedNode);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getChildNodesTest() {
        List<Node> dockedNodes = GraphUtils.getChildNodes(graphInstance.parentNode);
        assertEquals(dockedNodes.size(), 4);
        assertEquals(dockedNodes.get(0), graphInstance.startNode);
        assertEquals(dockedNodes.get(1), graphInstance.intermNode);
        assertEquals(dockedNodes.get(2), graphInstance.endNode);
        assertEquals(dockedNodes.get(3), graphInstance.dockedNode);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetChildIndex() {
        final OptionalInt index1 = GraphUtils.getChildIndex(graphInstance.parentNode, TestingGraphInstanceBuilder.START_NODE_UUID);
        assertTrue(index1.isPresent());
        assertEquals(0, index1.getAsInt());

        final OptionalInt index2 = GraphUtils.getChildIndex(graphInstance.parentNode, TestingGraphInstanceBuilder.INTERM_NODE_UUID);
        assertTrue(index2.isPresent());
        assertEquals(1, index2.getAsInt());

        final OptionalInt index3 = GraphUtils.getChildIndex(graphInstance.parentNode, TestingGraphInstanceBuilder.END_NODE_UUID);
        assertTrue(index3.isPresent());
        assertEquals(2, index3.getAsInt());

        final OptionalInt index4 = GraphUtils.getChildIndex(graphInstance.parentNode, TestingGraphInstanceBuilder.DOCKED_NODE_UUID);
        assertTrue(index4.isPresent());
        assertEquals(3, index4.getAsInt());

        final OptionalInt index5 = GraphUtils.getChildIndex(graphInstance.parentNode, "node_not_exist");
        assertFalse(index5.isPresent());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testComputeCardinalityState() {
        String canvasRootUUID = "rootUUID";
        Node rootNode = new NodeImpl<>(canvasRootUUID);
        Node node1 = new NodeImpl<>("node1");
        Node node2 = new NodeImpl<>("node2");
        Node node3 = new NodeImpl<>("node3");
        Metadata metadata = new MetadataImpl();
        metadata.setCanvasRootUUID(canvasRootUUID);
        Graph graph = new GraphImpl<>("graph1", new GraphNodeStoreImpl());
        DiagramImpl diagram = new DiagramImpl("diagram1", metadata);
        diagram.setGraph(graph);
        assertEquals(GraphUtils.CardinalityCountState.EMPTY, computeCardinalityState(diagram));
        graph.addNode(rootNode);
        assertEquals(GraphUtils.CardinalityCountState.EMPTY, computeCardinalityState(diagram));
        graph.addNode(node1);
        assertEquals(GraphUtils.CardinalityCountState.SINGLE_NODE, computeCardinalityState(diagram));
        graph.addNode(node2);
        assertEquals(GraphUtils.CardinalityCountState.MULTIPLE_NODES, computeCardinalityState(diagram));
        graph.clear();
        assertEquals(GraphUtils.CardinalityCountState.EMPTY, computeCardinalityState(diagram));
        graph.addNode(node1);
        assertEquals(GraphUtils.CardinalityCountState.SINGLE_NODE, computeCardinalityState(diagram));
        graph.addNode(rootNode);
        assertEquals(GraphUtils.CardinalityCountState.SINGLE_NODE, computeCardinalityState(diagram));
        graph.addNode(node2);
        assertEquals(GraphUtils.CardinalityCountState.MULTIPLE_NODES, computeCardinalityState(diagram));
        graph.clear();
        assertEquals(GraphUtils.CardinalityCountState.EMPTY, computeCardinalityState(diagram));
        graph.addNode(node1);
        assertEquals(GraphUtils.CardinalityCountState.SINGLE_NODE, computeCardinalityState(diagram));
        graph.addNode(node2);
        assertEquals(GraphUtils.CardinalityCountState.MULTIPLE_NODES, computeCardinalityState(diagram));
        graph.addNode(node3);
        assertEquals(GraphUtils.CardinalityCountState.MULTIPLE_NODES, computeCardinalityState(diagram));
    }

    @SuppressWarnings("all")
    @Ignore("It randomly fails in GitHub CI/CD")
    public void testComputeGraphHashCode() {
        final GraphImpl graph = (GraphImpl) graphInstance.graph;
        final int hashCode = GraphUtils.computeGraphHashCode(graph);
        // Assert hash is time inmutable
        assertEquals(hashCode, GraphUtils.computeGraphHashCode(graph), 0d);
        // Change some node's shape location.
        View nodeContent = (View) graphInstance.startNode.getContent();
        Bound lowerRight = nodeContent.getBounds().getLowerRight();
        Double x = lowerRight.getX();
        Double y = lowerRight.getY();
        nodeContent.getBounds().setLowerRight(Bound.create(123d, 123d));
        assertNotEquals(hashCode, GraphUtils.computeGraphHashCode(graph), 0d);
        nodeContent.getBounds().setLowerRight(Bound.create(x, y));
        // Rollback node's location changed.
        assertEquals(hashCode, GraphUtils.computeGraphHashCode(graph), 0d);
        // Change some connection.
        ViewConnector<MagnetConnection> connectorContent = (ViewConnector) graphInstance.edge1.getContent();
        MagnetConnection sconnection = (MagnetConnection) connectorContent.getSourceConnection().get();
        MagnetConnection tconnection = (MagnetConnection) connectorContent.getTargetConnection().get();
        Point2D sconnectionLocation = sconnection.getLocation();
        Point2D tconnectionLocation = tconnection.getLocation();
        sconnection.setLocation(Point2D.create(123d, 123d));
        tconnection.setLocation(Point2D.create(321d, 321d));
        assertNotEquals(hashCode, GraphUtils.computeGraphHashCode(graph), 0d);
        sconnection.setLocation(sconnectionLocation);
        tconnection.setLocation(tconnectionLocation);
        // Rollback connection changed.
        assertEquals(hashCode, GraphUtils.computeGraphHashCode(graph), 0d);
    }
}
