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


package org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step01;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Graphs;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.LayeredGraph;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ReverseEdgesCycleBreakerTest {

    @Test
    public void testAcyclicGraphs() {
        final LayeredGraph graph = new LayeredGraph(Graphs.SIMPLE_ACYCLIC);

        final ReverseEdgesCycleBreaker breaker = new ReverseEdgesCycleBreaker();
        breaker.breakCycle(graph);

        assertTrue(graph.isAcyclic());
    }

    @Test
    public void testSimpleCyclicGraph() {
        final LayeredGraph graph = new LayeredGraph(Graphs.SIMPLE_CYCLIC);
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "D");
        graph.addEdge("D", "A");

        assertFalse(graph.isAcyclic());

        final ReverseEdgesCycleBreaker breaker = new ReverseEdgesCycleBreaker();
        breaker.breakCycle(graph);

        assertTrue(graph.isAcyclic());
    }

    @Test
    public void testCyclicGraph1() {
        final LayeredGraph graph = new LayeredGraph(Graphs.CYCLIC_GRAPH_1);

        final ReverseEdgesCycleBreaker breaker = new ReverseEdgesCycleBreaker();
        breaker.breakCycle(graph);

        assertTrue(graph.isAcyclic());
    }

    @Test
    public void testCyclicGraphFull() {
        final LayeredGraph graph = new LayeredGraph(Graphs.Full);
        assertFalse(graph.isAcyclic());
        final ReverseEdgesCycleBreaker breaker = new ReverseEdgesCycleBreaker();
        breaker.breakCycle(graph);
        assertTrue(graph.isAcyclic());
    }
}