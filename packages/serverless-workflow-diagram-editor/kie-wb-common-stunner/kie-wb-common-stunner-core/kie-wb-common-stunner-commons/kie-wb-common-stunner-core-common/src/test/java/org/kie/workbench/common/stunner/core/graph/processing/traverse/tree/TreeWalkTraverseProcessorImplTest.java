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


package org.kie.workbench.common.stunner.core.graph.processing.traverse.tree;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TreeWalkTraverseProcessorImplTest {

    private TestingGraphMockHandler graphTestHandler;
    private TreeWalkTraverseProcessorImpl tested;

    @Before
    public void setup() {
        this.graphTestHandler = new TestingGraphMockHandler();
        this.tested = new TreeWalkTraverseProcessorImpl()
                .useStartNodePredicate(n -> n.getInEdges().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTraverseGraph1() {
        final TestingGraphInstanceBuilder.TestGraph1 result =
                TestingGraphInstanceBuilder.newGraph1(graphTestHandler);
        final TreeTraverseCallback callback = mock(TreeTraverseCallback.class);
        when(callback.startEdgeTraversal(any(Edge.class))).thenReturn(true);
        when(callback.startNodeTraversal(any(Node.class))).thenReturn(true);
        tested.traverse(result.graph,
                        callback);
        verify(callback,
               times(1)).startGraphTraversal(eq(result.graph));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.startNode));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.intermNode));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.endNode));
        verify(callback,
               times(1)).endNodeTraversal(eq(result.startNode));
        verify(callback,
               times(1)).endNodeTraversal(eq(result.intermNode));
        verify(callback,
               times(1)).endNodeTraversal(eq(result.endNode));
        verify(callback,
               times(1)).startEdgeTraversal(eq(result.edge1));
        verify(callback,
               times(1)).endEdgeTraversal(eq(result.edge1));
        verify(callback,
               times(1)).startEdgeTraversal(eq(result.edge2));
        verify(callback,
               times(1)).endEdgeTraversal(eq(result.edge2));
        verify(callback,
               times(1)).endGraphTraversal();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTraverseGraph2() {
        final TestingGraphInstanceBuilder.TestGraph2 result =
                TestingGraphInstanceBuilder.newGraph2(graphTestHandler);
        final TreeTraverseCallback callback = mock(TreeTraverseCallback.class);
        when(callback.startEdgeTraversal(any(Edge.class))).thenReturn(true);
        when(callback.startNodeTraversal(any(Node.class))).thenReturn(true);
        tested.traverse(result.graph,
                        callback);
        verify(callback,
               times(1)).startGraphTraversal(eq(result.graph));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.parentNode));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.startNode));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.intermNode));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.endNode));
        verify(callback,
               times(1)).endNodeTraversal(eq(result.parentNode));
        verify(callback,
               times(1)).endNodeTraversal(eq(result.startNode));
        verify(callback,
               times(1)).endNodeTraversal(eq(result.intermNode));
        verify(callback,
               times(1)).endNodeTraversal(eq(result.endNode));
        verify(callback,
               times(1)).startEdgeTraversal(eq(result.edge1));
        verify(callback,
               times(1)).endEdgeTraversal(eq(result.edge1));
        verify(callback,
               times(1)).startEdgeTraversal(eq(result.edge2));
        verify(callback,
               times(1)).endEdgeTraversal(eq(result.edge2));
        verify(callback,
               times(1)).endGraphTraversal();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTraverseGraph2UsingRootNode() {
        final TestingGraphInstanceBuilder.TestGraph2 result =
                TestingGraphInstanceBuilder.newGraph2(graphTestHandler);
        Node anotherNode = graphTestHandler.newNode("anotherNode",
                                                    "anotherId",
                                                    Optional.empty());
        final TreeTraverseCallback callback = mock(TreeTraverseCallback.class);
        when(callback.startEdgeTraversal(any(Edge.class))).thenReturn(true);
        when(callback.startNodeTraversal(any(Node.class))).thenReturn(true);
        tested.traverse(result.graph,
                        result.parentNode,
                        callback);
        verify(callback,
               never()).startNodeTraversal(eq(anotherNode));
        verify(callback,
               never()).endNodeTraversal(eq(anotherNode));
        verify(callback,
               times(1)).startGraphTraversal(eq(result.graph));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.parentNode));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.startNode));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.intermNode));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.endNode));
        verify(callback,
               times(1)).endNodeTraversal(eq(result.parentNode));
        verify(callback,
               times(1)).endNodeTraversal(eq(result.startNode));
        verify(callback,
               times(1)).endNodeTraversal(eq(result.intermNode));
        verify(callback,
               times(1)).endNodeTraversal(eq(result.endNode));
        verify(callback,
               times(1)).startEdgeTraversal(eq(result.edge1));
        verify(callback,
               times(1)).endEdgeTraversal(eq(result.edge1));
        verify(callback,
               times(1)).startEdgeTraversal(eq(result.edge2));
        verify(callback,
               times(1)).endEdgeTraversal(eq(result.edge2));
        verify(callback,
               times(1)).endGraphTraversal();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTraverseGraph1AndPendingEdges() {
        final TestingGraphInstanceBuilder.TestGraph1 result =
                TestingGraphInstanceBuilder.newGraph1(graphTestHandler);
        // Create and add a new incoming view edge into the intermediate node,
        // in order to check it's being processed as well.
        final Edge newEdge = graphTestHandler.newEdge("newEdgeUUID",
                                                      Optional.empty());
        graphTestHandler.addEdge(newEdge,
                                 result.intermNode);
        final TreeTraverseCallback callback = mock(TreeTraverseCallback.class);
        when(callback.startEdgeTraversal(any(Edge.class))).thenReturn(true);
        when(callback.startNodeTraversal(any(Node.class))).thenReturn(true);
        tested.traverse(result.graph,
                        callback);
        verify(callback,
               times(1)).startGraphTraversal(eq(result.graph));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.startNode));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.intermNode));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.endNode));
        verify(callback,
               times(1)).endNodeTraversal(eq(result.startNode));
        verify(callback,
               times(1)).endNodeTraversal(eq(result.intermNode));
        verify(callback,
               times(1)).endNodeTraversal(eq(result.endNode));
        verify(callback,
               times(1)).startEdgeTraversal(eq(result.edge1));
        verify(callback,
               times(1)).endEdgeTraversal(eq(result.edge1));
        verify(callback,
               times(1)).startEdgeTraversal(eq(result.edge2));
        verify(callback,
               times(1)).endEdgeTraversal(eq(result.edge2));
        verify(callback,
               times(1)).startEdgeTraversal(eq(newEdge));
        verify(callback,
               times(1)).endEdgeTraversal(eq(newEdge));
        verify(callback,
               times(1)).endGraphTraversal();
    }
}
