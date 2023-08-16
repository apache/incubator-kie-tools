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
import java.util.List;
import java.util.stream.Collectors;

import com.ait.lienzo.client.core.layout.VertexPosition;

/**
 * A layer in a layered graph.
 */
public final class GraphLayerImpl implements GraphLayer {

    private int level;
    private final List<VertexPosition> vertices;

    public GraphLayerImpl(final int level) {
        this();
        this.level = level;
    }

    public GraphLayerImpl() {
        this.vertices = new ArrayList<>();
    }

    @Override
    public void addVertex(final VertexPosition vertexPosition) {
        this.vertices.add(vertexPosition);
    }

    @Override
    public List<VertexPosition> getVertices() {
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

    @Override
    public GraphLayer copy() {
        final GraphLayerImpl clone = new GraphLayerImpl(this.level);
        final List<VertexPosition> cloneVertices = clone.getVertices();
        for (final VertexPosition v : this.vertices) {
            cloneVertices.add(v.copy());
        }
        return clone;
    }

    @Override
    public String toString() {
        return vertices.stream()
                .map(VertexPosition::getId)
                .collect(Collectors.joining(", ", "LAYER " + this.level + " [", "]"));
    }
}
