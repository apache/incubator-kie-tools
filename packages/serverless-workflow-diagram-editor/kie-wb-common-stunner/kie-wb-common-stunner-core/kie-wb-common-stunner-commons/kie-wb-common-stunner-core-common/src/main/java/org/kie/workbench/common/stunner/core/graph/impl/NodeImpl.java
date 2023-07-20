/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.impl;

import java.util.ArrayList;
import java.util.List;

import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

@JsType
public class NodeImpl<C> extends AbstractElement<C> implements Node<C, Edge> {

    private final List<Edge> inEdges = new ArrayList<Edge>();
    private final List<Edge> outEdges = new ArrayList<Edge>();

    public NodeImpl(String uuid) {
        super(uuid);
    }

    @Override
    public List<Edge> getInEdges() {
        return inEdges;
    }

    public Edge[] inEdgeArray() {
        return inEdges.toArray(new Edge[0]);
    }

    public Edge[] inConnectors() {
        List<Edge> inEdges = getInEdges();
        return inEdges.stream()
                .filter(e -> e.getContent() instanceof ViewConnector)
                .toArray(Edge[]::new);
    }

    @Override
    public List<Edge> getOutEdges() {
        return outEdges;
    }

    public Edge[] outEdgeArray() {
        return outEdges.toArray(new Edge[0]);
    }

    public Edge[] outConnectors() {
        List<Edge> outEdges = getOutEdges();
        return outEdges.stream()
                .filter(e -> e.getContent() instanceof ViewConnector)
                .toArray(Edge[]::new);
    }

    @Override
    public Node<C, Edge> asNode() {
        return this;
    }

    @Override
    public Edge<C, Node> asEdge() {
        return null;
    }
}
