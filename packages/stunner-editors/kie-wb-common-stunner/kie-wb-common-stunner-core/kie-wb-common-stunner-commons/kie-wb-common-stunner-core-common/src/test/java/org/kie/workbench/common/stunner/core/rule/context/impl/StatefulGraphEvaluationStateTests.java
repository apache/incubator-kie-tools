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


package org.kie.workbench.common.stunner.core.rule.context.impl;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstances;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.Silent.class)
public class StatefulGraphEvaluationStateTests {

    private TestingGraphMockHandler graphTestHandler;
    private TestingGraphInstances.Level1Graph graphInstance;
    private StatefulGraphEvaluationState tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        graphTestHandler = new TestingGraphMockHandler();
        graphInstance = TestingGraphInstances.newLevel1Graph(graphTestHandler);
        tested = new StatefulGraphEvaluationState(graphInstance.graph);
    }

    @Test
    public void testGetGraph() {
        assertEquals(graphInstance.graph, tested.getGraph());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCardinalityState() {
        StatefulGraphEvaluationState.StatefulCardinalityState cardinalityState = tested.getCardinalityState();
        NodeImpl someNewNode = new NodeImpl<>("someNewNodeUUID");
        cardinalityState.add(someNewNode);
        cardinalityState.delete(graphInstance.nodeA);
        assertTrue(cardinalityState.getAddedElements().contains(someNewNode));
        assertTrue(cardinalityState.getDeletedElements().contains(graphInstance.nodeA));
        Iterable<Node> stateNodes = cardinalityState.nodes();
        Set<Node> nodes = StreamSupport.stream(stateNodes.spliterator(), false).collect(Collectors.toSet());
        assertEquals(6, nodes.size());
        assertTrue(nodes.contains(graphInstance.parentNode));
        assertTrue(nodes.contains(graphInstance.containerNode));
        assertTrue(nodes.contains(graphInstance.startNode));
        assertTrue(nodes.contains(graphInstance.intermNode));
        assertTrue(nodes.contains(graphInstance.endNode));
        assertTrue(nodes.contains(someNewNode));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConnectorCardinalityState() {
        StatefulGraphEvaluationState.StatefulConnectorCardinalityState cardinalityState = tested.getConnectorCardinalityState();
        Edge someNewEdge = new EdgeImpl<>("someNewEdgeUUID");
        cardinalityState.addIncoming(graphInstance.nodeA, someNewEdge);
        cardinalityState.deleteIncoming(graphInstance.intermNode, graphInstance.edge1);
        cardinalityState.addOutgoing(graphInstance.endNode, someNewEdge);
        cardinalityState.deleteOutgoing(graphInstance.intermNode, graphInstance.edge2);
        Collection<Edge<? extends View<?>, Node>> incomingA = cardinalityState.getIncoming(graphInstance.nodeA);
        assertEquals(1, incomingA.size());
        assertEquals(someNewEdge, incomingA.iterator().next());
        Collection<Edge<? extends View<?>, Node>> incomingIterm = cardinalityState.getIncoming(graphInstance.intermNode);
        assertTrue(incomingIterm.isEmpty());
        Collection<Edge<? extends View<?>, Node>> outgoingEnd = cardinalityState.getOutgoing(graphInstance.endNode);
        assertEquals(1, outgoingEnd.size());
        assertEquals(someNewEdge, outgoingEnd.iterator().next());
        Collection<Edge<? extends View<?>, Node>> outgoingIterm = cardinalityState.getOutgoing(graphInstance.intermNode);
        assertTrue(outgoingIterm.isEmpty());
        Collection<Edge<? extends View<?>, Node>> outgoingStart = cardinalityState.getOutgoing(graphInstance.startNode);
        assertEquals(1, outgoingStart.size());
        assertEquals(graphInstance.edge1, outgoingStart.iterator().next());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConnectionState() {
        StatefulGraphEvaluationState.StatefulConnectionState connectionState = tested.getConnectionState();
        connectionState.setSourceNode(graphInstance.edge1, graphInstance.intermNode);
        connectionState.setTargetNode(graphInstance.edge1, graphInstance.endNode);
        connectionState.setTargetNode(graphInstance.edge2, graphInstance.nodeA);
        assertEquals(graphInstance.intermNode, connectionState.getSource(graphInstance.edge1));
        assertEquals(graphInstance.endNode, connectionState.getTarget(graphInstance.edge1));
        assertEquals(graphInstance.intermNode, connectionState.getSource(graphInstance.edge2));
        assertEquals(graphInstance.nodeA, connectionState.getTarget(graphInstance.edge2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testContainmentState() {
        StatefulGraphEvaluationState.StatefulContainmentState containmentState = tested.getContainmentState();
        containmentState.setParent(graphInstance.startNode, graphInstance.parentNode);
        containmentState.setParent(graphInstance.nodeA, graphInstance.containerNode);
        assertEquals(graphInstance.parentNode, containmentState.getParent(graphInstance.startNode));
        assertEquals(graphInstance.containerNode, containmentState.getParent(graphInstance.nodeA));
        assertEquals(graphInstance.containerNode, containmentState.getParent(graphInstance.intermNode));
        assertEquals(graphInstance.containerNode, containmentState.getParent(graphInstance.endNode));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDockingState() {
        StatefulGraphEvaluationState.StatefulDockingState dockingState = tested.getDockingState();
        dockingState.setDockedTo(graphInstance.nodeA, graphInstance.startNode);
        assertEquals(graphInstance.startNode, dockingState.getDockedTo(graphInstance.nodeA));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClear() {
        StatefulGraphEvaluationState.StatefulCardinalityState cardinalityState = tested.getCardinalityState();
        NodeImpl someNewNode = new NodeImpl<>("someNewNodeUUID");
        cardinalityState.add(someNewNode);
        cardinalityState.delete(graphInstance.nodeA);
        StatefulGraphEvaluationState.StatefulConnectorCardinalityState connectorCardinalityState = tested.getConnectorCardinalityState();
        Edge someNewEdge = new EdgeImpl<>("someNewEdgeUUID");
        connectorCardinalityState.addIncoming(graphInstance.nodeA, someNewEdge);
        connectorCardinalityState.deleteIncoming(graphInstance.intermNode, graphInstance.edge1);
        connectorCardinalityState.addOutgoing(graphInstance.endNode, someNewEdge);
        connectorCardinalityState.deleteOutgoing(graphInstance.intermNode, graphInstance.edge2);
        StatefulGraphEvaluationState.StatefulContainmentState containmentState = tested.getContainmentState();
        containmentState.setParent(graphInstance.startNode, graphInstance.parentNode);
        StatefulGraphEvaluationState.StatefulDockingState dockingState = tested.getDockingState();
        dockingState.setDockedTo(graphInstance.nodeA, graphInstance.startNode);
        tested.clear();
        assertTrue(cardinalityState.getAddedElements().isEmpty());
        assertTrue(cardinalityState.getDeletedElements().isEmpty());
        assertTrue(connectorCardinalityState.getIncoming().isEmpty());
        assertTrue(connectorCardinalityState.getOutgoing().isEmpty());
        assertTrue(containmentState.getParents().isEmpty());
        assertTrue(dockingState.getDockedElements().isEmpty());
    }
}
