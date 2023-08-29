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


package com.ait.lienzo.client.core.layout.sugiyama.step02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.ait.lienzo.client.core.layout.ReorderedGraph;
import com.ait.lienzo.client.core.layout.VertexPosition;
import com.ait.lienzo.client.core.layout.sugiyama.GraphLayer;
import com.ait.lienzo.client.core.layout.sugiyama.GraphLayerImpl;
import com.ait.lienzo.client.core.layout.sugiyama.LayeredGraph;

/**
 * Assign each vertex in a graph to a layer, using the longest path algorithm.
 */
public final class LongestPathVertexLayerer implements VertexLayerer {

    private VertexPosition[] vertices;
    private final HashMap<String, Integer> vertexHeight;
    private final HashMap<Integer, List<VertexPosition>> layeredVertices;

    private LayeredGraph graph;

    public LongestPathVertexLayerer() {
        this.vertexHeight = new HashMap<>();
        this.layeredVertices = new HashMap<>();
    }

    public Map<Integer, List<VertexPosition>> getLayeredVertices() {
        return layeredVertices;
    }

    /**
     * Create layers for the graph and assign each vertex to a layer.
     *
     * @param graph The graph.
     */
    @Override
    public void createLayers(final ReorderedGraph graph) {
        this.graph = (LayeredGraph) graph;
        this.vertices = new VertexPosition[this.graph.getVertices().size()];

        for (int i = 0; i < this.graph.getVertices().size(); i++) {
            final String v = this.graph.getVertices().get(i);
            final VertexPosition createdVertexPosition = new VertexPosition(v);
            createdVertexPosition.setWidth(this.graph.getVertexWidth(v));
            createdVertexPosition.setHeight(this.graph.getVertexHeight(v));

            this.vertices[i] = createdVertexPosition;

            this.vertexHeight.put(v, -1);
        }

        for (final VertexPosition vertexPosition : this.vertices) {
            visit(vertexPosition);
        }

        // Add and create layers
        getLayeredVertices().forEach((layer, verticesInLayer) -> verticesInLayer.forEach(v -> addToLayer(v, layer)));
    }

    private int visit(final VertexPosition vertexPosition) {
        final int height = this.vertexHeight.getOrDefault(vertexPosition.getId(), 0);
        if (height >= 0) {
            return height;
        }

        int maxHeight = 1;

        final String[] verticesFromHere = graph.getVerticesTo(vertexPosition.getId());
        for (final String nextVertex : verticesFromHere) {
            if (!Objects.equals(nextVertex, vertexPosition.getId())) {
                final VertexPosition next = Arrays.stream(this.vertices)
                        .filter(f -> Objects.equals(f.getId(), nextVertex))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException(
                                "Can not found the vertex pointed in other side of the OrientedEdge."
                        ));

                final int targetHeight = visit(next);
                maxHeight = Math.max(maxHeight, targetHeight + 1);
            }
        }

        this.vertexHeight.put(vertexPosition.getId(), maxHeight);
        addToLayeredVertices(vertexPosition, maxHeight);

        return maxHeight;
    }

    private void addToLayeredVertices(final VertexPosition vertexPosition,
                                      final int layerIndex) {

        layeredVertices.computeIfAbsent(layerIndex, index -> new ArrayList<>());

        final List<VertexPosition> layer = layeredVertices.get(layerIndex);
        if (!layer.contains(vertexPosition)) {
            layer.add(vertexPosition);
        }
    }

    private void addToLayer(final VertexPosition vertexPosition,
                            final int height) {
        for (int i = this.graph.getLayers().size(); i < height; i++) {
            this.graph.getLayers().add(0, new GraphLayerImpl());
        }

        final int level = this.graph.getLayers().size() - height;
        final GraphLayer layer = this.graph.getLayers().get(level);
        layer.setLevel(height);
        layer.addVertex(vertexPosition);
        vertexHeight.put(vertexPosition.getId(), height);
    }
}

