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


package com.ait.lienzo.client.core.layout.sugiyama.step03;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ait.lienzo.client.core.layout.sugiyama.GraphLayer;
import com.ait.lienzo.client.core.layout.sugiyama.OrientedEdge;

/**
 * Counts the edges crossing.
 */
public class LayerCrossingCount {

    /**
     * Defines the default weight(cost) to reach a vertex.
     */
    private static final int DEFAULT_VERTEX_WEIGHT = 1;

    /**
     * Counts the edges crossing considering two layers.
     *
     * @param edges The existing edges.
     * @param north The north layer.
     * @param south The south layer.
     * @return The total of edges crossing.
     */
    public int crossing(final List<OrientedEdge> edges,
                        final GraphLayer north,
                        final GraphLayer south) {

        final Object[] entries = flat(edges, north, south);

        int firstIndex = 1;
        while (firstIndex < south.getVertices().size()) {
            firstIndex <<= 1;
        }
        final int treeSize = 2 * firstIndex - 1;
        firstIndex -= 1;
        final int[] tree = new int[treeSize];

        int crossings = 0;

        for (final Object entry : entries) {
            int index = ((Integer) entry) + firstIndex;
            if (index < 0) {
                continue;
            }
            tree[index] += DEFAULT_VERTEX_WEIGHT;
            int weightSum = 0;
            while (index > 0) {
                if (index % 2 != 0) {
                    weightSum += tree[index + 1];
                }
                index = (index - 1) >> 1;
                tree[index] += DEFAULT_VERTEX_WEIGHT;
            }
            crossings += DEFAULT_VERTEX_WEIGHT * weightSum;
        }

        return crossings;
    }

    /**
     * Counts the total of edges crossing in all layers.
     *
     * @param layers Existing layers.
     * @param edges  Existing edges.
     * @return The sum of edges crossing between all layers.
     */
    int crossing(final List<GraphLayer> layers,
                 final List<OrientedEdge> edges) {
        int crossingCount = 0;
        for (int i = 1; i < layers.size(); i++) {
            crossingCount += crossing(edges, layers.get(i - 1), layers.get(i));
        }
        return crossingCount;
    }

    private Object[] flat(final List<OrientedEdge> edges,
                          final GraphLayer north,
                          final GraphLayer south) {

        final ArrayList<String> southPos = new ArrayList<>(south.getVertices().size());
        for (int i = 0; i < south.getVertices().size(); i++) {
            southPos.add(south.getVertices().get(i).getId());
        }

        return north.getVertices().stream().flatMap(v -> {
            List<OrientedEdge> connectedEdges = edges.stream()
                    .filter(e -> (e.getToVertexId().equals(v.getId()) || e.getFromVertexId().equals(v.getId())))
                    .collect(Collectors.toList());

            return connectedEdges.stream().map(e -> {
                if (southPos.contains(e.getToVertexId())) {
                    return southPos.indexOf(e.getToVertexId());
                }
                return southPos.indexOf(e.getFromVertexId());
            }).sorted();
        }).toArray();
    }
}
