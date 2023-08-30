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


package org.kie.workbench.common.stunner.core.graph.processing.index.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;

@Dependent
public class MapIndexBuilder implements GraphIndexBuilder<MapIndex> {

    @Override
    public MapIndex build(final Graph<?, Node> graph) {
        assert graph != null;
        return doWork(graph,
                      null);
    }

    @Override
    public void update(final MapIndex index,
                       final Graph<?, Node> graph) {
        doWork(graph,
               index);
    }

    private MapIndex doWork(final Graph<?, Node> graph,
                            final MapIndex current) {
        final Map<String, Node> nodes = new HashMap<>();
        final Map<String, Edge> edges = new HashMap<>();
        Iterable<Node> nodesIter = graph.nodes();
        for (Node node : nodesIter) {
            processNode(nodes,
                        edges,
                        node);
        }
        if (null == current) {
            // Requesting a new index.
            return new MapIndex(graph,
                                nodes,
                                edges);
        } else {
            // Updating an existing index.
            current.nodes.clear();
            current.nodes.putAll(nodes);
            current.edges.clear();
            current.edges.putAll(edges);
            return current;
        }
    }

    @SuppressWarnings("unchecked")
    private void processNode(final Map<String, Node> nodes,
                             final Map<String, Edge> edges,
                             final Node node) {
        if (!nodes.containsKey(node.getUUID())) {
            nodes.put(node.getUUID(),
                      node);
            final List<Edge> outEdges = node.getOutEdges();
            if (null != outEdges && !outEdges.isEmpty()) {
                for (final Edge edge : outEdges) {
                    processEdge(nodes,
                                edges,
                                edge);
                }
            }
        }
    }

    private void processEdge(final Map<String, Node> nodes,
                             final Map<String, Edge> edges,
                             final Edge edge) {
        if (!edges.containsKey(edge.getUUID())) {
            edges.put(edge.getUUID(),
                      edge);
        }
    }
}
