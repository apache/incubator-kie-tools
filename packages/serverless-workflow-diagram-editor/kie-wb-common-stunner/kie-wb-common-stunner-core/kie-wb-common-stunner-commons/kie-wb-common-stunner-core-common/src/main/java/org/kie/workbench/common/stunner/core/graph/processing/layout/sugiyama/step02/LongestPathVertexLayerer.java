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


package org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step02;

import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Objects;

import javax.enterprise.inject.Default;

import org.kie.workbench.common.stunner.core.graph.processing.layout.ReorderedGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.GraphLayer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.GraphLayerImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.LayeredGraph;

/**
 * Assign each vertex in a graph to a layer, using the longest path algorithm.
 */
@Default
public final class LongestPathVertexLayerer implements VertexLayerer {

    private Vertex[] vertices;
    private final HashMap<String, Integer> vertexHeight;
    private LayeredGraph graph;

    public LongestPathVertexLayerer() {
        this.vertexHeight = new HashMap<>();
    }

    /**
     * Create layers for the graph and assign each vertex to a layer.
     * @param graph The graph.
     */
    @Override
    public void createLayers(final ReorderedGraph graph) {
        this.graph = (LayeredGraph) graph;
        this.vertices = new Vertex[this.graph.getVertices().size()];

        for (int i = 0; i < this.graph.getVertices().size(); i++) {
            final String v = this.graph.getVertices().get(i);
            this.vertices[i] = new Vertex(v);
            this.vertexHeight.put(v, -1);
        }

        for (final Vertex vertex : this.vertices) {
            visit(vertex);
        }
    }

    private int visit(final Vertex vertex) {
        final int height = this.vertexHeight.getOrDefault(vertex.getId(), 0);
        if (height >= 0) {
            return height;
        }

        int maxHeight = 1;

        final String[] verticesFromHere = graph.getVerticesFrom(vertex.getId());
        for (final String nextVertex : verticesFromHere) {
            if (!Objects.equals(nextVertex, vertex.getId())) {
                final Vertex next = Arrays.stream(this.vertices)
                        .filter(f -> Objects.equals(f.getId(), nextVertex))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException(
                                "Can not found the vertex pointed in other side of the OrientedEdge."
                        ));

                final int targetHeight = visit(next);
                maxHeight = Math.max(maxHeight, targetHeight + 1);
            }
        }

        addToLayer(vertex, maxHeight);
        return maxHeight;
    }

    private void addToLayer(final Vertex vertex,
                            final int height) {
        for (int i = this.graph.getLayers().size(); i < height; i++) {
            this.graph.getLayers().add(0, new GraphLayerImpl());
        }

        final int level = this.graph.getLayers().size() - height;
        final GraphLayer layer = this.graph.getLayers().get(level);
        layer.setLevel(height);
        layer.addVertex(vertex);
        vertexHeight.put(vertex.getId(), height);
    }
}