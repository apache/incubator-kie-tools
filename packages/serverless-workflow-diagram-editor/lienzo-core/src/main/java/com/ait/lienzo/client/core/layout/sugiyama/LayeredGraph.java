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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import com.ait.lienzo.client.core.layout.OrientedEdgeImpl;
import com.ait.lienzo.client.core.layout.ReorderedGraph;

public class LayeredGraph implements ReorderedGraph {

    private final List<String> vertices;
    private final List<OrientedEdge> edges;
    private final List<GraphLayer> layers;
    private final HashMap<String, Integer> verticesWidth;
    private final HashMap<String, Integer> verticesHeight;

    private String startingVertexId;
    private String endingVertexId;

    int DEFAULT_VERTEX_WIDTH = 100;
    int DEFAULT_VERTEX_HEIGHT = 50;

    /**
     * Default constructor.
     */
    public LayeredGraph() {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.layers = new ArrayList<>();
        this.verticesWidth = new HashMap<>();
        this.verticesHeight = new HashMap<>();
    }

    /**
     * Constructor.
     *
     * @param edgesMatrix Each row is an edge, each column is a vertex.
     */
    public LayeredGraph(final String[][] edgesMatrix) {
        this();
        for (final String[] edge : edgesMatrix) {
            addEdge(edge[0], edge[1]);
        }
    }

    public void setStartingVertexId(final String startingVertexId) {
        this.startingVertexId = startingVertexId;
    }

    public String getStartingVertexId() {
        return startingVertexId;
    }

    public void setEndingVertexId(final String endingVertexId) {
        this.endingVertexId = endingVertexId;
    }

    public String getEndingVertexId() {
        return endingVertexId;
    }

    public void addEdge(final String from,
                        final String to) {
        addEdge(new OrientedEdgeImpl(from, to));
    }

    public void addEdge(final OrientedEdgeImpl edge) {
        if (!this.edges.contains(edge)) {
            this.edges.add(edge);
        }

        if (!this.vertices.contains(edge.getFromVertexId())) {
            this.vertices.add(edge.getFromVertexId());
        }

        if (!this.vertices.contains(edge.getToVertexId())) {
            this.vertices.add(edge.getToVertexId());
        }
    }

    public List<GraphLayer> getLayers() {
        return this.layers;
    }

    public List<String> getVertices() {
        return this.vertices;
    }

    @Override
    public List<OrientedEdge> getEdges() {
        return this.edges;
    }

    @Override
    public int getVertexHeight(final String vertexId) {
        return verticesHeight.getOrDefault(vertexId, DEFAULT_VERTEX_HEIGHT);
    }

    @Override
    public int getVertexWidth(final String vertexId) {
        return verticesWidth.getOrDefault(vertexId, DEFAULT_VERTEX_WIDTH);
    }

    @Override
    public void setVertexSize(final String vertexId, final int width, final int height) {
        verticesWidth.put(vertexId, width);
        verticesHeight.put(vertexId, height);
    }

    public boolean isAcyclic() {
        final HashSet<String> visited = new HashSet<>();
        for (final String vertex : this.vertices) {
            if (leadsToACycle(vertex, visited)) {
                return false;
            }
        }
        return true;
    }

    private boolean leadsToACycle(final String vertex,
                                  final HashSet<String> visited) {
        if (visited.contains(vertex)) {
            return true;
        }

        visited.add(vertex);

        final String[] verticesFromThis = getVerticesFrom(vertex);
        for (final String nextVertex : verticesFromThis) {
            if (leadsToACycle(nextVertex, visited)) {
                return true;
            }
        }

        visited.remove(vertex);
        return false;
    }

    public String[] getVerticesFrom(final String vertex) {
        final HashSet<String> verticesFrom = new HashSet<>();
        for (final OrientedEdge edge : this.edges) {
            if (Objects.equals(edge.getFromVertexId(), vertex)) {
                verticesFrom.add(edge.getToVertexId());
            }
        }
        return verticesFrom.toArray(new String[0]);
    }

    public String[] getVerticesTo(final String vertex) {
        final HashSet<String> verticesTo = new HashSet<>();
        for (final OrientedEdge edge : this.edges) {
            if (Objects.equals(edge.getToVertexId(), vertex)) {
                verticesTo.add(edge.getFromVertexId());
            }
        }
        return verticesTo.toArray(new String[0]);
    }
}
