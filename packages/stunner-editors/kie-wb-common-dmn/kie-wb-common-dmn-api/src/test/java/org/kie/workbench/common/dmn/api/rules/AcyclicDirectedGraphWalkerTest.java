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
package org.kie.workbench.common.dmn.api.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AcyclicDirectedGraphWalkerTest {

    @Mock
    private TreeTraverseCallback callback;

    @Captor
    private ArgumentCaptor<Node> nodeStartVisitCaptor;

    @Captor
    private ArgumentCaptor<Node> nodeEndVisitCaptor;

    @Captor
    private ArgumentCaptor<Edge> edgeStartVisitCaptor;

    @Captor
    private ArgumentCaptor<Edge> edgeEndVisitCaptor;

    private GraphImpl graph = new GraphImpl("uuid",
                                            new GraphNodeStoreImpl());

    private TreeWalkTraverseProcessor walker;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(callback.startNodeTraversal(any(Node.class))).thenReturn(true);
        when(callback.startEdgeTraversal(any(Edge.class))).thenReturn(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkSimpleWalk() {
        final Node node1 = new NodeImpl<>("node1");
        final Node node2 = new NodeImpl<>("node2");
        final Node node3 = new NodeImpl<>("node3");
        final Edge c1 = new EdgeImpl<>("edge1:node1-node2");
        final Edge c2 = new EdgeImpl<>("edge2:node2-node3");

        //Connect node1 and node2 with c1
        connectNodes(node1,
                     node2,
                     c1);

        graph.addNode(node1);
        graph.addNode(node2);

        //Propose to connect node2 to node3 with c2
        walker = new AcyclicDirectedGraphWalker(node2,
                                                node3,
                                                c2);

        walker.traverse(graph,
                        callback);

        verify(callback).startGraphTraversal(eq(graph));
        verify(callback).endGraphTraversal();

        verify(callback,
               atLeast(1)).startNodeTraversal(nodeStartVisitCaptor.capture());
        verify(callback,
               atLeast(1)).endNodeTraversal(nodeEndVisitCaptor.capture());

        verify(callback,
               atLeast(1)).startEdgeTraversal(edgeStartVisitCaptor.capture());
        verify(callback,
               atLeast(1)).endEdgeTraversal(edgeEndVisitCaptor.capture());

        assertEquals(3,
                     nodeStartVisitCaptor.getAllValues().size());
        assertEquals(3,
                     nodeEndVisitCaptor.getAllValues().size());
        //node1->node2, node2->node3, node3->[no connections]
        assertNodeVisits(nodeStartVisitCaptor.getAllValues(),
                         "node1",
                         "node2",
                         "node3");
        //node1->node2, node2->node3, node3->[no connections]
        assertNodeVisits(nodeEndVisitCaptor.getAllValues(),
                         "node1",
                         "node2",
                         "node3");

        assertEquals(2,
                     edgeStartVisitCaptor.getAllValues().size());
        assertEquals(2,
                     edgeEndVisitCaptor.getAllValues().size());
        assertEdgeVisits(edgeStartVisitCaptor.getAllValues(),
                         "edge1:node1-node2",
                         "edge2:node2-node3");
        assertEdgeVisits(edgeEndVisitCaptor.getAllValues(),
                         "edge1:node1-node2",
                         "edge2:node2-node3");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkDisconnectedTargetNode() {
        final Node node1 = new NodeImpl<>("node1");
        final Node node2 = new NodeImpl<>("node2");
        final Edge c1 = new EdgeImpl<>("edge1:node1-null");
        final Edge c2 = new EdgeImpl<>("edge2:node1-node2");

        //Connect node1 with nothing!
        connectNode(node1,
                    c1);

        graph.addNode(node1);
        graph.addNode(node2);

        //Propose to connect node12 to node2 with c2
        walker = new AcyclicDirectedGraphWalker(node1,
                                                node2,
                                                c2);

        walker.traverse(graph,
                        callback);

        verify(callback).startGraphTraversal(eq(graph));
        verify(callback).endGraphTraversal();

        verify(callback,
               atLeast(1)).startNodeTraversal(nodeStartVisitCaptor.capture());
        verify(callback,
               atLeast(1)).endNodeTraversal(nodeEndVisitCaptor.capture());

        verify(callback,
               atLeast(1)).startEdgeTraversal(edgeStartVisitCaptor.capture());
        verify(callback,
               atLeast(1)).endEdgeTraversal(edgeEndVisitCaptor.capture());

        assertEquals(3,
                     nodeStartVisitCaptor.getAllValues().size());
        assertEquals(3,
                     nodeEndVisitCaptor.getAllValues().size());
        //node1->null, node1->node2, node2->[no connections]
        assertNodeVisits(nodeStartVisitCaptor.getAllValues(),
                         "node1",
                         "node1",
                         "node2");
        //node1->null, node1->node2, node2->[no connections]
        assertNodeVisits(nodeEndVisitCaptor.getAllValues(),
                         "node1",
                         "node1",
                         "node2");

        assertEquals(2,
                     edgeStartVisitCaptor.getAllValues().size());
        assertEquals(2,
                     edgeEndVisitCaptor.getAllValues().size());
        assertEdgeVisits(edgeStartVisitCaptor.getAllValues(),
                         "edge1:node1-null",
                         "edge2:node1-node2");
        assertEdgeVisits(edgeEndVisitCaptor.getAllValues(),
                         "edge1:node1-null",
                         "edge2:node1-node2");
    }

    private void assertNodeVisits(final List<Node> nodes,
                                  final String... expectedUUIDs) {
        final List<String> actualUUIDs = nodes.stream().map(Node::getUUID).collect(Collectors.toList());
        assertVisits(Arrays.asList(expectedUUIDs),
                     actualUUIDs);
    }

    private void assertEdgeVisits(final List<Edge> edges,
                                  final String... expectedUUIDs) {
        final List<String> actualUUIDs = edges.stream().map(Edge::getUUID).collect(Collectors.toList());
        assertVisits(Arrays.asList(expectedUUIDs),
                     actualUUIDs);
    }

    private void assertVisits(final List<String> expectedUUIDs,
                              final List<String> actualUUIDs) {
        assertEquals(expectedUUIDs.size(),
                     actualUUIDs.size());
        final List<String> matches = new ArrayList<>(expectedUUIDs);
        matches.removeAll(actualUUIDs);
        assertTrue(matches.isEmpty());
    }

    @SuppressWarnings("unchecked")
    private void connectNode(final Node source,
                             final Edge connector) {
        source.getOutEdges().add(connector);
        connector.setSourceNode(source);
    }

    @SuppressWarnings("unchecked")
    private void connectNodes(final Node source,
                              final Node target,
                              final Edge connector) {
        source.getOutEdges().add(connector);
        target.getInEdges().add(connector);
        connector.setSourceNode(source);
        connector.setTargetNode(target);
    }
}
