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

import java.util.HashSet;
import java.util.Objects;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import org.kie.workbench.common.stunner.core.graph.processing.layout.OrientedEdgeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.ReorderedGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.LayeredGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.OrientedEdge;

/**
 * Break cycles in a graph reversing some edges.
 */
@Default
@Dependent
public final class ReverseEdgesCycleBreaker implements CycleBreaker {

    private LayeredGraph graph;
    private final HashSet<String> visitedVertices;

    public ReverseEdgesCycleBreaker() {
        this.visitedVertices = new HashSet<>();
    }

    /**
     * Breaks all cycles found in a cyclic graph to make it acyclic.
     * @param graph The graph.
     */
    @Override
    public void breakCycle(final ReorderedGraph graph) {
        this.graph = (LayeredGraph) graph;

        for (final String vertex : this.graph.getVertices()) {
            visit(vertex);
        }
    }

    /**
     * Visit a vertex searching for acyclic paths.
     * @param vertex The vertex to visit.
     * @return true if the path is acyclic, false if is cyclic.
     */
    private boolean visit(final String vertex) {
        if (visitedVertices.contains(vertex)) {
            // Found a cycle.
            return false;
        }
        visitedVertices.add(vertex);

        final String[] verticesFromThis = getVerticesFrom(vertex);
        for (final String nextVertex : verticesFromThis) {
            if (!visit(nextVertex)) {
                final OrientedEdge toReverse = this.graph.getEdges()
                        .stream()
                        .filter(edge -> Objects.equals(edge.getFromVertexId(), vertex)
                                && Objects.equals(edge.getToVertexId(), nextVertex))
                        .findFirst()
                        .orElse(null);

                if (toReverse != null) {
                    this.graph.getEdges().remove(toReverse);
                    final OrientedEdge reversed = new OrientedEdgeImpl(toReverse.getToVertexId(), toReverse.getFromVertexId());
                    this.graph.getEdges().add(reversed);
                }
            }
        }

        visitedVertices.remove(vertex);
        return true;
    }

    private String[] getVerticesFrom(final String vertex) {
        final HashSet<String> verticesFrom = new HashSet<>();
        for (final OrientedEdge edge : this.graph.getEdges()) {
            if (Objects.equals(edge.getFromVertexId(), vertex)) {
                verticesFrom.add(edge.getToVertexId());
            }
        }
        return verticesFrom.toArray(new String[0]);
    }
}
