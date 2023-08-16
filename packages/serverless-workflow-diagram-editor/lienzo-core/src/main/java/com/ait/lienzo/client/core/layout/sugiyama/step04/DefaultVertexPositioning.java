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


package com.ait.lienzo.client.core.layout.sugiyama.step04;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.ait.lienzo.client.core.layout.Edge;
import com.ait.lienzo.client.core.layout.OrientedEdgeImpl;
import com.ait.lienzo.client.core.layout.ReorderedGraph;
import com.ait.lienzo.client.core.layout.VertexPosition;
import com.ait.lienzo.client.core.layout.sugiyama.GraphLayer;
import com.ait.lienzo.client.core.layout.sugiyama.LayeredGraph;
import com.ait.lienzo.client.core.layout.sugiyama.OrientedEdge;
import com.ait.lienzo.client.core.types.Point2D;

import static java.util.stream.Collectors.toMap;

/**
 * Calculate position for each vertex in a graph using the simplest approach.
 * 1. Vertices are horizontal distributed inside its layer, using the same space between each one
 * 2. All layers are vertical centered
 * 3. The space between layers is the same
 */

public class DefaultVertexPositioning implements VertexPositioning {

    public static final int DEFAULT_VERTEX_SPACE = 50;
    public static final int DEFAULT_LAYER_SPACE = 125;
    public static final int DEFAULT_LAYER_HORIZONTAL_PADDING = 50;
    public static final int DEFAULT_LAYER_VERTICAL_PADDING = 50;

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

        final Set<VertexPosition> vertices = getVertices(layered);

        arrangeVertices(layered.getLayers(), arrangement, layered);

        convertVirtualVerticesToBendingPoints(graph.getEdges(), vertices);

        removeVirtualVertices(graph.getEdges(), vertices);
        removeVirtualVerticesFromLayers(layered.getLayers(), vertices);
    }

    /**
     * Convert all virtual vertices to bending points.
     *
     * @param edges    The edges to have the bending points added.
     * @param vertices All vertices.
     */
    void convertVirtualVerticesToBendingPoints(final List<OrientedEdge> edges,
                                               final Set<VertexPosition> vertices) {

        final HashMap<String, VertexPosition> virtualVertices = getVirtualVertices(vertices);

        for (final VertexPosition vertex : vertices.stream().filter(v -> !v.isVirtual()).collect(Collectors.toList())) {

            final List<OrientedEdge> edgesFromVertex = getEdgesLinkedWith(edges, vertex);

            for (final OrientedEdge nextEdge : edgesFromVertex) {

                final List<Point2D> bendingPoints = new ArrayList<>();

                addBendingPoints(vertex.getId(),
                                 edges,
                                 virtualVertices,
                                 bendingPoints,
                                 nextEdge);

                if (!bendingPoints.isEmpty()) {

                    final String realVertexId = getRealVertexIdFrom(vertex.getId(), nextEdge, edges, virtualVertices);
                    final Optional<Edge> graphEdge = vertex.getOutgoingEdges().stream()
                            .filter(e -> Objects.equals(e.getTarget(), realVertexId)).findFirst();
                    graphEdge.ifPresent(edge -> edge.getBendingPoints().addAll(bendingPoints));
                }
            }
        }
    }

    private List<OrientedEdge> getEdgesLinkedWith(List<OrientedEdge> edges, VertexPosition vertex) {
        return edges.stream().filter(e -> e.isLinkedWithVertexId(vertex.getId())).collect(Collectors.toList());
    }

    private HashMap<String, VertexPosition> getVirtualVertices(Set<VertexPosition> vertices) {
        return vertices.stream()
                .filter(VertexPosition::isVirtual)
                .collect(toMap(VertexPosition::getId, vertex -> vertex, (a, b) -> b, HashMap::new));
    }

    private String getRealVertexIdFrom(
            final String source,
            final OrientedEdge nextEdge,
            final List<OrientedEdge> edges,
            HashMap<String, VertexPosition> virtualVertices) {

        String nextId;
        if (Objects.equals(nextEdge.getFromVertexId(), source)) {
            nextId = nextEdge.getToVertexId();
        } else {
            nextId = nextEdge.getFromVertexId();
        }

        if (virtualVertices.containsKey(nextId)) {

            final Optional<OrientedEdge> next = edges.stream().filter(e -> e.isLinkedWithVertexId(nextId) && !e.isLinkedWithVertexId(source)).findFirst();
            if (next.isPresent()) {
                return getRealVertexIdFrom(nextId, next.get(), edges, virtualVertices);
            }
        }

        return nextId;
    }

    private void addBendingPoints(final String source,
                                  final List<OrientedEdge> edges,
                                  final HashMap<String, VertexPosition> virtualVertices,
                                  final List<Point2D> bendingPoints,
                                  final OrientedEdge currentOrientedEdge) {

        String nextVertexId;
        if (Objects.equals(source, currentOrientedEdge.getToVertexId())) {
            nextVertexId = currentOrientedEdge.getFromVertexId();
        } else {
            nextVertexId = currentOrientedEdge.getToVertexId();
        }

        // R1 -> Vn -> Vn+1 -> .....->  Rn
        // Where R real vertices and V are virtual vertices.
        // We need to call it recursive until we found a real vertex.
        // Each virtual vertex we found is a bending point.
        if (virtualVertices.containsKey(nextVertexId)) {
            bendingPoints.add(createBendingPoint(virtualVertices.get(nextVertexId)));

            // Virtual vertices are linked only with two vertices, this one (source) and another one.
            // We want that other one.
            final Optional<OrientedEdge> nextEdge = edges.stream().filter(e -> e.isLinkedWithVertexId(nextVertexId)
                            && !e.isLinkedWithVertexId(source))
                    .findFirst();

            if (nextEdge.isPresent()) {
                addBendingPoints(nextVertexId, edges, virtualVertices, bendingPoints, nextEdge.get());
            }
        }
    }

    Point2D createBendingPoint(final VertexPosition vertexPosition) {
        return new Point2D(vertexPosition.getX(),
                           vertexPosition.getY());
    }

    Set<VertexPosition> getVertices(final LayeredGraph layered) {
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
                         final LayeredGraph graph) {

        final HashMap<Integer, Integer> layersWidth = createHashForLayersWidth();

        int largestWidth = calculateLayersWidth(layers, layersWidth);

        // center everything based on largest width
        final HashMap<Integer, Integer> layersStartX = getLayersStartX(graph, layersWidth, largestWidth);

        int y = DEFAULT_LAYER_VERTICAL_PADDING;
        if (arrangement == LayerArrangement.BottomUp) {
            for (int i = 0; i < layers.size(); i++) {
                y = distributeVertices(layers, layersStartX, y, i, graph);
            }
        } else if (arrangement == LayerArrangement.TopDown) {
            for (int i = layers.size() - 1; i >= 0; i--) {
                y = distributeVertices(layers, layersStartX, y, i, graph);
            }
        }
    }

    void orderNodesInFirstLayer(final GraphLayer layer, String startingVertexId) {
        final Optional<VertexPosition> startNode = layer.getVertices()
                .stream().filter(v -> Objects.equals(v.getId(), startingVertexId))
                .findFirst();

        if (startNode.isPresent()) {
            final VertexPosition start = startNode.get();
            layer.getVertices().remove(start);
            layer.getVertices().add(0, start);
        }
    }

    HashMap<Integer, Integer> createHashForLayersWidth() {
        return new HashMap<>();
    }

    HashMap<Integer, Integer> getLayersStartX(final LayeredGraph graph,
                                              final HashMap<Integer, Integer> layersWidth,
                                              final int largestWidth) {

        final List<GraphLayer> layers = graph.getLayers();
        if (!layers.isEmpty()) {
            orderNodesInFirstLayer(layers.get(layers.size() - 1), graph.getStartingVertexId());
        }
        final HashMap<Integer, Integer> layersStartX = new HashMap<>();
        final int middle = largestWidth / 2;

        // We ignore the layer with Start node
        for (int i = 0; i < layers.size() - 1; i++) {
            final int layerWidth = layersWidth.get(i);
            final int firstHalf = layerWidth / 2;
            int startPoint = middle - firstHalf;
            startPoint += DEFAULT_LAYER_HORIZONTAL_PADDING;
            layersStartX.put(i, startPoint);
        }

        setTopLayerStartX(layers, layersStartX, middle);

        return layersStartX;
    }

    private void setTopLayerStartX(final List<GraphLayer> layers,
                                   final HashMap<Integer, Integer> layersStartX,
                                   final int middle) {
        if (layers.isEmpty()) {
            return;
        }
        final GraphLayer startLayer = layers.get(layers.size() - 1);
        final int middleOfStart = startLayer.getVertices().get(0).getWidth() / 2;

        // The startX of the first layer is the middle of the diagram
        int start = middle - middleOfStart + DEFAULT_LAYER_HORIZONTAL_PADDING;
        layersStartX.put(layers.size() - 1, start);
    }

    int calculateLayersWidth(final List<GraphLayer> layers,
                             final HashMap<Integer, Integer> layersWidth) {
        int largestWidth = 0;
        for (int i = 0; i < layers.size(); i++) {
            final GraphLayer layer = layers.get(i);
            int totalVertexWidth = 0;

            for (final VertexPosition vertex : layer.getVertices()) {
                if (totalVertexWidth > 0) {
                    totalVertexWidth += DEFAULT_VERTEX_SPACE;
                }
                if (!vertex.isVirtual()) {
                    totalVertexWidth += vertex.getWidth();
                }
            }

            layersWidth.put(i, totalVertexWidth);
            largestWidth = Math.max(largestWidth, totalVertexWidth);
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

        for (final VertexPosition v : layer.getVertices()) {
            v.setX(x);
            v.setY(y);

            if (v.isVirtual()) {
                v.setY(y + (graph.getVertexHeight(v.getId()) / 2));
            }
            x += DEFAULT_VERTEX_SPACE;
            if (!v.isVirtual()) {
                x += graph.getVertexWidth(v.getId());
            }
            highestY = Math.max(highestY, graph.getVertexHeight(v.getId()));
        }

        return y + highestY + DEFAULT_LAYER_SPACE;
    }

    void removeVirtualVerticesFromLayers(final List<GraphLayer> layers,
                                         final Set<VertexPosition> vertices) {
        final Set<String> ids = vertices.stream().map(VertexPosition::getId).collect(Collectors.toSet());
        for (final GraphLayer layer : layers) {
            for (int i = 0; i < layer.getVertices().size(); i++) {
                final VertexPosition existingVertexPosition = layer.getVertices().get(i);
                if (!ids.contains(existingVertexPosition.getId())) {
                    layer.getVertices().remove(existingVertexPosition);
                    i--;
                }
            }
        }
    }

    public boolean removeVirtualVertex(final OrientedEdge edge,
                                       final List<OrientedEdge> edges,
                                       final Set<VertexPosition> vertices) {

        final Optional<VertexPosition> toVirtual = vertices.stream()
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

        final Optional<VertexPosition> fromVirtual = vertices.stream()
                .filter(v -> v.isVirtual() && Objects.equals(edge.getFromVertexId(), v.getId()))
                .findFirst();

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
                                      final Set<VertexPosition> vertices) {
        while (vertices.stream().anyMatch(VertexPosition::isVirtual)) {
            for (int i = 0; i < edges.size(); i++) {
                if (removeVirtualVertex(edges.get(i), edges, vertices)) {
                    i--;
                }
            }
        }
    }
}
