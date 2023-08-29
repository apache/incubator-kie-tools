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

import java.util.Map;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;

public class MapIndex implements MutableIndex<Node, Edge> {

    final Graph<?, Node> graph;
    final Map<String, Node> nodes;
    final Map<String, Edge> edges;

    public MapIndex(final Graph<?, Node> graph,
                    final Map<String, Node> nodes,
                    final Map<String, Edge> edges) {
        this.graph = graph;
        this.nodes = nodes;
        this.edges = edges;
    }

    @Override
    public Graph<?, Node> getGraph() {
        return graph;
    }

    @Override
    public Element get(final String uuid) {
        Element node = nodes.get(uuid);
        if (null == node) {
            return edges.get(uuid);
        }
        return node;
    }

    @Override
    public Node getNode(final String uuid) {
        return nodes.get(uuid);
    }

    @Override
    public Edge getEdge(final String uuid) {
        return edges.get(uuid);
    }

    @Override
    public MutableIndex<Node, Edge> addNode(final Node node) {
        nodes.put(node.getUUID(),
                  node);
        return this;
    }

    @Override
    public MutableIndex<Node, Edge> removeNode(final Node node) {
        nodes.remove(node.getUUID());
        return this;
    }

    @Override
    public MutableIndex<Node, Edge> addEdge(final Edge edge) {
        edges.put(edge.getUUID(),
                  edge);
        return this;
    }

    @Override
    public MutableIndex<Node, Edge> removeEdge(final Edge edge) {
        edges.remove(edge.getUUID());
        return this;
    }

    @Override
    public void clear() {
        nodes.clear();
        edges.clear();
    }
}
