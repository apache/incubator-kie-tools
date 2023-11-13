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
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.Silent.class)
public class StatelessGraphEvaluationStateTests {

    private TestingGraphMockHandler graphTestHandler;
    private TestingGraphInstances.Level1Graph graphInstance;
    private StatelessGraphEvaluationState tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        graphTestHandler = new TestingGraphMockHandler();
        graphInstance = TestingGraphInstances.newLevel1Graph(graphTestHandler);
        tested = new StatelessGraphEvaluationState(graphInstance.graph);
    }

    @Test
    public void testGetGraph() {
        assertEquals(graphInstance.graph, tested.getGraph());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCardinalityState() {
        StatelessGraphEvaluationState.StatelessCardinalityState cardinalityState = tested.getCardinalityState();
        Iterable<Node> stateNodes = cardinalityState.nodes();
        Set<Node> nodes = StreamSupport.stream(stateNodes.spliterator(), false).collect(Collectors.toSet());
        assertEquals(6, nodes.size());
        assertTrue(nodes.contains(graphInstance.parentNode));
        assertTrue(nodes.contains(graphInstance.containerNode));
        assertTrue(nodes.contains(graphInstance.startNode));
        assertTrue(nodes.contains(graphInstance.intermNode));
        assertTrue(nodes.contains(graphInstance.endNode));
        assertTrue(nodes.contains(graphInstance.nodeA));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConnectorCardinalityState() {
        StatelessGraphEvaluationState.StatelessConnectorCardinalityState cardinalityState = tested.getConnectorCardinalityState();
        Collection<Edge<? extends View<?>, Node>> incomingInterm = cardinalityState.getIncoming(graphInstance.intermNode);
        assertEquals(1, incomingInterm.size());
        assertEquals(graphInstance.edge1, incomingInterm.iterator().next());
        Collection<Edge<? extends View<?>, Node>> outgoingEnd = cardinalityState.getOutgoing(graphInstance.endNode);
        assertTrue(outgoingEnd.isEmpty());
        Collection<Edge<? extends View<?>, Node>> outgoingIterm = cardinalityState.getOutgoing(graphInstance.intermNode);
        assertEquals(1, outgoingIterm.size());
        assertEquals(graphInstance.edge2, outgoingIterm.iterator().next());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConnectionState() {
        StatelessGraphEvaluationState.StatelessConnectionState connectionState = tested.getConnectionState();
        assertEquals(graphInstance.startNode, connectionState.getSource(graphInstance.edge1));
        assertEquals(graphInstance.intermNode, connectionState.getTarget(graphInstance.edge1));
        assertEquals(graphInstance.intermNode, connectionState.getSource(graphInstance.edge2));
        assertEquals(graphInstance.endNode, connectionState.getTarget(graphInstance.edge2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testContainmentState() {
        StatelessGraphEvaluationState.StatelessContainmentState containmentState = tested.getContainmentState();
        assertEquals(graphInstance.containerNode, containmentState.getParent(graphInstance.startNode));
        assertEquals(graphInstance.containerNode, containmentState.getParent(graphInstance.intermNode));
        assertEquals(graphInstance.containerNode, containmentState.getParent(graphInstance.endNode));
        assertEquals(graphInstance.parentNode, containmentState.getParent(graphInstance.nodeA));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDockingState() {
        graphTestHandler.dockTo(graphInstance.startNode, graphInstance.nodeA);
        StatelessGraphEvaluationState.StatelessDockingState dockingState = tested.getDockingState();
        assertEquals(graphInstance.startNode, dockingState.getDockedTo(graphInstance.nodeA));
        assertNull(dockingState.getDockedTo(graphInstance.startNode));
        assertNull(dockingState.getDockedTo(graphInstance.intermNode));
        assertNull(dockingState.getDockedTo(graphInstance.endNode));
    }
}
