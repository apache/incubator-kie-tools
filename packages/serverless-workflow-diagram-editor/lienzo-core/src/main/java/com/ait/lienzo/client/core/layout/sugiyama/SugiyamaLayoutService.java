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


package com.ait.lienzo.client.core.layout.sugiyama;

import java.util.HashMap;
import java.util.List;

import com.ait.lienzo.client.core.layout.AbstractLayoutService;
import com.ait.lienzo.client.core.layout.Edge;
import com.ait.lienzo.client.core.layout.Layout;
import com.ait.lienzo.client.core.layout.VertexPosition;
import com.ait.lienzo.client.core.layout.graph.OutgoingEdge;
import com.ait.lienzo.client.core.layout.graph.Vertex;
import com.ait.lienzo.client.core.layout.sugiyama.step01.CycleBreaker;
import com.ait.lienzo.client.core.layout.sugiyama.step02.VertexLayerer;
import com.ait.lienzo.client.core.layout.sugiyama.step03.VertexOrdering;
import com.ait.lienzo.client.core.layout.sugiyama.step04.LayerArrangement;
import com.ait.lienzo.client.core.layout.sugiyama.step04.VertexPositioning;

/**
 * Implementation of the Sugiyama automatic layout method.
 * This method is developed by Kozo Sugiyama, and draws the graph in a layered way.
 * This method is divided in 4 steps:
 * 1. Cycle Breaker: removes cycles in a cyclic graph.
 * 2. Vertex Layering: puts each vertex (node) in a layer.
 * 3. Vertex positioning: positions each vertex inside its layer.
 * 4. Vertex arrangement: sets the x and y position of the vertex.
 * <p>
 * The method used in each step could be changed in order to achieve better results,
 * with a better node disposition and less edges crossing.
 */
public class SugiyamaLayoutService extends AbstractLayoutService {

    private final CycleBreaker cycleBreaker;
    private final VertexLayerer vertexLayerer;
    private final VertexOrdering vertexOrdering;
    private final VertexPositioning vertexPositioning;

    static final LayerArrangement DEFAULT_LAYER_ARRANGEMENT = LayerArrangement.TopDown;

    /**
     * Default constructor.
     *
     * @param cycleBreaker      The strategy used to break cycles in cycle graphs.
     * @param vertexLayerer     The strategy used to choose the layer for each vertex.
     * @param vertexOrdering    The strategy used to order vertices inside each layer.
     * @param vertexPositioning The strategy used to position vertices on screen (x,y coordinates).
     */
    public SugiyamaLayoutService(final CycleBreaker cycleBreaker,
                                 final VertexLayerer vertexLayerer,
                                 final VertexOrdering vertexOrdering,
                                 final VertexPositioning vertexPositioning) {
        this.cycleBreaker = cycleBreaker;
        this.vertexLayerer = vertexLayerer;
        this.vertexOrdering = vertexOrdering;
        this.vertexPositioning = vertexPositioning;
    }

    @Override
    public Layout createLayout(final List<Vertex> vertices,
                               final String startingVertexId,
                               final String endingVertexId) {

        final HashMap<String, Vertex> indexByUuid = createIndex(vertices);
        final LayeredGraph layeredGraph = createLayeredGraph(vertices, startingVertexId, endingVertexId);

        this.cycleBreaker.breakCycle(layeredGraph);
        this.vertexLayerer.createLayers(layeredGraph);
        this.vertexOrdering.orderVertices(layeredGraph);

        createEdges(layeredGraph, indexByUuid);

        this.vertexPositioning.calculateVerticesPositions(layeredGraph,
                                                          DEFAULT_LAYER_ARRANGEMENT);

        return buildLayout(indexByUuid, layeredGraph);
    }

    void createEdges(final LayeredGraph layeredGraph,
                     final HashMap<String, Vertex> realVertices) {

        for (final GraphLayer layer : layeredGraph.getLayers()) {
            for (final VertexPosition vertex : layer.getVertices()) {

                if (!vertex.isVirtual()) {
                    final Vertex realVertex = realVertices.get(vertex.getId());
                    for (final OutgoingEdge outgoingEdge : realVertex.getOutgoingEdges()) {

                        final Edge edge = new Edge(outgoingEdge.getId(), vertex.getId(), outgoingEdge.getTarget().getId());
                        vertex.getOutgoingEdges().add(edge);
                    }
                }
            }
        }
    }

    HashMap<String, Vertex> createIndex(final List<Vertex> shapes) {

        final HashMap<String, Vertex> indexByUuid = new HashMap<>();
        for (final Vertex n : shapes) {

            indexByUuid.put(n.getId(), n);
        }

        return indexByUuid;
    }

    LayeredGraph createLayeredGraph(final Iterable<? extends Vertex> nodes,
                                    final String startingVertexId,
                                    final String endingVertexId) {

        final LayeredGraph layeredGraph = getLayeredGraph();
        for (final Vertex n : nodes) {

            addEdges(layeredGraph, n);

            layeredGraph.getVertices().add(n.getId());

            layeredGraph.setVertexSize(n.getId(), n.getWidth(), n.getHeight());
        }

        layeredGraph.setStartingVertexId(startingVertexId);
        layeredGraph.setEndingVertexId(endingVertexId);

        return layeredGraph;
    }

    LayeredGraph getLayeredGraph() {
        return new LayeredGraph();
    }

    void addEdges(final LayeredGraph layeredGraph, final Vertex n) {

        for (final OutgoingEdge outVertex : n.getOutgoingEdges()) {
            layeredGraph.addEdge(n.getId(), outVertex.getTarget().getId());
        }
    }

    Layout buildLayout(final HashMap<String, Vertex> indexByUuid,
                       final LayeredGraph layeredGraph) {

        final List<GraphLayer> layers = layeredGraph.getLayers();
        final Layout layout = new Layout(layers);

        for (int i = layers.size() - 1; i >= 0; i--) {
            final GraphLayer layer = layers.get(i);
            for (final VertexPosition position : layer.getVertices()) {
                final Vertex vertex = indexByUuid.get(position.getId());

                final int x = position.getX();
                final int y = position.getY();
                final int width;

                if (isCloseToZero(vertex.getWidth())) {
                    width = x + VertexPositioning.DEFAULT_VERTEX_WIDTH;
                } else {
                    width = x + vertex.getWidth();
                }

                final int height;
                if (isCloseToZero(vertex.getHeight())) {
                    height = y + VertexPositioning.DEFAULT_VERTEX_HEIGHT;
                } else {
                    height = y + vertex.getHeight();
                }

                position.setBottomRightX(width);
                position.setBottomRightY(height);
                position.setHeight(layeredGraph.getVertexHeight(position.getId()));
                position.setWidth(layeredGraph.getVertexWidth(position.getId()));
            }
        }

        return layout;
    }
}
