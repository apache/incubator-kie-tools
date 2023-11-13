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


package org.kie.workbench.common.stunner.core;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndex;

// Only visible on the test classpath.
// It allows proper setup when mocking objects.
public class NullSafeMapMutableIndex implements MutableIndex {

    public final MapIndex index;

    public NullSafeMapMutableIndex(final Graph<?, Node> graph) {
        this(graph, new HashMap<>(), new HashMap<>());
    }

    public NullSafeMapMutableIndex(final Graph<?, Node> graph,
                                   final Map<String, Node> nodes,
                                   final Map<String, Edge> edges) {
        this.index = new MapIndex(graph, nodes, edges);
    }

    @Override
    public Graph<?, Node> getGraph() {
        return index.getGraph();
    }

    @Override
    public Element get(String uuid) {
        if (null == uuid) {
            return null;
        }
        return index.get(uuid);
    }

    @Override
    public Node getNode(String uuid) {
        if (null == uuid) {
            return null;
        }
        return index.getNode(uuid);
    }

    @Override
    public Edge getEdge(String uuid) {
        if (null == uuid) {
            return null;
        }
        return index.getEdge(uuid);
    }

    @Override
    public MutableIndex addNode(Node node) {
        if (null == node) {
            return null;
        }
        return index.addNode(node);
    }

    @Override
    public MutableIndex removeNode(Node node) {
        if (null == node) {
            return null;
        }
        return index.removeNode(node);
    }

    @Override
    public MutableIndex addEdge(Edge edge) {
        if (null == edge) {
            return null;
        }
        return index.addEdge(edge);
    }

    @Override
    public MutableIndex removeEdge(Edge edge) {
        return null;
    }

    @Override
    public void clear() {

    }
}
