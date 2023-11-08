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


package org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.graph.processing.layout.OrientedEdgeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.ReorderedGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.GraphLayer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.LayeredGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.OrientedEdge;

import static java.util.stream.Collectors.toList;

/**
 * Order vertices inside layers trying to reduce crossing between edges.
 */
@Default
@Dependent
public final class DefaultVertexOrdering implements VertexOrdering {

    private final VertexLayerPositioning vertexPositioning;
    private final LayerCrossingCount crossingCount;
    private final VerticesTransposer verticesTransposer;

    /**
     * Maximum number of iterations to perform.
     * 24 is the optimal number (Gansner et al 1993).
     */
    private static final int MAX_ITERATIONS = 24;

    /**
     * Default constructor.
     * @param vertexPositioning The strategy to find the position of the vertices inside a layer.
     * @param crossingCount The strategy to count the edges crossing.
     * @param verticesTransposer The strategy to transpose vertices in a layer.
     */
    @Inject
    public DefaultVertexOrdering(final VertexLayerPositioning vertexPositioning,
                                 final LayerCrossingCount crossingCount,
                                 final VerticesTransposer verticesTransposer) {
        this.vertexPositioning = vertexPositioning;
        this.crossingCount = crossingCount;
        this.verticesTransposer = verticesTransposer;
    }

    /**
     * Reorder the vertices to reduce edges crossing.
     * @param graph The graph.
     */
    @Override
    public void orderVertices(final ReorderedGraph graph) {
        final LayeredGraph layered = (LayeredGraph) graph;
        final List<OrientedEdge> edges = graph.getEdges();
        final List<GraphLayer> virtualized = createVirtual(edges, layered);
        List<GraphLayer> best = clone(virtualized);

        final Object[][] nestedBestRanks = new Object[virtualized.size()][];
        // Starts with the current order
        for (int i = 0; i < nestedBestRanks.length; i++) {
            final GraphLayer layer = best.get(i);
            nestedBestRanks[i] = new Object[layer.getVertices().size()];
            for (int j = 0; j < layer.getVertices().size(); j++) {
                nestedBestRanks[i][j] = layer.getVertices().get(j);
            }
        }

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            this.vertexPositioning.positionVertices(virtualized, edges, i);
            this.verticesTransposer.transpose(virtualized, edges, i);
            if (this.crossingCount.crossing(best, edges) > this.crossingCount.crossing(virtualized, edges)) {
                best = clone(virtualized);
            } else {
                break;
            }
        }

        layered.getLayers().clear();
        layered.getLayers().addAll(best);
    }

    private List<GraphLayer> clone(final List<GraphLayer> input) {
        final ArrayList<GraphLayer> clone = new ArrayList<>(input.size());
        for (final GraphLayer value : input) {
            clone.add(value.clone());
        }
        return clone;
    }

    /**
     * Creates virtual vertices in edges that crosses multiple layers.
     * @param edges The existing edges.
     * @param graph The graph.
     * @return The layers with virtual vertices.
     */
    private List<GraphLayer> createVirtual(final List<OrientedEdge> edges,
                                           final LayeredGraph graph) {
        int virtualIndex = 0;
        final List<GraphLayer> virtualized = clone(graph.getLayers());

        for (int i = 0; i < virtualized.size() - 1; i++) {
            final GraphLayer currentLayer = virtualized.get(i);
            final GraphLayer nextLayer = virtualized.get(i + 1);
            for (final Vertex vertex : currentLayer.getVertices()) {

                final List<OrientedEdge> outgoing = edges.stream()
                        .filter(e -> Objects.equals(e.getFromVertexId(), vertex.getId()))
                        .filter(e -> Math.abs(getLayerNumber(e.getToVertexId(), virtualized) - getLayerNumber(vertex.getId(), virtualized)) > 1)
                        .collect(toList());

                final List<OrientedEdge> incoming = edges.stream()
                        .filter(e -> Objects.equals(e.getToVertexId(), vertex.getId()))
                        .filter(e -> Math.abs(getLayerNumber(e.getFromVertexId(), virtualized) - getLayerNumber(vertex.getId(), virtualized)) > 1)
                        .collect(toList());

                for (final OrientedEdge edge : outgoing) {
                    final Vertex virtualVertex = new Vertex("V" + virtualIndex++, true);
                    nextLayer.getVertices().add(virtualVertex);
                    edges.remove(edge);
                    final OrientedEdge v1 = new OrientedEdgeImpl(edge.getFromVertexId(), virtualVertex.getId());
                    final OrientedEdge v2 = new OrientedEdgeImpl(virtualVertex.getId(), edge.getToVertexId());
                    edges.add(v1);
                    edges.add(v2);
                }

                for (final OrientedEdge edge : incoming) {
                    final Vertex virtualVertex = new Vertex("V" + virtualIndex++, true);
                    nextLayer.getVertices().add(virtualVertex);
                    edges.remove(edge);
                    final OrientedEdge v1 = new OrientedEdgeImpl(virtualVertex.getId(), edge.getToVertexId());
                    final OrientedEdge v2 = new OrientedEdgeImpl(edge.getFromVertexId(), virtualVertex.getId());
                    edges.add(v1);
                    edges.add(v2);
                }
            }
        }

        return virtualized;
    }

    private int getLayerNumber(final String vertex,
                               final List<GraphLayer> layers) {
        final GraphLayer layer = layers
                .stream()
                .filter(l -> l.getVertices().stream().anyMatch(v -> Objects.equals(v.getId(), vertex)))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Can not found the layer of the vertex."));

        return layer.getLevel();
    }
}
