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


package org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;

/**
 * A layer in a layered graph.
 */
public final class GraphLayerImpl implements GraphLayer {

    private int level;
    private final List<Vertex> vertices;

    public GraphLayerImpl(final int level) {
        this();
        this.level = level;
    }

    public GraphLayerImpl() {
        this.vertices = new ArrayList<>();
    }

    @Override
    public void addVertex(final Vertex vertex) {
        this.vertices.add(vertex);
    }

    @Override
    public List<Vertex> getVertices() {
        return this.vertices;
    }

    @Override
    public void setLevel(final int level) {
        this.level = level;
    }

    @Override
    public int getLevel() {
        return level;
    }

    public void addNewVertex(final String vertexId) {
        this.vertices.add(new Vertex(vertexId));
    }

    @Override
    public GraphLayer clone() {
        final GraphLayerImpl clone = new GraphLayerImpl(this.level);
        final List<Vertex> cloneVertices = clone.getVertices();
        for (final Vertex v : this.vertices) {
            cloneVertices.add(v.clone());
        }
        return clone;
    }

    @Override
    public String toString() {
        return vertices.stream()
                .map(Vertex::getId)
                .collect(Collectors.joining(", ", "LAYER " + this.level + " [", "]"));
    }
}