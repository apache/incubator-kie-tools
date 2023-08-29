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


package org.kie.workbench.common.stunner.core.graph.impl;

import java.util.List;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;

public class GraphCleanup {

    public static void clear(Graph<?, Node> graph) {
        graph.nodes().forEach(n -> clearNode(n));
        graph.clear();
    }

    public static void clearNode(Node node) {
        List<Edge> outEdges = node.getOutEdges();
        clearEdges(outEdges);
        List<Edge> inEdges = node.getInEdges();
        clearEdges(inEdges);
    }

    public static void clearEdges(List<Edge> edges) {
        if (null != edges) {
            edges.forEach(e -> clearEdge(e));
            edges.clear();
        }
    }

    public static void clearEdge(Edge edge) {
        edge.setSourceNode(null);
        edge.setTargetNode(null);
    }
}
