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


package org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import org.kie.workbench.common.stunner.core.graph.processing.layout.OrientedEdgeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.ReorderedGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.GraphLayer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.LayeredGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.OrientedEdge;

/**
 * Calculate position for each vertex in a graph using the simplest approach.
 * 1. Vertices are horizontal distributed inside its layer, using the same space between each one
 * 2. All layers are vertical centered
 * 3. The space between layers is the same
 */
@Default
@Dependent
public class DefaultVertexPositioning implements VertexPositioning {

    static final int DEFAULT_VERTEX_SPACE = 75;
    private static final int DEFAULT_LAYER_SPACE = 125;
    static final int DEFAULT_LAYER_HORIZONTAL_PADDING = 50;
    static final int DEFAULT_LAYER_VERTICAL_PADDING = 50;

    /*
     * Pre:
     * 1. De-reverse reversed layers
     * 2. Remove dummy vertices and reconnect each side
     */
    @Override
    public void calculateVerticesPositions(final ReorderedGraph graph,
                                           final LayerArrangement arrangement) {
        final LayeredGraph layered = (LayeredGraph) graph;
        deReverseEdges(graph);

        final Set<Vertex> vertices = getVertices(layered);

        removeVirtualVertices(graph.getEdges(), vertices);
        removeVirtualVerticesFromLayers(layered.getLayers(), vertices);
        arrangeVertices(layered.getLayers(), arrangement, graph);
    }

    Set<Vertex> getVertices(final LayeredGraph layered) {
        return layered.getLayers().stream()
                .flatMap(l -> l.getVertices().stream()).collect(Collectors.toSet());
    }

    void deReverseEdges(final ReorderedGraph graph) {
        for (final OrientedEdge edge : graph.getEdges()) {
            if (edge.isReversed()) {
                edge.reverse();
            }
        }
    }

    void arrangeVertices(final List<GraphLayer> layers,
                         final LayerArrangement arrangement,
                         final ReorderedGraph graph) {

        final HashMap<Integer, Integer> layersWidth = createHashForLayersWidth();

        int largestWidth = calculateLayersWidth(layers, layersWidth);

        // center everything based on largest width
        final HashMap<Integer, Integer> layersStartX = getLayersStartX(layers.size(), layersWidth, largestWidth);

        int y = DEFAULT_LAYER_VERTICAL_PADDING;
        switch (arrangement) {
            case TopDown:
                for (int i = 0; i < layers.size(); i++) {
                    y = distributeVertices(layers, layersStartX, y, i, graph);
                }
                break;

            case BottomUp:
                for (int i = layers.size() - 1; i >= 0; i--) {
                    y = distributeVertices(layers, layersStartX, y, i, graph);
                }
                break;
        }
    }

    HashMap<Integer, Integer> createHashForLayersWidth() {
        return new HashMap<>();
    }

    HashMap<Integer, Integer> getLayersStartX(final int layersCount,
                                              final HashMap<Integer, Integer> layersWidth,
                                              final int largestWidth) {
        final HashMap<Integer, Integer> layersStartX = new HashMap<>();
        for (int i = 0; i < layersCount; i++) {
            final int middle = largestWidth / 2;
            final int layerWidth = layersWidth.get(i);
            final int firstHalf = layerWidth / 2;
            int startPoint = middle - firstHalf;
            startPoint += DEFAULT_LAYER_HORIZONTAL_PADDING;
            layersStartX.put(i, startPoint);
        }
        return layersStartX;
    }

    int calculateLayersWidth(final List<GraphLayer> layers,
                             final HashMap<Integer, Integer> layersWidth) {
        int largestWidth = 0;
        for (int i = 0; i < layers.size(); i++) {
            final GraphLayer layer = layers.get(i);
            int currentWidth = layer.getVertices().size() * DEFAULT_VERTEX_WIDTH;
            currentWidth += (layer.getVertices().size() - 1) * DEFAULT_VERTEX_SPACE;
            layersWidth.put(i, currentWidth);
            largestWidth = Math.max(largestWidth, currentWidth);
        }
        return largestWidth;
    }

    int distributeVertices(final List<GraphLayer> layers,
                           final HashMap<Integer, Integer> layersStartX,
                           final int y,
                           final int i,
                           final ReorderedGraph graph) {

        final GraphLayer layer = layers.get(i);
        int x = layersStartX.get(i);

        int highestY = 0;

        for (final Vertex v : layer.getVertices()) {

            v.setX(x);
            v.setY(y);

            x += DEFAULT_VERTEX_SPACE;
            x += graph.getVertexWidth(v.getId());
            highestY = Math.max(highestY, graph.getVertexHeight(v.getId()));
        }

        return y + highestY + DEFAULT_LAYER_SPACE;
    }

    void removeVirtualVerticesFromLayers(final List<GraphLayer> layers,
                                         final Set<Vertex> vertices) {
        final Set<String> ids = vertices.stream().map(Vertex::getId).collect(Collectors.toSet());
        for (final GraphLayer layer : layers) {
            for (int i = 0; i < layer.getVertices().size(); i++) {
                final Vertex existingVertex = layer.getVertices().get(i);
                if (!ids.contains(existingVertex.getId())) {
                    layer.getVertices().remove(existingVertex);
                    i--;
                }
            }
        }
    }

    public boolean removeVirtualVertex(final OrientedEdge edge,
                                       final List<OrientedEdge> edges,
                                       final Set<Vertex> vertices) {

        final Optional<Vertex> toVirtual = vertices.stream()
                .filter(v -> v.isVirtual() && Objects.equals(edge.getToVertexId(), v.getId())).findFirst();

        OrientedEdge newEdge = null;
        if (toVirtual.isPresent()) {
            final String virtualVertex = edge.getToVertexId();
            // gets other side
            final Optional<OrientedEdge> otherSide = edges.stream()
                    .filter(e -> Objects.equals(e.getFromVertexId(), virtualVertex))
                    .findFirst();

            if (otherSide.isPresent()) {
                // this_vertex->virtual
                final String realToVertex = otherSide.get().getToVertexId();
                newEdge = new OrientedEdgeImpl(edge.getFromVertexId(), realToVertex);
                edges.remove(edge);
                vertices.remove(toVirtual.get());

                final Optional<OrientedEdge> oldEdge = edges.stream()
                        .filter(e -> Objects.equals(e.getFromVertexId(), toVirtual.get().getId())
                                && Objects.equals(e.getToVertexId(), realToVertex))
                        .findFirst();

                oldEdge.ifPresent(edges::remove);
            }
        }

        final Optional<Vertex> fromVirtual = vertices.stream()
                .filter(v -> v.isVirtual() && Objects.equals(edge.getFromVertexId(), v.getId())).findFirst();

        if (fromVirtual.isPresent()) {
            // virtual->this_vertex
            final String virtualVertex = edge.getFromVertexId();

            final Optional<OrientedEdge> otherSide = edges.stream()
                    .filter(e -> (Objects.equals(e.getToVertexId(), virtualVertex)))
                    .findFirst();

            if (otherSide.isPresent()) {
                final String realFromVertex = otherSide.get().getFromVertexId();
                if (newEdge == null) {
                    newEdge = new OrientedEdgeImpl(realFromVertex, edge.getToVertexId());
                } else {
                    newEdge = new OrientedEdgeImpl(realFromVertex, newEdge.getToVertexId());
                }

                edges.remove(edge);
                vertices.remove(fromVirtual.get());

                final Optional<OrientedEdge> oldEdge = edges.stream()
                        .filter(e -> Objects.equals(e.getToVertexId(), fromVirtual.get().getId()))
                        .findFirst();

                oldEdge.ifPresent(edges::remove);
            }
        }

        if (newEdge != null) {
            edges.add(newEdge);
            return true;
        }

        return false;
    }

    public void removeVirtualVertices(final List<OrientedEdge> edges,
                                      final Set<Vertex> vertices) {
        while (vertices.stream().anyMatch(Vertex::isVirtual)) {
            for (int i = 0; i < edges.size(); i++) {
                if (removeVirtualVertex(edges.get(i), edges, vertices)) {
                    i--;
                }
            }
        }
    }
}
